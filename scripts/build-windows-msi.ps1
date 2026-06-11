$ErrorActionPreference = 'Stop'

$RepoRoot = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $RepoRoot

. (Join-Path $PSScriptRoot 'windows-jpackage-prep.ps1')

if ([string]::IsNullOrWhiteSpace($ReleaseWinUpgradeUuid)) {
    throw '$ReleaseWinUpgradeUuid is not set in release.ps1 (required for MSI upgrades; must stay stable across releases)'
}

$msiPath = Join-Path $RepoRoot "${PACKAGE_NAME}-${VERSION}.msi"
if (Test-Path -LiteralPath $msiPath) {
    Remove-Item -LiteralPath $msiPath -Force
}

$appImageDir = Join-Path $jpackageRoot $PACKAGE_NAME
if (Test-Path -LiteralPath $appImageDir) {
    Remove-Item -LiteralPath $appImageDir -Recurse -Force
}

Push-Location -LiteralPath $jpackageRoot
try {
    & jpackage.exe `
        --type msi `
        --name $PACKAGE_NAME `
        --dest $RepoRoot `
        --input input `
        --main-jar $mainJar `
        --runtime-image runtime `
        --app-version $VERSION `
        --win-console `
        --vendor $VENDOR `
        --win-dir-chooser `
        --win-per-user-install `
        --win-upgrade-uuid $ReleaseWinUpgradeUuid
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

if (-not (Test-Path -LiteralPath $msiPath)) {
    throw "Expected installer not found: $msiPath (jpackage may use a different name; check --dest output)"
}
