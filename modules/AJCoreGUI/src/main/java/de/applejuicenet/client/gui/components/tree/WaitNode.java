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
 * Copyright: General Public License
 */
public class WaitNode extends DefaultMutableTreeNode implements Node
{
   public Icon getConvenientIcon()
   {
      return IconManager.getInstance().getIcon("warten");
   }

   public String toString()
   {
      String display = LanguageSelector.getInstance().getFirstAttributeByTagName("javagui.downloadform.waitnodetext");

      return display;
   }
}
