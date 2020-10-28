package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.DelegationObject;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;

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
