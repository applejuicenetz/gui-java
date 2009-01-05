/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.tray;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import de.applejuicenet.client.gui.AppleJuiceDialog;

public interface TrayIF
{
   boolean makeTray(Image image, String title, final AppleJuiceDialog dialog, final JMenuItem popupShowHideMenuItem,
                    final Icon zeigenIcon, final Icon versteckenIcon, final JPopupMenu popup);

   void setTextVerstecken(String verstecken);

   void setTextZeigen(String zeigen);
}
