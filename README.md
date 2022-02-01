# appleJuice Client GUI

![](https://img.shields.io/github/release/applejuicenetz/gui-java.svg)
![](https://img.shields.io/github/downloads/applejuicenetz/gui-java/total)
![](https://img.shields.io/github/license/applejuicenetz/gui-java.svg)

![](https://github.com/applejuicenetz/gui-java/workflows/release/badge.svg)

![](https://github.com/applejuicenetz/gui-java/actions/workflows/snapcraft.yml/badge.svg)
![](https://snapcraft.io/applejuice-gui/badge.svg)

Dieses GUI ist das grafisches Interface (Graphical User Interface) für den appleJuice Core.

## Installation

### Windows

Bitte das Setup von [hier](https://github.com/applejuicenetz/gui-java//releases) oder die `AJCoreGUI.zip` von [hier](https://github.com/applejuicenetz/gui-java/releases) nehmen.

### macOS

- die `AJCoreGUI.dmg` von [hier](https://github.com/applejuicenetz/gui-java/releases) nehmen

### Linux

Für Linux wurde eine `snap` Paket zusammengestellt.

[![Installieren vom Snap Store](https://snapcraft.io/static/images/badges/de/snap-store-white.svg)](https://snapcraft.io/applejuice-gui)

## Themes

Das GUI hat einen Theme Support, weitere Themes gibt es [hier](https://github.com/l2fprod/javootoo.com/tree/master/plaf/skinlf/themepacks)

## Changelog

Ein aktuelles Changelog befindet sich [hier](CHANGELOG.md)

## mehr Informationen Button

Der `Suche nach mehr Informationen` Button im Kontextmenü öffnet eine URL, mit dem dahinterliegenden `ajfsp` Link als GET Parameter.

Dafür muss im `~/appleJuice/gui/` Ordner eine `rel.properties` Datei mit folgender Konfiguration vorhanden sein (wird automatisch angelegt):

```ini
host=https://relinfo.tld/api/ajfsp/?link=%s
```

Das letzte `%s` wird mit dem vollständigen `ajfsp` Link ersetzt (urlencoded).
