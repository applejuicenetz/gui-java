# Changelog

**0.83.4**
- [red171] die URL für die GUI NEWS ist nun manuell in der `ajgui.properties` konfigurierbar (`options_news_url`)
- [red171] die URL für die Serverliste ist nun manuell in der `ajgui.properties` konfigurierbar (`options_server_list_url`)
- [red171] die URL für den Update Server Feed ist nun manuell in der `ajgui.properties` konfigurierbar (`options_update_server_url`)
- [red171] den Browser Bug bei der Update Benachrichtigung final behoben ;)
- [red171] der FAQ link aus dem "Dein Client" Bereich wurde entfernt

**0.83.3**

- [red171] import der dekompilierten `tklcontrols`
- [red171] alte `AjCoreGUI.exe` wiederhergestellt (mehrfach Linkübernahme funktioniert nun wieder!)

**0.83.1**

- [red171] neues, zeitgemäßes Icon der ausführbaren Dateien und Trayicon
- [red171] Linkübernahme in Browsern auf Basis von Chromium behoben, auch wenn die GUI bereits geöffnet ist
- [red171] der Release Info Button öffnet nun eine konfigurierte URL mit `ajfsp` Link angehängt
- [red171] die Konfiguration der Release-Info wird nun ebenfalls im `user.home` gespeichert, außerdem ist der API Pfad konfigurierbar
- [red171] Im Upload Tab ist jetzt auch Kontext Menü
- [red171] das IRC Plugin wurde entfernt
- [red171] für die Update-Überprüfung gibt es nur noch einen an/aus Schalter 
- [red171] der "neue Version verfügbar" Dialog hat nur noch eine URL und zeigt auf das Github Release
- [red171] Gui und Plugins werden mit Maven kompiliert, Pakete (leider) noch via `ant` komprimiert
- [red171] alle GUI Plugins mit Java8 und UTF-8 Encoding neu kompiliert, Sprachdateien mit UTF-8 neu kodiert

**0.82.1**

