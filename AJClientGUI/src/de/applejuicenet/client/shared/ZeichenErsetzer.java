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
  public static String korrigiereUmlaute(String text){
    if (text==null)
      return "";
    String result = text.replaceAll("&uuml;", "ü");
    result = result.replaceAll("&Uuml;", "Ü");
    result = result.replaceAll("&auml;", "ä");
    result = result.replaceAll("&Auml;", "Ä");
    result = result.replaceAll("&ouml;", "ö");
    result = result.replaceAll("&Ouml;", "Ö");
    result = result.replaceAll("&szlig;", "ß");
    result = result.replaceAll("&amp;", "&");
    result = result.replaceAll("&lt;", "<");
    result = result.replaceAll("&gt;", ">");
    return result;
  }
}