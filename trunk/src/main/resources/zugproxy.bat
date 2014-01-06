:: turn off the display of the ECHO command.
@echo off
:: setting local for Environment changes made after SETLOCAL are local to the batch file
setlocal
set cwd=-pwd=
set cwd=%cwd%"%cd%"

:: A variable to hold the full path where this bat file is present. So that we can have the full path and give this :: absolute path to java
set SAMPLE_ROOT=%~dp0.

::echo %SAMPLE_ROOT%
pushd "%~dp0."

set CLASSPATH=.;lib\*

java -cp %CLASSPATH% -DproxySet=true -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8888 -jar automature-zug.jar  %* %cwd%

::echo %* %cwd%
:: Deleting the Temporary Files that are created as part of the Execution
::IF EXIST *.txt del *.txt

popd

:: restoring the environment changes.
endlocal
set /p delBuild=press ENTER to close the window
