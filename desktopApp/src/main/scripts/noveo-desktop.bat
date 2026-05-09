@echo off
setlocal
set "APP_HOME=%~dp0.."
java -cp "%APP_HOME%\lib\*" ir.hienob.noveo.desktop.DesktopMainKt
