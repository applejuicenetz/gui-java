@echo off
cls
d:
cd\
SET PROJEKT_VERZEICHNIS=d:\projects\java\
cd %PROJEKT_VERZEICHNIS%
CHOICE /C:JN /T:J,5 Ordner anlegen ((J)a, (N)ein)?
if errorlevel 2 GOTO NICHT_ANLEGEN
@md AJClientGUI

:NICHT_ANLEGEN
@cd AJClientGUI

@set PATH=C:\Programme\Together6.0\bin\win32;%PATH%
CHOICE /C:JNA /N  /T:J,5 Login ((J)a, (N)ein, (A)bbrechen)?
if errorlevel 1 GOTO ANMELDEN
if errorlevel 2 GOTO ANONYM
if errorlevel 3 GOTO TOTALES_ENDE

:ANMELDEN
ECHO loevenwong anmelden...
@set CVSROOT=:pserver:loevenwong@cvs.berlios.de:/cvsroot/applejuicejava
GOTO LOGINSTART

:ANONYM
ECHO anonymous anmelden...
cvs -d:pserver:anonymous@cvs.applejuicejava.berlios.de:/cvsroot/applejuicejava login
GOTO NICHT_ANMELDEN

:LOGINSTART
SET i=0
:LOGINSCHLEIFE
IF %i%==3 GOTO TOTALES_ENDE ELSE GOTO LOGIN

:LOGIN
cvs -d%CVSROOT% login
SET login=0
if errorlevel 1 SET login=1
IF %login%==0 GOTO ANMELDUNG_FERTIG ELSE GOTO AUSNAHME

:AUSNAHME
IF %i%==2 SET i=3
IF %i%==1 SET i=2
IF %i%==0 SET i=1
GOTO LOGINSCHLEIFE

:NICHT_ANMELDEN
:ANMELDUNG_FERTIG

@cvs checkout -P AJClientGUI

:ENDE
echo Abrufen beendet
cd\
cd %PROJEKT_VERZEICHNIS%

:TOTALES_ENDE
cd\
cd %PROJEKT_VERZEICHNIS%
pause
