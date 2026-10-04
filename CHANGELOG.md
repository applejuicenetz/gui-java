# Changelog

## 0.86.3

### Sichtbare Änderungen

- [red171] „Mein Share“:
  - Lädt beim ersten Öffnen automatisch
  - Shareanzeige erneuern behält aufgeklappte Ordner und vorhandene Auswahl bei
  - Prio-Zahlen mittig ausgerichtet
  - Datenspalten kompakt; übrige Breite für Namen
  - Ordner-Kontextmenü erstellt eine Dateiliste mit allen enthaltenen Dateien,
    einschließlich Unterordnern
  - Dateilisten öffnen rechts im Hauptfenster, vertikal mittig und
    innerhalb des Bildschirms
  - Dateilisten passen die Größe-Spalte an Inhalte an, nutzen die Restbreite
    für Dateinamen und zeigen Größen in passenden Einheiten wie KB, MB und GB
- [red171] Downloads:
  - Einzelner Download wird automatisch ausgewählt und zeigt seine Quellen
  - Beide Tabellen passen Datenspalten an ihren Inhalt an; Dateiname nutzt die
    Restbreite, Zielverzeichnis bleibt begrenzt und Spaltenbreiten werden nicht gespeichert
  - Keine doppelten Downloads nach einem Core-Neustart (ghosting)
  - Powerdownload-Bereich bietet genug Platz für Text und Eingaben und scrollt
    bei geringer Höhe ohne Überlappung
- [red171] Bedienung und Darstellung:
  - `ajfsp`- und `web+ajfsp`-Links lassen sich per Drag-and-drop ins Hauptfenster
    übernehmen; das gewählte Zielverzeichnis bleibt berücksichtigt
  - Offene Verbindungen stehen statt auf der Startseite neben dem Verbindungsstatus
    in der Statusbar, ohne zusätzliche Abfragen
  - Statusanzeige nennt den geladenen Servernamen vor DynIP und Port
  - Upload-Fortschrittsbalken bleiben durch Innenabstand klar voneinander getrennt
  - Suchergebnisse passen Datenspalten laufend an Inhalte an; Dateiname nutzt
    die Restbreite, ohne gespeicherte Spaltenbreiten
  - Upload- und Servertabellen passen Datenspalten an Inhalte an; Dateiname
    beziehungsweise Servername nutzt die Restbreite, ohne gespeicherte Spaltenbreiten
  - Einzelne Einträge in Upload- und Servertabellen werden automatisch ausgewählt
  - Speed-Spalten wählen automatisch passende Einheiten wie KB/s, MB/s und GB/s
  - Dialoge öffnen mittig über dem Hauptfenster statt mittig auf dem Bildschirm
  - „Über“ zeigt Programmierer und besonderen Dank ohne Mailadressen und Scrollen
  - Spaltenbreiten passen sich ohne gespeicherte Werte automatisch an
  - Cmd-/Strg+A/C/V/X funktionieren in allen Textfeldern
  - Startbild ohne Ladebalken und künstliche Wartepausen
  - macOS: Menüs (Extras, Sprache, Themes, Core …) erscheinen in der Menüleiste
    oben am Bildschirm
  - Menü „Beenden“ mit Symbol; Sprachflaggen auch unter macOS aktiviert
- [red171] Soundpakete:
  - Wechseln ohne GUI-Neustart
  - Auswahl spielt eine Hörprobe, auch bei stummer GUI
- [red171] Sprachen und Themes:
  - Nicht ladbare oder entfernte Themes fallen auf ein Standard-Theme zurück,
    damit die GUI weiterhin startet
  - Sprachverwaltung beim GUI-Start verbessert
  - Unvollständige Sprachen Italienisch und Türkisch entfernt
  - SkinLF und alte Skin-Theme-Packs entfernt

### Technische Änderungen

- [red171] Core-Abfragen:
  - Download- und Uploaddaten werden nur bei aktiver Ansicht abgefragt
  - Automatischer Powerdownload hält die Download-Abfrage aktiv
  - Abfrageintervall von 2 auf 1 Sekunde verkürzt
- [red171] Verbindungen und Aktualisierung:
  - Suchabbruch startet keine parallelen Wiederholungen; Stop beendet auch
    wartende Polling- und Suchabbruch-Abrufe
  - Core-Anfragen warten nicht mehr unbegrenzt und geben Verbindungen frei
  - News, Serverliste und Update-Prüfung warten nicht mehr unbegrenzt
  - Aktualisierung erholt sich nach fehlerhaften Core-Antworten und läuft nach
    Stopp nicht weiter
- [red171] Code und Bibliotheken:
  - Ungenutzte GUI-Komponenten entfernt
  - Zusätzliche XML-Bibliotheken durch Java-Bordmittel ersetzt
  - JSON-Bibliothek für die Update-Prüfung ausgetauscht
  - Layout-Bibliothek für Download-Link-Leiste und Uploadansicht entfernt

## 0.86.2

### Sichtbare Änderungen

- [red171] Bedienung und Darstellung:
  - Darstellung von Ordnerbäumen und Listen verbessert, auch bei größeren
    Schriften und unterschiedlichen Themes
  - Share-Dateiliste mit Pfeiltasten bedienbar; Ordner lassen sich mit
    links/rechts schließen und öffnen
- [red171] Listen und Aktualisierung:
  - Download-, Upload-, Server- und Suchansicht aktualisieren sich ohne
    unnötigen Neuaufbau
  - Stabilere Aktualisierung von Listen und Verzeichnisauswahl während des Betriebs
- [red171] Tray:
  - Fehlende Tray-Funktion in Installationspaketen behoben

### Technische Änderungen

- [red171] Paketbau:
  - Enthaltene Bibliotheken werden beim Paketbau geprüft

## 0.86.1

### Sichtbare Änderungen

