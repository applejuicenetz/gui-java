package de.tklsoft.gui.controls;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

public class TKLFrame extends JFrame {

   private WindowListener closeWindowListener;


   public TKLFrame() {
      this("");
   }

   public TKLFrame(String title) {
      super(title);
      this.closeWindowListener = new WindowAdapter() {
         public void windowClosing(WindowEvent e) {
            TKLFrame.this.closeFrame(0);
         }
      };
      this.addWindowListener(this.closeWindowListener);
   }

   protected void closeFrame(int i) {
      System.exit(i);
   }

   public void enableCloseWindowListener(boolean enable) {
      if(enable) {
         this.addWindowListener(this.closeWindowListener);
      } else {
         this.removeWindowListener(this.closeWindowListener);
      }

   }
}
