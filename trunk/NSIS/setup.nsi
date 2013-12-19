; Script generated by the HM NIS Edit Script Wizard.

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "Automature-ZUG"
!define PRODUCT_PUBLISHER "Automature, Inc."
!define PRODUCT_WEB_SITE "http://www.automature.com"
!define PRODUCT_DIR_REGKEY "Software\Microsoft\Windows\CurrentVersion\App Paths\${PRODUCT_NAME}"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"

SetCompressor lzma

XPStyle on

var /global IsSilent
var /global Update
var /global JavaSOURCE
var /global JavaBROWSESOURCE
var /global JavaSOURCETEXT
var /global ZipName

Var Java_SOURCE_TEXT
var dialog
Var Hostname
Var DBName
Var DBUser
Var DBPassword
Var SOURCE
Var SOURCETEXT
Var BROWSESOURCE


Var Content_BROWSESOURCE
Var Content_Hostname
Var Content_DBName
Var Content_DBUser
Var Content_DBPassword
Var TotalLength 
Var CompletepathLength
Var InstallDirLength 

Var NoDotNet


; MUI 1.67 compatible ------
!include "MUI.nsh"
!include nsDialogs.nsh
!include "ZipDLL.nsh"
!include "functions.nsi"
!include EnvVarUpdate.nsh
!include x64.nsh
!define /date NOW "%H:%M:%S %d %b, %Y"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "Zug.ico"
!define MUI_UNICON "Zug.ico"

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE "ZugEULA.txt"
; Directory page
;Java Directory Page
Page custom JavaPage JavaPageLeave
; External atom location
Page custom AtomPage AtomPageLeave 
; Framework configuration 
Page custom CredentialPage CredentialPageLeave 
!define MUI_PAGE_CUSTOMFUNCTION_PRE CheckInstDirReg
!insertmacro MUI_PAGE_DIRECTORY
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; Reserve files
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS

; MUI end ------

Name "${PRODUCT_NAME} ${Zug_Version}"
OutFile "ZugSetup.exe"
InstallDir "$PROGRAMFILES\Automature"
InstallDirRegKey HKLM "${PRODUCT_DIR_REGKEY}" ""
ShowInstDetails show
ShowUnInstDetails show


Function CheckInstDirReg

${If} $Update == "TRUE"
    Abort
${EndIf}

FunctionEnd


Function .onInit

  ReadRegStr $R0 HKLM "Software\Microsoft\Windows\CurrentVersion\App Paths\Automature-ZUG" "Zugpath"
  
  IfSilent 0 notSilent
      CreateDirectory '$APPDATA\ZUG Logs'
      ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `${__DATE__}  ${__TIME__} ::  ${PRODUCT_NAME} - ${Zug_Version} $\r$\n` 
  notSilent:
  
  ${If} ${RunningX64}
    IfFileExists $windir\Microsoft.NET\Framework64\v4.0.30319\RegAsm.exe Continue 0
       StrCpy $NoDotNet "TRUE"
  ${Else}
    IfFileExists $windir\Microsoft.NET\Framework\v4.0.30319\RegAsm.exe Continue 0
      StrCpy $NoDotNet "TRUE"
  ${EndIf}
  
  StrCmp $NoDotNet "TRUE" 0 Continue
    IfSilent 0 Log1
      ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `You do not have DotNet Framework v4.0 or higher.Please install and then run ZugSetup.$\r$\n Download Location : http://www.microsoft.com/en-in/download/confirmation.aspx?id=17718 `
      Goto exit1
    Log1:
      MessageBox MB_OK "You do not have DotNet Framework v4.0 or higher.Please install and then run ZugSetup."
    exit1:
    quit
  Continue:
  
       IfFileExists "$R0\ZUG\runzug.bat"  0 jumpdeletebatch
     ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `Please do not use /S option for first time installation.$\r$\n`
                  MessageBox MB_OK "An existing version of ZUG is detected! Please uninstall this version and reinstall the latest version….."
                  Abort
      jumpdeletebatch:
      
      IfFileExists "$R0\ZUG\zug.jar"  0 jumpdeletejar
      ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `An existing version of ZUG is detected! Please uninstall this version and reinstall the latest version…...$\r$\n`
                  MessageBox MB_OK "An existing version of ZUG is detected! Please uninstall this version and reinstall the latest version….."
                  Abort
      jumpdeletejar:
  
    IfFileExists $R0\ZUG\automature-zug.jar 0 FirstTimeInstall
     StrCpy $INSTDIR "$R0"
     StrCpy $Update "TRUE"
      
     IfSilent J1 0 
        MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Upgrading ZUG, keeping your previous settings. Do you want to continue?" IDYES +2
        quit
     Goto J1 
  FirstTimeInstall: 
       
        ReadRegStr $R1 HKLM "Software\JavaSoft\Java Runtime Environment" "CurrentVersion"
        ReadRegStr $R2 HKLM "Software\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  
    J1:
      IfSilent 0 Jend
        SetSilent silent     
        StrCmp $Update "TRUE" 0 Jerror
            StrCpy $IsSilent "TRUE"
            Goto Jend
        Jerror:
            ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `Please do not use /S option for first time installation.$\r$\n`
            MessageBox MB_OK "Please do not use /S option for first time installation."
            quit
        
  Jend:
