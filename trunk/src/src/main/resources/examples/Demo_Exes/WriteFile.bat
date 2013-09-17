:: path
if %1=="" exit 3        
:: text
if %2=="" exit 3

@echo %2>%1
exit 0
