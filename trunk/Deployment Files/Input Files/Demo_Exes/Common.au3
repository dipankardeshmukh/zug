#Region Includes
#include-once
#include <Array.au3>
#include <WinAPI.au3>
#include <GUIConstantsEx.au3>
#include <GUIComboBox.au3>
#include <Constants.au3>
#include "LmConstants.au3"
#EndRegion Includes

#Region Global Defines
Global $textLogFileName
Global $textLogHandle
Global $harnessLoggingEnabled
Global $harnessUtilityFileName
Global $harnessCOMObj
Global $ScriptParams
#EndRegion Global Defines


#Region Executable Entry
;~ If someone tries to launch this script explicitly, fail out with illegal call error code
If @ScriptName = "Common.au3" Then Exit $g_XCodeIllegalCall
#EndRegion

#Region AutoIt Enter and Exit Functions
#Region OnAutoItStart
;~ Runs automatically by AutoIt at startup, creates/appends to the log file and adds the header for this session
Func OnAutoItStart()
	
	TraySetToolTip(@AutoItExe & " running with PID " & @AutoItPID)

	Global $textLogFileName = "autoitprimitive.log"
	
	; Attempt to instantiate the harness COM object, if successful use it for logging, if not use text logging
	;Global $harnessCOMObj = ObjCreate("AutomationHarness.HarnessAPI")
	;If @error Then
		Global $harnessLoggingEnabled = False
	;Else 
	;	Global $harnessLoggingEnabled = True
	;EndIf
	
	; Dump the log file header and add an error if it is doing text file logging
	DumpLogEntryHeader()
	If Not $harnessLoggingEnabled Then FileWriteLine($textLogHandle, "ERROR: No such COM object 'AutomationHarness.HarnessAPI' registered on this computer, or DLL unaccessable.")
	
	Global $ScriptParams = GetScriptParams()
	AutoItSetOption("SendKeyDelay", 250)
	AutoItSetOption("SendKeyDownDelay", 50)

EndFunc
#EndRegion OnAutoItStart

#Region OnAutoItExit
;~ Runs automatically by AutoIt at script exit, dumps the exit codes into the log, adds the session footer, and closes the log
Func OnAutoItExit()
	
	LogInfo("Exit method was " & @exitMethod)
	LogInfo("Exit code was " & @exitCode)
	LogInfo("--------------- PRIMITIVE ENDED " & @MON & "/" & @MDAY & "/" & @YEAR & " " & @HOUR & ":" & @MIN & ":" & @SEC & "---------------")
	
	If Not $harnessLoggingEnabled Then FileClose($textLogHandle)
	
	Exit @exitCode
	
EndFunc
#EndRegion OnAutoItExit
#EndRegion AutoIt Enter and Exit Functions

#Region Harness API Functions
;~ Log as error message to the log file
Func LogError($msg)
	LogMsg("error", $msg)
EndFunc

;~ Log as fatal error message to the log file
Func LogFatal($msg)
	LogMsg("fatal", $msg)
EndFunc

;~ Log as debug message to the log file
Func LogDebug($msg)
	LogMsg("debug", $msg)
EndFunc

;~ Log as info message to the log file
Func LogInfo($msg)
	LogMsg("info", $msg)
EndFunc

;~ Log as success message to the log file
Func LogSuccess($msg)
	LogMsg("info", $msg)
EndFunc

;~ Implementation of message logging, should not be called directly
Func LogMsg($msgType, $msg)
	
	DllCall("kernel32.dll", "none", "OutputDebugString", "str", $msgType &" : "&$msg)
	
	If $harnessLoggingEnabled Then $harnessCOMObj.Log($msgType, $msg)
	
	If @error Then
		Global $harnessLoggingEnabled = False
		Global $textLogHandle = FileOpen($textLogFileName, 1)
		DumpLogEntryHeader()
		LogError("Logging via harness COM object failed, reverting to text file logging...")
	EndIf
			
	If Not $harnessLoggingEnabled Then
		FileWriteLine($textLogHandle, @HOUR & ":" & @MIN & ":" & @SEC & ": " & $msgType & ": " & $msg)
	EndIf

EndFunc

