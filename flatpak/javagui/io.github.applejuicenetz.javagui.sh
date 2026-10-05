#!/bin/sh
cd /app/share/io.github.applejuicenetz.javagui/ || exit 1
exec java --enable-preview --enable-native-access=ALL-UNNAMED -Djava.net.preferIPv4Stack=true -Dsun.java2d.xrender=false -jar AJCoreGUI.jar "$@"
