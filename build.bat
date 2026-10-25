@echo off
setlocal
echo ===================================================================
echo  CampusPlacementPortal - Building Java Swing Desktop App
echo ===================================================================

if not exist bin mkdir bin

echo [1/3] Scanning Java source files...
dir /s /b src\main\java\*.java > sources.txt

echo [2/3] Compiling with javac...
javac -encoding UTF-8 -d bin @sources.txt
if errorlevel 1 (
    echo [ERROR] Compilation failed. Ensure JDK 17+ is installed.
    del sources.txt
    pause
    exit /b 1
)

echo [3/3] Packaging into CampusPlacementPortal.jar...
jar --create --file CampusPlacementPortal.jar --main-class com.placement.Main -C bin .
if errorlevel 1 (
    echo [ERROR] Packaging failed.
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo ===================================================================
echo  SUCCESS: CampusPlacementPortal.jar is ready!
echo  Run 'run.bat' or double-click CampusPlacementPortal.jar to start.
echo ===================================================================
pause
