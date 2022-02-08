package de.tklsoft.gui.controls;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TKLTextArea extends JTextArea {

   public TKLTextArea(String text) {
      super(text);
      this.init();
   }

   public TKLTextArea() {
      this("");
   }

   private void init() {
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLTextArea.this.getParent(), keyEvent);
         }
      });
   }

   public void enableBorder() {
      this.setBorder(BorderFactory.createEtchedBorder());
   }
}
