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
Exit _ChangeBIPState(GetCmdLineParamsValue("FILEEXTENSION"), GetCmdLineParamsValue("ENABLE"))
#EndRegion Executable Entry

#Region _ChangeBIPState
Func _ChangeBIPState($FileExtension, $ENABLE)
	
	LogFuncStart("ChangeBIPState->_ChangeBIPState")
	$BIPstate= "Check"
	Local $ReturnCode=1
	
	if (StringCompare($ENABLE,"false",0)==0) Then $BIPstate = "Uncheck"
	
	LogDebug("Sending key Windows+e to open the explorer")
	Send("#e")
	
	WinWait("My Computer", "", $WindowTO)
		
	If Not WinActive("My Computer","") Then WinActivate("My Computer","")
	
	$ExplorerHandle = WinGetHandle("My Computer","")
	
	if @error Then
		
		DumpWindowsToDebugLog()
		LogDebug("Unable to get the handle for wit window name 'My Computer'")
		LogError("Unable to get the handle for wit window name 'My Computer'")
		LogFuncEnd("ChangeBIPState->_ChangeBIPState")
		return 1
		
	EndIf
	
	LogDebug("Explorer window Handle is - "&$ExplorerHandle)
	
	; Activate Droplet
	If Not ActivateWindow($ExplorerHandle, $WindowTO) Then
		
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
		
	If Not ActivateWindow($HWndFolderOpt, $WindowTO) Then
		
		WinClose($ExplorerHandle)
		LogFuncEnd(@ScriptName&"->_AssignExpiredLMPolicy")
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
		Sleep(3000)
		
		if ControlCommand($HWndFolderOpt,"",$AdvanceButtonClass,"IsEnabled","") Then
			
			$ButtonEnabled = True
			ExitLoop
			
		EndIf
		
		$Count = $Count + 1
		
	WEnd
			
	If $ButtonEnabled == True Then
				
		$ItemCount = ControlListView($HWndFolderOpt,"","[CLASS:SysListView32;INSTANCE:1]","FindItem",$FileExtension)
		
		if $ItemCount Then
			ControlListView($HWndFolderOpt,"","[CLASS:SysListView32;INSTANCE:1]", "Select",$ItemCount, $ItemCount)
			
			if ControlListView($HWndFolderOpt,"","[CLASS:SysListView32;INSTANCE:1]", "IsSelected",$ItemCount) Then
				
				LogDebug("Sending button click to Advance Button")
				ControlClick($HWndFolderOpt,"",$AdvanceButtonClass)
				sleep(100)						
				
				$HWndPropDlg = WinGetHandle("[TITLE:Edit File Type")
				
				if not WinActive($HWndPropDlg) then WinActivate($HWndPropDlg)
				
				if WinWaitActive($HWndPropDlg,"",500) Then
					$BIPCheckBoxConfirm = ControlGetHandle($HWndPropDlg, "", "[CLASS:Button;Text: Confirm &open after download]")
					ControlCommand($HWndPropDlg,"",$BIPCheckBoxConfirm, "Uncheck")
										
					$BIPCheckBox = ControlGetHandle($HWndPropDlg, "", "[CLASS:Button;Text: &Browse in same window]")
					ControlCommand($HWndPropDlg,"",$BIPCheckBox,$BIPstate)
					
					sleep(100)					
					Send("{ENTER}")
					
					;Close all the windows
					sleep(100)			
					WinActivate($HWndFolderOpt)
					if WinWaitActive($HWndFolderOpt,"",10) Then Send("!{F4}")
					
					sleep(100)					
					WinActivate($ExplorerHandle)
					if WinWaitActive($ExplorerHandle,"",10) Then Send("!{F4}")
					
					return 0
				EndIf
				
			Else
				
				LogError("FAILED to select the Given File Type")
				;"Folder Options","","[CLASS:SysListView32;INSTANCE:1]", "IsSelected",$ItemCount
				WinClose($HWndFolderOpt)
				WinClose($ExplorerHandle)
				LogFuncEnd("ChangeBIPState->_ChangeBIPState")
				return 1
				
			EndIf
		Else
				LogError("File type Not defined...")
				WinClose($HWndFolderOpt)
				WinClose($ExplorerHandle)
				LogFuncEnd("ChangeBIPState->_ChangeBIPState")
				return 1
		EndIf
		
	Else
		
		LogError("File types are not populated!!! Time taken is more than expected.")
		WinClose("Folder Options")
		WinClose($HWndFolderOpt)
		WinClose($ExplorerHandle)
		LogFuncEnd("ChangeBIPState->_ChangeBIPState")
		Return 1
	EndIf
		
	LogFuncEnd("ChangeBIPState->_ChangeBIPState")
	Return $ReturnCode
	
EndFunc
#EndRegion _ChangeBIPState

