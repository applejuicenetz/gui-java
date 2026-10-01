# development

### neues Release in dieser Reihenfolge erstellen

```bash
mvn versions:set \
    -DgroupId=de.applejuicenet.client.gui \
    -DartifactId=AJCoreGUI \
    -DgenerateBackupPoms=false \
    -DoldVersion="*" \
    -N versions:update-child-modules \
    -DnewVersion=0.86.0
    
    mvn versions:set \
    -DgroupId=de.applejuicenet.client \
    -DartifactId=AJClientGUI \
    -DgenerateBackupPoms=false \
    -DoldVersion="*" \
    -N versions:update-child-modules \
    -DnewVersion=0.86.0
```

2. Changelog anpassen
3. Änderungen committen und mit der neuen Version taggen
4. die github action erstellt zum Tag das Release mit passenden Assets


## Pakete bauen

JDK 25 (inklusive `jpackage`) und Python 3 installieren. Maven verwendet `--release 25`.

```bash
mvn clean package
python scripts/package.py prepare
```

`target/AJCoreGUI.zip` enthält JAR, Bibliotheken, Plugins und Ressourcen und benötigt
Java 25 auf dem Zielsystem. Native Pakete enthalten die Java-25-Laufzeit:

```bash
# Auf macOS (Architektur passend zum installierten JDK wählen):
python scripts/package.py native --platform macos --arch aarch64 --type dmg
# Auf Windows, mit WiX 5 und UI-/Util-Erweiterungen:
python scripts/package.py native --platform windows --arch amd64 --type exe
# Lokaler Launcher-Test auf Linux:
python scripts/package.py native --platform linux --arch aarch64 --type app-image
```

Die Release-Pipeline baut DMG und EXE jeweils für amd64 und aarch64 sowie zwei
Linux-Flatpaks mit OpenJDK 25. Jeder Build läuft auf passendem OS und passender
Architektur. Alle sechs Pakete plus `AJCoreGUI.zip` bleiben bei manueller Ausführung
als Actions-Artefakt verfügbar; Tags veröffentlichen dieselben Dateien als Release.
Tag (optional mit `v` davor) muss zur Maven-Version passen.

Windows installiert pro Benutzer. WiX registriert `ajfsp` unter
`HKCU\Software\Classes\ajfsp`; der Befehl lautet
`"[INSTALLDIR]AJCoreGUI.exe" "%1"`. Deinstallation entfernt die vom Installer
angelegten Registry-Werte. Das WiX-Template stammt direkt aus dem verwendeten
JDK 25 und wird vor dem Installer-Build um diese Registrierung ergänzt.
macOS registriert `ajfsp` über `CFBundleURLTypes`, Flatpak über Desktop-MIME-Typen.
AJL-Dateizuordnungen bleiben erhalten. Laufende GUI nimmt weitere Links über den
lokalen Listener an; Dateipfade und URLs werden als einzelne Argumente übergeben.

Smoke-Test nach Installation: GUI aus anderem Arbeitsverzeichnis starten,
`ajfsp`-Link über Browser/OS öffnen, bei laufender GUI wiederholen und AJL-Datei
mit Leerzeichen/Umlauten im Pfad öffnen. macOS- und Windows-Installer sind unsigniert.

## libtray

Wie im Collector nutzt Linux `dev.hivens:libtray:0.1.3-flatpak.2` für
StatusNotifierItem. macOS/Windows und fehlende Linux-Backends nutzen AWT.
Das bestehende Swing-Menü liefert Aktionen und Übersetzungen. Upload-/Download-
Regler bleiben über das Swing-Popup erreichbar. Flatpak erlaubt Zugriff auf
`org.kde.StatusNotifierWatcher`.

Die libtray-Version liegt in GitHub Packages (`red171/libtray`). Maven benötigt
Server `github` mit Benutzername und Token mit `read:packages`; GitHub Actions
konfiguriert dies über `actions/setup-java` und `GITHUB_TOKEN`. Keine Tokens ins
Repository schreiben. Test-Builds auf Branches `build/javagui-*` erzeugen alle
Pakete als Artefakte, ohne Release zu veröffentlichen.

macOS verlangt eine positive erste Zahl in `CFBundleVersion`. Für GUI-Versionen
`0.x.y` nutzt jpackage deshalb intern Buildnummer `1.x.y`; sichtbare GUI-Version,
`CFBundleShortVersionString`, Tag und JAR bleiben `0.x.y`.
