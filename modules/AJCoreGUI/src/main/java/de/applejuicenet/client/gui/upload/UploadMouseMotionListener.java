/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload;

import de.applejuicenet.client.gui.components.GuiController;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class UploadMouseMotionListener extends MouseMotionAdapter
{
   private GuiController guiController;
   private int           actionId;

   public UploadMouseMotionListener(GuiController guiController, int actionId)
   {
      this.guiController = guiController;
      this.actionId      = actionId;
   }

   public void mouseDragged(MouseEvent e)
   {
      guiController.fireAction(actionId, e);
   }
}