- [red171] „Mein Share“:
  - Share-Größe mit passender binärer Einheit (MiB/GiB/TiB) statt festem MB
    angezeigt (#15)
  - Shareanzeige nach Dateiänderungen korrekt aktualisiert (#14)
- [red171] Bedienung und Fenster:
  - Zielordner-Auswahl (F3) zeigt vorhandene Incoming-Unterordner auch ohne
    zugewiesene Downloads (#21)
  - Ungültige Fensterpositionen beim Start korrigiert; Tray-Klick stellt
    minimierte Fenster wieder her
  - Extras-Menü um „Beenden“ unter „Über“ ergänzt; GUI speichert Einstellungen
    und beendet sich
  - Spaltenbreiten und Reihenfolge pro View gespeichert; unbesuchte Tabs
    überschreiben keine Einstellungen, Suchergebnisse übernehmen ihr Layout (#6)
- [red171] Themes und Darstellung:
  - Darcula durch FlatLaf 3.7.2 ersetzt; Darkmode bleibt unter Java 25 ohne
    Zugriff auf interne Swing-Klassen nutzbar
  - Temp- und Incoming-Felder in den Optionen übernehmen die Theme-Farben
    statt festem Weiß
  - VLC-Feld, Ratio-Feld (PWDL) und SpeedGraph-Hintergrund nutzen standardmäßig
    die Theme-Farbe
  - Verbindungs-Assistent übernimmt die Theme-Farben statt festem Weiß
- [red171] Tray:
  - Tray-Einzelklick schaltet Fenster um
  - Tray-Menü-Icons auf 16×16 begrenzt; „Zeigen“ bläht das Menü nicht mehr auf
  - macOS-Tray-Kontextmenü nativ wie im Core; Rechtsklick ohne Swing-Popup
  - „Beenden“ im Tray-Menü ergänzt; GUI speichert Einstellungen vor dem Schließen
  - Upload-/Download-Regler aus dem Tray-Menü entfernt
  - Linux-Tray mit libtray wie im Collector integriert
- [red171] LogViewer:
  - Rechtsklick auf eine Logdatei bietet Löschen mit Rückfrage
  - Button "Alle Logs löschen" unten links; laufendes Log bleibt erhalten,
    DEBUG-Einträge für das Löschen
- [red171] Links, Dateien und Verbindungen:
  - `web+ajfsp://`-Links zusätzlich zu `ajfsp://` registriert (Windows, macOS,
    Flatpak) und im Link-Eingabefeld akzeptiert
  - AJL-Dateien an laufende GUI übergeben (#13)
  - Native Launcher übernehmen ajfsp-Links und AJL-Dateien
  - Dateien über 2 GiB können korrekt behandelt werden
  - Ports bis 65535 erlaubt (#17)
- [red171] Installation:
  - Windows-Installer wie das NSIS-Setup: Program Files, Startmenügruppe,
    Desktop-Verknüpfung, Hilfe-/Info-Link und `ajfsp`-/`.ajl`-Registrierung
  - Sechs OS-Pakete mit Laufzeit; ZIP bleibt erhalten

### Technische Änderungen

- [red171] Build und Abhängigkeiten:
  - Java 25 als Laufzeit- und Build-Basis
  - GitHub Actions und Maven-Abhängigkeiten aktualisiert, CodeQL entfernt
- [red171] Dateiformate:
  - AJL-Dateien als UTF-8 gespeichert und gelesen (#16)

## 0.85.3

- [red171] Zeige Core-Version auch an, wenn die News einen HTTP-Status `404` liefern (z. B. wenn die News-URL nicht erreichbar ist)

## 0.85.2

- [red171] Erzeuge den Splashscreen direkt transparent ohne Bildschirmaufnahme (erzeugt keine Abfrage zur Bildschirmfreigabe mehr)

## 0.85.1

- [red171] Erlaube `%7C` (`|`) in Links auch im „Link hinzufügen“-Feld
- [red171] In der `wizard.xml` den `maxdownloads`-Wert standardmäßig auf `0` gestellt

## 0.85.0

- [meins57] Iconpack `modern` zusammengestellt
- [meins57] Neue Java-GUI-Logos für `modern` erstellt
- [red171] Iconpack `modern` als Standard hinzugefügt
- [red171] `speedgraph`-Plugin wiederhergestellt
- [red171] Logger `log4j` gegen `logback` ausgetauscht
- [red171] `ajcorefassade`-Bibliothek als eigenständiges Modul
- [red171] `tklcontrols`-Bibliothek als eigenständiges Modul

## 0.84.2

- [red171] Iconpack-Unterstützung (siehe [icons](./resources/icons/)-Ordner)
- [red171] Soundpack-Unterstützung (siehe [sounds](./resources/sounds/)-Ordner)
- [red171] Unter macOS funktioniert nun `Cmd+V` / `Cmd+C` usw.

## 0.84.1

- [red171] Darkmode über das Java-Look-and-Feel-Theme `Darcula` integriert
- [red171] Die alternativen Skins funktionieren wieder
- [red171] Alle alternativen Skins werden mitinstalliert
- [red171] Sounds funktionieren wieder!

## 0.84.0

- [red171] Codebasis auf Java 11 angehoben
- [red171] `ajfsp`-URL-Handler funktioniert nun unter macOS und Linux
- [red171] `.ajl`-Dateien können direkt mit dem GUI geöffnet/verknüpft werden
- [red171] Das `serverwatcher`-Plugin wurde entfernt
- [red171] Standard-Plugins werden jetzt auf allen Plattformen geladen
- [red171] Für macOS wird jetzt eine `.dmg`-Datei erstellt (vereinfacht die Installation)

## 0.83.4

- [red171] Die URL für die GUI-News ist nun manuell in der `ajgui.properties` konfigurierbar (`options_news_url`)
- [red171] Die URL für die Serverliste ist nun manuell in der `ajgui.properties` konfigurierbar (`options_server_list_url`)
- [red171] Die URL für den Update-Server-Feed ist nun manuell in der `ajgui.properties` konfigurierbar (`options_update_server_url`)
- [red171] Den Browser-Bug bei der Update-Benachrichtigung final behoben ;)
- [red171] Der FAQ-Link aus dem „Dein Client“-Bereich wurde entfernt

## 0.83.3

- [red171] Import der dekompilierten `tklcontrols`
- [red171] Alte `AJCoreGUI.exe` wiederhergestellt (mehrfache Linkübernahme funktioniert nun wieder!)

## 0.83.1

- [red171] Neues, zeitgemäßes Icon für die ausführbaren Dateien und das Tray-Icon
- [red171] Linkübernahme in Browsern auf Basis von Chromium behoben, auch wenn die GUI bereits geöffnet ist
- [red171] Der Release-Info-Button öffnet nun eine konfigurierte URL mit `ajfsp`-Link angehängt
- [red171] Die Konfiguration der Release-Info wird nun ebenfalls im Verzeichnis `user.home` gespeichert, außerdem ist der API-Pfad konfigurierbar
- [red171] Im Upload-Tab gibt es jetzt auch ein Kontextmenü
- [red171] Das IRC-Plugin wurde entfernt
- [red171] Für die Update-Überprüfung gibt es nur noch einen Ein-/Aus-Schalter
- [red171] Der „Neue Version verfügbar“-Dialog hat nur noch eine URL und zeigt auf das GitHub-Release
- [red171] GUI und Plugins werden mit Maven kompiliert, Pakete (leider) noch via `ant` komprimiert
- [red171] Alle GUI-Plugins mit Java 8 und UTF-8-Kodierung neu kompiliert, Sprachdateien mit UTF-8 neu kodiert

## 0.82.1

- [red171] Benutze unter Linux [xdg-open](https://wiki.ubuntuusers.de/xdg-utils/#xdg-open) als Standardbrowser für URLs

## 0.82.0

- [red171] [wizard.xml](./AJClientGUI/wizard.xml) an die aktuellen Gegebenheiten angepasst
- [red171] Jegliche Konfigurationsdateien werden jetzt im Verzeichnis `user.home` gesichert
- [red171] Unter Linux wird jetzt der interne Fenstertitel korrekt gesetzt (für `.desktop`-Dateien wichtig)

## 0.81.1

- [red171] Linkübernahme in Browsern auf Basis von Chromium behoben (`%7C` vs `|`)

## 0.80.1

- [red171] VCS-Import
- [red171] JRE-Versionsprüfung entfernt (so funktioniert das GUI auch mit `Java >= 11`)
- [red171] URL der Updateprüfung auf GitHub umgestellt
- [red171] Kompatibilität der Ant-Datei `build.xml` zu Java 8 hergestellt

## 0.71.1

- [Maj0r] Release-Info auf `applefiles.cc` umgestellt

## 0.71.0

- [Maj0r] Sortierung der Tabellen wird gespeichert
- [Maj0r] Menüpunkt Release-Info in Share, Suche und Download hinzugefügt
- [Maj0r] Bäume in Suche, Download und Upload durch Tabellen ersetzt

## 0.70.5

- [Maj0r] Fehler beim Hinzufügen von Share-Ordnern behoben
- [Maj0r] Priorität der Uploads in der Form `1:2,2` (Prioritätswert) anzeigen
- [Maj0r] Sortierung im Downloadreiter korrigiert

## 0.70.4

- [Maj0r] Deadlock beim Start gefixt
- [Maj0r] Anzeige der Downloadgeschwindigkeit und des gesamten Down- und Uploads korrigiert
- [Maj0r] Pwdl der Uploads in der Form `1:2,2` anzeigen
- [Maj0r] Mit Java 6 lassen sich bei lokaler Core-Verbindung Share-Dateien und laufende Downloads nun mit dem Standardprogramm öffnen
- [Maj0r] Bug #670 Reiter per Tastaturschnelltaste anwählbar

## 0.70.2

- [Maj0r] Core-Versionsprüfung gefixt

## 0.70.1

Java ab 5.0 wird benötigt

- [Maj0r] Tray-Icon von Java 6 eingebaut und funktioniert nun unter Windows, Mac und Linux (GNOME-Tray und KDE-Tray)
- [Maj0r] CPU-Last der Suchergebnisdarstellung wesentlich verringert.
  Nun sind auch große Treffermengen gut verwendbar (Test mit 1500 bis 2000 Treffern pro Suche bei 3 Suchen).
  Lediglich das initiale Laden der Treffer dauert etwas, das Zeug muss halt über die Leitung und einmal geparst werden.
- [Maj0r] Anzahl aller unterschiedlichen gefundenen Dateien einer Suche wird im Suchergebnisreiter angezeigt
- [Maj0r] Ist beim Start der Loglevel `DEBUG` eingestellt, wird ein weiterer Reiter „Debug“ angezeigt, der alle Lognachrichten enthält
- [Maj0r] Feature-Request: Gesamtpunkte der gesetzten Priorität werden im Sharebereich (`x/1000`) angezeigt. (Danke an `fdh`)
- [Maj0r] `TKLControls` eingebaut ([www.tkl-soft.de](http://www.tkl-soft.de))
  In den Optionen und im Wizard werden nun Felder mit modifizierten Werten blau umrahmt. Mit `Strg+Z` kann der Ursprungswert wiederhergestellt werden.
  Ungültige Werte werden rot umrahmt (z. B. im Downloadlinkfeld im Downloadbereich).
- [Maj0r] Bug #527 gefixt: Bug mit 100 % CPU-Last behoben. Trat immer auf, wenn man die Größe der Partliste veränderte. (Danke an `fdh`)
- [Maj0r] Feature-Request #476: Suchergebnisse, die bereits im Temp- oder Share-Verzeichnis vorhanden sind, werden grün markiert. (Danke an `clickweg`)
- [Maj0r] GUI komplett überarbeitet und auf die neue `CoreFassade 1.0` umgebaut
- [loevenwong] Feature-Request #549: Automatischen Powerdownload komplett überarbeitet. (Danke an `xxluckystrikexx`)

## 0.61.2

- [Maj0r] Bug #528 gefixt: Deadlock bei der Darstellung der Partliste behoben. (Danke an `akku`)
- [Maj0r] Bug #525 gefixt: Das GUI nutzte bei ungünstiger Datenkonstellation 100 % CPU-Zeit (Danke an `akku` und `apokalypse1982`)

## 0.61.1

- [Maj0r] Auf vielfachen Wunsch Partlistenanzeige wieder auf mehrere Threads aufgeteilt. Braucht wieder etwas mehr Ressourcen, ist aber beim gefühlten Laden schneller.
- [Maj0r] Bug #524 gefixt (Danke an `akku` und `fdh`)
- [Maj0r] Bug gefixt: Nach Benutzung von „Priorität löschen“ wurde die View nicht aktualisiert.

## 0.61.0

Core ab Version **0.30.146.1202** wird benötigt

- [Maj0r] Wenn ein Download hinzugefügt wird, gibt es nun, wenn der Core einen Fehler meldet, eine Benachrichtigung.
- [Maj0r] Bug gefixt (Danke an Up)
  Coreseitig beendete Suchen werden nun auch im GUI als beendet dargestellt.
- [Maj0r] `skinlf.jar` aktualisiert
- [Maj0r] `.ajl`-Listen können nun direkt mit Angabe eines Zielverzeichnisses importiert werden
- [Maj0r] Downloads können nun direkt mit Angabe eines Zielverzeichnisses gestartet werden
- [Maj0r] Anzeige zusätzlicher Informationen freigegebener Dateien (Datum letzter Anfrage, Anzahl Downloadanfragen, Anzahl Suchanfragen)
- [Maj0r] Bug gefixt
  Es konnte mit jedem offenen Port eine Verbindung hergestellt werden, das GUI blieb anschließend leer.
  Unterscheidung zwischen ungültiger Core-Adresse und falschem Passwort eingebaut.
- [Maj0r] Feature-Request #465 (Danke an clickweg)
  Beim ersten Start des GUIs wird versucht, anhand der Standardeinstellungen zu verbinden.
- [Maj0r] Die Datei `properties.xml` wurde durch die Datei `ajgui.properties` ersetzt.
- [Maj0r] Einschränkung aufgehoben
  Im Verbindungsdialog können nun beliebig viele Cores gespeichert werden.
- [Maj0r] Bug #490 gefixt (Danke an Up)
  Umlaute sind nun in Verzeichnis- und Dateinamen in den Optionen möglich.
- [Maj0r] Bug gefixt
  Im Verbindungsdialog funktionierte die Datenübernahme nicht, wenn im Host- oder Passwortfeld `Return` betätigt wurde.
- [Maj0r] Feature-Request #475 (Danke an clickweg)
  Anzeige der Verfügbarkeit in Prozent der aktuell gezeigten Partliste.
- [Maj0r] Feature-Request #481 (Danke an johannes8)
  Firewall-Warnung wird nun zusätzlich als Symbol und als Tooltip ganz links in der Statusleiste angezeigt.
- [Maj0r] Bug #494 gefixt (Danke an dsp2004)
- [Maj0r] Bug #505 gefixt (Danke an rexcorda)
- [loevenwong] Fokus wird auf das Passwortfeld gesetzt.

## 0.60.0

- [Maj0r] Feature-Request #472 (Danke an clickweg)
  Downloads, Shares und Suchergebnisse werden nun mit passenden Icons dargestellt (vgl. Suche).
- [loevenwong] Feature-Request #458; Verbindungswizard kann über „Optionen → Verbindungen“ gestartet werden.
- [loevenwong] Feature-Request #414 (Danke an clickweg)
  Download-Tooltips können per „Optionen → Ansicht“ deaktiviert werden.
- [loevenwong] Wenn „Automatisch verbinden“ ausgewählt ist, wird anschließend noch geprüft, ob die `Shift`-Taste gedrückt wird (nach Erscheinen des Splashscreens),
  falls doch ein anderer Core verwendet werden soll.
- [loevenwong] Tastaturereignisse beim Anmeldedialog um `Enter`/`Escape` erweitert.
- [Maj0r] Feature-Request
  Unter „Optionen → Ansicht“ kann ein Programm ausgewählt werden (z. B. VLC).
  Wenn der Core auf dem gleichen Rechner wie das GUI läuft, dann wird in der Downloadtabelle und in der Sharetabelle im Kontextmenü ein neuer Menüpunkt aktiviert.
  Mit diesem wird der Shareeintrag an das verknüpfte Programm übergeben.
- [Maj0r] Bug #175 gefixt (Danke an jr17)
  Tray-Icon gefixt.
- [Maj0r] Bug #413 gefixt (Danke an hirsch.marcel)
  Leere Suchen werden nicht mehr ausgeführt. Leerstellen am Anfang und am Ende eines Suchbegriffs werden entfernt.
- [Maj0r] Feature-Request #442 (Danke an clickweg)
  Uploads können nun per Kontextmenü als Links in die Zwischenablage kopiert werden.
- [Maj0r] Stats eingebaut. Parameter:

  ```text
  -command=getajstats
  ```

- [loevenwong] Versionsanzeige eingebaut. Parameter:

  ```text
  -command=getajinfo
  ```

## 0.59.3

- [Maj0r] Bugfix (Danke an muhviehstarr)
  Zwei Deadlocks behoben. Einer bewirkte, dass das GUI beim Start beim Splashscreen hängen bleiben konnte.
- [Maj0r] `\n` in der Servernachricht wird nicht mehr beachtet. HTML-Tags verwenden.
- [Maj0r] Feature-Request
  Automatischer Powerdownload pausiert nun standardmäßig nicht mehr die Downloads.
  Außerdem kann nun ein Einstellungsdialog implementiert werden, um Anpassungen während des Betriebs des automatischen Powerdownloads vorzunehmen.
- [Maj0r] Bugfix
  Bei sehr hohen Maxupload- und/oder Maxdownloadwerten kam es zu Fehlern im Tray-Icon.
- [Maj0r] Bug #421 gefixt (Danke an Up)
  Wizard wurde um Standardeinstellungen für DSL 1000, DSL 2000 und DSL 3000 erweitert.
- [Maj0r] Bug #423 gefixt (Danke an hirsch.marcel und Up)
  Aktive, indirekte Uploads werden wieder angezeigt.
- [Maj0r] Dreckigen Rest im Uploadbereich entfernt.

## 0.59.2

- [Maj0r] Bug #420 gefixt (Danke an Up)
  Ganz frischen `NullPointer` gefixt.

## 0.59.1

Core ab Version **0.30.145.610** wird benötigt

- [Maj0r] Wasserstände der einzelnen Uploader werden angezeigt.
- [Maj0r] Beim Serverwechsel wird nun eine qualifizierte Warnung ausgegeben, wenn die aktuelle Verbindung noch keine 30 Minuten besteht.
- [Maj0r] Bugfix
  Beim Neuerzeugen der `properties.xml` wurden die neuen Coredaten nicht für die aktuelle Sitzung übernommen.
  Folge war ein Verbindungsverlust.

## 0.59.0

- [Maj0r] Durch Ändern einer Quellcodevariable in der `AutomaticPowerdownloadPolicy.java` kann das Pausieren von Dateien verhindert werden.
- [Maj0r] Bug #392 gefixt (Danke an Up und jr17)
  Im Zuge der XML-Parser-Umstellung (0.56.1) ist die Firewall-Warnung verloren gegangen.
- [Maj0r] Icons für Uploads in der Warteschlange korrigiert.
- [Maj0r] Downloadadresse für die Updateinfodatei auf Wunsch der BerliOS-Crew von `berlios.de` auf `tkl-soft.de` geändert.
- [Maj0r] Uploads, die zwar in der Warteschlange sind, aber keine aktive Verbindung halten, werden jetzt im dreckigen Rest angezeigt.

## 0.58.0

Core ab Version **0.30.144.522** wird benötigt

- [Maj0r] Bug #360 gefixt (Danke an fapu & panterfrau)
  Beim Linkklicken konnte es passieren, dass zwei GUIs gestartet werden.
- [Maj0r] Bug #361 gefixt (Danke an panterfrau)
  Logfehlermeldung bei überlastetem Core wird nun nur noch als `DEBUG` geloggt.
- [Maj0r] Feature-Request (Danke an Up)
  Bei Servern ohne Namen wird nun im Startbereich `IP:Port` angezeigt.
- [Maj0r] Willkommensnachricht des Servers eingebaut.
- [Maj0r] Anzeige, ob die Warteschlange voll ist, im Uploadbereich eingebaut.
- [maj0r] In der Statusspalte eines nicht aktiven Uploads wird nun die Corezeit der letzten Aktivität angezeigt.

## 0.57.1

- [Maj0r] Bugfix (Danke an mich ;) )
  Beim Laden von Plugins konnten Fehler beim Classloading auftreten. Wenn der Statusbalken bei „Lade Plugins...“ hängen bleibt, bitte updaten.
- [Maj0r] Change (Danke an hirsch.marcel)
  IRC-Server im Infodialog angepasst.
- [Maj0r] Archivdifferenzierung beseitigt (besonderen Dank an Up für die Windows-Starter-EXE!)
  Es gibt fortan keine separaten Archive mehr für Windows und andere Betriebssysteme.
- [Maj0r] Feature-Request #319 (Danke an tom62)
  Sortierung im Dateilistenexport eingebaut.
- [Maj0r] Bugfix (Danke an muhviehstarr)
  Sortierung nach Zeit in der Serveranzeige korrigiert.
- [Maj0r] Bug #322 gefixt (Danke an torsten_altreiter)
  Der Dateiname wurde per Linkübernahme aus der Suche nicht korrekt übernommen.
- [loevenwong] Updateprüfung auch per Menüeintrag ermöglicht.
- [Maj0r] Standardaussehen auf JGoodies geändert (weniger ressourcenlastig)
  Themes können natürlich weiterhin verwendet werden.

## 0.57.0

- [Maj0r] Bugfix (Danke an whitewindow)
  Die Anzahl der Quellen pro gefundener Datei wurde bei neuen Ergebnissen nicht korrigiert.
  Die Anzahl der gefundenen Dateien war korrekt, jedoch die Anzahl der Quellen pro Datei entsprechend niedrig.
- [Maj0r] Bug #306 gefixt (Danke an dsp2004)
  Bei Partlistanfragen an einen überlasteten Core kam es zu Fehlern.
- [Maj0r] Fortschrittsbalken in den Splashscreen eingebaut.
- [Maj0r] Tooltips in der ersten Spalte der Downloadtabelle eingebaut.
- [Maj0r] Info-Dialog geändert
  Credits geändert.
  Credits lassen sich nun per Mausklick im Dialog anhalten bzw. fortsetzen.

## 0.56.2

- [Maj0r] Bug #293 gefixt (Danke an dsp2004)
  Bei Partlistanfragen an einen überlasteten Core kam es zu Fehlern.
- [Maj0r] Bug #260 gefixt (Danke an computer.ist.org)
  Buttons im Sharebereich dürfen erst aktiviert werden, wenn die Einstellungen vom Core geholt wurden.
- [Maj0r] Bug #282 gefixt (Danke an tnt23)
  `NullPointer` behoben, der auftrat, wenn man im Sharebereich auf „Priorität setzen“ geklickt hat, ohne vorher einen Eintrag zu selektieren.
- [Maj0r] Bug #273 gefixt
  Es kam zu einem Fehler, wenn die Partliste nicht den gesamten reservierten Bereich bedeckte und dieser Teil von der Maus überwandert wurde.
- [loevenwong] Feature-Request #222: ComboBox zur Auswahl der letzten 3 Verbindungen eingebaut. (Danke an `hirsch.marcel`)
- [Maj0r] Feature-Request #222 (Danke an `johannes8`)
  Download-Umbenennungsdialog bietet nun eine Auswahl der gefundenen Namen der Quellen des Downloads an.
- [Maj0r] Kontextmenü im Downloadbereich überarbeitet
  F-Tasten eingebaut.
  Pausieren und Fortsetzen auf vielfachen Wunsch getrennt.
- [Maj0r] Dialog zur Eingabe eines Datei-Incoming-Verzeichnisses kann jetzt per `Return` bestätigt werden.
- [Maj0r] Bug behoben, der sich durch die dynamische Generierung der `properties.xml` ohne Neustart eingeschlichen hat.
- [loevenwong] Einfügen per Kontextmenü im Download-Textfeld eingebaut.
- [loevenwong] Einstellungen der JGoodies werden jetzt gespeichert.
- [Maj0r] Installierte Look-and-Feels werden beim Generieren der Standard-XML mit aufgenommen.

## 0.56.1

Diese Version benötigt den Core ab Version **0.29.135.208**

- [Maj0r] GUI startet nur noch bei unterstützter Core-Version
- [loevenwong] GUI muss nicht neu gestartet werden, wenn noch keine Properties-Datei vorhanden ist (wird weiterhin automatisch erzeugt).
- [Maj0r] Feature-Request #254 gefixt (Danke an te_real_ZeroBANG)
  Downloadtabelle wird jetzt beim Start standardmäßig nach Dateinamen sortiert.
- [Maj0r] Feature-Request #274 gefixt (Danke an johannes8)
  Downloads können per `F2` umbenannt werden.
- [Maj0r] Bug #264 gefixt (Danke an muhviestarr)
  Verbindungsstatus wird richtig angezeigt.
- [Maj0r] Modifizierbare und potenziell modifizierbare Dateien bei Nicht-Windows-Systemen verschoben

  ```text
  properties.xml → ~/appleJuice/gui
  Plugins        → ~/appleJuice/gui/plugins
  Logs           → ~/appleJuice/gui/logs
  ```

- [Maj0r] Pluginschnittstelle komplett überarbeitet
  Alle vorhandenen Plugins müssen an die neue Schnittstelle angepasst werden.
  Gründe für die Überarbeitung:

  1. Einfacher
  2. Eingrenzung der Plugins und Minimierung der Fehlermöglichkeiten
  3. Sprachdateien werden unterstützt (z. B. `language_xml_deutsch.xml` im Plugin-JAR)
- [Maj0r] Suchergebnisse werden nun, wenn möglich, mit einem sprechenden Icon angezeigt.
- [Maj0r] Suche um Filter erweitert
  Die Filter in der Suchergebnistabelle wirken sich NICHT auf die Suche aus, lediglich die Treffer werden gefiltert.
- [Maj0r] Status „Warteschlange voll“ wird nun auch in „In Warteschlange“ angezeigt, da diese z. B. für Pwdl-Änderungen genauso relevant sind.
- [Maj0r] Tabellenköpfe werden in allen Tabellen gleich dargestellt.
- [Maj0r] Unterstützung für fremde Look-and-Feels eingebaut
  Um Look-and-Feels zu verwenden, müssen die Themes deaktiviert werden.
  Ausgeliefert werden nur JGoodies als alternative Look-and-Feels.
  Es können alle konformen Look-and-Feels verwendet werden. Dazu einfach ein passendes JAR in `/lib` legen und die Look-and-Feel-Klasse in der `properties.xml` eintragen.
- [Maj0r] Unnötiges Passwortfeld unter „Optionen → Passwort“ entfernt.
- [Maj0r] Passwortfeld unter „Optionen → Proxy“ ist nun wirklich ein Passwortfeld und stellt das Passwort nicht mehr im Klartext dar.
- [Maj0r] Standard-Theme-Pack auf Toxic geändert.

## 0.56.0

Meine wahrscheinlich letzte GUI für den Core 0.29.x

- Soundausgabe bei korrektem Login korrigiert.
- Sound bei fertigem Download eingebaut.
- Unicode-Verwendung im Umgang mit den Sprachdateien korrigiert.
- Kleinere Korrekturen.

## 0.55.10

- Bug #246 gefixt: Nun können auch bei „voller“ Dateilistentabelle im Sharebereich neue Dateien hineingezogen werden. (Danke an `mail_tom62`)
- Automatische Sortierung nach Dateinamen eingebaut.
- Feature-Request #244 gefixt: Standardmäßig ist nun beim automatischen Powerdownload der Inaktiv-Button selektiert. (Danke an `Homer1Simpson`)
- Bug #243 gefixt: GUI stört sich nicht mehr an Nicht-Theme-ZIPs im Themes-Verzeichnis. (Danke an `RoadRunner`)
- Bug #241 gefixt: Farbgebung war genau umgekehrt. Nun gilt wirklich: je dunkler, desto mehr Quellen gefunden. (Danke an `computer.ist.org`)
- Partliste zeigt nun per Mouseover-Effekt den Tooltip zum ausgewählten Partstück an.

## 0.55.9

- Link zur FAQ im Startbereich hinzugefügt.
- Bug #242 gefixt: Legende für Partliste um „aktive Übertragung“ erweitert. (Danke an `Kossi-Jaki`)
- Bug behoben, der im `VersionChecker` zu einer `NoSuchElementException` führte. (Danke an `computer.ist.org`)
- Bug #239 gefixt: `ArrayIndexOutOfBoundsException` behoben. (Danke an `dsp2004`)
- Bug #235 gefixt: Passwortfeld im Logindialog funktioniert wieder ordentlich. (Danke an `Up`)

## 0.55.8

- Bug #234 gefixt: Tabellen werden beim Ändern von Spaltengrößen nicht mehr sortiert. (Danke an `hirsch.marcel`)
- Feature-Request #228: Im Pwdl-Eingabefeld funktionieren nun auch die Hoch-/Runter-Pfeiltasten. (Danke an `Major-Tom`)
- Links werden nun bei Übernahme in eine verwertbare Schreibweise geparst.
- Bug #226 gefixt (Danke an dsp2004)
- GUI reagiert ordentlich auf eine coreseitige Passwortänderung.
- Server werden nun korrekt angezeigt.
- Weitere Speicheroptimierung.

## 0.55.7

- Bug #223 und #224 gefixt: Das waren noch Bugs in Verbindung mit der DOM/SAX-Umstellung... (Danke an `dsp2004`, `Up` und `whitewindow`)

## 0.55.6

- Die meisten Teile von DOM auf SAX umgebaut, RAM-Verbrauch sollte dadurch spürbar gesenkt werden.
- Bug #219 gefixt: 100 % CPU-Last bei Eingabe eines falschen Passwortes beim Anmeldedialog gefixt. (Danke an `Up`)
- Bug #220 gefixt: `OutOfMemoryError` behoben. (Danke an `dsp2004`)

## 0.55.5

- Alten Timestampfehler beseitigt. Trotz Sessionumsetzung wurde immer noch der Timestamp mitgeschleppt.
- Bug #215 gefixt: Partliste wird nun auch bei kleinen Dateien korrekt gezeichnet. (Danke an `dsp2004`)
- Bug #129 gefixt: `WebsiteException` durch Überlastung des Cores sollte nun weitgehend unterbunden sein. (Danke an dsp2004)

## 0.55.4

- Bug #23 gefixt: Suche abbrechen korrigiert. (Danke an `computer.ist.org`)

## 0.55.3

- Mehr Logging für `WebSiteNotFoundException` eingebaut.
- Partliste bearbeitet:
  Hoffentlich den letzten Fehler behoben.
  Anzeige der Teile, die zurzeit übertragen werden (hellgelb bis dunkelgelb).
  Aktualisierungsintervall auf 2 Sekunden geändert.
- Button zum Verwerfen einer abgebrochenen Suche in den Suchreiter verschoben.
- Link mit Quellen kann nun auch im Sharebereich erzeugt werden.
- Bug #195 gefixt: Bug bei Pwdl-Einstellung korrigiert. (Danke an `supermuhkuh`)
- Bug #167 gefixt: Sortierung nach Anzahl in der Suchtabelle korrigiert. (Danke an `arnoldfake`)
- Bug #198 gefixt: Sortierung nach Downloadstatus korrigiert. (Danke an `froeschle567`)

## 0.55.2

Core ab **0.29.135.208** ist zu verwenden.

- Max. Anzahl von Quellen pro Datei kann nun begrenzt werden
- `SplitPane` im Sharebereich eingebaut.
- Sortierung des Sharebaums verbessert.
- Sortierung im Incoming-/Temp-Auswahlbaum eingebaut.
- Partliste überarbeitet.
- Startbereich scrollbar gemacht, wenn die Darstellung zu klein ist.
- Rand der `JSplitPane` im Downloadbereich entfernt (Danke an `muhviestarr`).
- Icons für `Upload-DirectStates` eingebaut.
- Verwendete Java-Version wird in die Logdatei geschrieben.
- Wizarddialog korrigiert: Nickname wird nun auf Richtigkeit geprüft und gespeichert wird erst nach Durchlaufen des gesamten Wizards.
- Bug #94 gefixt: Zulässige Werte für Core-Port und XML-Port sind `1024 < x <= 32000`. (Danke an `error666`)
- Bug #185 gefixt: Einstellungen des GUIs werden beim Schließen des Cores gesichert. (Danke an `muhviestarr`)
- Downloadlinks können optional mit der eigenen Quelle und ggf. mit dem verbundenen Server in die Ablage kopiert werden.
- Serverlinks können in die Ablage kopiert werden.

## 0.55.1

Core ab Version 0.29.133.201 ist zu verwenden.

- Kommunikation mit dem Core erfolgt nun komprimiert

## 0.54.7

- `AutomaticPowerdownloadPolicies` können nun von Benutzern mit Java-Erfahrung selbst implementiert werden
  Dazu muss die Klasse `AutomaticPowerdownloadPolicy` abgeleitet und ein JAR gebaut werden.
  Zum Bauen des JAR-Archivs gibt es ein neues Target in der `build.xml`.
  Das GUI erwartet diese JARs im Unterordner `/pwdlpolicies`.
- „Verbindung zum Core verloren“ sollte nicht mehr so schnell kommen (Danke an `the_Killerbee`).
- Interne Umbauten, um Objekte zu sparen
  Alle Plugins, die auf globale Objekte mittels `MapSetStringKey` zugreifen, müssen angepasst werden, sind in ihrer alten Version nicht mehr lauffähig und führen zu `ClassNotFoundExceptions` im Log.
  Statt des bisherigen Aufrufs

  ```text
  new MapSetStringKey(String)
  ```

  reicht nun dieser String oder bei IDs:

  ```text
  Integer.toString(int)
  ```

  Die Klasse `MapSetStringKey` wurde restlos entfernt.
- Logging verbessert
  `main()` in eine eigene `ThreadGroup` gepackt, dadurch kann keine Exception mehr „durchrasseln“, alle Exceptions finden sich im Log.

## 0.54.6

Nur frische Bugs beseitigt

- Bug #155 gefixt (Danke an daa803)
  Sharebaum wird nun wieder korrekt dargestellt.
- Bug #154 gefixt (Danke an hirsch.marcel)
  Alte Objekte werden jetzt wieder korrekt entfernt.
- Bug #160 gefixt (Danke an octron80)
  Fertige oder abgebrochene Downloads können nun wieder entfernt werden.
- Bug #153 umgesetzt (Danke an jr17)
  Verbindungsdialog kann nun per Option beim nächsten GUI-Start erzwungen werden.

## 0.54.5

- Filter beim Start des GUIs eingebaut
  Die Quellen werden beim ersten Holen der Daten vom Core nicht abgefragt, sodass Downloads sehr schnell gezeigt werden können.
  Nachteil: Da die Quellen anfangs fehlen, stehen die Geschwindigkeiten aller Downloads auf 0 kb/s. Die Gesamtgeschwindigkeit wird jedoch angezeigt.
  Das lange Laden aufgrund von vielen Quellen wird so folglich nur verschoben und der Benutzer bekommt früh erste Informationen zu sehen.
- Icons für NetWare und OS/2 eingefügt.
- Wiederholtes, zeitintensives Laden der gesamten Infos sollte nun durch ein überarbeitetes Sessionmanagement unterbunden sein.
- Fehlerhafte Anzeige von Menütexten bei Nicht-Windows-Systemen gefixt.

## 0.54.4

- Kontextmenüs mit Icons ausgestattet.
- Optionenmenü überarbeitet.
- An neue Coreschnittstelle angepasst.
- Tray-Icon-Bug hoffentlich behoben.

## 0.54.3

- Reihenfolge der Spalten der Download- und Uploadtabelle wird gespeichert
  Da die `properties.xml` angefasst werden musste, wird diese beim ersten Start neu generiert.
- Bug #74 gefixt (Danke an habkeineMail)
- `.ajl`-Listen können nun über das Menü importiert werden.
- Sprachdatei „türkisch“ eingebaut (Danke an nurseppel).
- Upload-Fortschrittsanzeige wird jetzt nur noch bei aktiven Uploads angezeigt.
- Laden von Plugins verbessert.
  Müll oder nicht standardkonforme Plugins im Plugin-Ordner werden nun korrekt behandelt.
- Bug #98 gefixt (Danke an twix)

## 0.54.2

- Bug #91 umgesetzt (Danke an hirsch.marcel)
  Maxupload- und Maxdownloadgeschwindigkeit können nun über das Tray-Icon eingestellt werden (Windowsversion).
- Bug #82 gefixt (Danke an hirsch.marcel)
  Sortierung von Downloads innerhalb von Unterverzeichnissen der Downloadtabelle korrigiert.
- Sortierung in die Suchergebnistabelle eingebaut.
- Bug #92 gefixt (Danke an daa803)
- Bug #77 gefixt (Danke an spam_blocker)
  Selektionsproblem der Downloadtabelle beim Entfernen von fertigen Downloads behoben.
- Tabellenspalten der Download- und Uploadtabelle können nun über ein Kontextmenü bei Rechtsklick auf den Tabellenheader aus-/eingeblendet werden.
- Im Downloadbereich sind nun der obere (Tabelle) und der untere Bereich (Powerdownload, Partliste) in der Höhe verstellbar.
- Bug #83 gefixt (Danke an hirsch.marcel)
  Tabellenspalten können nun korrekt verschoben werden.
- Bug #33 gefixt (Danke an oz_2k)
  Obwohl ich denke, dass es sich um ein Feature der Themes handelt, wurde der Vollbildmodus auf Wunsch vieler Benutzer an den Windows-Standard angepasst.

## 0.54.1

- Bug #53 gefixt (Danke an o_a_s_e_)
  Bug mit 98 % CPU-Last durch Suche gefixt.
- Warnmeldung bezüglich 30-Minuten-Sperre bei manuellem Serverwechsel eingebaut.
- Bug #63 umgesetzt (Danke an clickweg)
  Den Link zum Holen von Servern in einen Button umgebaut, da der Link wohl von vielen übersehen wurde.
- Bug #23 gefixt (Danke an computer.ist.org)
  Suche lässt sich nun korrekt abbrechen.

## 0.53.2

- Bug #67 gefixt (Danke an dsp2004)
  Probleme mit der Funktion automatisch Partliste anzeigen korrigiert.

## 0.53.1

- Wenn die Verbindung zum Core aufgrund von Überlastung des Cores abreißt, wird zweimal erneut probiert, bevor das GUI beendet wird.
- Tray-Icon für Windowsplattformen eingebaut
- Bug #56 gefixt (Danke an MeineR)
  Das Laden der Plugins beim Start kann über das Optionenmenü deaktiviert werden.
- Bug #43 gefixt (Danke an flabeg)
  Shareverzeichnis wird bei Prioritätenänderung nicht mehr komplett neu geladen, sondern nur aktualisiert.
- Bug #13 umgesetzt (Danke an HabkeineMail)
  Powerdownload-Werte werden jetzt bei Klick auf einen Download oder eine Quelle im Powerdownloadfeld angezeigt.
- Bug #42 umgesetzt (Danke an dsp2004)
  Partlisten werden nun durch eine Option wahlweise bei Mausklick auf den Download oder eine Quelle oder über den Button „Partliste anzeigen“ geholt.
- `properties.xml` aus den Download-Archiven entfernt
  Beim Update auf eine neuere Version muss diese nun nur noch bei einer Formatänderung erneuert werden.
  Nachteil: Bei einer kompletten Neuinstallation erhält man beim ersten Start eine Fehlermeldung und muss das GUI neu starten.
- Wenn eine neue Version gefunden wird, kann diese nun direkt mit dem Standardbrowser heruntergeladen werden
  Der Standardbrowser muss in den Optionen ausgewählt werden.
- Links im Startbereich sind jetzt anklickbar, sofern ein Standardbrowser ausgewählt ist.
- Bug #40 umgesetzt (Danke an hirsch.marcel)
  Incoming-Verzeichnis kann nun für mehrere Downloads gleichzeitig geändert werden.
- `PluginOptionenDialog` überarbeitet.
- Dialog bei fehlgeschlagenem Verbindungsversuch überarbeitet.
- Menüpunkt zum Beenden des Cores auf vielfachen Wunsch an separate Stelle verschoben.
- Bug #17 gefixt (Danke an HabkeineMail)
  Partlisten von einigen wenigen Downloadquellen wurden bei Bedarf nicht geholt.
- Sonstige Kleinigkeiten.

## 0.52.1

- Plugin-Entwickler können nun ein `JPanel` für Optionen implementieren, welches ggf. im Plugin-Reiter der Optionen aufgerufen werden kann.
- Plugin-Entwickler können nun Objekte direkt mittels ID vom Core erfragen (Danke an webhamster).
- Bug #19 gefixt (Danke an dsp2004)
  `NullPointer` behoben.

## 0.51.2

- Bug #14 umgesetzt (Danke an Dragonne)
  Es konnte zu einem Fehler kommen, wenn gleichzeitig zwei Instanzen des GUIs liefen.
- Der Core kann jetzt übers GUI beendet werden.
- Downloads können nun umbenannt werden.
- Das Zielverzeichnis für einen Download (Incoming-Unterverzeichnis) kann nun geändert werden.
- Überprüfung auf gültige Java-Version eingebaut
  Es wird mindestens 1.4 benötigt (empfohlen 1.4.2).
- Suchanzeige korrigiert
  Es kann passieren, dass nicht alle gefundenen Suchergebnisse beim Core ankommen, die Ausgabe wurde entsprechend korrigiert.
- Bug #8 umgesetzt (Danke an finn)
  Downloadlinks kann man nun auch direkt in der Downloadtabelle per Kontextmenü erzeugen.
  Ich wollte es eigentlich nicht umsetzen, da ich die Funktion an dieser Stelle für falsch platziert erachte, doch da der Wunsch bei vielen Benutzern bestand, habe ich es nun doch eingebaut.
  In die Uploadtabelle werde ich es definitiv NICHT einbauen.
- Bug #10 gefixt (Danke an muhviestarr)
  Wenn man keine Downloads hat, steht nun nicht mehr „bitte warten“ in der Downloadtabelle.

## 0.51.1

- Downloadlinks werden jetzt in `ISO-8859-1` an den Core übertragen.
- Versionsupdateinformation geändert
  Über die Optionen kann nun gewählt werden, ob man nur bei neuen Versionen (0.51.1), wichtigen Änderungen (0.51.1) oder sogar bei kosmetischen Korrekturen (0.51.1) benachrichtigt wird.
  Standard ist eine Benachrichtigung bei wichtigen Änderungen.
- Bug #1 gefixt (Danke an muhviestarr)
  Look & Feel stimmt nun auch beim Verbindungsdialog.
- Bug #2 gefixt (Danke an muhviestarr)
  Taskbar-Eintrag für den Splashscreen und den Verbindungsdialog eingebaut.
- Bug #4 gefixt (Danke an muhviestarr)
  Shareanzeige bei Prioritätenänderung gefixt.
- Dateigrößen in der Sharetabelle werden nun korrekt ausgegeben (Danke an schnigger und TuxHomer).
- `NullPointer` behoben, der auftrat, wenn der verbundene Server keinen Namen hat (Danke an paderborner).

## 0.50

- Logging kann nun komplett deaktiviert werden (Danke an muhviestarr).
- Im Verbindungsfenster geht nun ein einfaches `Enter` (Danke an muhviestarr).
- Legende für die Servertabelle eingebaut (Danke an muhviestarr).
- Text von Netzwerk, Neuigkeiten und Nachrichten ist nun auch schwarz (Danke an muhviestarr).
- Die Überschrift „Warnungen“ auf der Startseite wird nun ausgeblendet, wenn es keine Warnungen gibt (Danke an muhviestarr).
- Gridlines werden nun in der Servertabelle nicht mehr angezeigt (Danke an muhviestarr).
- Splashscreen wird nun früher angezeigt (Danke an muhviestarr).
- Bug in der Partliste behoben.
- DAU-Button zum Anzeigen der Partliste eingebaut.
- Bug der Tabellenköpfe der Share- und der Uploadtabelle behoben (Danke an muhviestarr).

## 0.49

- Bug bei der Wiedergabe von Sounds korrigiert (Danke an mrbond).
  Audiogerät wird nun nach Ausgabe eines Sounds wieder freigegeben.
- Es kann nun der Link einer freigegebenen Datei über das Popupmenü der Sharetabelle als UBB-Code in die Ablage kopiert werden.
- Entwicklercreditsanzeige im Infodialog korrigiert.
- Bug im Sharebaum behoben.
- Bug beim Sortieren der Sharetabelle behoben.

## 0.48

- Initialen Aufruf des Sharetabs durch einen Initialisierungsthread beschleunigt.
- In der Downloadtabelle nun ein Warte-Icon angezeigt, bis erstmalig Daten geholt wurden.
- Verhalten des Popupmenüs der Servertabelle überarbeitet.
- Partliste wird nun nur noch über das Popupmenü geholt.
  Wenn der Downloadtab verlassen wird, wird das Aktualisieren der aktuellen Partliste beendet.

## 0.47

- Sharetabelle auf vielfachen Wunsch komplett überarbeitet.
- Partliste wird erst nach 2 Sekunden Wartezeit geholt (Danke an muhviestarr).
  Wenn innerhalb dieser Zeit auf einen anderen Download bzw. eine andere Quelle geklickt wird, wird die Wartezeit neu gestartet.
- Suche kann nun GUI-seitig abgebrochen werden.
  Der aktuelle Core (0.29.124.1215) hat an dieser Stelle noch einen Bug, eine Suche wird jedoch nach einiger Zeit automatisch beendet.
- Tabellenspalten-Root-Handles werden nun in der Downloadtabelle angezeigt (Danke an muhviestarr).
  So weiß auch der letzte Benutzer, dass man auf einen Download klicken kann, um die Quellen zu finden.
- Neuen Downloadstatus „Fehler beim Fertigstellen“ und neuen Quellenstatus „Eigenes Limit erreicht“ eingebaut.
- Rundungsfehler beim automatischen Powerdownload behoben (Danke an garnichda).

## 0.46

- Bug beim automatischen Powerdownload behoben, der auftrat, wenn nur eine Datei im Download war.
- Bug im Menü behoben, Auswahl eines Menüpunktes geht nun gewohnt schnell.
- Kleinere optische Korrekturen (Danke an DBZfan)
  z. B. Hintergrundfarben aller Scrollbereiche an ihre Tabelle angepasst.
- Prozentangabe bei Downloads nun auf zwei Nachkommastellen genau (Danke an muhviestarr)
- Parameterübergabe an das GUI geändert
  Mögliche Parameter können per `-help` angezeigt werden.
  Die `.reg`-Datei muss neu angepasst und importiert werden, da diese ebenfalls modifiziert werden musste.
- Diverse andere Bugs behoben

## 0.45

- Links können nun an das GUI übermittelt werden (für den Internet Explorer muss die entsprechende `.reg`-Datei angepasst und importiert und die Windows-EXE verwendet werden).
- Themes sind nun deaktivierbar

## 0.44

- Themes eingebaut (Danke an `LinuxDoc`)
  Passende Themes gibt es auf [GitHub](https://github.com/l2fprod/javootoo.com/tree/master/plaf/skinlf/themepacks).
- Automatischen Powerdownload eingebaut.
  Verschiedene Arten des automatischen Powerdownloads können in Zukunft durch selbst implementierte Klassen per ComboBox ausgewählt werden (nächste Version).

## 0.43

- Anzeige des maximalen RAM-Verbrauchs im Memory-Monitor eingebaut (Anzeige oben links nun „reserviert / max allocated“).
- Sound-Icons optisch korrigiert.
- Fehler bei der Soundausgabe bei fehlerhaften Audiodateien (z. B. falsches Format) oder fehlendem Audiogerät behoben.

## 0.42

- Memory-Monitor eingebaut (ja, die Anzeige geht richtig und zeigt den echten RAM-Verbrauch der Anwendung)
- Manuellen Garbage Collector bei jeder 30. Aktualisierung eingebaut

## 0.41

- Fehler im Pwdl-Textfeld behoben
- Sortieren der Downloadtabelle nach Status eingefügt
- Speicheroptimierungen.

## 0.40

- Soundeffekte für diverse Ereignisse eingefügt.

## 0.39

- Standarduploadpriorität ist im Core noch nicht implementiert und deshalb erst einmal wieder aus dem GUI geflogen (Danke an `xcalibur`)
- Buttons zum Ändern der Pwdl-Werte entsprechend dem Standard vertauscht (Danke an `lova`)
- Baum zur Auswahl des Temp- und Incoming-Ordners korrigiert. Bug hat sich erst mit `v0.38` eingeschlichen (Danke an `lova` und `akku`)
