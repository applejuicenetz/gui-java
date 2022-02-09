/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload;

import de.applejuicenet.client.gui.components.GuiController;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HeaderPopupListener extends MouseAdapter
{
   private GuiController guiController;
   private int           actionId;

   public HeaderPopupListener(GuiController guiController, int actionId)
   {
      this.guiController = guiController;
      this.actionId      = actionId;
   }

   public void mousePressed(MouseEvent me)
   {
      super.mousePressed(me);
      maybeShowPopup(me);
   }

   public void mouseReleased(MouseEvent e)
   {
      super.mouseReleased(e);
      maybeShowPopup(e);
   }

   private void maybeShowPopup(MouseEvent e)
   {
      if(e.isPopupTrigger())
      {
         guiController.fireAction(actionId, e);
      }
   }
}
