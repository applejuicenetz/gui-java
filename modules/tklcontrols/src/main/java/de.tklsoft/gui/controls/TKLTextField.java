package de.tklsoft.gui.controls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

public class TKLTextField extends JTextField implements ModifyableComponent {

   private StatusHolder statusHolder;
   private String oldValue = "";


   public TKLTextField() {
      this.init();
   }

   public TKLTextField(int columncount) {
      super(columncount);
      this.init();
   }

   public TKLTextField(String text) {
      super(text);
      this.oldValue = text;
      this.init();
   }

   private void init() {
      this.statusHolder = new StatusHolder(this, -1);
      this.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent e) {
            if((e.isMetaDown() || e.isControlDown()) && e.getKeyCode() == KeyEvent.VK_Z) {
               TKLTextField.this.setText(TKLTextField.this.oldValue);
            }
         }
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLTextField.this.getParent(), keyEvent);
            TKLTextField.this.fireCheckRules();
         }
      });
   }

   public void addStatusPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
      this.statusHolder.addStatusPropertyChangeListener(propertyChangeListener);
   }

   public void removeStatusPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
      this.statusHolder.removeStatusPropertyChangeListener(propertyChangeListener);
   }

   public void addInvalidRule(InvalidRule rule) {
      this.statusHolder.addInvalidRule(rule);
   }

   public void removeInvalidRule(InvalidRule rule) {
      this.statusHolder.removeInvalidRule(rule);
   }

   public void ignoreInvalidRules(boolean ignore) {
      this.statusHolder.ignoreInvalidRules(ignore);
   }

   public void ignoreStatus(StatusHolder.STATUSFLAG statusFlag, boolean ignore) {
      this.statusHolder.ignoreStatus(statusFlag, ignore);
   }

   public void resetValidStatus() {
      this.statusHolder.resetValidStatus();
   }

   public void fireCheckRules() {
      this.statusHolder.fireCheckRules();
   }

   public boolean isInvalid() {
      return this.statusHolder.isInvalid();
   }

   public Object getOldValue() {
      return this.oldValue;
   }

   public void confirmNewValue() {
      this.oldValue = this.getText();
      this.statusHolder.fireCheckRules();
   }

   public void resetToOldValue() {
      this.setText(this.oldValue);
   }

   public JComponent getComponent() {
      return this;
   }

   public boolean isDirty() {
      return !this.oldValue.equals(this.getText());
   }

   public void disableDirtyComponent(boolean disable) {
      this.statusHolder.ignoreStatus(StatusHolder.STATUSFLAG.MODIFIED, disable);
      this.statusHolder.fireCheckRules();
   }
}