FunctionEnd





Function JavaPage

  ${If} $Update == "TRUE"
    Abort
  ${EndIf}
  
  !insertmacro MUI_HEADER_TEXT "Java Install Directory" "Choose Install Location of Java in your machine."
	#Create Dialog and quit if error
	nsDialogs::Create 1018
	Pop $Dialog
	${If} $Dialog == error
		Abort
	${EndIf}
	${NSD_CreateLabel} 0 10 100% 12u "Java Runtime Environment Path:*"
	${NSD_CreateText} 0 30 70% 12u "$R2"
	pop $JavaSOURCETEXT
	${NSD_CreateBrowseButton} 320 30 20% 12u "Browse"
	pop $JavaBROWSESOURCE
  ${NSD_CreateLabel} 0 140 100% 18u "* If you do not have Java installed in your machine, then please install JRE 1.6 or above first and run the installer again."
	${NSD_OnClick} $JavaBROWSESOURCE JavaBrowsesource
  nsDialogs::Show
FunctionEnd


Function JavaBrowsesource
nsDialogs::SelectFolderDialog "Select Source Folder" "c:\"
pop $JavaSOURCE
${If} $JavaSOURCE == 'error'
   StrCpy $JavaSOURCE ""
${EndIf}
${NSD_SetText} $JavaSOURCETEXT $JavaSOURCE
FunctionEnd

Function JavaPageLeave
${NSD_GetText} $JavaSOURCETEXT $Java_SOURCE_TEXT
${If} $Java_SOURCE_TEXT == ""
MessageBox MB_OK "Java installation directory missing. Please choose proper installation directory."
abort
${EndIf}
IfFileExists $Java_SOURCE_TEXT\bin\java.exe +3 0
MessageBox MB_OK "Wrong Java installation directory. Please choose proper installation directory."
abort
FunctionEnd

Function AtomPage
   
   ${If} $Update == "TRUE"
    Abort
  ${EndIf}
  
  !insertmacro MUI_HEADER_TEXT "Atom Path Settings" "Select out of process atom path location"
	#Create Dialog and quit if error
	nsDialogs::Create 1018
	Pop $Dialog
	${If} $Dialog == error
		Abort
	${EndIf}
  
  ${NSD_CreateLabel} 10 10u 100% 10u "Atom Path Location (Folders where your out of process atoms can be found)"  
  ${NSD_CreateText} 10 20u 75% 10u ""
  Pop $SOURCETEXT  
  ${NSD_CreateBrowseButton} 350 20u 20% 12u "Browse"
	pop $BROWSESOURCE
	${NSD_OnClick} $BROWSESOURCE ExternalAtomPageBrowsesource
  nsDialogs::Show
  
