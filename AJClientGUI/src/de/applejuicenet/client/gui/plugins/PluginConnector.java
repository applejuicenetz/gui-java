package de.applejuicenet.client.gui.plugins;

import javax.swing.JPanel;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import javax.swing.ImageIcon;
import java.net.URL;
import java.awt.Toolkit;
import java.awt.Image;
import de.applejuicenet.client.shared.exception.NoIconAvailableException;

/**
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Maj0r
 * @version 1.0
 */

public abstract class PluginConnector extends JPanel implements LanguageListener, DataUpdateListener{
  /*Diese Datei sollte nicht verändert werden!
    Um ein Plugin zu erstellen, muss diese Klasse überschrieben werden.
    Die Plugin Klasse muss zwingend AppleJuicePlugin.java heissen.
    Beim Pluginstart wird automatisch der Standardkonstruktor aufgerufen, alle anderen werden ignoriert.*/


  //Titel, der als Reitertext ausgegeben wird
  public abstract String getTitle();

  //Versions-Nr
  public abstract String getVersion();

  //Man will sich ja schließlich auch verewigen;-)
  public abstract String getAutor();

  //true, wenn das Plugin eine sichtbare Oberfläche haben soll
  public abstract boolean istReiter();

  //Liefert eine Kurzbeschreibung des Plugins zurück.
  public abstract String getBeschreibung();

  /*Das Icon, welches in der Lasche angezeigt werden soll, es muss als icon.gif im package plugins gespeichert
    werden, damit es später an die richtige Stelle im jar-Archiv wandert (ca. 16x16)*/
  public ImageIcon getIcon() throws NoIconAvailableException{
    ImageIcon icon;
    try{
      URL url = getClass().getResource("icon.gif");
      Image img = Toolkit.getDefaultToolkit().getImage(url);
      icon = new ImageIcon();
      icon.setImage(img);
    }
    catch(Exception e){
      throw new NoIconAvailableException();
    }
    return icon;
  }

  /*Wird automatisch aufgerufen, wenn die Sprache geändert wurde.
    Ggf. kann an dieser Stelle eine eigene xml-Datei zur Anpassung der eigenen Panels ausgewertet werden*/
  public abstract void fireLanguageChanged();

  /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
    Über den DataManger können diese abgerufen werden.*/
  public abstract void fireContentChanged();
}