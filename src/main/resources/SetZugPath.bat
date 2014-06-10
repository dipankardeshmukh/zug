IF EXIST "C:\Program Files (x86)\Automature\ZUG" (set ZugPath="C:\Program Files (x86)\Automature\ZUG") ELSE (set ZugPath="C:\Program Files\Automature\ZUG")
set ZugPath=%ZugPath:"=%
echo.%PATH%|findstr /C:%ZugPath% >nul 2>&1 && echo "Path already set" || setx -m path "%PATH%;%ZugPath%"

IF EXIST "C:\Program Files (x86)\Automature\ZUG" (ren "C:\Program Files (x86)\Automature\ZUG\ZugINI.xml.Windows" "ZugINI.xml") ELSE (ren "C:\Program Files\Automature\ZUG\ZugINI.xml.Windows" "ZugINI.xml")

IF EXIST "C:\Program Files (x86)\Automature\ZUG" (DEL "C:\Program Files (x86)\Automature\ZUG\ZugINI.xml.Linux" /s /f /q ) ELSE (DEL "C:\Program Files\Automature\ZUG\ZugINI.xml.Linux" /s /f /q )

IF EXIST "C:\Program Files (x86)\Automature\ZUG" (DEL "C:\Program Files (x86)\Automature\ZUG\ZugINI.xml.Mac" /s /f /q ) ELSE (DEL "C:\Program Files\Automature\ZUG\ZugINI.xml.Mac" /s /f /q )




