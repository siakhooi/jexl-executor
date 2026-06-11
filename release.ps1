# PowerShell release metadata for Windows packaging.
# Dot-sourced by scripts/windows-jpackage-prep.ps1 — do not rely on running this file alone.
#
# Keep $ReleaseVersion in sync with RELEASE_VERSION in release.env (Linux / shell).
#
# $ReleaseWinUpgradeUuid: stable GUID for jpackage MSI (--win-upgrade-uuid). Do not change
# after the first MSI is published or Windows upgrade/uninstall behavior can break.

$ReleaseVersion = '1.6.2'
$ReleaseVendor  = 'Siak Hooi'
$ReleasePackageName = 'jexl-executor'
$ReleaseWinUpgradeUuid = 'a83f5c2e-1d4b-5f6a-9e7c-2b4d6e8f0a1c'
