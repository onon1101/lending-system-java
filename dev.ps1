[CmdletBinding()]
param(
    [switch]$SkipBackendBuild
)

$ErrorActionPreference = 'Stop'

$projectRoot = $PSScriptRoot
$frontendDirectory = Join-Path $projectRoot 'frontend'
$mavenWrapper = Join-Path $projectRoot 'mvnw.cmd'

if (-not (Test-Path -LiteralPath $frontendDirectory)) {
    throw "Frontend directory was not found: $frontendDirectory"
}

if (-not (Test-Path -LiteralPath $mavenWrapper)) {
    throw "Maven wrapper was not found: $mavenWrapper"
}

if (-not (Get-Command npm.cmd -ErrorAction SilentlyContinue)) {
    throw 'npm.cmd was not found. Install Node.js and ensure npm is available on PATH.'
}

if (-not (Get-Command java.exe -ErrorAction SilentlyContinue)) {
    throw 'java.exe was not found. Install the project JDK and ensure Java is available on PATH.'
}

if (-not $SkipBackendBuild) {
    Write-Host 'Building the Spring Boot application...'
    & $mavenWrapper -pl app -am package '-DskipTests'

    if ($LASTEXITCODE -ne 0) {
        throw "Backend build failed with exit code $LASTEXITCODE."
    }
}

$backendTargetDirectory = Join-Path $projectRoot 'app\target'

if (-not (Test-Path -LiteralPath $backendTargetDirectory)) {
    throw 'The backend target directory was not found. Run without -SkipBackendBuild first.'
}

$backendJar =
    Get-ChildItem -LiteralPath $backendTargetDirectory -Filter 'app-*.jar' |
        Where-Object { $_.Name -notlike '*.original' } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1

if ($null -eq $backendJar) {
    throw 'The Spring Boot application JAR was not found. Run without -SkipBackendBuild first.'
}

$frontendJob = Start-Job -Name 'lending-system-frontend' -ArgumentList $frontendDirectory -ScriptBlock {
    param($workingDirectory)

    Set-Location -LiteralPath $workingDirectory
    & npm.cmd run dev
}

try {
    Start-Sleep -Seconds 1

    if ($frontendJob.State -eq 'Failed') {
        Receive-Job -Job $frontendJob
        throw 'The SvelteKit development server failed to start.'
    }

    Write-Host 'Frontend: http://localhost:5173'
    Write-Host 'Backend:  http://localhost:8080'
    Write-Host 'Press Ctrl+C to stop both applications.'

    & java.exe -jar $backendJar.FullName

    if ($LASTEXITCODE -ne 0) {
        throw "Backend exited with code $LASTEXITCODE."
    }
}
finally {
    Stop-Job -Job $frontendJob -ErrorAction SilentlyContinue
    Receive-Job -Job $frontendJob -ErrorAction SilentlyContinue
    Remove-Job -Job $frontendJob -Force -ErrorAction SilentlyContinue
}
