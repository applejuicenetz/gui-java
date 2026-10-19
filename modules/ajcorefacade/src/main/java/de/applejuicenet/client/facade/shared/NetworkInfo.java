/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.shared;

/**
 *
 * Copyright: General Public License
 *
 */
public class NetworkInfo implements Cloneable {
    public NetworkInfo snapshot() {
        try {
            return (NetworkInfo) super.clone();
        } catch (CloneNotSupportedException ex) {
            throw new AssertionError(ex);
        }
    }

    private long ajTotalUsers;
    private long ajFileCount;
    private String ajTotalShare;
    private boolean firewalled;
    private String externalIP;
    private int tryConnectToServer;
    private int connectedWithServerId;
    private String welcomeMessage;
    private long connectionTime;

    public NetworkInfo() {
    }

    public NetworkInfo(long ajTotalUsers, long ajFileCount, String ajTotalShare, boolean firewalled, String externalIP,
                       int tryConnectToServer, int connectedWithServerId, String welcomeMessage, long connectionTime) {
        this.ajTotalUsers = ajTotalUsers;
        this.ajFileCount = ajFileCount;
        this.ajTotalShare = ajTotalShare;
        this.firewalled = firewalled;
        this.externalIP = externalIP;
        this.tryConnectToServer = tryConnectToServer;
        this.connectedWithServerId = connectedWithServerId;
        this.connectionTime = connectionTime;
        setWelcomeMessage(welcomeMessage);
    }

    public NetworkInfo(String ajTotalUsers, String ajFileCount, String ajTotalShare, boolean firewalled, String externalIP,
                       int tryConnectToServer, int connectedWithServerId) {
        if (ajTotalUsers == null || ajTotalUsers.length() == 0) {
            this.ajTotalUsers = 0;
        } else {
            this.ajTotalUsers = Long.parseLong(ajTotalUsers);
        }

        if (ajFileCount == null || ajFileCount.length() == 0) {
            this.ajFileCount = 0;
        } else {
            this.ajFileCount = Long.parseLong(ajFileCount);
        }

        this.ajTotalShare = ajTotalShare;
        this.firewalled = firewalled;
        this.externalIP = externalIP;
        this.tryConnectToServer = tryConnectToServer;
        this.connectedWithServerId = connectedWithServerId;
    }

    public void setAjTotalUsers(long ajTotalUsers) {
        this.ajTotalUsers = ajTotalUsers;
    }

    public void setAjFileCount(long ajFileCount) {
        this.ajFileCount = ajFileCount;
    }

    public void setAjTotalShare(String ajTotalShare) {
        this.ajTotalShare = ajTotalShare;
    }

    public void setFirewalled(boolean firewalled) {
        this.firewalled = firewalled;
    }

    public void setExternalIP(String externalIP) {
        this.externalIP = externalIP;
    }

    public void setTryConnectToServer(int tryConnectToServer) {
        this.tryConnectToServer = tryConnectToServer;
    }

    public void setConnectedWithServerId(int connectedWithServerId) {
        this.connectedWithServerId = connectedWithServerId;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        if (!welcomeMessage.toLowerCase().contains(StringConstants.HTML_START)) {
            welcomeMessage = StringConstants.HTML_START + welcomeMessage + StringConstants.HTML_END;
        }

        this.welcomeMessage = welcomeMessage;
    }

    public void setConnectionTime(long connectionTime) {
        this.connectionTime = connectionTime;
    }

    public String getAJTotalShare(long factor) {
        ajTotalShare = ajTotalShare.replace(',', '.');
        double share = Double.parseDouble(ajTotalShare);

        if (share == 0) {
            return StringConstants._0_00_MB;
        }

        if (factor == 0) { // choose automatically
            if (share / 1024 < 1024) {
                factor = 1024;
            } else if (share / 1048576 < 1024) {
                factor = 1048576;
            } else if (share / 1073741824 < 1048576) {
                factor = 1073741824;
            } else {
                factor = 1;
            }
        }

        share = share / factor;
        String result = Double.toString(share);

        if (result.indexOf(StringConstants.POINT) + 3 < result.length()) {
            result = result.substring(0, result.indexOf(StringConstants.POINT) + 3);
        }

        result = result.replace('.', ',');
        if (factor == 1) {
            result += StringConstants.MB;
        } else if (factor == 1024) {
            result += StringConstants.GB;
        } else if (factor == 1048576) {
            result += StringConstants.TB;
        } else if (factor == 1073741824) {
            result += StringConstants.PB;
        } else {
            result += StringConstants.TWO_QUESTIONMARKS;
        }

        return result;
    }

    public String getAJTotalShareWithPoints(long factor) {
        String result = getAJTotalShare(factor);

        return insertPoints(result);
    }

    public boolean isFirewalled() {
        return firewalled;
    }

    public String getExternalIP() {
        return externalIP;
    }

    public long getAJTotalUsers() {
        return ajTotalUsers;
    }

    public String getAJTotalUsersAsString() {
        return Long.toString(ajTotalUsers);
    }

    public String getAJTotalUsersAsStringWithPoints() {
        String result = getAJTotalUsersAsString();

        return insertPoints(result);
    }

    public long getAJFileCount() {
        return ajFileCount;
    }

    public String getAJFileCountAsString() {
        return Long.toString(ajFileCount);
    }

    public String getAJFileCountAsStringWithPoints() {
        return insertPoints(Long.toString(ajFileCount));
    }

    private String insertPoints(String tochange) {
        StringBuilder result = new StringBuilder(tochange);
        int length;

        if (result.indexOf(StringConstants.COMMA) == -1) {
            if (result.indexOf(StringConstants.SPACE) == -1) {
                length = result.length();
            } else {
                length = result.indexOf(StringConstants.SPACE);
            }
        } else {
            length = result.indexOf(StringConstants.COMMA);
        }

        int counter = 0;

        for (int i = length - 1; i > 0; i--) {
            counter++;
            if (counter == 3) {
                counter = 0;
                result.insert(i, '.');
            }
        }

        return result.toString();
    }

    public int getTryConnectToServer() {
        return tryConnectToServer;
    }

    public int getConnectedWithServerId() {
        return connectedWithServerId;
    }

    public String getWelcomeMessage() {
        if (welcomeMessage == null) {
            return StringConstants.EMPTY;
        }

        return welcomeMessage;
    }

    public long getConnectionTime() {
        return connectionTime;
    }
}
