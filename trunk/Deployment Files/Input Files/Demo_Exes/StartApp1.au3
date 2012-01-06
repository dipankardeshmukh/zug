#Region Includes
#include "Common.au3"
#include <NamedPipes.au3>
#include <WindowsConstants.au3>
#EndRegion Includes

#Region Local Defines
#EndRegion Local Defines

#Region Executable Entry
Global $exitCode = 1

Switch GetCmdLineParamsValue("APP")
	Case "Word"
		$appVer = RegRead("HKEY_CLASSES_ROOT\Word.Document\CurVer", "")
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\" & $appVer & "\Shell\Open\command", ""), "[TITLE:Microsoft Word;]", GetCmdLineParamsValue("RETURNWINDOW"), "CP=/N")
	Case "Excel"
		$appVer = RegRead("HKEY_CLASSES_ROOT\Excel.Sheet\CurVer", "")
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\" & $appVer & "\Shell\Open\command", ""), "[TITLE:Microsoft Excel;]", GetCmdLineParamsValue("RETURNWINDOW"), "CP=/E")
	Case "PowerPoint"
		$appVer = RegRead("HKEY_CLASSES_ROOT\PowerPoint.Slide\CurVer", "")
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\" & $appVer & "\Shell\Open\command", ""), "[TITLE:Microsoft PowerPoint;]", GetCmdLineParamsValue("RETURNWINDOW"), "CP=/C")
	Case "Visio"
		$appVer = RegRead("HKEY_CLASSES_ROOT\Visio.Drawing\CurVer", "")
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\" & $appVer & "\Shell\Open\command", ""), "[TITLE:Microsoft Visio;]", GetCmdLineParamsValue("RETURNWINDOW"), "CP=/NONEW /NOLOGO")
	Case "Adobe"
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\Applications\Acrobat.exe\shell\Open\command", ""), "[TITLE:Adobe Acrobat Professional; CLASS:AdobeAcrobat]", GetCmdLineParamsValue("RETURNWINDOW"),"CP=")
	Case "Reader"
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\Applications\AcroRD32.exe\shell\Read\command", ""), "[TITLE:Adobe Reader; CLASS:AcrobatSDIWindow]", GetCmdLineParamsValue("RETURNWINDOW"),"CP=")
	Case "LMViewer"
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\Applications\lmviewer.exe\shell\open\command", ""), "[TITLE:Liquid Machines Viewer; CLASS:Liquid Machines Viewer]", GetCmdLineParamsValue("RETURNWINDOW"),"CP=")
	Case "IEXPLORE"
		AutoItSetOption("WinTitleMatchMode", 2)
		$appVer = RegRead("HKEY_CLASSES_ROOT\InternetExplorer.Application\CurVer", "")
		$exitCode = _StartApp(RegRead("HKEY_CLASSES_ROOT\Applications\iexplore.exe\Shell\Open\command", ""), "[TITLE:Internet Explorer;CLASS:IEFrame]", GetCmdLineParamsValue("RETURNWINDOW"), "CP=about:Blank")
		
	Case Else
		logFatal("Application " & GetCmdLineParamsValue("APP") & " not yet supported.")
		$exitCode = $g_XCodeIncorrectArgs
EndSwitch

Exit $exitCode
#EndRegion Executable Entry

#Region _StartApp
Func _StartApp($strExePath, $winTitle, $returnContextVar, $CmdLineParams = "")
	
	LogFuncStart("StartApp->_StartApp")
	
	; Fine tune command line
	$strExePath = StringReplace($strExePath, '"%1"', "")
	$strExePath = StringReplace($strExePath, '%1', "")
	$strExePath = StringStripWS($strExePath, 2) ; Strip trailing white space
	$command = StringUpper($strExePath & " " & $CmdLineParams)
	LogDebug("Command line is '" & $command & "'")
	Sleep(60000)
	; Need 3 strings for the functions: directory of EXE, name of EXE, and command line params required\
	$fileName = StringRegExp($command, "[[:alpha:]]*.EXE", 1)
	If IsArray($fileName) Then
		$fileName = $fileName[0]
		LogDebug("File Name : "&$fileName)
	Else
		LogFatal("Could not find executable name in executable string '" & $command &"'")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf
	$fileDir = StringRegExp($command, "[[:alpha:]]:.*\\", 1)
	If IsArray($fileDir) Then
		LogDebug("File Directory : "&$fileDir)
		$fileDir = $fileDir[0]
	Else
		LogFatal("Could not find directory in executable string '" & $command &"'")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf
	
	$cmdLnParams = StringRegExp($command, "CP=.*", 1)
	If IsArray($cmdLnParams) Then
		$cmdLnParams = StringReplace($cmdLnParams[0],"CP=","")
		LogDebug("Command Line Params : "&$cmdLnParams)
	Else
		LogFatal("Could not find additional command line params in executable string '" & $command &"'")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf
	
	; Create a pipe for the child process's STDOUT
	Local $hReadPipe, $hWritePipe
	If Not _NamedPipes_CreatePipe($hReadPipe, $hWritePipe) Then
		LogFatal("Could not create named pipe for process's execution.")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf
	
	; Set up structs for WinAPI CreateProcess
	$tProcess = DllStructCreate($tagPROCESS_INFORMATION)
	$tStartup = DllStructCreate($tagSTARTUPINFO)
	DllStructSetData($tStartup, "Size" , DllStructGetSize($tStartup))
	DllStructSetData($tStartup, "StdOutput", $hWritePipe)
	DllStructSetData($tStartup, "StdError" , $hWritePipe)
	
	LogDebug("Application Name : - "&$fileDir & "\" & $fileName)
	LogDebug("Command is : - "&$fileName & " " & $cmdLnParams)
	
	
	; Execute process using WinAPI CreateProcess to get a process handle back
	; Note that we need this to get a process handle which is different then a PID
	If Not _WinAPI_CreateProcess($fileDir & "\" & $fileName, $fileName & " " & $cmdLnParams, 0, 0, False, 0, 0, $fileDir, DllStructGetPtr($tStartup), DllStructGetPtr($tProcess)) Then
		LogFatal("_WinAPI_CreateProcess failed, could not create the process.")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf
	
	; Check to ensure PID is up and running
	If ProcessExists(DllStructGetData($tProcess, "ProcessID")) Then
		LogDebug("Process has PID of " & DllStructGetData($tProcess, "ProcessID"))
	Else
		LogFatal("Process does not have a valid PID, likely that process was not started correctly or with correct permissions.")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf
	
	; Pause execution until process is done loading and ready for input
	If _WinAPI_WaitForInputIdle(DllStructGetData($tProcess, "hProcess"), -1) Then
		LogDebug("Process is now ready.")
	Else
		LogFatal("Process is not ready for user input after timeout expired (" & $InputIdleTO & ")")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf
	
	; Update the return window context var to HWnd
	If WinWait($winTitle, "", 60) Then
		Local $window = WinGetHandle($winTitle)
		ExecuteHarnessUtility("altercontextvar "&$returnContextVar& " "& $window)
		;AlterContextVar($returnContextVar, $window)
		LogInfo("Window " & $window & " has a title of " & $winTitle)
		LogFuncEnd("StartApp->_StartApp")
		Return 0
	Else
		LogFatal("No window with title " & $winTitle & " was found.")
		LogFuncEnd("StartApp->_StartApp")
		Return 1
	EndIf

EndFunc
#EndRegion _StartApp