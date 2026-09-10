@echo off
REM Stops java-application-template on Windows (matches by jar name in the command line).
echo Stopping task-manager-service...
for /f "tokens=2" %%P in ('wmic process where "CommandLine like '%%java-application-template%%.jar%%'" get ProcessId ^| findstr /r "[0-9]"') do (
    echo Killing PID %%P
    taskkill /PID %%P /F
)
