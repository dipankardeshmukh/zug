#Region Includes
#include "Common.au3"
#EndRegion Includes

#Region Local Defines
#EndRegion Local Defines

#Region Executable Entry
$exitCode = 1
$ProcessCloseWaitTo = 60

Switch GetCmdLineParamsValue("APP")
	Case "Word"
		$exitCode = _ExitApp("winword.exe")
	Case "Excel"
		$exitCode = _ExitApp("excel.exe")
	Case "PowerPoint"
		$exitCode = _ExitApp("powerpnt.exe")
	Case "Visio"
		$exitCode = _ExitApp("visio.exe")
	Case "Adobe"
		$exitCode = _ExitApp("acrobat.exe")
	Case "Reader"
		$exitCode = _ExitApp("acrord32.exe")
	Case "LMViewer"
		$exitCode = _ExitApp("lmviewer.exe")
	Case "IEXPLORE"
		$exitCode = _ExitApp("IExplore.exe")	
	Case "ImageWriter"
		$exitCode = _ExitApp("MSPVIEW.EXE")	
	Case Else
		LogError("Application " & GetCmdLineParamsValue("APP") & " not yet supported.")
		$exitCode = $g_XCodeIncorrectArgs
EndSwitch

Exit $exitCode
#EndRegion Executable Entry

#Region _ExitApp
Func _ExitApp($exeName)
	
	LogFuncStart("ExitApp->_ExitApp")
	Local $returnValue = 1
	
	$aProcessWindows = GetProcessWindows($exeName)
	
	For $i = 1 To $aProcessWindows[0]
		If WinExists(HWnd($aProcessWindows[$i])) Then
			WinActivate(HWnd($aProcessWindows[$i]))
			
			if WinWaitActive(HWnd($aProcessWindows[$i]),"",35) Then
				
				Send("{ALT}")
				Send("f")
				Send("x")
				LogDebug("Alt-F-X sent to window with HWnd " & $aProcessWindows[$i])
				
			EndIf
			Sleep(500)
		EndIf
	Next
	
	Sleep(30000)

	If ProcessExists($exeName) Then 
		LogDebug("Process is still running!!!Closing the process")
		ProcessClose($exeName)
		LogDebug("Waiting for Process to exit")
		
		if NOT ProcessWaitClose($exeName,$ProcessCloseWaitTo) Then
		
		LogDebug("Process close wait time out occured")
		
		EndIf
		LogDebug("Process " & $exeName & " was closed.")
	EndIf
	
	
	
	If ProcessExists($exeName) Then
		LogDebug("Process " & $exeName & " still exists after attempts at closing it were called.")
		$returnValue = 0
	Else
		LogDebug("Process " & $exeName & " was closed.")
		$returnValue = 0
	EndIf
	
	LogFuncEnd("ExitApp->_ExitApp")
	Return $returnValue
	
EndFunc