package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.facade.shared.ProxySettings;

/**
 * Copyright: General Public License
 */

public class ProxyManagerImpl implements ProxyManager{
    private static ProxyManager instance = null;
    private PropertiesManager propertiesManager;

    private ProxyManagerImpl(){
        propertiesManager = PropertiesManager.getInstance();
    }

    public static synchronized ProxyManager getInstance(){
        if (instance==null){
            instance = new ProxyManagerImpl();
        }
        return instance;
    }

    public ProxySettings getProxySettings(){
        return propertiesManager.getProxySettings();
    }

    public void saveProxySettings(ProxySettings proxySettings){
        propertiesManager.saveProxySettings(proxySettings);
    }
}
