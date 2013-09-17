;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
;   Name of Primitive        : ChangeBIPState.exe
;   Purpose                  : Purpose is to change the BIPstate for specified file type(extension)
;   Implemented by           : Vinit Meta.
;   Date                     : 13 Jan 2008
;   Parameters Accepted      : FILEEXTENSION – File extension for which BIP state is to change.  (mandatory)
;                              ENABLE - To enable or disable the state. True=Enable , False=Disable (mandatory)
;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


#Region Includes
#include "Common.au3"
#Include <GuiTab.au3>
#EndRegion Includes

#Region Local Defines
#EndRegion Local Defines

#Region Executable Entry
Exit _VerifyBIPState(GetCmdLineParamsValue("FILEEXTENSION"), GetCmdLineParamsValue("EXPECTED"))
#EndRegion Executable Entry

#Region _VerifyBIPState
Func _VerifyBIPState($FileExtension, $Expected)
	
	LogFuncStart("ChangeBIPState->_ChangeBIPState")
	Local $ReturnCode=1
	
	if Not StringCompare($Expected,"true") Then
		$state=1
	Else
		$state=0
	EndIf
	
	LogDebug("Sending key Windows+e to open the explorer")
	Send("#e")
	
	WinWait("My Computer", "", $WindowTO)
		
	If Not WinActive("My Computer","") Then WinActivate("My Computer","")
	
	$ExplorerHandle = WinGetHandle("My Computer","")
	
	if @error Then
		
		DumpWindowsToDebugLog()
		LogDebug("Unable to get the handle for wit window name 'My Computer'")
		LogError("Unable to get the handle for wit window name 'My Computer'")
		LogFuncEnd(@ScriptName&"->_VerifyBIPState")
		return 1
		
	EndIf
	
	LogDebug("Explorer window Handle is - "&$ExplorerHandle)
	
	; Activate Droplet
	If Not ActivateWindow($ExplorerHandle, $ActivateWindowTO) Then
		
		LogFuncEnd(@ScriptName&"->_AssignExpiredLMPolicy")
		Return 1
		
	EndIf
		
	LogDebug("Sending Al+t to open the tool menu")
	Send("{ALT DOWN}")
	Send("t")
	Send("{ALT UP}")
	Sleep(100)
		
	;Open Folder Options
	LogDebug("Opening Folder Option Dialogue")
	Send("{O}")
	
	If Not WinActive("Folder Options","") Then WinActivate("Folder Options","")
	
	$HWndFolderOpt = WinGetHandle("Folder Options","")
		
	If Not ActivateWindow($HWndFolderOpt, $ActivateWindowTO) Then
		
		WinClose($ExplorerHandle)
		LogFuncEnd(@ScriptName&"->_VerifyBIPState")
		Return 1
		
	EndIf
	
	
	LogDebug("Folder Options window Handle is - " &$HWndFolderOpt)
	
	Local $ControlHandle = ControlGetHandle($HWndFolderOpt, "", "[CLASS:SysTabControl32;CLASSNN:SysTabControl321]")
	
	if @error Then
		LogDebug("Failed to get the windows tab control handle...Selecting the required Tab by sending Key Sequence.")
		LogDebug("Sending Key Sequence Shift + Tab,Shift + Tab, Right Arrow Key,Right Arrow Key")
				
		Sleep(100)
		Send("+{TAB}")
	
		Sleep(100)
		Send("{RIGHT 2}")
		
	Else
		
		LogDebug("Success - Getting the handle of Tab control...Selecting the required Tab through AutoIt Function.")
		$TabIndex = _GUICtrlTab_FindTab($ControlHandle, "File Types")
		if $TabIndex == -1 Then
			LogDebug("Failed to select the given tab!!!! Sending Key sequence ")
			Send("+{TAB}")
	
			Sleep(100)
			Send("{RIGHT 2}")
		
		Else
			
			_GUICtrlTab_SetCurFocus($ControlHandle, $TabIndex)
			
		EndIf	
		
	EndIf
	
	
	
	$AdvanceButtonClass = "[CLASS:Button;Text:Ad&vanced]"
	;if @OSVersion == "WIN_XP" Then $AdvanceButtonClass = "[CLASS:Button;Text:Advanced]"

	Local $Count=1
	Local $ButtonEnabled = False
	
	while $Count <=5
		Sleep(1000)
		
		if ControlCommand($HWndFolderOpt,"",$AdvanceButtonClass,"IsEnabled","") Then
			
			$ButtonEnabled = True
			ExitLoop
			
		EndIf
		
		$Count = $Count + 1
		
	WEnd
			
	If $ButtonEnabled == True Then
				
		$ItemCount = ControlListView($HWndFolderOpt,"","[CLASS:SysListView32;INSTANCE:1]","FindItem",$FileExtension)
		LogDebug("Index of "&$FileExtension &" is "&$ItemCount &" in system List View.")
		if $ItemCount Then
			LogDebug("Selecting the index "&$ItemCount)
			ControlListView($HWndFolderOpt,"","[CLASS:SysListView32;INSTANCE:1]", "Select",$ItemCount, $ItemCount)
			Sleep(1000)
			
			if ControlListView($HWndFolderOpt,"","[CLASS:SysListView32;INSTANCE:1]", "IsSelected",$ItemCount) Then
				
				LogDebug("Sending button click to Advance Button")
				
				if not ControlClick($HWndFolderOpt,"",$AdvanceButtonClass) Then
					LogError("Failed to click the Advanve button")
					WinActivate($HWndFolderOpt)
					if WinWaitActive($HWndFolderOpt,"",$ActivateWindowTO) Then Send("!{F4}")
					
					sleep(100)					
					WinActivate($ExplorerHandle)
					if WinWaitActive($ExplorerHandle,"",$ActivateWindowTO) Then Send("!{F4}")
					LogFuncEnd(@ScriptName&"->_VerifyBIPState")
					return 1
				EndIf
				LogDebug("Button click send successfully!!!")
				sleep(100)						
				
				$HWndPropDlg = WinGetHandle("[TITLE:Edit File Type")
				
				if not WinActive($HWndPropDlg) then WinActivate($HWndPropDlg)
				
				if WinWaitActive($HWndPropDlg,"",$ActivateWindowTO) Then
															
					$ButtonID = ControlGetHandle($HWndPropDlg, "", "[CLASS:Button;Text: &Browse in same window]")
					if($state=ControlCommand($HWndPropDlg, "",$ButtonID,"IsChecked")) Then
						LogDebug("BIP state is as per Expectitation!!!")
						$returnCode=0
					Else
						LogDebug("BIP state is NOT as per Expectitation!!!")
						$returnCode=1
					EndIf
					
					
					sleep(100)					
					Send("{ENTER}")
					
					;Close all the windows
					sleep(100)			
					WinActivate($HWndFolderOpt)
					if WinWaitActive($HWndFolderOpt,"",$ActivateWindowTO) Then Send("!{F4}")
					
					sleep(100)					
					WinActivate($ExplorerHandle)
					if WinWaitActive($ExplorerHandle,"",$ActivateWindowTO) Then Send("!{F4}")
					
					return $returnCode
				EndIf
				
			Else
				
				LogError("FAILED to select the Given File Type")
				;"Folder Options","","[CLASS:SysListView32;INSTANCE:1]", "IsSelected",$ItemCount
				WinClose($HWndFolderOpt)
				WinClose($ExplorerHandle)
				LogFuncEnd(@ScriptName&"->_VerifyBIPState")
				return 1
				
			EndIf
		Else
				LogError("File type Not defined...")
				WinClose($HWndFolderOpt)
				WinClose($ExplorerHandle)
				LogFuncEnd(@ScriptName&"->_VerifyBIPState")
				return 1
		EndIf
		
	Else
		
		LogError("File types are not populated!!! Time taken is more than expected.")
		WinClose("Folder Options")
		WinClose($HWndFolderOpt)
		WinClose($ExplorerHandle)
		LogFuncEnd(@ScriptName&"->_VerifyBIPState")
		Return 1
	EndIf
		
	LogFuncEnd(@ScriptName&"->_VerifyBIPState")
	Return $ReturnCode
	
EndFunc
#EndRegion _VerifyBIPState

