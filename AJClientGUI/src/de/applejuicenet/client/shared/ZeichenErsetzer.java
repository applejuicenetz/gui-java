package de.applejuicenet.client.shared;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
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