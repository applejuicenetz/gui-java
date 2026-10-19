package de.applejuicenet.client.facade.shared;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/CharacterReplacer.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Title: AppleJuice Client-GUI
 * </p>
 * <p>
 * Description: Official GUI for the appleJuice core developed by muhviehstarr
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 */

public abstract class CharacterReplacer {

    public static String fixUmlauts(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder temp = new StringBuilder(text);
        int index;
        while ((index = temp.indexOf("\\\"")) != -1) {
            temp.replace(index, index + 2, "\"");
        }
        while ((index = temp.indexOf("&uuml;")) != -1) {
            temp.replace(index, index + 6, "ü");
        }
        while ((index = temp.indexOf("&Uuml;")) != -1) {
            temp.replace(index, index + 6, "Ü");
        }
        while ((index = temp.indexOf("&auml;")) != -1) {
            temp.replace(index, index + 6, "ä");
        }
        while ((index = temp.indexOf("&Auml;")) != -1) {
            temp.replace(index, index + 6, "Ä");
        }
        while ((index = temp.indexOf("&ouml;")) != -1) {
            temp.replace(index, index + 6, "ö");
        }
        while ((index = temp.indexOf("&Ouml;")) != -1) {
            temp.replace(index, index + 6, "Ö");
        }
        while ((index = temp.indexOf("&szlig;")) != -1) {
            temp.replace(index, index + 7, "ß");
        }
        while ((index = temp.indexOf("&amp;")) != -1) {
            temp.replace(index, index + 5, "");
        }
        while ((index = temp.indexOf("&lt;")) != -1) {
            temp.replace(index, index + 4, "<");
        }
        while ((index = temp.indexOf("&gt;")) != -1) {
            temp.replace(index, index + 4, ">");
        }
        while ((index = temp.indexOf("#&")) != -1) {
            try {
                int decimal = Integer.parseInt(temp.substring(index + 2,
                        index + 6));
                String test = String.valueOf((char) decimal);
                temp.replace(index, index + 7, test);
            } catch (Exception e) {
                temp.replace(index, index + 7, "!Error!");
            }
        }
        return temp.toString();
    }
}
