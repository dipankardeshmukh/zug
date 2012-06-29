:: Automation - It contains command to run ZUG - The Automation Framework.
:: Author : Gurpreet Anand
:: � Copyright 2009 Automature Inc.. All Rights Reserved.



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

ECHO Automation Started
ECHO --------------------------------------------------------------------------------

:: Deleting the Temporary Files (ANy old Temporary Files)
IF EXIST *.txt del *.txt

ZUG -DproxySet=true -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8888 -jar ZUG.jar %* %cwd%
::echo %* %cwd%
:: Deleting the Temporary Files that are created as part of the Execution
IF EXIST *.txt del *.txt

ECHO --------------------------------------------------------------------------------
ECHO Automation Finished.


popd

:: restoring the environment changes.
endlocal