Func ExecuteHarnessUtility($Parameters)
	Local $var = Run(@ComSpec & ' /c HarnessUtility.exe ' &$Parameters , "", @SW_HIDE, $STDERR_CHILD + $STDOUT_CHILD)
	Local $line
	Local $lineError
	While 1
		$line = $line & StdoutRead($var)
		If @error Then ExitLoop
	Wend
	

	While 1
		$lineError = $lineError & StderrRead($var)
		If @error Then ExitLoop
	Wend

	MsgBox(0, "Output", $line)
	MsgBox(0, "Error", $line)

EndFunc

;~ Alter a context variable
;~ This function will only allow changing the contents of a context variable, not creating a new one
Func AlterContextVar($name, $value)
	
	LogDebug("Setting context var " & $name & " to value " & $value )
	
	
	If $harnessLoggingEnabled Then
				
		$value = String($value)
		LogDebug("Setting context var " & $name & " to value " & $value)
		$previousValue = GetContextVar($name)
		
		If @error Or ($previousValue == "") Then
			LogError("AlterContextVar was called to change variable " & $name & " to value " & $value & " but the context variable " & $name & " does not currently exist.")
			Return 1
		EndIf
		
		;$harnessCOMObj.AlterContextVar($name, $value)
		
		If @error or ($previousValue == "") Then
			LogError("AlterContextVar was called to change variable " & $name & " to value " & $value & " but function failed to update the value.")
			Return 1
		Else
			LogDebug("Variable altered successfully!!!")
		EndIf
		
	Else
		LogError("AlterContextVar() could not function because harness COM is not accessable.")
	EndIf	
	
EndFunc

;~ Get a context variable
Func GetContextVar($name)
	
	Local $retValue = ""

	If $harnessLoggingEnabled Then
		
		$retValue = $harnessCOMObj.GetContextVar($name)
		 
		If @error Then
			LogError("Could not retrieve the value of context variable " & $name & ", error message returned from harness was " & $retValue)
		Else
			LogDebug("The value for context variable " & $name & " is " & $retValue)
		EndIf
	
	Else
		LogError("GetContextVar() could not function because harness COM is not accessable.")
	EndIf	
			
	Return $retValue
	
EndFunc
#EndRegion Harness API Functions

#Region Logging Functions
#Region LogFuncStart
;~ Marks the log when script execution entereed a function, needed to debug script errors by provid
Func LogFuncStart($strFuncName)
	LogDebug("Function entered: " & $strFuncName)
EndFunc
#EndRegion LogFuncStart

#Region LogFuncEnd
;~ Marks when the end of a functions is encountered
Func LogFuncEnd($strFuncName)
	LogDebug("Function exited: " & $strFuncName)
EndFunc
#EndRegion LogFuncEnd

#Region DumpLogEntryHeader
;~ Dumps the log file header to the log
Func DumpLogEntryHeader()

	If Not $harnessLoggingEnabled Then
		Global $textLogHandle = FileOpen($textLogFileName, 1)
		FileWriteLine($textLogHandle, "--------------- PRIMITIVE STARTED " & @MON & "/" & @MDAY & "/" & @YEAR & " " & @HOUR & ":" & @MIN & ":" & @SEC & "---------------")
		FileWriteLine($textLogHandle, "Command line was " & @AutoItExe & " " & $CmdLineRaw)
		FileWriteLine($textLogHandle, "User script was launched under was " & @UserName)
		FileWriteLine($textLogHandle, "Computer name was " & @ComputerName)
	Else
		$harnessCOMObj.Log("info", "--------------- PRIMITIVE STARTED " & @MON & "/" & @MDAY & "/" & @YEAR & " " & @HOUR & ":" & @MIN & ":" & @SEC & "---------------")
		$harnessCOMObj.Log("info", "Command line was " & @AutoItExe & " " & $CmdLineRaw)
		$harnessCOMObj.Log("info", "User script was launched under was " & @UserName)
		$harnessCOMObj.Log("info", "Computer name was " & @ComputerName)
	EndIf
	
EndFunc
#EndRegion DumpLogEntryHeader
#EndRegion Logging Functions

