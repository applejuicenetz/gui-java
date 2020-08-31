package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.fassade.shared.ProxySettings;

public interface ProxyManager {
    public ProxySettings getProxySettings();

    public void saveProxySettings(ProxySettings proxySettings);
}