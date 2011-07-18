@echo off

set Zugpath=%1

set Zugpath=%Zugpath:"=%


RMDIR "%Zugpath%" /s /q

