package de.applejuicenet.client.shared;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class ProxyConfiguration {
  private String ip;
  private int port;
  private String username;
  private String password;
  private boolean use;

  public ProxyConfiguration(String ip, int port, String username, String password, boolean use){
    this.ip = ip;
    this.port = port;
    this.username = username;
    this.password = password;
    this.use = use;
  }

  public ProxyConfiguration(){}

  public void setIP(String ip){
    this.ip = ip;
  }

  public void useProxy(boolean use){
    this.use = use;
  }

  public void setPort(int port){
    this.port = port;
  }

  public void setUsername(String username){
    this.username = username;
  }

  public void setPassword(String password){
    this.password = password;
  }

  public String getIP(){
    return ip;
  }

  public int getPort(){
    return port;
  }

  public String getUsername(){
    return username;
  }

  public String getPassword(){
    return password;
  }

  public boolean isProxyUsed(){
    return use;
  }
}