package de.applejuicenet.client.gui.plugins;

import java.net.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

import de.applejuicenet.client.gui.*;
import de.applejuicenet.client.gui.listener.*;
import de.applejuicenet.client.shared.exception.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/plugins/PluginConnector.java,v 1.9 2003/08/19 19:27:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: PluginConnector.java,v $
 * Revision 1.9  2003/08/19 19:27:12  maj0r
 * no message
 *
 * Revision 1.8  2003/06/13 22:29:38  maj0r
 * Schnittstelle korrigiert.
 *
 * Revision 1.7  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public abstract class PluginConnector
    extends JPanel
    implements LanguageListener, DataUpdateListener, RegisterI {
  /*Diese Datei sollte nicht verändert werden!
    Um ein Plugin zu erstellen, muss diese Klasse überschrieben werden.
    Die Plugin Klasse muss zwingend wie das jar-File heissen.
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
  public ImageIcon getIcon() throws NoIconAvailableException {
    ImageIcon icon;
    try {
      URL url = getClass().getResource("icon.gif");
      Image img = Toolkit.getDefaultToolkit().getImage(url);
      icon = new ImageIcon();
      icon.setImage(img);
    }
    catch (Exception e) {
      throw new NoIconAvailableException();
    }
    return icon;
  }

  //Wird aufgerufen, wenn der Reiter ausgewählt wird.
  public abstract void registerSelected();

  /*Wird automatisch aufgerufen, wenn die Sprache geändert wurde.
    Ggf. kann an dieser Stelle eine eigene xml-Datei zur Anpassung der eigenen Panels ausgewertet werden*/
  public abstract void fireLanguageChanged();

  /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
    Über den DataManger können diese abgerufen werden.*/
  public abstract void fireContentChanged(int type, Object content);
}