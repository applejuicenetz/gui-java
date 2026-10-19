/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.start;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Information;
import de.applejuicenet.client.facade.entity.Server;
import de.applejuicenet.client.facade.shared.NetworkInfo;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import javax.swing.*;
import java.util.Map;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/start/StartController.java,v 1.13 2009/01/28 09:44:09 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */
public class StartController extends GuiController {
    private static StartController instance = null;
    private final StartPanel startPanel;
    private boolean firstChange = true;
    private boolean firewalled = false;
    private String status2Text;
    private String connectionMessageText;
    private NetworkInfo netInfo = null;
    private Information information = null;
    private String firewallWarningText;
    private String noServer = "";

    private StartController() {
        super();
        startPanel = new StartPanel(this);
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public static synchronized StartController getInstance() {
        if (null == instance) {
            instance = new StartController();
        }

        return instance;
    }

    private void init() {
        HyperlinkAdapter hyperlinkAdapter = new HyperlinkAdapter(startPanel.getServerMessagePane());

        startPanel.getServerMessagePane().addHyperlinkListener(hyperlinkAdapter);
        startPanel.getMessagesPane().addHyperlinkListener(hyperlinkAdapter);

        AppleJuiceClient.getAjFacade().addDataUpdateListener(this, DATALISTENER_TYPE.NETINFO_CHANGED);
        AppleJuiceClient.getAjFacade().addDataUpdateListener(this, DATALISTENER_TYPE.INFORMATION_CHANGED);
        LanguageSelector.getInstance().addLanguageListener(this);
    }

    public Value[] getCustomizedValues() {
        return null;
    }

    public void fireAction(int actionId, Object source) {

        // nix
    }

    public JComponent getComponent() {
        return startPanel;
    }

    public void componentSelected() {

        // nix zu tun
    }

    public void componentLostSelection() {

        // nix zu tun
    }

    protected void languageChanged() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();

        noServer = languageSelector.getFirstAttributeByTagName("javagui.mainform.noserver");
        status2Text = languageSelector.getFirstAttributeByTagName("mainform.status.status2");
        StringBuffer temp = new StringBuffer(status2Text);
        int pos;

        if (netInfo != null) {
            pos = temp.indexOf("%d");
            if (pos != -1) {
                temp.replace(pos, pos + 2, netInfo.getAJTotalUsersAsStringWithPoints());
            }

            pos = temp.indexOf("%d");
            if (pos != -1) {
                temp.replace(pos, pos + 2, netInfo.getAJFileCountAsStringWithPoints());
            }

            pos = temp.indexOf("%s");
            if (pos != -1) {
                temp.replace(pos, pos + 2, netInfo.getAJTotalShareWithPoints(0));
            }
        } else {
            pos = temp.indexOf("%d");
            if (pos != -1) {
                temp.replace(pos, pos + 2, "0");
            }

            pos = temp.indexOf("%d");
            if (pos != -1) {
                temp.replace(pos, pos + 2, "0");
            }

            pos = temp.indexOf("%s");
            if (pos != -1) {
                temp.replace(pos, pos + 2, "0 MB");
            }
        }

        startPanel.getLblStatus().setText(temp.toString());
        firewallWarningText = languageSelector.getFirstAttributeByTagName("mainform.firewallwarning.caption");
        if (firewalled) {
            startPanel.getLblWarning().setVisible(true);
            startPanel.getLblWarningIcon().setVisible(true);
            startPanel.getLblFirewallWarning().setText(firewallWarningText);
        } else {
            startPanel.getLblWarning().setVisible(false);
            startPanel.getLblWarningIcon().setVisible(false);
            startPanel.getLblFirewallWarning().setText("");
        }

        connectionMessageText = languageSelector.getFirstAttributeByTagName("mainform.html10");
        temp = new StringBuffer(connectionMessageText);
        pos = temp.indexOf("%s");
        if (pos != -1) {
            temp.replace(pos, pos + 2, noServer);
        }

        pos = temp.indexOf("%d");
        if (pos != -1) {
            Map<Integer, Server> servers = AppleJuiceClient.getAjFacade().getAllServer();
            int count;

            if (servers != null) {
                count = servers.size();
            } else {
                count = 0;
            }

            temp.replace(pos, pos + 2, Integer.toString(count));
        }

        startPanel.getLblConnectionMessage().setText(temp.toString());
        startPanel.getLblNetwork()
                .setText("<html><font><h2>" + languageSelector.getFirstAttributeByTagName("mainform.html7") + "</h2></font></html>");
        startPanel.getLblNews()
                .setText("<html><font><h2>" + languageSelector.getFirstAttributeByTagName("mainform.html13") + "</h2></font></html>");
        startPanel.getLblYourClient()
                .setText("<html><font><h2>" + languageSelector.getFirstAttributeByTagName("mainform.html1") + "</h2></font></html>");
        startPanel.getLblWarning()
                .setText("<html><font><h2>" + languageSelector.getFirstAttributeByTagName("mainform.html15") + "</h2></font></html>");
    }

