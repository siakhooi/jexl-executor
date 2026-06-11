$ErrorActionPreference = 'Stop'

$RepoRoot = Split-Path -Parent $PSScriptRoot
Set-Location -LiteralPath $RepoRoot

. (Join-Path $PSScriptRoot 'windows-jpackage-prep.ps1')

$exePath = Join-Path $RepoRoot "${PACKAGE_NAME}-${VERSION}.exe"
if (Test-Path -LiteralPath $exePath) {
    Remove-Item -LiteralPath $exePath -Force
}

$appImageDir = Join-Path $jpackageRoot $PACKAGE_NAME
if (Test-Path -LiteralPath $appImageDir) {
    Remove-Item -LiteralPath $appImageDir -Recurse -Force
}

Push-Location -LiteralPath $jpackageRoot
try {
    & jpackage.exe `
        --type exe `
        --name $PACKAGE_NAME `
        --dest $RepoRoot `
        --input input `
        --main-jar $mainJar `
        --runtime-image runtime `
        --app-version $VERSION `
        --win-console `
        --vendor $VENDOR `
        --win-dir-chooser `
        --win-per-user-install
    if ($LASTEXITCODE -ne 0) {
        throw "jpackage failed with exit code $LASTEXITCODE"
    }
}
finally {
    Pop-Location
}

if (-not (Test-Path -LiteralPath $exePath)) {
    throw "Expected installer not found: $exePath (jpackage may use a different name; check --dest output)"
}
