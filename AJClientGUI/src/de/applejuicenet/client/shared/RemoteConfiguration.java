package de.applejuicenet.client.shared;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class RemoteConfiguration {
  private String host;
  private boolean use;
  private String oldPassword;
  private String newPassword;

  public RemoteConfiguration(String host, String oldPassword, boolean use){
    this.host = host;
    this.oldPassword = oldPassword;
    this.use = use;
  }

  public RemoteConfiguration(){}

  public void setHost(String host){
    this.host = host;
  }

  public void useRemote(boolean use){
    this.use = use;
  }

  public void setOldPassword(String oldPassword){
    this.oldPassword = oldPassword;
  }

  public void setNewPassword(String newPassword){
    this.newPassword = newPassword;
  }

  public String getHost(){
    return host;
  }

  public String getOldPassword(){
    return oldPassword;
  }

  public String getNewPassword(){
    return newPassword;
  }

  public boolean isRemoteUsed(){
    return use;
  }
}
