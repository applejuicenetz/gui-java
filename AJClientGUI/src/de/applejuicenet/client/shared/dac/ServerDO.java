package de.applejuicenet.client.shared.dac;

import java.text.*;
import java.util.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/dac/Attic/ServerDO.java,v 1.8 2003/11/03 15:18:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ServerDO.java,v $
 * Revision 1.8  2003/11/03 15:18:39  maj0r
 * Optimierungen.
 *
 * Revision 1.7  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.6  2003/08/02 12:03:38  maj0r
 * An neue Schnittstelle angepasst.
 *
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class ServerDO {
    private final int id;
    private String host;
    private String name;
    private String port;
    private long timeLastSeen;
    private boolean connected = false;
    private boolean tryConnect = false;

    public ServerDO(int id, String name, String host, String port, long lastSeen) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.port = port;
        timeLastSeen = lastSeen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public long getTimeLastSeen() {
        return timeLastSeen;
    }

    public void setTimeLastSeen(long timeLastSeen) {
        this.timeLastSeen = timeLastSeen;
    }

    public int getID() {
        return id;
    }

    public String getIDasString() {
        return Integer.toString(id);
    }

    public String getTimeLastSeenAsString() {
        if (timeLastSeen == 0)
        {
            return "";
        }
        else
        {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            return formatter.format(new Date(timeLastSeen));
        }
    }

    public boolean equals(Object obj) {
        if (obj.getClass()!=getClass())
        {
            return false;
        }
        return (id == ((ServerDO) obj).getID());
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isTryConnect() {
        return tryConnect;
    }

    public void setTryConnect(boolean tryConnect) {
        this.tryConnect = tryConnect;
    }
}