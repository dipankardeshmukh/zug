:: PROVIDE FULL USER CONTROL TO SPARK

echo %1
echo %2

:: Set SPARK path
IF EXIST "C:\Program Files (x86)\Automature\SPARK" (set Spark_Path="C:\Program Files (x86)\Automature") ELSE (set Spark_Path="C:\Program Files\Automature")
cd %Spark_Path%
set Spark_Path=%Spark_Path:"=%

icacls "SPARK" /grant Users:F