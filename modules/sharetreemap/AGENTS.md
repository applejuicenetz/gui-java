# Share-Treemap

- Eigenständiges Swing-Plugin, geladen über `plugin.properties` und den bestehenden `PluginJarClassLoader`; `icon.gif` ist dafür erforderlich.
- `ShareTree` verarbeitet ausschließlich Pfade und Größen aus Core-Share-Daten. Keine lokalen Dateisystemzugriffe, auch bei Remote-Core.
- Flächen entsprechen aufsummierten Dateigrößen. Dateien mit Größe null werden nicht als Fläche dargestellt. Ordnerfarbe im Dateityp-Modus folgt dem größten enthaltenen Eintrag.
- Upload-Heatmap zählt nur positive Positionsdifferenzen zwischen beobachteten Upload-Samples bei aktivem Treemap-Tab. Erstes Sample, geänderte Upload-Range und Tabwechsel setzen die Basis neu; Datenlücken werden nicht hochgerechnet. Core-Trennung verwirft Aktivität und Share-Anzeige.
- Upload-Polling wird bei Tabselektion aktiviert und beim Verlassen deaktiviert, entsprechend dem bestehenden Upload-Controller. Kein eigener Hintergrund-Poller.
- Oberflächenänderungen auf dem Swing-EDT ausführen. Größensnapshots bleiben unveränderlich.
- Bauen: `mvn -pl modules/sharetreemap -am package`. GUI-Tests brauchen einen Display-Server; auf Linux gegebenenfalls `xvfb-run -a` voranstellen.
- Paket-Loader-Test nach Build mit `-Dtest=PluginJarTest -Dsurefire.failIfNoSpecifiedTests=false -Dtreemap.jar=/absoluter/pfad/sharetreemap.jar` ausführen.
- Maven kopiert `sharetreemap.jar` nach `resources/plugins`; `scripts/package.py prepare` übernimmt dieses Verzeichnis in die Distribution.
