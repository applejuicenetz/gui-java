# appleJuice Client GUI

![](https://img.shields.io/github/release/applejuicenetz/gui-java.svg)
![](https://img.shields.io/github/downloads/applejuicenetz/gui-java/total)
![](https://img.shields.io/github/license/applejuicenetz/gui-java.svg)

![](https://github.com/applejuicenetz/gui-java/workflows/release/badge.svg)

Dieses GUI ist das grafisches Interface (Graphical User Interface) für den appleJuice Core.

## Installation

| Platform 	| Link          	                                                     |
|----------	|-----------------------------------------------------------------------|
| Windows  	| [setup.exe](https://github.com/applejuicenetz/gui-java/releases)   	 |
| macOS    	| [AJcoreGUI.dmg](https://github.com/applejuicenetz/gui-java/releases) 	 |
| Linux    	| [Flatpak-Bundle](https://github.com/applejuicenetz/gui-java/releases) |

## Changelog

Ein aktuelles Changelog befindet sich [hier](CHANGELOG.md)

Die Release-Pipeline baut mit Java 21 vier native `jpackage`-Installer (macOS DMG,
Windows EXE jeweils für amd64/aarch64) und zwei Linux-Flatpaks. Alle sechs OS-Pakete
enthalten die Laufzeit. `AJCoreGUI.zip` bleibt als plattformneutrales Paket verfügbar
und benötigt installiertes Java 21. Installer registrieren `ajfsp`-/`web+ajfsp`-Links und AJL-Dateien.
Build-Anleitung: [AGENTS.md](AGENTS.md).

## Versteckte Parameter

Die nachfolgenden Parameter können nur direkt in den `properties` Dateien geändert werden.

### ajgui.properties

- GUI NEWS URL als `options_news_url`
- Serverliste URL als `options_server_list_url`
- Update-Server Feed als `options_update_server_url`

### rel.properties

Der `Suche nach mehr Informationen` Button im Kontextmenü öffnet eine URL, mit dem dahinterliegenden `ajfsp` Link als GET Parameter.

Dafür muss im `~/appleJuice/gui/` Ordner eine `rel.properties` Datei mit folgender Konfiguration vorhanden sein (wird automatisch angelegt):

```ini
host=https://relinfo.tld/api/ajfsp/?link=%s
```

Das letzte `%s` wird mit dem vollständigen `ajfsp` Link ersetzt (urlencoded).
