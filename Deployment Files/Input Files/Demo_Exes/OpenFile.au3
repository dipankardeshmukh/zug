#Region Includes
#include "Common.au3"
#EndRegion Includes

#Region Local Defines
Const $appWinTO = 30

#EndRegion Local Defines

#Region Executable Entry
Exit _OpenFile(GetParamsValue("METHOD"), GetCmdLineParamsValue("WINDOW"), GetCmdLineParamsValue("FILE"), GetCmdLineParamsValue("RETURNWINDOW"), GetParamsValue("FOLDER"))
#EndRegion Executable Entry

#Region _OpenFile
Func _OpenFile($method, $HWnd, $file, $returnContextVar, $folder="")
	
	LogFuncStart(@ScriptName&"->_OpenFile")
	$returnCode = 1
	
	If Not IsHWnd($HWnd) Then $HWnd = ConvertToHWnd($HWnd)
	
	if @error Then
		logError("Error converting the String handle to Hwind Type.")
		LogFuncEnd(@ScriptName&"->_OpenFile")
		return 1
	EndIf
	
	Local $HWndOpenDialog = 0
	Local $OpenTextboxID = 0
	
	If Not (StringCompare($folder, $paramNotFound)==0) Then $file = $folder & "\" & $file
	
	If Not FileExists($file) Then
		LogFatal("The file " & $file & " does not exist.")
		LogFuncEnd(@ScriptName&"->_OpenFile")
		Return 1
	EndIf
	
	Opt("WinTitleMatchMode", 2)
	
	If Not WinWait($HWnd, "", $WindowTO) Then
		LogFatal("Cannot open a file when application is not running!")
		LogFuncEnd(@ScriptName&"->_OpenFile")
		Return 1
	EndIf
	
	Local $fileParts = StringSplit($file, "\")
	Local $fileName = $fileParts[$fileParts[0]]
	
	If $method == $paramNotFound Then $method = "HotKey"
	
	if not ActivateWindow($HWnd, $WindowTO) Then
		
		LogFuncEnd(@ScriptName&"->_OpenFile")
		return 1
		
	EndIf
	Sleep(1000)
	OpenCommand($method, $HWnd)
	
	Local $counter = 0
	
	while $counter < 3 
		
		If WinWaitActive("Open", "", $OpenFileDialogTO) Then
			$HWndOpenDialog = WinGetHandle("Open", "")
			$OpenDialogClassName = _WinAPI_GetClassName($HWndOpenDialog)
			
			Dim $text = WinGetClassList($HWndOpenDialog)
		
			$StringClasses = String($text)
			
			if StringInStr( $StringClasses, "RichEdit20W2") Then
				LogDebug("Text box class is RichEdit20W2")
				$OpenTextboxID = ControlGetHandle($HWndOpenDialog, "", "RichEdit20W2")
				LogDebug("Text box Handle is : "&$OpenTextboxID)
			Elseif StringInStr( $StringClasses, "RichEdit20W") Then
				LogDebug("Text box class is RichEdit20W")
				$OpenTextboxID = ControlGetHandle($HWndOpenDialog, "", "RichEdit20W")
				LogDebug("Text box Handle is : "&$OpenTextboxID)
			ElseIf StringInStr($StringClasses, "Edit1") Then
				LogDebug("Text box class is Edit1")
				$OpenTextboxID = ControlGetHandle($HWndOpenDialog, "", "Edit1")
				LogDebug("Text box Handle is : "&$OpenTextboxID)
			Elseif StringInStr($StringClasses, "Edit") Then
				LogDebug("Text box class is Edit")
				Send("{BS}")
				$OpenTextboxID = ControlGetHandle($HWndOpenDialog, "", "Edit")
				LogDebug("Text box Handle is : "&$OpenTextboxID)
			Else
				WinClose($OpenDialogClassName)
				LogFuncEnd(@ScriptName&"->_OpenFile")
				logError("Unable to find the tesxt box class to open the file.")
				return 1
			EndIf
			ExitLoop
		Else
			$counter += 1
			LogDebug("Open dialog not found, resending open command...")
			ActivateWindow($HWnd, $WindowTO)
			Sleep(1000)
			OpenCommand($method, $HWnd)
		EndIf
	WEnd
	
	If $counter >= 3 Then
		LogFatal("Sent open file command 3 times to window " & $HWnd & " with no open file dialog box!")
		DumpWindowsToDebugLog()
		LogFuncEnd(@ScriptName&"->_OpenFile")
		Return 1
	EndIf
	;$OpenTextboxID=ConvertToHWnd($OpenTextboxID)
	LogDebug("the open text box handle used in controlcommand is " & $OpenTextboxID)
	ControlCommand("Open", "", $OpenTextboxID, "Editpaste", $file)
	ControlSend($HWndOpenDialog, "", $OpenTextboxID, "{ENTER}")
	LogDebug("Editpaste is complete")
	Sleep(3000)
		
	Opt("WinTitleMatchMode", 2)
	
	If Not WinWait($fileName, "", $WindowTO) Then
		;LogFatal("Opening file " & $file & " failed!")
		LogDebug("Failed to get the Window handle...Use FindWindow to get the handle of Window!!!")
		$returnCode = 0
	Else
		$WindowHandle = WinGetHandle($fileName, "")
		if @error Then
			$WindowHandle = 0
			LogDebug("Failed to get the handle of window with title - "&$fileName)
			$returnCode = 0
		Else
			$returnCode = AlterContextVar($returnContextVar, $WindowHandle)
		EndIf
		
		
	EndIf
	
	LogFuncEnd(@ScriptName&"->_OpenFile")
	Return 0
	
EndFunc
#EndRegion _OpenFile

#Region OpenCommand
Func OpenCommand($method, $HWnd)
	
	LogFuncStart(@ScriptName&"->OpenCommand")
	
	Switch $method
		Case "ContextMenu"
			WinMenuSelectItem($HWnd, "", "&File", "&Open")
			LogDebug("File>Open context menu selected on window " & $HWnd)
		Case "HotKey"
			Send("^o")
			LogDebug("Ctrl-O sent to window " & $HWnd)
		Case Else
			LogFatal("Open file method type " & $method & " is not supported.")
			Exit 1
	EndSwitch
	
	LogFuncEnd(@ScriptName&"->OpenCommand")
	
EndFunc
#EndRegion OpenCommand