#Region Script Tools
#Region Command Line Parsing Functions
#Region ConvertToHWnd
;~ Easy shortcut for ensuring a string passed in by command line is a HWnd
Func ConvertToHWnd($strHWnd)
	;To convert 64bit Window handle from string. Get Right 8 digit from string and append '0x' to it. Then use HWnd() function to convert string
	;to Window handle. HWnd() will return proper window handle depends on machine is 32 bit or 64 bit.
	
	LogFuncStart(@ScriptName & "->ConvertToHWnd")
	LogDebug("Converting handle " & $strHWnd & " to 32 bit.")
	Local $strHWnd32 = '0x' & StringRight($strHWnd, 8)
	LogDebug("Converted 32 bit handle- " & $strHWnd32)
	Local $returnValue
	
	If Not IsHWnd($strHWnd32) Then
		$returnValue = HWnd($strHWnd32)
	Else
		$returnValue = $strHWnd32
	EndIf
	
	If @error Or ($returnValue == 0) Then
		LogError("Window with handle " & $strHWnd & " does not exist!")
		DumpWindowsToDebugLog()
		LogFuncEnd(@ScriptName & "->ConvertToHWnd")
		SetError(1)
		Return 0
	EndIf
	
	LogFuncEnd(@ScriptName & "->ConvertToHWnd")
	Return $returnValue
	
EndFunc
#EndRegion ConvertToHWnd

#Region GetScriptParams
;~ Creates a 2 dimensional array from a tagged command line that is split sets by spaces and var name / value by a =
Func GetScriptParams()
	
	LogFuncStart(@ScriptName & "->GetScriptParams")
	Local $returnValue = 0
	
	If IsArray($CmdLine) Then
		Local $counter = 1
		Local $params[$CmdLine[0] + 1][2]
		$params[0][0] = $CmdLine[0]
		
		For $index=1 To $CmdLine[0]
			$aTemp = StringSplit($CmdLine[$index], "=")
			If $aTemp[0] = 1 Then
				$params[0][0] -= 1
				_ArrayDelete($params, $counter)
			ElseIf $aTemp[0] = 2 Then
				$params[$counter][0] = $aTemp[1]
				$params[$counter][1] = $aTemp[2]
				$counter += 1
			ElseIf $aTemp[0] > 2 Then
				LogInfo("Param " & $CmdLine[$index] & " malformed, has too many '=' in it (should only have 0 or 1)!")
				Exit 1
			EndIf
		Next
		
		$returnValue = $params
	EndIf
	
	LogFuncEnd(@ScriptName & "->GetScriptParams")
	Return $returnValue
	
EndFunc
#EndRegion GetScriptParams

#region GetCmdLineParamsValue
;~ Retrieves a value from the command line based on the formal var name it was tagged with
;~ Throws error if the formal var name was not found
Func GetCmdLineParamsValue($key)
	
	LogFuncStart(@ScriptName & "->GetCmdLineParamsValue")
	Local $returnValue = 0
	
	If Not IsArray($ScriptParams) Then
		LogError("No parameters sent into script!")
		Exit $g_XCodeIncorrectArgs
	EndIf
	
	Local $value = GetParamsValue($key)
	
	If IsString($value) Then
		$returnValue = $value
	Else
		Exit $g_XCodeIncorrectArgs
	EndIf
	
	LogFuncEnd(@ScriptName & "->GetCmdLineParamsValue")
	Return $returnValue
	
EndFunc
#EndRegion GetCmdLineParamsValue

#region GetParamsValue
;~ Retrieves a value from the command line based on the formal var name it was tagged with
;~ Does not throw error if the formal var name was not found
Func GetParamsValue($key)
	
	LogFuncStart(@ScriptName & "->GetParamsValue")
	
	If Not IsArray($ScriptParams) Then Return 0
	
	Local $index = _ArraySearch($ScriptParams, $key, 0, 0, 0, 0, 1, 0)
	If $index == -1 Then
		Switch @error
			Case 1, 6
				LogDebug("Param " & $key & " was not included in parameters.")
			Case Default
				LogDebug("Internal error at command line parsing, error code = " & @error)
		EndSwitch
		SetError(@error)
		Return $paramNotFound
	EndIf
	
	LogFuncEnd(@ScriptName & "->GetParamsValue")
	Return $ScriptParams[$index][1]
	
EndFunc
#EndRegion GetParamsValue
#EndRegion Command Line Parsing Functions

