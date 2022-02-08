package de.tklsoft.gui.controls;

import javax.swing.*;
import java.awt.*;

public class TKLDesktopPane extends JDesktopPane {

   private int x = 20;
   private int xModifier = 0;
   private int y = 20;
   private Dimension dim = null;
   private int width;
   private int height;


   public Rectangle getPreferredPanelRectangle() {
      if(this.dim == null) {
         this.dim = this.getSize();
         this.width = this.dim.width;
         this.height = this.dim.height;
      } else {
         this.dim = this.getSize();
      }

      if(this.x > this.width / 2 || this.y > this.height / 2) {
         this.xModifier += 20;
         this.x = 20 + this.xModifier;
         this.y = 20;
      }

      this.x += 30;
      this.y += 30;
      return new Rectangle(this.x, this.y, this.width / 3 * 2, this.height / 3 * 2);
   }
}
