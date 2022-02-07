# versteckte Parameter

Die nachfolgenden Parameter können nur direkt in den `properties` Dateien geändert werden.

## ajgui.properties

- GUI NEWS URL als `options_news_url`
- Serverliste URL als `options_server_list_url`
- Update-Server Feed als `options_update_server_url`

## rel.properties

Der `Suche nach mehr Informationen` Button im Kontextmenü öffnet eine URL, mit dem dahinterliegenden `ajfsp` Link als GET Parameter.

Dafür muss im `~/appleJuice/gui/` Ordner eine `rel.properties` Datei mit folgender Konfiguration vorhanden sein (wird automatisch angelegt):

```ini
host=https://relinfo.tld/api/ajfsp/?link=%s
```

Das letzte `%s` wird mit dem vollständigen `ajfsp` Link ersetzt (urlencoded).
