@echo off
title Campus Placement Portal - 1-Click Launch
cd /d "%~dp0"
cls
echo ===================================================================
echo   CAMPUS PLACEMENT DRIVE & JOB APPLICATION PORTAL
echo   Mini Project - 4th Year B.Tech Computer Science & Engineering
echo.
echo   Team Members:
echo   1. Kshitij Jangam (PRN: 1032230618) - Team Lead
echo   2. Aayush Sharma  (PRN: 1032220990)
echo ===================================================================
echo.
echo [1/2] Checking Java Runtime Environment...
where java >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Java is not installed or not found in system PATH.
    echo Please install Java 17+ (e.g. OpenJDK / Eclipse Temurin).
    echo You can install it via PowerShell: winget install Microsoft.OpenJDK.17
    echo Or run SETUP_AND_RUN.bat for automated help.
    pause
    exit /b 1
)

echo [2/2] Launching Java Swing Desktop GUI & College Web Intranet...
start "" javaw -jar CampusPlacementPortal.jar
if errorlevel 1 (
    echo Fallback: starting with console java...
    start "" java -jar CampusPlacementPortal.jar
)

timeout /t 2 /nobreak >nul
start "" http://localhost:8080

echo.
echo ===================================================================
echo  PORTAL LAUNCHED SUCCESSFULLY!
echo  - Desktop GUI is active on your screen.
echo  - Web Intranet opened in your browser at: http://localhost:8080
echo ===================================================================
exit
