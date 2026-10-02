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

Die Release-Pipeline baut mit Java 25 vier native `jpackage`-Installer (macOS DMG,
Windows EXE jeweils für amd64/aarch64) und zwei Linux-Flatpaks. Alle sechs OS-Pakete
enthalten die Laufzeit. `AJCoreGUI.zip` bleibt als plattformneutrales Paket verfügbar
und benötigt installiertes Java 25. Installer registrieren `ajfsp`-/`web+ajfsp`-Links und AJL-Dateien.
Build-Anleitung: [DEVELOP.md](DEVELOP.md).
