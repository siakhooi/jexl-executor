# Installation

## Homebrew (macOS and Linux)

[Homebrew](https://brew.sh/) must be installed. Then run:

```bash
brew tap siakhooi/tap
brew install jexl-executor
```

Verify with `jexl-executor --version`.

## Download binaries

To install `jexl-executor` manually, visit the [Release page](https://github.com/siakhooi/jexl-executor/releases) and download the appropriate binary for your operating system and architecture.

1. Go to the [Release page](https://github.com/siakhooi/jexl-executor/releases).
2. Find the latest release and download the binary matching your OS and CPU architecture.
3. Extract the downloaded file and move the binary to a directory in your `$PATH` (for example `/usr/local/bin`).
4. Verify installation by running `jexl-executor --version`.

## Ubuntu / Debian

```bash
sudo curl -L https://siakhooi.github.io/apt/siakhooi-apt.list | sudo tee /etc/apt/sources.list.d/siakhooi-apt.list > /dev/null
sudo curl -L https://siakhooi.github.io/apt/siakhooi-apt.gpg  | sudo tee /usr/share/keyrings/siakhooi-apt.gpg > /dev/null
sudo apt update

sudo apt install siakhooi-jexl-executor
```

## Fedora / Red Hat

```bash
sudo curl -L https://siakhooi.github.io/rpms/siakhooi-rpms.repo | sudo tee /etc/yum.repos.d/siakhooi-rpms.repo > /dev/null

sudo dnf install siakhooi-jexl-executor
# or
sudo yum install siakhooi-jexl-executor
```

## Windows (winget)

Install [Windows Package Manager (winget)](https://learn.microsoft.com/en-us/windows/package-manager/winget/) if it is not already available (it ships with recent Windows 10 and Windows 11 via App Installer). Then run:

```powershell
winget install -e --id SiakHooi.JexlExecutor
```

The binary is installed at `%LOCALAPPDATA%\jexl-executor\jexl-executor.exe`. Depending on the package, that folder may not be on your `PATH`, so a new terminal might not find `jexl-executor` until you add it or use the full path below.

### Put `jexl-executor` on your PATH (recommended)

**Using PowerShell (current user):** run this once, then open a **new** terminal.

```powershell
$dir = Join-Path $env:LOCALAPPDATA 'jexl-executor'
$userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
if ($userPath -notlike "*$dir*") {
  $newPath = if ([string]::IsNullOrEmpty($userPath)) { $dir } else { "$userPath;$dir" }
  [Environment]::SetEnvironmentVariable('Path', $newPath, 'User')
}
```

**Using the GUI:** press Win, search for **Environment Variables**, open **Edit environment variables for your account**, select **Path** → **Edit** → **New**, paste `%LOCALAPPDATA%\jexl-executor`, confirm with **OK**, then open a new terminal.

### Without changing PATH

- Run by full path, for example:
  `& "$env:LOCALAPPDATA\jexl-executor\jexl-executor.exe" --version`
- Or in **Command Prompt**:
  `"%LOCALAPPDATA%\jexl-executor\jexl-executor.exe" --version`
- Optional: in File Explorer go to `%LOCALAPPDATA%\jexl-executor`, right-click `jexl-executor.exe` → **Show more options** → **Create shortcut** (e.g. on the desktop). Double-clicking only flashes a window for a CLI tool; prefer **Open in Terminal** from the right-click menu on Windows 11, or use PATH and run from PowerShell or CMD.

Verify installation with `jexl-executor --version` (after PATH is set) or using the full path as above.
