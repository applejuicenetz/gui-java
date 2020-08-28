/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import de.applejuicenet.client.gui.components.GuiController;

public class DownloadMouseMotionListener extends MouseMotionAdapter
{
   private GuiController guiController;
   private int           actionId;

   public DownloadMouseMotionListener(GuiController guiController, int actionId)
   {
      this.guiController = guiController;
      this.actionId      = actionId;
   }

   public void mouseDragged(MouseEvent e)
   {
      guiController.fireAction(actionId, e);
   }
}
