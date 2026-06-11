# Build all Windows artifacts in one run (used by GitHub Actions). Runs jdeps/jlink once, then
# jpackage for app-image (zip), EXE installer, and MSI. For a single artifact locally, use
# build-windows-zip.ps1, build-windows-exe.ps1, or build-windows-msi.ps1 instead.
$ErrorActionPreference = 'Stop'

$RepoRoot = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $RepoRoot

. (Join-Path $PSScriptRoot 'windows-jpackage-prep.ps1')

if ([string]::IsNullOrWhiteSpace($ReleaseWinUpgradeUuid)) {
    throw '$ReleaseWinUpgradeUuid is not set in release.ps1 (required for MSI)'
}

$exePath = Join-Path $RepoRoot "${PACKAGE_NAME}-${VERSION}.exe"
$msiPath = Join-Path $RepoRoot "${PACKAGE_NAME}-${VERSION}.msi"
$zipName = "${PACKAGE_NAME}-${VERSION}-windows-x64.zip"
$zipPath = Join-Path $RepoRoot $zipName
foreach ($p in @($exePath, $msiPath, $zipPath)) {
    if (Test-Path -LiteralPath $p) {
        Remove-Item -LiteralPath $p -Force
    }
}

$appImageDir = Join-Path $jpackageRoot $PACKAGE_NAME
if (Test-Path -LiteralPath $appImageDir) {
    Remove-Item -LiteralPath $appImageDir -Recurse -Force
}

Push-Location -LiteralPath $jpackageRoot
try {
    $common = @(
        '--name', $PACKAGE_NAME,
        '--input', 'input',
        '--main-jar', $mainJar,
        '--runtime-image', 'runtime',
        '--app-version', $VERSION,
        '--win-console',
        '--vendor', $VENDOR
    )

    & jpackage.exe @('--type', 'app-image') @common
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage app-image failed with exit code $LASTEXITCODE"
    }

    Compress-Archive -Path $PACKAGE_NAME -DestinationPath $zipPath -CompressionLevel Optimal

    & jpackage.exe @('--type', 'exe', '--dest', $RepoRoot) @common @('--win-dir-chooser', '--win-per-user-install')
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage exe failed with exit code $LASTEXITCODE"
    }

    & jpackage.exe @('--type', 'msi', '--dest', $RepoRoot) @common @(
        '--win-dir-chooser',
        '--win-per-user-install',
        '--win-upgrade-uuid', $ReleaseWinUpgradeUuid
    )
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage msi failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

foreach ($p in @($exePath, $msiPath, $zipPath)) {
    if (-not (Test-Path -LiteralPath $p)) {
        throw "Expected output missing: $p"
    }
}
