:: PROVIDE FULL USER CONTROL TO SPARK

echo %1
echo %2

:: Set SPARK path
set spark_path=C:\Program Files\automature
cd %spark_path%
set spark_path=%spark_path:"=%

icacls "SPARK" /grant Users:F