FunctionEnd           

Function ExternalAtomPageBrowsesource
  nsDialogs::SelectFolderDialog "Select Source Folder" "c:\"
  pop $SOURCE
  ${If} $SOURCE == 'error'
     StrCpy $SOURCE ""
  ${EndIf}
  ${NSD_SetText} $SOURCETEXT $SOURCE
FunctionEnd

Function AtomPageLeave

  ${NSD_GetText} $SOURCETEXT $Content_BROWSESOURCE

FunctionEnd


Function CredentialPage

  ${If} $Update == "TRUE"
    Abort
  ${EndIf}
  
  !insertmacro MUI_HEADER_TEXT "Davos Config Settings" "Set Test Management Framework configuration settings. You can skip this, if you do not want reporting of your test results."
	#Create Dialog and quit if error
	nsDialogs::Create 1018
	Pop $Dialog
	${If} $Dialog == error
		Abort
	${EndIf}


  ${NSD_CreateLabel} 10 35u 100% 10u "Davos Host Name(e.g. http://192.168.0.45:4567)" 
  ${NSD_CreateText} 10 45u 60% 10u ""
   Pop $Hostname
  ${NSD_CreateLabel} 10 60u 100% 10u "Database Name" 
  ${NSD_CreateText} 10 70u 40% 10u "Framework"
   Pop $DBName
  ${NSD_CreateLabel} 10 85u 100% 10u "Davos User Name" 
  ${NSD_CreateText} 10 95u 40% 10u "davosuser"
   Pop $DBUser
  ${NSD_CreateLabel} 10 110u 100% 10u "Davos Password" 
  ${NSD_CreatePassword} 10 120u 40% 10u "user"
   Pop $DBPassword
  nsDialogs::Show
FunctionEnd

Function CredentialPageLeave
${NSD_GetText} $Hostname   $Content_Hostname
${NSD_GetText} $DBName $Content_DBName
${NSD_GetText} $DBUser $Content_DBUser
${NSD_GetText} $DBPassword $Content_DBPassword
FunctionEnd


Section "MainSection" SEC01
  Delete "$TEMP\automature-zug-bin-*.zip"
  SetOutPath "$TEMP"
  SetOverwrite on
  File "automature-zug-bin-${Zug_Version}.zip"
  StrCpy $ZipName "$TEMP\automature-zug-bin-${Zug_Version}.zip"
 ;=================================  UPDATE ========================================================= 
  ${If} $Update == "TRUE"
  
    IfFileExists $INSTDIR\ZUG\ZugINI.xml backup donot_backup
    backup: 
      ${If} $IsSilent == "TRUE"
        ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `Backing up existing ZugINI.xml file.$\r$\n`
      ${EndIf}
      Delete "$INSTDIR\ZUG\ZugINI.xml.bak"
      Rename "$INSTDIR\ZUG\ZugINI.xml" "$INSTDIR\ZUG\ZugINI.xml.temp"
    donot_backup:  
      ${If} $IsSilent == "TRUE"
        ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `Extracting *.zip file in $INSTDIR .$\r$\n`
      ${EndIf}
      
      ;IfFileExists "$TEMP\automature-zug-bin-*.zip" 0 continue1
          ;StrCpy $ZipName "$TEMP\automature-zug-bin-*.zip"
        !insertmacro ZIPDLL_EXTRACT "$ZipName" "$INSTDIR" "<ALL>"
      ;continue1:
       
      IfFileExists $INSTDIR\ZUG\ZugINI.xml.temp interchange donot_interchange
    interchange: 
      Rename "$INSTDIR\ZUG\ZugINI.xml" "$INSTDIR\ZUG\ZugINI.xml.bak"
      Rename "$INSTDIR\ZUG\ZugINI.xml.temp" "$INSTDIR\ZUG\ZugINI.xml"  
    donot_interchange:  
      ExecWait "$INSTDIR\ZUG\Setup.cmd"
      AccessControl::GrantOnFile \
      "$INSTDIR\ZUG" "(BU)" "GenericRead + GenericWrite"
     ${WriteToFile} `$APPDATA\ZUG Logs\install_log.txt` `${__DATE__} ::  ${PRODUCT_NAME} - ${Zug_Version} installed successfully. $\r$\n` 
     
     
;xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx UPDATE  XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX     
  ${Else}
 
      
      
      ;IfFileExists "$TEMP\automature-zug-bin-${Zug_Version}.zip" 0 continue2
          ;StrCpy $ZipName $TEMP\automature-zug-bin-*.zip
          ;MessageBox MB_OK "($ZipName)"
          !insertmacro ZIPDLL_EXTRACT "$ZipName" "$INSTDIR" "<ALL>"
      ;continue2:
      
      AccessControl::GrantOnFile \
      "$INSTDIR\ZUG" "(BU)" "GenericRead + GenericWrite"
      
      ;CopyFiles `$Java_SOURCE_TEXT\bin\java.exe` `$INSTDIR\ZUG\ZUG.exe`
      CopyFiles `$INSTDIR\ZUG\lib\sqlitejdbc-v056.jar` `$Java_SOURCE_TEXT\lib\ext\`
      
      ${If} ${RunningX64}
      CopyFiles `$INSTDIR\ZUG\SDK\System.Data.Sqlite.Net4.0\x86\System.Data.SQLite.DLL` `$INSTDIR\ZUG\System.Data.SQLite.DLL`
      ${Else}
      CopyFiles `$INSTDIR\ZUG\SDK\System.Data.Sqlite.Net4.0\x64\System.Data.SQLite.DLL` `$INSTDIR\ZUG\System.Data.SQLite.DLL`
      ${EndIf}
      
      ExecWait "$INSTDIR\ZUG\Setup.cmd"
      
      nsExec::ExecToStack '"$INSTDIR\ZUG\zug.bat"'
      
  
  ${EndIf}
  
     Delete "$TEMP\automature-zug-bin-*.zip"
 ; ================================ Add new XML tags ================================================
        
      
  nsisXML::create
	nsisXML::load "$INSTDIR\ZUG\ZugINI.xml"
  nsisXML::select '/root/configurations/scriptlocation'
	  IntCmp $2 0 0 SkipSLoc SkipSLoc 
    nsisXML::select '/root/configurations'
    nsisXML::createElement "scriptlocation"
    nsisXML::appendChild
    SkipSLoc:
    nsisXML::getText
    StrCmp $3 "" 0 SkipSValLoc
      nsisXML::setText $Content_BROWSESOURCE
    SkipSValLoc:
  
  nsisXML::select '/root/configurations/dbhostname'
   	IntCmp $2 0 0 SkipHName SkipHName 
    nsisXML::select '/root/configurations'
    nsisXML::createElement "dbhostname"
    nsisXML::appendChild  
  SkipHName:
    nsisXML::getText
    StrCmp $3 "" 0 SkipHNameVal
      nsisXML::setText $Content_Hostname 
    SkipHNameVal:
    
  nsisXML::select '/root/configurations/dbname'
   	IntCmp $2 0 0 SkipDbName SkipDbName 
    nsisXML::select '/root/configurations'
    nsisXML::createElement "dbname"
    nsisXML::appendChild  
    SkipDbName:
    nsisXML::getText
    StrCmp $3 "" 0 SkipDbNameVal
      nsisXML::setText $Content_DBName
  SkipDbNameVal:
  
  nsisXML::select '/root/configurations/dbusername'
   	IntCmp $2 0 0 SkipDbUName SkipDbUName 
    nsisXML::select '/root/configurations'
    nsisXML::createElement "dbusername"
    nsisXML::appendChild 
    SkipDbUName:
    nsisXML::getText
    StrCmp $3 "" 0 SkipDbUNameVal 
      nsisXML::setText $Content_DBUser
  SkipDbUNameVal:
  
  
  nsisXML::select '/root/configurations/dbuserpassword'
   	IntCmp $2 0 0 SkipDbPassword SkipDbPassword 
    nsisXML::select '/root/configurations'
    nsisXML::createElement "dbuserpassword"
    nsisXML::appendChild     
    SkipDbPassword:
    nsisXML::getText
    StrCmp $3 "" 0 SkipDbPasswordVal 
      nsisXML::setText $Content_DBPassword
  SkipDbPasswordVal:
  
  nsisXML::save "$INSTDIR\ZUG\ZugINI.xml"
      
 ; xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx Add new XML tags xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx 
SectionEnd

Section "WriteEnvironment" SEC07

  ReadEnvStr $0 "PATH"
  StrLen $CompletepathLength $0
  StrLen $InstallDirLength $INSTDIR\ZUG
  IntOp $TotalLength $CompletepathLength + $InstallDirLength 
  ${If} $ == ""
   ${OrIf} $TotalLength >= ${NSIS_MAX_STRLEN}   
      MessageBox MB_OK "Current environment variable PATH length ($TotalLength) too long to modify by Installer.Please set manually."
   ${Else} 
      ReadRegStr $R0 HKLM "Software\Microsoft\Windows\CurrentVersion\App Paths\${PRODUCT_NAME}" "Zugpath"
      ${EnvVarUpdate} $0 "PATH" "R" "HKLM" "$R0\ZUG"
      ${EnvVarUpdate} $0 "PATH" "A" "HKLM" "$INSTDIR\ZUG" 
   ${EndIf}  
SectionEnd


Section -Post 
  SetShellVarContext all
  WriteUninstaller "$INSTDIR\ZUG\uninstZUG.exe"
  WriteRegStr HKLM "${PRODUCT_DIR_REGKEY}" "Zugpath" "$INSTDIR"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\ZUG\uninstZUG.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${Zug_Version}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
  createDirectory "$SMPROGRAMS\Automature\ZUG"
  CreateShortCut "$SMPROGRAMS\Automature\ZUG\uninstall.lnk" "$INSTDIR\ZUG\uninstZUG.exe" "" "$INSTDIR\ZUG\Images\Zug.ico"
  CreateShortCut "$SMPROGRAMS\Automature\ZUG\ZUG.lnk" "$INSTDIR\ZUG\zug.bat" "-gui" "$INSTDIR\ZUG\Images\Zug.ico" 0 SW_SHOWMINIMIZED
  CreateShortCut "$DESKTOP\ZUG.lnk" "$INSTDIR\ZUG\zug.bat" "-gui" "$INSTDIR\ZUG\Images\Zug.ico" 0 SW_SHOWMINIMIZED
SectionEnd


Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) was successfully removed from your computer."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure you want to completely remove $(^Name) and all of its components?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  SetShellVarContext all
  RMDir "$SMPROGRAMS\ZUG"
  RMDir "$INSTDIR\ZUG"
  Delete "$INSTDIR\README.txt"
  SetOutPath "$TEMP"
  SetOverwrite ifnewer
  Delete "$SMPROGRAMS\Automature\ZUG\*.*"
  RMDir "$SMPROGRAMS\Automature\ZUG"
  File "DeleteZug.cmd"
  ReadRegStr $R0 HKLM "Software\Microsoft\Windows\CurrentVersion\App Paths\Automature-ZUG" "Zugpath"
 IfFileExists $R0\ZUG\automature-zug.jar 0 +3
  Execwait '"$TEMP\DeleteZug.cmd" "$R0\ZUG"'
  Goto +2
    MessageBox MB_OK "Cannot find Zug path in the registry. Please delete the folder ZUG manually"
   ${un.EnvVarUpdate} $0 "PATH" "R" "HKLM" "$R0\ZUG"
  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  DeleteRegKey HKLM "${PRODUCT_DIR_REGKEY}"
  Delete "$R0\*.*"
  Delete "$INSTDIR\uninstZUG.exe"
  SetAutoClose true
SectionEnd