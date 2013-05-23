:: Automation - It contains command to install ZUG.
:: Author : Gurpreet Anand
:: Modiied by : Nitish Rawat on 23-07-2010,Sankha on 05-01-2012,Chayan on 08-07-2011,Sankha on 12-06-2012,Sankha on 18-02-2013, Chayan 24-05-2013
:: © Copyright 2009 Automature Inc.. All Rights Reserved.

cls
:: turn off the display of the ECHO command.
@echo off

::if "%1"=="-l" goto license
::if "%1"=="-h" goto showhelp
::if "%1"=="" goto setup
::goto showhelp
:setup

:: setting local for Environment changes made after SETLOCAL are local to the batch file
setlocal enableextensions enabledelayedexpansion
set errorlevel=0

:: A variable to hold the full path where this bat file is present. So that we can have the full path.
set SAMPLE_ROOT=%~dp0.

:: Working directory will be current directory.
pushd "%~dp0."

:: Checking the .net Framework v4.0 installation
rem if not exist %windir%\Microsoft.NET\Framework\v4.0.30319 goto EndNotAbleToFindNetFramework

:: Get processor type
set processorType=%PROCESSOR_ARCHITECTURE%
if %errorlevel% NEQ 0 goto EndProcessorArchNotFound

::Install HarnessAPI.dll COM dll
rem %windir%\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe .\COM\ZugAPI.dll /codebase /tlb
rem if %errorlevel% NEQ 0 goto EndNotAbleToInstallMatness

::Add .Net dll in GAC
rem .\GAC\gacutil32Bit.exe /i System.Data.SQLite.DLL
rem if %errorlevel% NEQ 0 goto EndNotAbleToAddNetDllToGac

::Install HarnessAPI.dll COM dll for 32Bit
IF %PROCESSOR_ARCHITECTURE%==x86 %windir%\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe .\COM\x86\ZugAPI.dll /codebase /tlb
if %errorlevel% NEQ 0 goto EndNotAbleToInstallMatness32




::Add .Net dll in GAC for 32Bit
IF %PROCESSOR_ARCHITECTURE%==x86 .\GAC\gacutil32Bit.exe /i System.Data.SQLite.DLL
if %errorlevel% NEQ 0 goto EndNotAbleToAddNetDllToGAC32



::Install HarnessAPI.dll COM dll for 64Bit
IF NOT %PROCESSOR_ARCHITECTURE%==x86 %windir%\Microsoft.NET\Framework64\v4.0.30319\RegAsm.exe .\COM\x64\ZugAPI.dll /codebase /tlb
if %errorlevel% NEQ 0 goto EndNotAbleToInstallMatness64



::Add .Net dll in GAC for 64Bit
IF NOT %PROCESSOR_ARCHITECTURE%==x86 .\GAC\gacutil64Bit.exe /i .\GAC\System.Data.SQLite.DLL
if %errorlevel% NEQ 0 goto EndNotAbleToAddNetDllToGAC64



::Copy Specific ZugUtility.exe for 32Bit
IF %PROCESSOR_ARCHITECTURE%==x86 COPY .\ZugUtilityExe\x86\ZugUtility.exe .\
if %errorlevel% NEQ 0 goto EndNotAbleToCopyZugUtility



::Copy Specific ZugUtility.exe for 64Bit
IF NOT %PROCESSOR_ARCHITECTURE%==x86 COPY .\ZugUtilityExe\x64\ZugUtility.exe .\
if %errorlevel% NEQ 0 goto EndNotAbleToCopyZugUtility



::setting runZUG.bat in path variables. EditSystemPathVariable.exe 
EditSystemPathVariable.exe
if %errorlevel% NEQ 0 goto EndNotAbleToEditSystemPathVariable


Echo.
Echo. ---------------------------------------------------
ECHO. Successfully installed ZUG and its Dependent Files.
ECHO. runZUG.bat location set in System PATH variables.


ipconfig/all > config.txt


Echo.
Echo. Config file has been created with name "config.txt"
Echo. Kindly email the file Config.txt (in your Zug folder) to sales@automature.com to receive the copy of license for Zug

::EXIT the ZUG Installation - Successful Exit
EXIT /B 0


:EndProcessorArchNotFound
Echo Error MESSAGE : Not able to retrieve the Processor Architecture.
EXIT /B 2

:EndNotAbleToInstallMatness
Echo Error Message : Not able to Install Harness.dll COM dll
EXIT /B 3

:EndNotAbleToAddNetDllToGac
Echo Error Message : Not able to Install .Net dll in GAC
EXIT /B 4

:EndNotAbleToInstallMatness32
Echo Error MESSAGE : Not able Install HarnessAPI.dll COM dll for 32Bit.
EXIT /B 3

:EndNotAbleToAddNetDllToGAC32
Echo Error MESSAGE : Not able Install .Net dll in GAC for 32Bit.
EXIT /B 4

:EndNotAbleToInstallMatness64
Echo Error MESSAGE : Not able Install HarnessAPI.dll COM dll for 64Bit.
EXIT /B 5

:EndNotAbleToAddNetDllToGAC64
Echo Error MESSAGE : Not able Install .Net dll in GAC for 64Bit.
EXIT /B 6


:EndNotAbleToEditSystemPathVariable
Echo Error MESSAGE : Not able to set runZUG.bat in path variables
EXIT /B 7

:EndNotAbleToCopyZugUtility
Echo Error MESSAGE: Not able to Copy ZugUtility.exe
EXIT /B 8

:EndNotAbleToFindNetFramework
Echo Error Message : Not able to find .Net Framework v4.0.30319 installed on system
Echo Error Message : Please install .Net Framework v4.0.30319 on your system
Echo Error Message : You may download it from the ZUG Install Kit
Echo -------------------------------------------------------------------------
Echo ZUG Installation Failed
EXIT /B 8


:FailToCopy
Echo Error MESSAGE : Not able to copy sqlitejdbc-v056.jar inside jre/lib/ext
EXIT /B 9

:FailToMake
Echo Error MESSAGE : Not able to make Zug.exe inside Automature\Zug
EXIT /B 10

:license
if not exist "ZUG license.key" goto licensenotexist
echo Congratulations, License of Zug exists.

exit /b 0

:licensenotexist
echo License not found in proper place.
echo Please avail your license from www.automature.com

exit /b 1
:showhelp
echo Usage:
echo default: To start Zug installation
echo syntax : Setup
echo.
echo -l : to check your license
echo syntax : Setup -l
echo.
echo -h : to show help
echo syntax : Setup -h
echo.


exit /b


endlocal