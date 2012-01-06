;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
;   Name of Primitive        : CloseFile.exe
;   Purpose                  : Purpose is to close the file that is open in a window.  This may or may not result in the window itself
;                              getting closed.
;   Implemented by           : Gil Hayes
;   Date                     : 11 Feb 2009
;   Parameters Accepted      : WINDOW – Window handle to the window to perform the action against (mandatory)
;                            ; FILE   - The full file path/name of the file that is to be closed (mandatory)
;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#Region Includes
#include "Common.au3"
#EndRegion Includes

#Region Local Defines
#EndRegion Local Defines

#Region Executable Entry
Exit _CloseFile(GetCmdLineParamsValue("WINDOW"), GetCmdLineParamsValue("FILE"))
#EndRegion Executable Entry

#Region _CloseFile
Func _CloseFile($HWnd, $file)
	
	LogFuncStart("CloseFile->_CloseFile")
	
	If Not IsHWnd($HWnd) Then $HWnd = ConvertToHWnd($HWnd)
	
	$filePieces = StringSplit($file, "\")
	$fileName = $filePieces[$filePieces[0]]
	
	; Exit immediately if window does not exist
	If Not WinExists($HWnd) Then
		LogInfo("Window " & $HWnd & " does not exist.")
		LogFuncEnd("CloseFile->_CloseFile")
		Return 0
	EndIf
	
	; Exit immediately if window does not contain the file to close
	If Not StringInStr(WinGetTitle($HWnd), $fileName) Then
		LogInfo("Window " & $HWnd & " does not contains file " & $fileName & ".")
		LogFuncEnd("CloseFile->_CloseFile")
		Return 0
	EndIf
	
	Local $counter=0
	
	; Try up to 3x to close file out of window
	While ($counter < 3 And (StringInStr(WinGetTitle($HWnd), $fileName)))
		LogDebug("Sending keys for closing app, counter = " & $counter)
		ActivateWindow($HWnd, 30)
		Send("{Alt}")
		Sleep(500)
		Send("f")
		Send("c")
		LogDebug("Alt F C sent to window " & $HWnd)
		
		; Deal with "do you want to save..." dialog if it appears
		WinWait("[CLASS:#32770]", "save",$WindowTO)
		;To make case insensitive comparison of "save"/"Save"
		If (WinExists("[CLASS:#32770]", "save") Or WinExists("[CLASS:#32770]", "Save"))Then
			LogDebug("Focus is on Do You want save message box.")
			;To make case insensitive comparison of "save"/"Save"
			WinActivate("[CLASS:#32770]", "save")				
			WinActivate("[CLASS:#32770]", "Save")
			Send("n")
		EndIf
		
		; Determine if file is still open in the window
		Sleep(10000)
		If WinExists($HWnd) And StringInStr(WinGetTitle($HWnd), $fileName) Then
			; If so increment counter and retry the loop
			LogDebug("File " & $fileName & " still is open inside window " & $HWnd & ", resending close command....")
			$counter+=1
			ContinueLoop
		Else
			; If not exit with pass condition
			LogSuccess("Window " & $HWnd & " not longer exists.")
			LogFuncEnd("CloseFile->_CloseFile")
			Return 0
		EndIf
		
	WEnd
	
	; If we got here, counter was incremented past the acceptable retry amount
	; Check the window (if it still exists) and pass/fail depending if i still contains the file in question
	If WinExists($HWnd) Then
		LogDebug("Window " & $HWnd & " no longer exists.")
		LogFuncEnd("CloseFile->_CloseFile")
		Return 0
	ElseIf Not (StringInStr(WinGetTitle($HWnd), $fileName)) Then
		LogSuccess("Window " & $HWnd & " does not contains file " & $fileName & ".")
		LogFuncEnd("CloseFile->_CloseFile")
		Return 0
	Else
		LogFatal("Window " & $HWnd & " still exists and contains file " & $fileName & ".")
		LogFuncEnd("CloseFile->_CloseFile")
		Return 1
	EndIf
	
EndFunc
#EndRegion CloseFile
