package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.XMLDecoder;
import java.io.File;
import de.applejuicenet.client.shared.ProxyConfiguration;
import org.apache.xerces.impl.dv.util.Base64;

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

  public ProxyConfiguration getProxySettings(){
    String[] proxySettings = new String[4];
    proxySettings[0] = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "ip"});
    if (proxySettings[0].compareTo("")==0)
      return null;
    proxySettings[1] = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "port"});
    proxySettings[2] = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "user"});
    proxySettings[3] = Base64.decode(getFirstAttrbuteByTagName(new String[] {"options", "proxy", "pass"}));
    boolean use = getFirstAttrbuteByTagName(new String[] {"options", "proxy", "use"}).equals("1");
    return new ProxyConfiguration(proxySettings[0], Integer.parseInt(proxySettings[1]), proxySettings[2], proxySettings[3], use);
  }

  public void saveProxy(ProxyConfiguration proxy){
    setAttributeByTagName(new String[] {"options", "proxy", "ip"}, proxy.getIP());
    setAttributeByTagName(new String[] {"options", "proxy", "port"}, Integer.toString(proxy.getPort()));
    setAttributeByTagName(new String[] {"options", "proxy", "user"}, proxy.getUsername());
    setAttributeByTagName(new String[] {"options", "proxy", "pass"}, new String(Base64.encode(proxy.getPassword().getBytes())));
    setAttributeByTagName(new String[] {"options", "proxy", "use"}, (proxy.isProxyUsed()? "1": "0"));
  }
}