@echo off
REM Starts java-application-template on Windows.

cd /d "%~dp0.."
set "APP_HOME=%cd%"
set "CONFIG_DIR=%APP_HOME%\config"

set "JAR="
for %%F in ("%APP_HOME%\lib\java-application-template*.jar") do set "JAR=%%F"

if not defined JAR (
    echo ERROR: no jar found in "%APP_HOME%\lib"
    exit /b 1
)

echo Starting java-application-template...

start "java-application-template" /B java ^
    -Dlog4j.configurationFile="%CONFIG_DIR%\log4j2.xml" ^
    -jar "%JAR%" ^
    --spring.config.additional-location="file:%CONFIG_DIR%\"

echo Started.
