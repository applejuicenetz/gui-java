# appleJuice Client GUI

Dieses GUI ist das grafisches Interface (Graphical User Interface) für den appleJuice Core.

## Themes
weitere Themes gibts [hier](https://github.com/l2fprod/javootoo.com/tree/master/plaf/skinlf/themepacks)

## Release Info

Im `~/appleJuice/gui/` Ordner muss eine `xrel.properties` Datei mit folgender Konfiguration vorhanden sein:

```ini
host=https://api.ajdomain.tld
port=443
path=/api/ajfsp/%s_%s.json
```
Im `path` ist der erste `%s` die `md5sum` und der zweite `%s` die Größe in `bytes` (wie im `ajfsp` Link).

Vom Core `0.31.149.111` sieht das dann wie folgt aus `65571b8f2f2e1ea2aceb38b8017cf871_318936.json`.

Der dazugehörige `ajfsp Links wäre folgender `ajfsp://file|ajcore-0.31.149.111.jar|65571b8f2f2e1ea2aceb38b8017cf871_318936|31893/`

Es wird folgendes JSON Format und ein Status Code `200` erwartet:

```json5
{
"fsk": false,   // TBD json vervollständigen
}
```