    protected void contentChanged(DATALISTENER_TYPE type, final Object content) {
        if (type == DATALISTENER_TYPE.NETINFO_CHANGED) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        if (firstChange) {
                            firstChange = false;
                            new MessagesWorker(StartController.this, startPanel.getLblVersion(), startPanel.getMessagesPane()).start();
                        }

                        netInfo = (NetworkInfo) content;
                        StringBuffer temp = new StringBuffer(status2Text);
                        int pos = temp.indexOf("%d");

                        if (pos != -1) {
                            temp.replace(pos, pos + 2, netInfo.getAJTotalUsersAsStringWithPoints());
                        }

                        pos = temp.indexOf("%d");
                        if (pos != -1) {
                            temp.replace(pos, pos + 2, netInfo.getAJFileCountAsStringWithPoints());
                        }

                        pos = temp.indexOf("%s");
                        if (pos != -1) {
                            temp.replace(pos, pos + 2, netInfo.getAJTotalShareWithPoints(0));
                        }

                        startPanel.getLblStatus().setText(temp.toString());
                        if (netInfo.isFirewalled() != firewalled) {
                            firewalled = !firewalled;
                            startPanel.getLblWarning().setVisible(firewalled);
                            startPanel.getLblWarningIcon().setVisible(firewalled);
                            if (firewalled) {
                                startPanel.getLblFirewallWarning().setText(firewallWarningText);
                            } else {
                                startPanel.getLblFirewallWarning().setText("");
                            }

                            startPanel.repaint();
                        }

                        String tmp = netInfo.getWelcomeMessage();

                        if (tmp.compareTo(startPanel.getServerMessagePane().getText()) != 0) {
                            startPanel.getServerMessagePane().setText(tmp);
                            startPanel.getServerMessagePane().setCaretPosition(0);
                        }
                    } catch (Exception e) {
                        logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                    }
                }
            });
        } else if (type == DATALISTENER_TYPE.INFORMATION_CHANGED) {
            SwingUtilities.invokeLater(() -> {
                try {
                    information = (Information) content;
                    StringBuffer temp = new StringBuffer(connectionMessageText);
                    int pos = temp.indexOf("%s");

                    if (pos != -1) {
                        if (information.getConnectionStatus() == Information.CONNECTED) {
                            if (information.getServerName() == null || information.getServerName().length() == 0) {
                                Server server = information.getServer();

                                if (server != null) {
                                    String tmp = server.getHost() + ":" + server.getPort();

                                    temp.replace(pos, pos + 2, tmp);
                                } else {
                                    temp.replace(pos, pos + 2, "?");
                                }
                            } else {
                                temp.replace(pos, pos + 2, information.getServerName());
                            }
                        } else {
                            temp.replace(pos, pos + 2, noServer);
                        }

                        pos = temp.indexOf("%d");
                        if (pos != -1) {
                            temp.replace(pos, pos + 2, Integer.toString(AppleJuiceClient.getAjFacade().getAllServer().size()));
                        }
                    }

                    startPanel.getLblConnectionMessage().setText(temp.toString());
                } catch (Exception e) {
                    logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
                }
            });
        }
    }
}