#Region FindDroplet
;~ Returns the HWnd of the associated droplet given the HWnd of the parent window
Func FindDroplet($HWnd)
	
	LogFuncStart(@ScriptName & "->FindDroplet")
	Local $returnValue = 0
	
	Local $hDroplet = 0
	Local $droplets = WinList("[CLASS:LQMI_Droplet]")
	
	For $i = 1 To $droplets[0][0]
		If _WinAPI_GetParent(WinGetHandle($droplets[$i][1])) == $HWnd Then
			LogDebug("Droplet with HWnd " & $droplets[$i][1] & " is a child of " & $HWnd)
			$hDroplet = $droplets[$i][1]
			ExitLoop
		Else
			LogDebug("Droplet with HWnd " & $droplets[$i][1] & " is a child of " & $HWnd)
		EndIf
	Next
	
	If $hDroplet == 0 Then
		LogInfo("Could not find a droplet for window " & $HWnd)
		LogFuncEnd(@ScriptName & "->FindDroplet")
		SetError(1)
		Return $returnValue
	Else
		$returnValue = $hDroplet	
	EndIf
	
	LogFuncEnd(@ScriptName & "->FindDroplet")
	Return $returnValue
	
EndFunc
#EndRegion Find Droplet

#Region GetProcessWindows
;~ Returns an array containing all of the HWnds associated with the given PID or executable name
Func GetProcessWindows($exeName)
	
	LogFuncStart(@ScriptName & "->GetProcessWindows")
    Local $aWinList = WinList()
	Local $aReturnList[1]
	Local $iIndex = 0
	Local $pid = ProcessExists($exeName)
	
	LogDebug("PID of interest is " & $pid)
	LogDebug("Found " & $aWinList[0][0] & " windows open, interrogating each for matching PID...")
   
    For $i = 1 To $aWinList[0][0]
        If BitAnd(WinGetState($aWinList[$i][1]), 7) Then
			If WinGetProcess($aWinList[$i][1]) == $pid Then 
				LogDebug("Window " & $aWinList[$i][1] & " found to have PID " & $pid & " and is visible.")
				$iIndex = _ArrayAdd($aReturnList, $aWinList[$i][1])
			EndIf
		EndIf
	Next

	$aReturnList[0] = $iIndex
	LogDebug($aReturnList[0] & " windows found for PID " & $pid)
	
	LogFuncEnd(@ScriptName & "->GetProcessWindows")
    Return $aReturnList
	
EndFunc
#EndRegion GetProcessWindows



Func _GetHwndFromPID($PID)
    $hWnd = 0
    $stPID = DllStructCreate("int")
    Do
        $winlist2 = WinList()
        For $i = 1 To $winlist2[0][0]
            If $winlist2[$i][0] <> "" Then
                DllCall("user32.dll", "int", "GetWindowThreadProcessId", "hwnd", $winlist2[$i][1], "ptr", DllStructGetPtr($stPID))
                If DllStructGetData($stPID, 1) = $PID Then
                    $hWnd = $winlist2[$i][1]
                    ExitLoop
                EndIf
            EndIf
        Next
        Sleep(100)
    Until $hWnd <> 0
    Return $hWnd
EndFunc

#region GetExecutableNameFromPID
;~ Gets the name of the executable file (without extension) given a PID
;~ For example, if you give it a PID for a running instance of Word, it will return "winword" representing the winword.exe
;~ REQUIRES PSLIST.EXE TO BE PRESENT IN SAME DIRECTORY
Func GetExecutableNameFromPID($pid)
	
	LogFuncStart(@ScriptName & "->GetExecutableNameFromPID")
	
	If Not FileExists(@ScriptDir & "\pslist.exe") Then
		LogFatal("Required file pslist.exe not in executing directory (" & @ScriptDir & ").")
		Exit $g_XCodeFileMissing
	EndIf
	
	; Get process name from PID using PsList.exe (SysInternals download)
	$psList = Run(@ComSpec & " /c pslist.exe " & $pid, @ScriptDir, @SW_HIDE, $STDOUT_CHILD)
	Local $psListOutput

	While 1
		$psListOutput = $psListOutput & StdoutRead($psList)
		If @error Then ExitLoop
	Wend
	$parsed = StringSplit($psListOutput, @CR)
	$parsed = $parsed[4]
	$parsed = StringSplit($parsed, " ")
	$parsed = StringStripWS(StringStripCR($parsed[1]), 8)
	
	If StringCompare($parsed, "") == 0 Then
		LogDebug("Could not find PID " & $pid & " in process list. Dumping PsList.exe output:" & @CR & $psListOutput)
		SetError(1)
	Else
		LogDebug("Found PID " & $pid & " is process executable " & $parsed)
	EndIf
			
	LogFuncEnd(@ScriptName & "->GetExecutableNameFromPID")
	Return $parsed
	
