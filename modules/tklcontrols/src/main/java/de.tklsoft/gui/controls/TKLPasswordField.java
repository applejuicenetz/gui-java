package de.tklsoft.gui.controls;

import de.tklsoft.gui.layout.Synchronizable;
import de.tklsoft.gui.layout.Synchronizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TKLPasswordField extends JPasswordField implements Synchronizable {

   private Synchronizer synchronizer = null;
   private boolean dirty = false;


   public TKLPasswordField() {
      this.init();
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void resetDirtyFlag() {
      this.dirty = false;
   }

   private void init() {
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            TKLPasswordField.this.dirty = true;
            DelegationObject.notifyPapas(TKLPasswordField.this.getParent(), keyEvent);
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
