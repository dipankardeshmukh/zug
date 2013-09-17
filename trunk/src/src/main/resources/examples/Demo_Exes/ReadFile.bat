:: path
if %1=="" exit 3        
set filepath=%1
set filepath=%filepath:"=%
set /p file_content=<"%filepath%"

zugutility.exe altercontextvar %2 %file_content%
