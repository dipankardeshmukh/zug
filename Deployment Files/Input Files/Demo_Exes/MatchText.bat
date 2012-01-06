if not %1 == %2 goto fail
::Text matches
exit 0

:fail
::Text do not match
exit 1