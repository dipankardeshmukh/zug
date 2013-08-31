::delete myfile.txt from c drive
del %1
set error=%errorlevel%
::Check for error
if not %error% == 0 goto fail
::If success exit with 0
exit 0

:fail
::If fail exit with 1
exit 1
