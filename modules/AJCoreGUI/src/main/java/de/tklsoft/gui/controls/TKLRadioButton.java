package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.DelegationObject;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JRadioButton;

public class TKLRadioButton extends JRadioButton {

   public TKLRadioButton(String schluesselname) {
      super(schluesselname);
      this.init();
   }

   private void init() {
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLRadioButton.this.getParent(), keyEvent);
         }
      });
   }
}
