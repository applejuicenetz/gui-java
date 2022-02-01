# development

### neues Release in dieser Reihenfolge erstellen

```bash
mvn versions:set \
    -DgroupId=de.applejuicenet.client.gui \
    -DartifactId=AJCoreGUI \
    -DgenerateBackupPoms=false \
    -DoldVersion="*" \
    -N versions:update-child-modules \
    -DnewVersion=0.84.0
```

2. Changelog anpassen
3. Änderungen committen und mit der neuen Version taggen
4. die github action erstellt zum Tag das Release mit passenden Assets

