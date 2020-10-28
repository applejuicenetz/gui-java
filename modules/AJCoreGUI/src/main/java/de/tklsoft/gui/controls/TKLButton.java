package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.DelegationObject;
import de.tklsoft.gui.layout.Synchronizable;
import de.tklsoft.gui.layout.Synchronizer;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JButton;

public class TKLButton extends JButton implements Synchronizable {

   private Synchronizer synchronizer;


   public TKLButton(String text, Icon icon) {
      super(text, icon);
      this.synchronizer = null;
      this.init();
   }

   public TKLButton(String text) {
      this(text, (Icon)null);
   }

   public TKLButton(Icon icon) {
      this("", icon);
   }

   public TKLButton() {
      this("", (Icon)null);
   }

   private void init() {
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLButton.this.getParent(), keyEvent);
         }
      });
   }

   public void setSynchronizer(Synchronizer synchronizer) {
      this.synchronizer = synchronizer;
   }

   public Dimension getPreferredSize() {
      return this.synchronizer == null?super.getPreferredSize():this.synchronizer.getSize(this);
   }

   public Dimension getNormalSize() {
      return super.getPreferredSize();
   }
}