EndFunc
#EndRegion GetExecutableNameFromPID

#Region CheckForBrownFox
; Checks contents of the file to ensure it is a readable bornw fox file.
Func CheckForBrownFox($HWnd)
	
	LogFuncStart(@ScriptName & "->CheckForBrownFox")	
	Const $key = "quick brown fox"
	
	If Not IsHWnd($HWnd) Then $HWnd = ConvertToHWnd($HWnd)
	
	If $HWnd == 1 Then
		SetError(1)
		LogFuncEnd(@ScriptName & "->CheckForBrownFox")
		Return 1
	EndIf
	
	$pid = WinGetProcess($HWnd)
	If $pid == -1 Then
		SetError(1)
		LogError("Could not detect a PID for window " & $HWnd)
		LogFuncEnd(@ScriptName & "->CheckForBrownFox")
		Return 1
	Else
		$app = GetExecutableNameFromPID($pid)
	EndIf
	
	Local $hNoFindWin = 0
	Local $returnCode = 1
	Local $noFindWinTitle
	Local $noFindWinText
	
	Opt("WinTitleMatchMode", 2)
	Switch $app
		Case "powerpnt"
			WinActivate($Hwnd)
			Send("^f")
			WinWait("[CLASS:#32770; TITLE:Find]", "", 10)
			$hControl = ControlGetHandle("[CLASS:#32770; TITLE:Find]", "", "REComboBox20W")
			ControlCommand($hControl, "", "", "Editpaste", $key)
			ControlSend($hControl, "", "", "{ENTER}")
			Sleep(5000)
			$noFindWinTitle = "[CLASS:#32770; TITLE:Microsoft Office PowerPoint]"
			$noFindWinText = "PowerPoint has finished searching the presentation. The search item wasn't found."
		Case "WINWORD"
			WinActivate($Hwnd)
			Send("^f")
			WinWait("[CLASS:bosa_sdm_Microsoft Office Word 12.0; TITLE:Find and Replace]", "", 10)
			$hControl = ControlGetHandle("[CLASS:bosa_sdm_Microsoft Office Word 12.0; TITLE:Find and Replace]", "", "RichEdit20W1")
			ControlCommand($hControl, "", "", "Editpaste", $key)
			ControlSend($hControl, "", "", "{ENTER}")
			Sleep(5000)
			$noFindWinTitle = "[CLASS:#32770; TITLE:Microsoft Office Word]"
			$noFindWinText = "Word has finished searching the document. The search item was not found."
		Case "excel"
			WinActivate($Hwnd)
			Send("^f")
			WinWait("[CLASS:bosa_sdm_XL9; TITLE:Find and Replace]", "", 10)
			$hControl = ControlGetHandle("[CLASS:bosa_sdm_XL9; TITLE:Find and Replace]", "", "EDTBX1")
			ControlCommand($hControl, "", "", "Editpaste", $key)
			ControlSend($hControl, "", "", "{ENTER}")
			Sleep(5000)
			$noFindWinTitle = "[CLASS:#32770; TITLE:Microsoft Office Excel]"
			$noFindWinText = "Microsoft Office Excel cannot find the data you're searching for."
		Case "visio"
			WinActivate($Hwnd)
			Send("^f")
			WinWait("[CLASS:AxonDlgCls; TITLE:Find]", "", 10)
			$hControl = ControlGetHandle("[CLASS:AxonDlgCls; TITLE:Find]", "", "Edit1")
			ControlCommand($hControl, "", "", "Editpaste", $key)
			ControlSend($hControl, "", "", "{ENTER}")
			Sleep(5000)
			$noFindWinTitle = "[CLASS:#32770; TITLE:Find]"
			$noFindWinText = "Visio has finished searching the page."
		Case "AcroRd32"
			WinActivate($Hwnd)
			$hControl = ControlGetHandle($Hwnd, "", "Edit1")
			ControlCommand($hControl, "", "", "Editpaste", $key)
			ControlSend($hControl, "", "", "{ENTER}")
			Sleep(10000)
			$noFindWinTitle = "[CLASS:#32770; TITLE:Adobe Reader]"
			$noFindWinText = "Reader has finished searching the document. No matches were found."
		Case "acrobat"
			WinActivate($Hwnd)
			Send("^f")
			WinWait("[CLASS:AVL_AVWindow; TITLE:Find]", "", 10)
			$hControl = ControlGetHandle("[CLASS:AVL_AVWindow; TITLE:Find]", "", "Edit1")
			ControlCommand($hControl, "", "", "Editpaste", $key)
			ControlSend($hControl, "", "", "{ENTER}")
			Sleep(10000)
			$noFindWinTitle = "[CLASS:#32770; TITLE:Adobe Acrobat]"
			$noFindWinText = "Acrobat has finished searching the document. The find item was not found"
		Case Else
			LogError(@ScriptName & "->CheckForBrownFox does not currently support application " & $app)
			LogFuncEnd(@ScriptName & "->CheckForBrownFox")
			SetError(1) 
	EndSwitch
	
	If WinExists($noFindWinTitle, $noFindWinText) Then
		$returnCode = 1
		LogError("Key '" & $key & "' was not found in window " & $HWnd)
		Send("{ESC}")
		Send("{ESC}")
		Send("{ESC}")
	Else
		$returnCode = 0
		LogSuccess("Key '" & $key & "' was found in window " & $HWnd)
		Send("{ESC}")
		Send("{ESC}")
	EndIf
		
	LogFuncEnd("Common->CheckForBrownFox")
	Return $returnCode
	
