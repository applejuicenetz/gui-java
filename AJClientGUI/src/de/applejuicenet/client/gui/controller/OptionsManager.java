package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.XMLDecoder;
import java.io.File;
import de.applejuicenet.client.shared.ProxyConfiguration;
import org.apache.xerces.impl.dv.util.Base64;
import de.applejuicenet.client.shared.RemoteConfiguration;
import java.security.MessageDigest;
import java.security.*;
import de.applejuicenet.client.shared.exception.InvalidPasswordException;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class OptionsManager extends XMLDecoder {
  private static OptionsManager instance = null;

  private OptionsManager(String path){
    super(path);
  }

  public static OptionsManager getInstance(){
    if (instance == null){
      String path = System.getProperty("user.dir") + File.separator + "properties.xml";
      instance = new OptionsManager(path);
    }
    return instance;
  }

  public String getSprache(){
    return getFirstAttrbuteByTagName(new String[] {"options", "sprache"});
  }

  public void setSprache(String sprache){
    setAttributeByTagName(new String[] {"options", "sprache"}, sprache.toLowerCase());
  }

  public RemoteConfiguration getRemoteSettings(){
    String host = getFirstAttrbuteByTagName(new String[] {"options", "remote", "host"});
    String passwort = Base64.decode(getFirstAttrbuteByTagName(new String[] {"options", "remote", "passwort"}));
    boolean use = getFirstAttrbuteByTagName(new String[] {"options", "remote", "use"}).equals("1");
    return new RemoteConfiguration(host, passwort, use);
  }

  public void saveRemote(RemoteConfiguration remote) throws InvalidPasswordException{
    if (!remote.isRemoteUsed()){
      setAttributeByTagName(new String[] {"options", "remote", "use"}, (remote.isRemoteUsed()? "1": "0"));
      return;
    }
    if (!remote.getNewPassword().equalsIgnoreCase("")){
      String altPasswortBase64FromXML = Base64.decode(getFirstAttrbuteByTagName(new String[] {"options", "remote", "passwort"}));
      if (altPasswortBase64FromXML.compareTo(remote.getOldPassword())==0)
        setAttributeByTagName(new String[] {"options", "remote", "passwort"}, new String(Base64.encode(remote.getNewPassword().getBytes())));
      else
        throw new InvalidPasswordException();
    }
    setAttributeByTagName(new String[] {"options", "remote", "host"}, remote.getHost());
    if (remote.isRemoteUsed())
      setAttributeByTagName(new String[] {"options", "remote", "use"}, (remote.isRemoteUsed()? "1": "0"));
  }

  public ProxyConfiguration getProxySettings(){
    String ip = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "ip"});
    String port = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "port"});
    String user = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "user"});
    String passwort = Base64.decode(getFirstAttrbuteByTagName(new String[] {"options", "proxy", "pass"}));
    boolean use = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "use"}).equals("1");
    return new ProxyConfiguration(ip, (port.equalsIgnoreCase(""))? 0 : Integer.parseInt(port), user, passwort, use);
  }

  public void saveProxy(ProxyConfiguration proxy){
    setAttributeByTagName(new String[] {"options", "proxy", "ip"}, proxy.getIP());
    setAttributeByTagName(new String[] {"options", "proxy", "port"}, Integer.toString(proxy.getPort()));
    setAttributeByTagName(new String[] {"options", "proxy", "user"}, proxy.getUsername());
    setAttributeByTagName(new String[] {"options", "proxy", "pass"}, new String(Base64.encode(proxy.getPassword().getBytes())));
    setAttributeByTagName(new String[] {"options", "proxy", "use"}, (proxy.isProxyUsed()? "1": "0"));
  }
}