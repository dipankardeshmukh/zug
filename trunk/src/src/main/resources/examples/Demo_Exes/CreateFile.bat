::Create a new file in c drive named myfile.txt
echo test>>%1
set error=%errorlevel%

::Check for error
if not %error% == 0 goto fail
::If success exit with 0
exit 0

:fail
::If fail exit with 1
exit 1
