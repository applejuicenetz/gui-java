package de.applejuicenet.client.gui.options;

import javax.swing.Icon;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/OptionsRegister.java,v 1.2 2006/05/03 14:52:00 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public interface OptionsRegister
{
   public Icon getIcon();

   public String getMenuText();

   public void reloadSettings();
}
