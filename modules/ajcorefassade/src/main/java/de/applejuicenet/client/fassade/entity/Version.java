/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.entity;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.StringConstants;

import java.util.StringTokenizer;

public abstract class Version {
    public static final int UNKNOWN = 0;
    public static final int WIN32 = 1;
    public static final int LINUX = 2;
    public static final int MACINTOSH = 3;
    public static final int SOLARIS = 4;
    public static final int OS2 = 5;
    public static final int FREEBSD = 6;
    public static final int NETWARE = 7;

    public abstract String getVersion();

    public abstract int getBetriebsSystem();

    public static int getOSTypByOSName(String OSName) {
        int result = -1;

        if (OSName.compareToIgnoreCase(StringConstants.WINDOWS_NT) == 0) {
            result = WIN32;
        } else if (OSName.compareToIgnoreCase(StringConstants.LINUX) == 0) {
            result = LINUX;
        }

        return result;
    }

    public final int compareTo(String versionNr) {
        if (getVersion().compareToIgnoreCase(versionNr) == 0) {
            return 0;
        }

        StringTokenizer token1 = new StringTokenizer(getVersion(), StringConstants.POINT);
        StringTokenizer token2 = new StringTokenizer(versionNr, StringConstants.POINT);

        if (token1.countTokens() != 4 || token2.countTokens() != 4) {
            // alles Mist
            return 0;
        }

        String[] foundCore = new String[4];
        String[] neededCore = new String[4];

        for (int i = 0; i < 4; i++) {
            foundCore[i] = token1.nextToken();
            neededCore[i] = token2.nextToken();
        }

        for (int i = 0; i < 4; i++) {
            if (Integer.parseInt(foundCore[i]) > Integer.parseInt(neededCore[i])) {
                break;
            } else if (Integer.parseInt(foundCore[i]) < Integer.parseInt(neededCore[i])) {
                return -1;
            }
        }

        return 1;
    }

    @Override
    public String toString() {
        return getVersion();
    }

    public final int checkForValidCoreVersion() {
        return compareTo(ApplejuiceFassade.MIN_NEEDED_CORE_VERSION);
    }
}
