if not exist %1 goto fail
::File exists
exit 0

:fail
::File donot exist
exit 1