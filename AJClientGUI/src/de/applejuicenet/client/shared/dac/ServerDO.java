package de.applejuicenet.client.shared.dac;

import java.text.*;
import java.util.*;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ServerDO {
  private final int id;
  private String host;
  private String name;
  private String port;
  private long timeLastSeen;

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
    if (timeLastSeen == 0) {
      return "";
    }
    else {
      SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
      return formatter.format(new Date(timeLastSeen));
    }
  }

  public boolean equals(Object obj) {
    if (! (obj instanceof ServerDO)) {
      return false;
    }
    if (id == ( (ServerDO) obj).getID()) {
      return true;
    }
    else {
      return false;
    }
  }
}