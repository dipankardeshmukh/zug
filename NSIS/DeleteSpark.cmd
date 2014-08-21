@echo off

set Sparkpath=%1

set Sparkpath=%Sparkpath:"=%


RMDIR "%Sparkpath%" /s /q

