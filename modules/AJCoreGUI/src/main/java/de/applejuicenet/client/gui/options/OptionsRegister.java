package de.applejuicenet.client.gui.options;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/OptionsRegister.java,v 1.2 2006/05/03 14:52:00 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public interface OptionsRegister
{
   public Icon getIcon();

   public String getMenuText();

   public void reloadSettings();
}
