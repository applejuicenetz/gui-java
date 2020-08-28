# locale Abhängigkeiten

Order für Lokale Abhängigkeiten, welche z.B. nicht im globalen Maven Repository zu finden sind.

### tklcontrols

Die `tklcontrols-1.0.jar` muss vor dem Kompilieren in das lokale `maven` Repository hinzugefügt werden:

```bash
mvn install:install-file -Dfile=tklcontrols-1.0.jar \
                         -DgroupId=de.applejuicenet.client \
                         -DartifactId=tklcontrols \
                         -Dversion=1.0 \
                         -Dpackaging=jar \
                         -DgeneratePom=true
```

Diese wird dann über die `pom.xml` com Core als lokale Abhängigkeit beim `mvn package` eingebunden.
