IF EXIST "C:\Program Files (x86)\Automature\ZUG" (set ZugPath="C:\Program Files (x86)\Automature\ZUG") ELSE (set ZugPath="C:\Program Files\Automature\ZUG")
set ZugPath=%ZugPath:"=%
echo.%PATH%|findstr /C:%ZugPath% >nul 2>&1 && echo "Path already set" || setx -m path "%PATH%;%ZugPath%"

