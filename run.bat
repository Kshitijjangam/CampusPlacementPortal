@echo off
setlocal
echo ===================================================================
echo  Launching Campus Placement Portal (Java Swing Desktop GUI)...
echo ===================================================================

if not exist CampusPlacementPortal.jar (
    echo CampusPlacementPortal.jar not found. Triggering automated build...
    call build.bat
)

start javaw -jar CampusPlacementPortal.jar
