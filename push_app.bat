@echo off
set DIR="F:\Workspace\DEMO\Test\HandsetTest---paster\app\build\outputs\apk\debug"
set FILE=""
for /R %DIR% %%f in (*.apk) do ( 
	set FILE=%%f
	break
)
adb root
adb remount
adb push %FILE% /system/priv-app/HandsetTest/HandsetTest.apk
adb reboot