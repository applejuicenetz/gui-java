package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/ZeichenErsetzer.java,v 1.5 2003/06/10 12:31:03 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: ZeichenErsetzer.java,v $
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
    String result = text.replaceAll("&uuml;", "ü");
    result = result.replaceAll("&Uuml;", "Ü");
    result = result.replaceAll("&auml;", "ä");
    result = result.replaceAll("&Auml;", "Ä");
    result = result.replaceAll("&ouml;", "ö");
    result = result.replaceAll("&Ouml;", "Ö");
    result = result.replaceAll("&szlig;", "ß");
    result = result.replaceAll("&amp;", "");
    result = result.replaceAll("&lt;", "<");
    result = result.replaceAll("&gt;", ">");
    return result;
  }

  public static String korrigiereUmlaute(String text, boolean revers) {
    if (text == null) {
      return "";
    }
    if (!revers) {
      return korrigiereUmlaute(text);
    }
    String result = text.replaceAll("ü", "&uuml;");
    result = result.replaceAll("Ü", "&Uuml;");
    result = result.replaceAll("ä", "&auml;");
    result = result.replaceAll("Ä", "&Auml;");
    result = result.replaceAll("ö", "&ouml;");
    result = result.replaceAll("Ö", "&Ouml;");
    result = result.replaceAll("ß", "&szlig;");
    result = result.replaceAll("&", "&amp;");
    result = result.replaceAll("<", "&lt;");
    result = result.replaceAll(">", "&gt;");
    return result;
  }
}