EndFunc
#EndRegion CheckForBrownFox

#Region ActivateWindow
Func ActivateWindow($HWnd, $timeOut)
	
	; This function works like built-in AutoIt functions
	; It returns 1 is successful and 0 if not, thus you can use it in IF statements like this:
	; If Not ActivateWindow($HWnd, 30) Then
	
	LogFuncStart("Common->ActivateWindow")
	
	$returnValue = 0
	
	if not IsHWnd($HWnd) then $HWnd = ConvertToHWnd($HWnd)
	if @error Then
		LogDebug("Error converting to Hwnd the handle : "&$HWnd)
		return 0
	EndIf	
	
	$counter = 0
	While $counter < 3
		If WinActivate($HWnd) Then ExitLoop
		LogDebug("Had to retry activating the window " & $HWnd & " as it failed to activated properly.")
		Sleep(2000)
		$counter += 1
	WEnd
	
	If WinWaitActive($HWnd, $timeOut) Then
		LogDebug("Window " & $HWnd & " is now active and focused.")
		$returnValue = 1
	Else
		LogDebug("Window " & $HWnd & " could not be activated.")
	EndIf
	
	LogFuncEnd("Common->ActivateWindow")
	Return $returnValue
	
EndFunc
#EndRegion ActivateWindow
#EndRegion Script Tools

#Region Debug Tools
#Region DumpWindowsToDebugLog
Func DumpWindowsToDebugLog($titleMatch = $paramNotFound)
	
	LogFuncStart("Common->DumpWindowsToDebugLog")
	Local $windows
	
	Opt("WinTitleMatchMode", 2)
	If ($titleMatch == $paramNotFound) Then
		LogDebug("No title match string given, dumping all top-level windows...")
		$windows = WinList()
	Else
		LogDebug("Title match string is '" & $titleMatch & "', will match all windows with a title containing that substring in it...")
		$windows = WinList($titleMatch)
	EndIf
	
	If ($windows[0][0] == 0) Then
		LogDebug("No windows found matching that criteria.")
	Else
		For $i = 0 To $windows[0][0] 
			LogDebug("Window with title '" & $windows[$i][0] & "' has handle " & $windows[$i][1])
		Next
	EndIf
			
	LogFuncEnd("Common->DumpWindowsToDebugLog")
	
EndFunc
#EndRegion DumpWindowsToDebugLog
#EndRegion Debug Tools
