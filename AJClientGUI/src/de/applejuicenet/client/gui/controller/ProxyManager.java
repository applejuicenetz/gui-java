package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.shared.ProxySettings;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/controller/ProxyManager.java,v 1.1 2003/09/12 13:19:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ProxyManager.java,v $
 * Revision 1.1  2003/09/12 13:19:26  maj0r
 * Proxy eingebaut, so dass nun immer Infos angezeigt werden koennen.
 * Version 0.30
 *
 *
 */

public interface ProxyManager {
    public ProxySettings getProxySettings();

    public void saveProxySettings(ProxySettings proxySettings);
}
