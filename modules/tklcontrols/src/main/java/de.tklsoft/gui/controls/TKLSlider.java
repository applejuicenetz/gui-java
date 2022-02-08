package de.tklsoft.gui.controls;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TKLSlider extends JSlider {

   public TKLSlider(int min, int max, int value) {
      super(min, max, value);
      this.init();
   }

   private void init() {
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLSlider.this.getParent(), keyEvent);
         }
      });
   }
}
