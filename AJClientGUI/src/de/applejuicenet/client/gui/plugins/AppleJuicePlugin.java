package de.applejuicenet.client.gui.plugins;

import javax.swing.ImageIcon;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public class AppleJuicePlugin extends PluginConnector {
  /*Dies ist ein Bespielplugin.
    Einfach komplieren, in ein jar-Archiv packen und dieses ins plugins-Verzeichnis kopieren.
    Anschlieﬂend GUI neu starten.*/


  public void fireLanguageChanged(){
    System.out.println("Sprache wurde ge‰ndert");
  }

  public void fireContentChanged(){
    System.out.println("Neue Daten vom Server");
  }

  public String getTitle(){
    return "DemoPlugin2";
  }

  public String getAutor(){
    return "Maj0r";
  }

  public String getBeschreibung(){
    return "Dies ist ein Demo-Plugin. Es soll demonstrieren, wie die Implementierung eines sehr einfachen Plugins auszusehen hat";
  }

  public String getVersion(){
    return "1.0";
  }

  public boolean istReiter(){
    return true;
  }
}