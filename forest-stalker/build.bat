@echo off
setlocal
cd /d "%~dp0"

rem Forge 1.12.2 must be built with JDK 8.
rem Either set JAVA_HOME to a JDK 8 installation, or unpack one into .\jdk8
if exist "jdk8\bin\java.exe" set "JAVA_HOME=%~dp0jdk8"

if not defined JAVA_HOME (
    echo [!] JAVA_HOME is not set.
    echo     Install JDK 8 and set JAVA_HOME, or unpack a JDK 8 into "%cd%\jdk8".
    exit /b 1
)

call gradlew.bat build
if errorlevel 1 exit /b 1

if not exist dist mkdir dist
copy /y "build\libs\forest-stalker-1.12.2-2.0.0.jar" "dist\" >nul
echo.
echo Done: dist\forest-stalker-1.12.2-2.0.0.jar
