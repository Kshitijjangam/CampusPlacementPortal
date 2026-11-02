@echo off
setlocal enabledelayedexpansion
title Campus Placement Portal - Setup & Handover Manager
cd /d "%~dp0"

:MENU
cls
echo ===================================================================
echo   CAMPUS PLACEMENT DRIVE & JOB APPLICATION PORTAL
echo   Mini Project - 4th Year B.Tech Computer Science & Engineering
echo ===================================================================
echo   Team Members:
echo   1. Kshitij Jangam (PRN: 1032230618) - Team Lead
echo   2. Aayush Sharma  (PRN: 1032220990)
echo ===================================================================
echo.
echo  Checking Pre-requisites:
where java >nul 2>nul
if errorlevel 1 (
    echo  [!] JAVA STATUS: NOT DETECTED!
    set JAVA_OK=0
) else (
    echo  [*] JAVA STATUS: INSTALLED & READY
    set JAVA_OK=1
)

where javac >nul 2>nul
if errorlevel 1 (
    echo  [!] JAVAC STATUS: COMPILER NOT IN PATH (Runtime JAR will still run)
) else (
    echo  [*] JAVAC STATUS: JDK COMPILER READY
)
echo.
echo ===================================================================
echo  SELECT AN OPTION:
echo ===================================================================
echo  [1] Launch Application (Desktop GUI + Web Intranet Portal)
echo  [2] Run Pre-requisites Check & Auto-Install Java via Winget
echo  [3] Recompile Entire Project from Source (javac & jar)
echo  [4] Open Printable Academic Project Report (HTML / PDF Ready)
echo  [5] Open Editable Word Document Report (.docx)
echo  [6] Open Student Handover & Viva Voce Guide (Text File)
echo  [7] Open Project Directory in Windows Explorer
echo  [0] Exit
echo ===================================================================
set /p opt="Enter your choice [1-7, 0]: "

if "%opt%"=="1" goto RUN_APP
if "%opt%"=="2" goto CHECK_JAVA
if "%opt%"=="3" goto COMPILE_APP
if "%opt%"=="4" goto OPEN_HTML_REPORT
if "%opt%"=="5" goto OPEN_DOCX_REPORT
if "%opt%"=="6" goto OPEN_HANDOVER
if "%opt%"=="7" goto OPEN_DIR
if "%opt%"=="0" goto EXIT_SCRIPT

echo Invalid selection. Please try again.
timeout /t 2 >nul
goto MENU

:RUN_APP
cls
echo ===================================================================
echo  LAUNCHING CAMPUS PLACEMENT PORTAL
echo ===================================================================
if not exist "CampusPlacementPortal.jar" (
    echo [!] CampusPlacementPortal.jar not found. Compiling first...
    call :COMPILE_ROUTINE
)
echo Starting Java Desktop GUI & College Intranet Web Server...
start "" javaw -jar CampusPlacementPortal.jar
if errorlevel 1 (
    start "" java -jar CampusPlacementPortal.jar
)
timeout /t 2 /nobreak >nul
start "" http://localhost:8080
echo.
echo Application started successfully!
echo - Swing GUI is running on your screen.
echo - Web Intranet is accessible at: http://localhost:8080
echo.
pause
goto MENU

:CHECK_JAVA
cls
echo ===================================================================
echo  PRE-REQUISITES & JAVA ENVIRONMENT DIAGNOSTIC
echo ===================================================================
echo.
where java >nul 2>nul
if errorlevel 1 (
    echo [X] Java Runtime (JRE/JDK) was not found in your system PATH.
    echo.
    echo Would you like to automatically install Microsoft OpenJDK 17
    echo using Windows Package Manager (winget)?
    echo.
    set /p installJava="Install OpenJDK 17 now? [Y/N]: "
    if /i "!installJava!"=="Y" (
        echo.
        echo Running: winget install Microsoft.OpenJDK.17
        winget install Microsoft.OpenJDK.17
        echo.
        echo Please restart your terminal/command prompt after installation completes.
    ) else (
        echo.
        echo You can download Java manually from: https://adoptium.net/
    )
) else (
    echo [OK] Java is detected on your system:
    java -version
    echo.
    where javac >nul 2>nul
    if not errorlevel 1 (
        echo [OK] Java Compiler (javac) is also available:
        javac -version
    )
)
echo.
pause
goto MENU

:COMPILE_APP
cls
echo ===================================================================
echo  RECOMPILING PROJECT FROM SOURCE
echo ===================================================================
call :COMPILE_ROUTINE
echo.
pause
goto MENU

:COMPILE_ROUTINE
echo [1/3] Scanning Java source files...
if not exist bin mkdir bin
dir /s /b src\main\java\*.java > sources.txt
echo [2/3] Compiling source files with javac...
javac -encoding UTF-8 -d bin @sources.txt
if errorlevel 1 (
    echo [ERROR] Compilation failed. Ensure JDK 17+ is installed.
    if exist sources.txt del sources.txt
    exit /b 1
)
if exist src\main\resources (
    echo Copying web resources into bin...
    xcopy /E /I /Y src\main\resources\* bin\ >nul
)
echo [3/3] Packaging into standalone CampusPlacementPortal.jar...
jar --create --file CampusPlacementPortal.jar --main-class com.placement.Main -C bin .
if exist sources.txt del sources.txt
echo [SUCCESS] CampusPlacementPortal.jar is compiled and ready!
exit /b 0

:OPEN_HTML_REPORT
cls
echo Opening Printable Project Report (PROJECT_REPORT.html)...
start "" "%~dp0PROJECT_REPORT.html"
echo Tip: In your browser, press Ctrl + P or click 'Print to PDF' to save as PDF.
pause
goto MENU

:OPEN_DOCX_REPORT
cls
echo Opening Microsoft Word Project Report (.docx)...
start "" "%~dp0Campus_Placement_Portal_Project_Report.docx"
pause
goto MENU

:OPEN_HANDOVER
cls
if exist "%~dp0HANDOVER_README.txt" (
    start "" notepad "%~dp0HANDOVER_README.txt"
) else (
    start "" notepad "%~dp0README.md"
)
goto MENU

:OPEN_DIR
start "" explorer "%~dp0"
goto MENU

:EXIT_SCRIPT
exit /b 0
