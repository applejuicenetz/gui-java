package de.applejuicenet.client.shared;

import java.io.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/ZeichenErsetzer.java,v 1.9 2004/01/19 16:30:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ZeichenErsetzer.java,v $
 * Revision 1.9  2004/01/19 16:30:19  maj0r
 * Nun unicode-kompatibel.
 *
 * Revision 1.8  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.7  2003/09/09 12:28:15  maj0r
 * Wizard fertiggestellt.
 *
 * Revision 1.6  2003/07/08 20:28:08  maj0r
 * Auf StringBuffer umgebaut.
 *
 * Revision 1.5  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public abstract class ZeichenErsetzer {

    public static String korrigiereUmlaute(String text) {
        if (text == null) {
            return "";
        }
        StringBuffer temp = new StringBuffer(text);
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
            try{
                int decimal = Integer.parseInt(temp.substring(index + 2,
                    index + 6));
                String test = new String(new char[] { (char) decimal});
                temp.replace(index, index + 7, test);
            }
            catch(Exception e){
                temp.replace(index, index + 7, "!Fehler!");
            }
        }
        return temp.toString();
    }

    public static String korrigiereUmlaute(String text, boolean revers) {
        if (text == null) {
            return "";
        }
        if (!revers) {
            return korrigiereUmlaute(text);
        }
        StringBuffer temp = new StringBuffer(text);
        int index;
        while ((index = temp.indexOf("\"")) != -1) {
            temp.replace(index, 1, "\\\"");
        }
        while ((index = temp.indexOf("ü")) != -1) {
            temp.replace(index, 1, "&uuml;");
        }
        while ((index = temp.indexOf("Ü")) != -1) {
            temp.replace(index, 1, "&Uuml;");
        }
        while ((index = temp.indexOf("ä")) != -1) {
            temp.replace(index, 1, "&auml;");
        }
        while ((index = temp.indexOf("Ä")) != -1) {
            temp.replace(index, 1, "&Auml;");
        }
        while ((index = temp.indexOf("ö")) != -1) {
            temp.replace(index, 1, "&ouml;");
        }
        while ((index = temp.indexOf("Ö")) != -1) {
            temp.replace(index, 1, "&Ouml;");
        }
        while ((index = temp.indexOf("ß")) != -1) {
            temp.replace(index, 1, "&szlig;");
        }
        while ((index = temp.indexOf("&")) != -1) {
            temp.replace(index, 1, "&amp;");
        }
        while ((index = temp.indexOf("<")) != -1) {
            temp.replace(index, 1, "&lt;");
        }
        while ((index = temp.indexOf(">")) != -1) {
            temp.replace(index, 1, "&gt;");
        }
        return temp.toString();
    }
}