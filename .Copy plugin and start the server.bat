@echo off
:head
echo Copying...
copy build\libs\galaxy-1.0-SNAPSHOT.jar D:\Mc\Servers\spongeforge1.12dev\mods /Y /V
echo Starting...
cd D:\Mc\Servers\spongeforge1.12dev
java -Xmx1024M -jar forge-1.12.2-14.23.2.2611-universal.jar nogui
echo Server stopped.
echo Press any key to copy the plugin and restart the server.
pause>nul
cd D:\JavaP\Git\oktw-galaxy\
goto :head