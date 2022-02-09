/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.components.tree;

import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/components/tree/WaitNode.java,v 1.5 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class WaitNode extends DefaultMutableTreeNode implements Node
{
   public Icon getConvenientIcon()
   {
      return IconManager.getInstance().getIcon("warten");
   }

   public String toString()
   {
      String anzeige = LanguageSelector.getInstance().getFirstAttrbuteByTagName("javagui.downloadform.waitnodetext");

      return anzeige;
   }
}
