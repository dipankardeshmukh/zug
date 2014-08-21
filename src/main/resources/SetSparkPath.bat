IF EXIST "C:\Program Files (x86)\Automature\SPARK" (set SparkPath="C:\Program Files (x86)\Automature\SPARK") ELSE (set SparkPath="C:\Program Files\Automature\SPARK")
set SparkPath=%SparkPath:"=%
echo.%PATH%|findstr /C:%SparkPath% >nul 2>&1 && echo "Path already set" || setx -m path "%PATH%;%SparkPath%"

IF EXIST "C:\Program Files (x86)\Automature\SPARK" (ren "C:\Program Files (x86)\Automature\SPARK\Spark.ini.Windows" "Spark.ini") ELSE (ren "C:\Program Files\Automature\SPARK\Spark.ini.Windows" "Spark.ini")

IF EXIST "C:\Program Files (x86)\Automature\SPARK" (DEL "C:\Program Files (x86)\Automature\SPARK\Spark.ini.Linux" /s /f /q ) ELSE (DEL "C:\Program Files\Automature\SPARK\Spark.ini.Linux" /s /f /q )

IF EXIST "C:\Program Files (x86)\Automature\SPARK" (DEL "C:\Program Files (x86)\Automature\SPARK\Spark.ini.Mac" /s /f /q ) ELSE (DEL "C:\Program Files\Automature\SPARK\Spark.ini.Mac" /s /f /q )