- [red171] benutze unter Linux [xdg-open](https://wiki.ubuntuusers.de/xdg-utils/#xdg-open) als Standard Browser für URLs 

**0.82.0**

- [red171] [wizard.xml](./AJClientGUI/wizard.xml) an die aktuellen Gegebenheiten angepasst
- [red171] jegliche Konfigurationsdateien werden jetzt im `user.home` gesichert
- [red171] unter Linux wird jetzt der interne Fenster Titel korrekt gesetzt (für `.desktop` Files wichtig)

**0.81.1**

- [red171] Linkübernahme in Browsern auf Basis von Chromium behoben (`%7C` vs `|`)

**0.80.1**

- [red171] VCS import
- [red171] JRE Version checker entfernt (so funktioniert das GUI auch mit Java >= 11)
- [red171] Update Checker URL auf github umgestellt
- [red171] ant `build.xml` kompatibilität zu Java 8 hergestellt

**0.71.1**

- [Maj0r] Release-Info auf `applefiles.cc` umgestellt

**0.71.0**

- [Maj0r] Sortierung der Tabellen wird gespeichert
- [Maj0r] Menüpunkt Release-Info in Share, Suche und Download hinzugefügt
- [Maj0r] Bäume in Suche, Download und Upload durch Tabellen ersetzt

**0.70.5**

- [Maj0r] Fehler beim Hinzufügen von Share-Ordnern behoben
- [Maj0r] Priorität der Uploads in der Form 1:2,2 (Priowert) anzeigen
- [Maj0r] Sortierung im Downloadreiter korrigiert

**0.70.4**

- [Maj0r] Deadlock beim Start gefixt
- [Maj0r] Anzeige der Downloadgechwindigkeit und des Gesamtdown- und uploads korrigiert
- [Maj0r] PwDl der Uploads in der Form 1:2,2 anzeigen
- [Maj0r] Mit Java6 lassen sich bei lokaler Coreverbindung Sharedateien und laufende Downloads nun mit dem Standardprogramm öffnen
- [Maj0r] Bug #670 Reiter per Tastaturschnelltaste anwaehlbar

**0.70.2**

- [Maj0r] Core-Versionsprüfung gefixt

**0.70.1**

Java ab 5.0 wird benötigt

- [Maj0r] TrayIcon von Java6 eingebaut und funktioniert nun unter Windows, Mac und Linux (Gnome-Tray und KDE-Tray)
- [Maj0r] CPU-Last der Suchergebnisdarstellung wesentlich verringert.
          Nun sind auch große Treffermengen gut verwendbar (Test mit 1500 bis 2000 Treffern pro Suche bei 3 Suchen).
          Lediglich das initiale Laden der Treffer dauert etwas, das Zeug muss halt über die Leitung und einmal geparst werden.
- [Maj0r] Anzahl aller unterschiedlichen gefundenen Dateien einer Suche wird im Suchergebnisreiter angezeigt
- [Maj0r] Ist beim Start Loglevel Debug eingestellt, wird ein weiterer Reiter "Debug" angezeigt, der alle Lognachrichten enthält
- [Maj0r] Featurerequest: Gesamtpunkte der gesetzten Priorität wird im Sharebereich ( x/1000 ) angezeigt. (Danke an `fdh`)
- [Maj0r] TKLControls eingebaut ([www.tkl-soft.de](http://www.tkl-soft.de))
          In den Optionen und im Wizard werden nun Felder mit modifizierten Werten blau umrahmt. Mit `Strg+z` kann der Ursprungswert wieder hergestellt werden.
          Ungültige Werte werden rot umrahmt (z.B. im Downloadlinkfeld im Downloadbereich).
- [Maj0r] Bug #527 gefixt: 100%-CPU-Bug behoben. Trat immer auf, wenn man die Größe der Partliste veränderte. (Danke an `fdh`)
- [Maj0r] Featurerequest #476: Suchergebnisse, die bereits im Temp oder im Share vorhanden sind, werden grün markiert. (Danke an `clickweg`)
- [Maj0r] GUI komplett refactored und auf die neue `CoreFassade 1.0` umgebaut
- [loevenwong] Featurerequest #549 Automatischen Powerdownload komplett überarbeitet. (Danke an `xxluckystrikexx`)

**0.61.2**

- [Maj0r] Bug #528 gefixt: Deadlock bei der Darstellung der Partliste behoben. (Danke an `akku`)
- [Maj0r] Bug #525 gefixt: Das GUI nutzte bei ungünstiger Datenkonstellation 100% CPU-Zeit (Danke an `akku` und `apokalypse1982`)

**0.61.1**

- [Maj0r] Auf vielfachen Wunsch Partlistanzeige wieder auf mehrere Threads aufgeteilt. Braucht wieder etwas mehr Ressourcen, ist aber beim gefühlten Laden schneller.
- [Maj0r] Bug #524 gefixt (Danke an `akku` und `fdh`)
- [Maj0r] Bug gefixt: Nach Benutzung von "Priorität löschen" wurde die View nicht aktualisiert.

**0.61.0**

Core ab Version **0.30.146.1202** wird benötigt

- [Maj0r] Wenn ein Download hinzugefügt wird, gibt es nun, wenn der Core einen Fehler meldet, eine Benachrichtigung.
- [Maj0r] Bug gefixt (Danke an Up)
 Coreseitig beendete Suchen werden nun auch im GUI als beendet dargestellt.
- [Maj0r] `skinlf.jar` aktualisiert
- [Maj0r] ajl-Listen können nun direkt mit Angabe eines Zielverzeichnisses importiert werden
- [Maj0r] Downloads können nun direkt mit Angabe eines Zielverzeichnisses gestartet werden
- [Maj0r] Anzeige zusätzlicher Information gesharter Dateien (Datum letzter Anfrage, Anzahl Downloadanfragen, Anzahl Suchanfragen)
- [Maj0r] Bug gefixt
 Es konnte mit jedem offenen Port eine Verbindung hergestellt werden, das GUI blieb anschließend leer.
 Unterscheidung zwischen ungültiger Coreadresse und falschem Passwort eingebaut.
- [Maj0r] Featurerequest #465 (Danke an clickweg)
 Beim ersten Start des GUIs wird versucht, anhand der Standardeinstellungen zu verbinden.
- [Maj0r] Die Datei properties.xml wurde durch die Datei ajgui.properties ersetzt.
- [Maj0r] Einschränkung aufgehoben
 Im Verbindungsdialog können nun beliebig viele Cores gespeichert werden.
- [Maj0r] Bug #490 gefixt (Danke an Up)
 Umlaute sind nun in Verzeichnis- und Dateinamen in den Optionen möglich.
- [Maj0r] Bug gefixt
 Im Verbindungsdialog funktionierte die Datenübernahme nicht, wenn im Host- oder Passwortfeld Return betätigt wurde.
- [Maj0r] Featurerequest #475 (Danke an clickweg)
 Anzeige der Verfügbarkeit in Prozent der aktuell gezeigten Partliste.
- [Maj0r] Featurerequest #481 (Danke an johannes8)
 Firewall-Warnung wird nun zusätzlich als Symbol und als Tooltipp ganz links in der Statusleiste angezeigt.
- [Maj0r] Bug #494 gefixt (Danke an dsp2004)
- [Maj0r] Bug #505 gefixt (Danke an rexcorda)
- [loevenwong] Focus wird auf das Passwort-Feld gesetzt.

**0.60.0**

- [Maj0r] Featurerequest #472 (Danke an clickweg)
 Downloads, Shares und Suchergebnisse werden nun mit passenden Icons dargestellt (vgl. Suche).
- [loevenwong] Featurerequest #458; Verbindungswizard kann über Optionen->Verbindungen gestartet werden.- [loevenwong] Featurerequest #414 (Danke an clickweg)
 Download-Tooltipps können per Optionen->Ansicht deaktiviert werden.
- [loevenwong] Wenn automatisch Verbinden ausgewählt ist, wird anschliessend noch geprüft, ob die SHIFT-Taste gedrückt wird (nach Erscheinen des Splash-Screens),
 falls doch ein anderer Core verwendet werden soll.- [loevenwong] Tastaturereignisse beim Anmeldedialog um ENTER/ESCAPE erweitert.- [Maj0r] Featurerequest
 Unter Optionen->Ansicht kann ein Programm ausgewählt werden (z.B. VLC).
 Wenn der Core auf dem gleichen Rechner wie das GUI läuft, dann wird in der Downloadtabelle und in der Sharetabelle im Kontextmenü ein neuer Menüpunkt aktiviert.
 Mit diesem wird der Shareeintrag an das verknüpfte Programm übergeben.
- [Maj0r] Bug #175 gefixt (Danke an jr17)
 TrayIcon gefixt.
- [Maj0r] Bug #413 gefixt (Danke an hirsch.marcel)
 Leere Suchen werden nicht mehr ausgeführt. Leerstellen am Anfang und am Ende eines Suchbegriffs werden entfernt.
- [Maj0r] Featurerequest #442 (Danke an clickweg)
 Uploads können nun per Kontextmenü als Links in die Zwischenablage kopiert werden.
- [Maj0r] Stats eingebaut. Parameter: -command=getajstats
- [loevenwong] Versionsanzeige eingebaut. Parameter: -command=getajinfo

**0.59.3**

- [Maj0r] Bugfix (Danke an muhviehstarr)
 Zwei Deadlocks behoben. Einer bewirkte, dass das GUI beim Start beim Splashscreen hängen bleiben konnte.
- [Maj0r] \n in der Servernachricht wird nicht mehr beachtet. Html-Tags verwenden.
- [Maj0r] Featurerequest
 Automatischer Powerdownload pausiert nun standardmäßig nicht mehr die Downloads.
 Außerdem kann nun ein Einstellungendialog implementiert werden, um Anpassungen während des Betriebs des autom. Pwdls vorzunehmen.
- [Maj0r] Bugfix
 Bei sehr hohen Maxupload- und/oder Maxdownloadwerten kam es zu Fehlern im TrayIcon.
- [Maj0r] Bug #421 gefixt (Danke an Up)
 Wizard wurde um Standardeinstellungen für DSL 1000, DSL 2000 und DSL 3000 erweitert.
- [Maj0r] Bug #423 gefixt (Danke an hirsch.marcel und Up)
 Aktive, indirekte Uploads werden wieder angezeigt.
- [Maj0r] Dreckigen Rest im Uploadbereich entfernt.

**0.59.2**

- [Maj0r] Bug #420 gefixt (Danke an Up)
 Ganz frischen NullPointer gefixt.

**0.59.1**

Core ab Version **0.30.145.610** wird benötigt

- [Maj0r] Wasserstände der einzelnen Uploader werden angezeigt.
- [Maj0r] Beim Serverwechsel wird nun eine qualifizierte Warnung ausgegeben, wenn die aktuelle Verbindung noch keine 30 Minuten besteht.
- [Maj0r] Bugfix
 Beim Neuerzeigen der properties.xml wurden die neuen Coredaten nicht für die aktuelle Sitzung übernommen.
 Folge war ein Verbindungsverlust.

**0.59.0**

- [Maj0r] Durch Ändern einer Source-Variable in der AutomaticPowerdownloadPolicy.java kann das Pausieren von Dateien verhindert werden.
- [Maj0r] Bug #392 gefixt (Danke an Up und jr17)
 Im Zuge der XML-Parser-Umstellung (0.56.1) ist die Firewallwarnung verschütt gegangen.
- [Maj0r] Icons für Uploads in der Warteschlange korrigiert.
- [Maj0r] Downloadadresse für die Updateinfodatei auf Wunsch der berlios-Crew von berlios.de auf tkl-soft.de geändert.
- [Maj0r] Uploads, die zwar in der Warteschlange sind, aber keine aktive Verbindung halten, werden jetzt im dreckigen Rest angezeigt.

**0.58.0**

Core ab Version **0.30.144.522** wird benötigt

- [Maj0r] Bug #360 gefixt (Danke an fapu & panterfrau)
 Beim Linkklicken konnte es passieren, dass zwei GUIs gestartet werden.
- [Maj0r] Bug #361 gefixt (Danke an panterfrau)
 Logfehlermeldung bei überlastetem Core wird nun nur noch als Debug gelogt.
- [Maj0r] Featurerequest (Danke an Up)
 Bei Servern ohne Namen wird nun im Startbereich IP:Port angezeigt.
- [Maj0r] Willkommensnachricht des Servers eingebaut.
- [Maj0r] Anzeige, ob die Warteschlange voll ist, im Uploadbereich eingebaut.
- [maj0r] In der Statusspalte eines nicht aktiven Uploads wird nun die Corezeit der letzen Aktivität angezeigt.

**0.57.1**

- [Maj0r] Bugfix (Danke an mich ;) )
 Beim Laden von Plugins konnten Fehler beim Classloading auftreten. Wenn der Statusbalken bei "Lade Plugins..." hängen bleibt, bitte updaten.
- [Maj0r] Change (Danke an hirsch.marcel)
 Irc-Server im Infodialog angepasst.
- [Maj0r] Archivdifferenzierung beseitigt (Besonderen Dank an Up für die Windows-Starter-Exe !!)
 Es gibt fortan keine separaten Archive mehr für Windows und andere Betriebssysteme.
- [Maj0r] Featurerequest #319 (Danke an tom62)
 Sortierung im Dateilistenexport eingebaut.
- [Maj0r] Bugfix (Danke an muhviehstarr)
 Sortierung nach Zeit in der Serveranzeige korrigiert.
- [Maj0r] Bug #322 gefixt (Danke an torsten_altreiter)
 Der Dateiname wurde per Linkübernahme aus der Suche nicht korrekt übernommen.
- [loevenwong] Updateprüfung auch per Menüeintrag ermöglicht.
- [Maj0r] Standardaussehen auf JGoodies geändert (weniger ressourcenlastig)
 Themes können natürlich weiterhin verwendet werden.

**0.57.0**

- [Maj0r] Bugfix (Danke an whitewindow)
 Die Anzahl der Quellen pro gefundener Datei wurde bei neuen Ergebnissen nicht korrigiert.
 Die Anzahl der gefundenen Dateien war korrekt, jedoch die Anzahl der Quellen pro Datei entsprechend niedrig.
- [Maj0r] Bug #306 gefixt (Danke an dsp2004)
 Bei Partlistanfragen an einen überlasteten Core kam es zu Fehlern.
- [Maj0r] Fortschrittsbalken in den Splashscreen eingebaut.
- [Maj0r] Tooltipps in der ersten Spalte der Downloadtabelle eingebaut.
- [Maj0r] Info-Dialog geändert
 Credits geändert.
 Credits lassen sich nun per Mausklick in den Dialog anhalten bzw. fortsetzen.

**0.56.2**

- [Maj0r] Bug #293 gefixt (Danke an dsp2004)
 Bei Partlistanfragen an einen überlasteten Core kam es zu Fehlern.
- [Maj0r] Bug #260 gefixt (Danke an computer.ist.org)
 Buttons im Sharebereich dürfen erst aktiviert werden, wenn die Einstellungen vom Core geholt wurden.
- [Maj0r] Bug #282 gefixt (Danke an tnt23)
 NullPointer behoben, der auftrat, wenn man im Sharebereich auf "Prioritaet setzen" geklickt hat, ohne vorher einen Eintrag zu selektieren.
- [Maj0r] Bug #273 gefixt
 Es kam zu einem Fehler, wenn die Partliste nicht den gesamten reservierten Bereich bedeckte und dieser Teil von der Maus überwandert wurde.
- [loevenwong] Featurerequest #222: Combobox zur Auswahl der letzten 3 Verbindungen eingebaut. (Danke an `hirsch.marcel`)
- [Maj0r] Featurerequest #222 (Danke an `johannes8`)
 Download-Umbennen-Dialog bietet nun eine Auswahl der gefundenen Namen der Sourcen des Downloads an.
- [Maj0r] Kontextmenü im Downloadbereich überarbeitet
 F-Tasten eingebaut.
 Pausieren und Fortsetzen auf vielfachen Wunsch getrennt.
- [Maj0r] Dialog zur Eingabe eines Datei-Incoming-Verzeichnisses kann jetzt per `RETURN` bestätigt werden.
- [Maj0r] Bug behoben, der sich durch die dynamische Generierung der properties.xml ohne Neustart eingeschlichen hat.
- [loevenwong] Einfügen per Kontextmenü im Download-Textfeld eingebaut.
- [loevenwong] Einstellungen der JGoodies werden jetzt gespeichert.
- [Maj0r] Installierte Look&Feels werden beim Generieren der Standard-XML mit aufgenommen.

**0.56.1**

diese Version benötigt den Core ab Version **0.29.135.208**

- [Maj0r] GUI startet nur noch bei unterstützter Coreversion
- [loevenwong] GUI muss nicht neugestartet werden, wenn noch kein Property-File vorhanden ist (wird weiterhin automatisch erzeugt).
- [Maj0r] Featurerequest #254 gefixt (Danke an te_real_ZeroBANG)
 Downloadtabelle wird jetzt beim Start standarmäßig nach Dateiname sortiert.
- [Maj0r] Featurerequest #274 gefixt (Danke an johannes8)
 Downloads können per F2 umbenannt werden.
- [Maj0r] Bug #264 gefixt (Danke an muhviestarr)
 Verbindungsstatus wird richtig angezeigt.
- [Maj0r] Modifizierbare und potenziell modifizierbare Dateien bei Nicht-Windows-System verschoben
 properties.xml nach ~/appleJuice/gui
 Plugins nach ~/appleJuice/gui/plugins
 Logs nach ~/appleJuice/gui/logs
- [Maj0r] Pluginschnittstelle komplett überarbeitet
 Alle vorhandenen Plugins müssen an die neue Schnittstelle angepasst werden.
 Gründe für die Überarbeitung:
 1\. einfacher
 2\. Eingrenzung der Plugins und Minimierung der Fehlermöglichkeiten
 3\. Sprachdateien werden unterstützt (zB language_xml_deutsch.xml im Plugin-Jar)
- [Maj0r] Suchergebnisse werden nun, wenn möglich mit einem sprechenden Icon angezeigt.
- [Maj0r] Suche um Filter erweitert
 Die Filter in der Suchergebnistabelle wirken sich NICHT auf die Suche aus, lediglich die Treffer werden gefiltert.
- [Maj0r] Status "Warteschlange voll" wird nun auch in "In Warteschlange" angezeigt, da diese zB für Pwdl-Änderungen genauso relevant sind.
- [Maj0r] TableHeader werden in allen Tabellen gleich dargestellt.
- [Maj0r] Unterstützung für fremde Look&Feels eingebaut
    Um Look&Feels zu verwenden, müssen die Themes deaktiviert werden.
    Ausgeliefert werden nur JGoodies als alternative Look&Feels.
    Es können alle konformen Look&Feels verwendet werden. Dazu einfach ein passendes Jar in /needed_jars legen und die Look&Feel-Klasse in der propertes.xml eintragen.
- [Maj0r] Unnützes Passwortfeld unter Optionen->Passwort entfernt.
- [Maj0r] Passwortfeld unter Optionen->Proxy ist nun wirklich ein Passwortfeld und stellt das Passwort nicht mehr im Klartext dar.
- [Maj0r] Standardthemepack auf Toxic geändert.

**0.56.0**

meine wahrscheinlich letzte GUI für den Core 0.29.x
**- Soundausgabe bei korrektem Login korrigiert.
- Sound bei fertigem Download eingebaut.
- Unicode-Verwendung im Umgang mit den Sprachdateien korrigiert.
- Kleinere Korrekturen.

**0.55.10**

- Bug #246 gefixt: Nun können auch bei "voller" Dateilistetabelle im Sharebereich neue Dateien hinein gezogen werden. (Danke an `mail_tom62`)
- Automatische Sortierung nach Dateiname eingebaut.
- Featurerequest #244 gefixt: Standardmäßig ist nun beim automatischen Powerdownload der Inaktiv-Button selektiert. (Danke an `Homer1Simpson`)
- Bug #243 gefixt: GUI stört sich nicht mehr an Nicht-Themes-Zips im Themes-Verzeichnis. (Danke an `RoadRunner`)
- Bug #241 gefixt: Farbgebung war genau umgekehrt. Nun gilt wirklich: je dunkler, desto mehr Quellen gefunden. (Danke an `computer.ist.org`)
- Partliste zeigt nun per MausOver-Effekt den Tooltipp zum ausgewählten Partstück an.

**0.55.9**

- Link zur FAQ im Startbereich hinzugefügt.
- Bug #242 gefixt: Legende für Partliste um "aktive Übertragung" erweitert. (Danke an `Kossi-Jaki`)
- Bug behoben, der im VersionChecker zu einer NoSuchElementException führte. (Danke an `computer.ist.org`)
- Bug #239 gefixt: ArrayIndexOutOfBoundsException behoben. (Danke an `dsp2004`)
- Bug #235 gefixt: Passwortfeld im Logindialog funktioniert wieder ordentlich. (Danke an `Up`)

**0.55.8**

- Bug #234 gefixt: Tabellen werden beim Ändern von Spaltengrößen nicht mehr sortiert. (Danke an `hirsch.marcel`)
- Featurerequest #228: Im Pwdl-Eingabefeld funktionieren nun auch die Hoch/Runter-Pfeiltasten. (Danke an `Major-Tom`)
- Links werden nun bei Übernahme in eine verwertbare Schreibweise geparst.
- Bug #226 gefixt (Danke an dsp2004)
- GUI reagiert ordentlich auf eine coreseitige Passwortänderung.
- Server werden nun korrekt angezeigt.
- weitere Speicheroptimierung.

**0.55.7**

- Bug #223 und #224 gefixt: Das waren noch Bugs in Verbindung mit der DOM/SAX-Umstellung... (Danke an `dsp2004`, `Up` und `whitewindow`)

**0.55.6**

- meisten Teile von DOM auf SAX umgebaut, RAM-Verbrauch sollte dadurch spürbar gesenkt werden.
- Bug #219 gefixt: 100%-CPU bei Eingabe eines falschen Passwortes beim Anmeldedialog gefixt.  (Danke an `Up`)
- Bug #220 gefixt: OutOfMemoryError behoben. (Danke an `dsp2004`)

**0.55.5**

- alten Timestampfehler beseitigt. Trotz Sessionumsetzung wurde immer noch der Timestamp mitgeschleppt.
- Bug #215 gefixt: Partliste wird nun auch bei kleinen Dateien korrekt gezeichnet. (Danke an `dsp2004`)
- Bug #129 gefixt: WebsiteException durch Überlastung des Cores sollte nun weitgehend unterbunden sein. (Danke an dsp2004)

**0.55.4**

- Bug #23 gefixt: Suche abbrechen korrigiert. (Danke an `computer.ist.org`)

**0.55.3**

- Mehr Logging für WebSiteNotFoundException eingebaut.
- Partliste bearbeitet:
    Hoffentlich den letzten Fehler behoben.
    Anzeige der Teile, die zurzeit übertragen werden (hellgelb bis dunkelgelb).
    Aktualisierungintervall auf 2 Sekunden geändert.
- Button zum Verwerfen einer abgebrochenen Suche in den Suchreiter verschoben.
- Link mit Quellen kann nun auch im Sharebereich erzeugt werden.
- Bug #195 gefixt: Bug bei Pwdl-Einstellung korrigiert.. (Danke an `supermuhkuh`)
- Bug #167 gefixt: Sortierung nach Anzahl in der Suchtabelle korrigiert. (Danke an `arnoldfake`)
- Bug #198 gefixt: Sortierung nach Downloadstatus korrigiert. (Danke an `froeschle567`)

**0.55.2**

Core ab **0.29.135.208** ist zu verwenden.

- Max. Anzahl von Quellen pro Datei kann nun begrenzt werden
- SplitPane in Sharebereich eingebaut.
- Sortierung des Sharebaums verbessert.
- Sortierung in Incoming- / Tempauswahlbaum eingebaut.
- Partliste überarbeitet.
- Startbereich scrollbar gemacht, wenn die Darstellung zu klein ist.
- Rand der JSplitPane im Downloadbereich entfernt (Danke an `muhviestarr`).
- Icons für Upload-DirectStates eingebaut.
- verwendete Java-Version wird in die Logdatei geschrieben.
- Wizarddialog korrigiert: Nickname wird nun auf Richtigkeit geprüft und gespeichert wird erst nach Durchlaufen des gesamten Wizards.
- Bug #94 gefixt: Zulässige Werte für Core-Port und XML-Port sind 1024<x<=32000. (Danke an `error666`)
- Bug #185 gefixt: Einstellungen des GUIs werden beim Schliessen des Core gesichert. (Danke an `muhviestarr`)
- Downloadlinks können optional mit der eigenen Quelle und ggf. mit dem verbundenen Server in die Ablage kopiert werden.
- Serverlinks können in die Ablage kopiert werden.

**0.55.1**

Core ab Version 0.29.133.201 ist zu verwenden.

- Kommunikation mit dem Core erfolgt nun komprimiert

**0.54.7**
- AutomaticPowerdownloadPolicies können nun von Benutzern mit Java-Erfahrung selbst implementiert werden
    Dazu muss die Klasse AutomaticPowerdownloadPolicy abgeleitet und ein Jar gebaut werden.
    Zum Bauen des jar-Archivs gibt es ein neues Target in der build.xml.
    Das GUI erwartet diese Jars im Unterordner /pwdlpolicies.
- "Verbindung zum Core verloren" sollte nicht mehr so schnell kommen (Danke an `the_Killerbee`).
- Interne Umbauten um Objekte zu sparen
 Alle Plugins, die auf globale Objekte mittels MapSetStringKey zugreifen, müssen angepasst werden, sind in ihrer alten Version nicht mehr lauffähig und führen zu ClassNotFoundExceptions im Log.
 Anstatt eines "new MapSetStringKey(String)" reicht nun dieser String oder ein "Integer.toString(int)" bei IDs.
 Die Klasse MapSetStringKey wurde restlos entfernt.
- Logging verbessert
 main() in eigene ThreadGroup gepackt, dadurch kann keine Exception mehr "durchrasseln", alle Exceptions finden sich im Log.

**0.54.6**

Nur Frische Bugs beseitigt

- Bug #155 gefixt (Danke an daa803)
 Sharebaum wird nun wieder korrekt dargestellt.
- Bug #154 gefixt (Danke an hirsch.marcel)
 Alte Objekte werden jetzt wieder korrekt entfernt.
- Bug #160 gefixt (Danke an octron80)
 Fertige oder abgebrochene Downloads können nun wieder entfernt werden.
- Bug #153 umgesetzt (Danke an jr17)
 Verbindungsdialog kann nun per Option beim nächsten GUI-Start erzwungen werden.

**0.54.5**

- Filter beim Start des GUI eingebaut
 Die Quellen werden beim ersten Holen der Daten vom Core nicht abgefragt, so dass Downloads sehr schnell gezeigt werden können.
 Nachteil: Da die Quellen anfangs fehlen, stehen die Geschwindigkeiten aller Downloads auf 0 kb/s. Die Gesamtgeschwindigkeit wird jedoch angezeigt.
 Das lange Laden aufgrund von vielen Quellen wird so folglich nur verschoben und der Benutzer bekommt früh erste Informationen zu sehen.
- Icons für Netware und OS/2 eingefügt.
- Wiederholtes, zeitintensives Laden der gesamten Infos sollte nun durch ein überarbeitetes Sessionmanagement unterbunden sein.
- Fehlerhafte Anzeige von Menütexten bei Nicht-Windows-Systemen gefixt.

**0.54.4**

- Kontextmenüs mit Icons ausgestattet.
- Optionenmenü überarbeitet.
- An neue Coreschnittstelle angepasst.
- TrayIcon-Bug hoffentlich behoben.

**0.54.3**

- Reihenfolge der Spalten der Download- und Uploadtabelle wird gespeichert
 Da die properties.xml angefasst werden musste, wird diese beim ersten Start neu generiert.
- Bug #74 gefixt (Danke an habkeineMail)
- ajl-Listen können nun über das Menü importiert werden.
- Sprachdatei "türkisch" eingebaut (Danke an nurseppel).
- Upload-Fortschrittsanzeige wird jetzt nur noch bei aktiven Uploads angezeigt.
- Laden von Plugins verbessert.
 Müll oder nicht standardkonforme Plugins im Plugin-Ordner werden nun korrekt behandelt.
- Bug #98 gefixt (Danke an twix)

**0.54.2**

- Bug #91 umgesetzt (Danke an hirsch.marcel)
 Maxupload- und Maxdownloadgeschwindigkeit kann nun über das TrayIcon eingestellt werden (Windowsversion).
- Bug #82 gefixt (Danke an hirsch.marcel)
 Sortierung von Downloads innerhalb von Unterverzeichnissen der Downloadtabelle korrigiert.
- Sortierung in die Suchergebnistabelle eingebaut.
- Bug #92 gefixt (Danke an daa803)
- Bug #77 gefixt (Danke an spam_blocker)
 Selektionsproblem der Downloadtabelle beim Entfernen von fertigen Downloads behoben.
- Tabellenspalten der Download- und Uploadtabelle können nun über ein Kontextmenü bei Rechtsklick auf den Tabellenheader aus/eingeblendet werden.
- Im Downloadbereich sind nun der obere (Tabelle) und der untere Bereich (Powerdownload, Partliste) in der Höhe verstellbar.
- Bug #83 gefixt (Danke an hirsch.marcel)
 Tabellenspalten können nun korrekt verschoben werden..
- Bug #33 gefixt (Danke an oz_2k)
 Obwohl ich denke, dass es sich um ein Feature der Themes handelt, wurde der Vollbildmodus auf Wunsch vieler Benutzer an Windowsstandard angepasst.

**0.54.1**

- Bug #53 gefixt (Danke an o_a_s_e_)
 98%-CPU-Last Bug durch Suche gefixt.
- Warnmeldung bezüglich 30-Minuten-Sperre bei manuellem Serverwechsel eingebaut.
- Bug #63 umgesetzt (Danke an clickweg)
 Den Link zum Holen von Servern in einen Button umgebaut, da der Link wohl von vielen übersehen wurde.
- Bug #23 gefixt (Danke an computer.ist.org)
 Suche lässt sich nun korrekt abrechen.

**0.53.2**

- Bug #67 gefixt (Danke an dsp2004)
 Probleme mit der Funktion automatisch Partliste anzeigen korrigiert.

**0.53.1**

- Wenn die Verbindung zum Core aufgrund von Überlastung des Core abreisst, wird 2x erneut probiert, bevor das GUI beendet wird.
- TrayIcon für Windowsplattformen eingebaut
- Bug #56 gefixt (Danke an MeineR)
 Das Laden der Plugins beim Start kann über das Optionenmenü deaktiviert werden.
- Bug #43 gefixt (Danke an flabeg)
 Shareverzeichnis wird bei Prioritätenänderung nicht mehr komplett neu geladen, sondern nur aktualsiert.
- Bug #13 umgesetzt (Danke an HabkeineMail)
 Powerdownload-Werte werden jetzt bei Klick auf einen Download / Quelle im Powerdownloadfeld angezeigt.
- Bug #42 umgesetzt (Danke an dsp2004)
 Partlisten werden nun durch eine Option wahlweise bei Mausklick auf den Download / Quelle oder über den Button "Partliste anzeigen" geholt.
- properties.xml aus den Download-Archiven entfernt
 Beim Update auf eine neuere Version muss diese nun nur noch bei einer Formatänderung erneuert werden.
 Nachteil: Bei einer kompletten Neuinstallation erhält man beim ersten Start eine Fehlermeldung und muss das GUI neu starten.
- Wenn eine neue Version gefunden wird, kann diese nun direkt mit dem Standardbrowser herunter geladen werden
 Der Standardbrowser muss in den Optionen ausgewählt werden.
- Links im Startbereich sind jetzt anklickbar, sofern ein Standardbrowser ausgewählt ist.
- Bug #40 umgesetzt (Danke an hirsch.marcel)
 Incoming-Verzeichnis kann nun für mehrere Downloads gleichzeitig geändert werden.
- PluginOptionenDialog überarbeitet.
- Dialog bei fehlgeschlagenem Verbindungsversuch überarbeitet.
- Menüpunkt zum Beenden des Core auf vielfachen Wunsch an separate Stelle verschoben.
- Bug #17 gefixt (Danke an HabkeineMail)
 Partlisten von einigen wenigen DownloadSourcen wurden bei Bedarf nicht geholt.
- sonstige Kleinigkeiten.

**0.52.1**

- Plugin-Entwickler können nun ein JPanel für Optionen implementieren, welches ggf. im Plugin-Reiter der Optionen aufgerufen werden kann..
- Plugin-Entwickler können nun Objekte direkt mittels ID vom Core erfragen (Danke an webhamster).
- Bug #19 gefixt (Danke an dsp2004)
 Nullpointer behoben.

**0.51.2**

- Bug #14 umgesetzt (Danke an Dragonne)
 Es konnte zu einem Fehler kommen, wenn gleichzeitig zwei Instanzen des GUIs liefen.
- Der Core kann jetzt übers GUI beendet werden.
- Downloads können nun umbenannt werden.
- Das Zielverzeichnis für einen Download (Incoming-Unterverzeichnis) kann nun geändert werden.
- Überprüfung auf gültige Javaversion eingebaut
 Es wird mindestens 1.4 benötigt (empfohlen 1.4.2).
- Suchanzeige korrigiert
 Es kann passieren, dass nicht alle gefundenen Suchergebnisse beim Core ankommen, die Ausgabe wurde entsprechend korrigiert.
- Bug #8 umgesetzt (Danke an finn)
 Downloadlinks kann man nun auch direkt in der Downloadtabelle per Kontexmenü erzeugen.
 Ich wollte es eigentlich nicht umsetzen, da ich die Funktion an dieser Stelle für falsch platziert erachte, doch da der Wunsch bei vielen Benutzern bestand, hab ich es nun doch eingebaut.
 In die Uploadtabelle werde ich es definitiv NICHT einbauen.
- Bug #10 fixed (Danke an muhviestarr)
 Wenn man keine Downloads hat, steht nun nicht mehr "bitte warten" in der Downloadtabelle.

**0.51.1**

- Downloadlinks werden jetzt in ISO-8859-1 an den Core übertragen.
- Versionupdateinformation geändert
 Über die Optionen kann nun gewählt werden, ob man nur bei neuen Versionen (0.51.1), wichtigen Änderungen (0.51.1) oder sogar bei kosmetischen Korrekturen (0.51.1) benachrichtigt wird.
 Standard ist Benachrichtigung bei wichtigen Änderungen.
- Bug #1 fixed (Danke an muhviestarr)
 Look & Feel stimmt nun auch beim Verbindungsdialog.
- Bug #2 fixed (Danke an muhviestarr)
 Taskbareintrag für den Splashscreen und den Verbindungsdialog eingebaut.
- Bug #4 fixed (Danke an muhviestarr)
 Shareanzeige bei Prioritaetenaenderung gefixt.
- Dateigrößen in der Sharetabelle werden nun korrekt ausgegeben (Danke an schnigger und TuxHomer).
- Nullpointer behoben der auftrat, wenn der verbundene Server keinen Namen hat (Danke an paderborner).

**0.50**

- Logging kann nun komplett deaktiviert werden (Danke an muhviestarr).
- Im Verbindungsfenster geht nun ein einfaches <Enter> (Danke an muhviestarr).
- Legende für die Servertabelle eingebaut (Danke an muhviestarr).
- Text von Netzwerk, Neuigkeiten und Nachrichten ist nun auch schwarz (Danke an muhviestarr).
- Die Überschrift "Warnungen" auf der Startseite wird nun ausgeblendet, wenn es keine Warnungen gibt (Danke an muhviestarr).
- Gridlines werden nun in der Servertabelle nicht mehr angezeigt (Danke an muhviestarr).
- Splashscreen wird nun früher angezeigt (Danke an muhviestarr).
- Bug in der Partliste behoben.
- Dau-Button zum Anzeigen der Partliste eingebaut.
- Bug der Tableheader der Share- und der Uploadtabelle behoben (Danke an muhviestarr).

**0.49**

- Bug bei der Wiedergabe von Sounds korrigiert (Danke an mrbond).
 Sounddevice wird nun nach Ausgabe eines Sounds wieder freigegeben.
- Es kann nun der Link einer gesharten Datei über das Popupmenü der Sharetabelle als UBB-Code in die Ablage kopiert werden.
- Entwicklercreditsanzeige im Infodialog korrigiert.
- Bug im Sharebaum behoben.
- Bug beim Sortieren der Sharetabelle behoben.

**0.48**

- Initialen Aufruf des Sharetabs durch einen Initialisierungsthread beschleunigt.
- In der Downloadtabelle nun ein Warteicon angezeigt, bis erstmalig Daten geholt wurden.
- Verhalten des Popupmenüs der Servertabelle überarbeitet.
- Partliste wird nun nur noch über das PopupMenü geholt.
 Wenn der Downloadtab verlassen wird, wird das Aktualisieren der aktuellen Partliste beendet.

**0.47**

- Sharetabelle auf vielfachen Wunsch komplett überarbeitet.
- Partliste wird erst nach 2 Sekunden Wartezeit geholt (Danke an muhviestarr).
 Wenn innerhalb dieser Zeit auf einen anderen Download.bzw. eine andere Quelle geklickt wird, wird die Wartezeit neu gestartet.
- Suche kann nun GUI-seitig abgebrochen werden.
 Der aktuelle Core (0.29.124.1215) hat an dieser Stelle noch einen Bug, eine Suche wird jedoch nach einiger Zeit automatisch beendet.
- Tabellenspaltenroothandles werden nun in der Downloadtabelle angezeigt (Danke an muhviestarr).
 So weiss auch der letzte Benutzer, dass man auf einen Download klicken kann, um die Quellen zu finden.
- Neuen Downloadstatus "Fehler beim Fertigstellen" und neuen Quellenstatus "Eigenes Limit erreicht" eingebaut.
- Rundungsfehler beim automatischen Powerdownload behoben (Danke an garnichda).

**0.46**

- Bug beim autom. Pwdl behoben, der auftrat, wenn nur eine Datei im Download war.
- Bug im Menü behoben, Auswahl eines Menüpunktes geht nun gewohnt schnell..
- Kleinere optische Korrekturen (Danke an DBZfan)
 z.B. Hintergrundfarben aller Scrollbereiche an ihre Tabelle angepasst.
- Prozentangabe bei Downloads nun auf zwei Nachkommastellen genau (Danke an muhviestarr)
- Parameterübergabe an das GUI geändert
 Mögliche Parameter können per -help angezeigt werden.
 Die reg-Datei muss neu angepasst und importiert werden, da diese ebenfalls modifiziert werden musste.
- diverse andere Bugs behoben

**0.45**

- Links können nun an das GUI übermittelt werden (für den INet-Explorer muss die entsprechende reg-Datei angepasst und importiert und die Windows-Exe verwendet werden).
- Themes sind nun deaktivierbar

**0.44**

- Themes eingebaut (Danke an `LinuxDoc`)
 Passende Themes gibt?s auf https://github.com/l2fprod/javootoo.com/tree/master/plaf/skinlf/themepacks.
- Automatischen Powerdownload eingebaut.
 Verschiedene Arten des autom. Pwdls können in Zukunft durch selbst implementierte Klassen per Combobox ausgewählt werden (nächste Version).

**0.43**

- max.RAM-Anzeige in Memory-Monitor eingebaut (Anzeige oben links nun "reserviert / max allocated").
- Soundicons optisch korrigiert.
- Fehler bei der Soundausgabe bei fehlerhaften Sounddateien (z.B. falsches Format) oder fehlendem Sounddevice behoben.

**0.42**

- Memory-Monitor eingebaut (ja, die Anzeige geht richtig und zeigt den echten RAM-Verbrauch der Anwendung)
- manuellen GarbageCollector bei jeder 30\
- Aktualisierung eingebaut

**0.41**

- Fehler im Pwdl-Textfeld behoben
- Sortieren der Downloadtabelle nach Status eingefügt
- Speicheroptimierungen.

**0.40**

- Soundeffekte für diverse Ereignisse eingefügt.

**0.39**

- Standarduploadpriorität ist im Core noch nicht implementiert und deshalb erstmal wieder aus dem GUI geflogen (Danke an `xcalibur`)
- Buttons zum Ändern der Pwdl-Werte entsprechend dem Standard vertauscht (Danke an `lova`)
- Baum zur Auswahl des Temp- und Incomingordners korrigiert. Bug hat sich erst mit `v0.38` eingeschlichen (Danke an `lova` und `akku`)
