package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.DelegationObject;
import de.tklsoft.gui.controls.InvalidRule;
import de.tklsoft.gui.controls.ModifyableComponent;
import de.tklsoft.gui.controls.StatusHolder;
import de.tklsoft.gui.layout.Synchronizable;
import de.tklsoft.gui.layout.Synchronizer;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;

public class TKLComboBox extends JComboBox implements ModifyableComponent, Synchronizable {

   private Synchronizer synchronizer;
   private StatusHolder statusHolder;
   private Object oldValue;


   public TKLComboBox(Object[] data) {
      super(data);
      this.synchronizer = null;
      this.oldValue = null;
      this.init();
   }

   public TKLComboBox() {
      this(new Object[0]);
   }

   private void init() {
      this.statusHolder = new StatusHolder(this, -1);
      this.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            if(e.getStateChange() == 1) {
               TKLComboBox.this.fireCheckRules();
            }

         }
      });
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLComboBox.this.getParent(), keyEvent);
         }
      });
   }

   public void setSelectedItem(Object anObject, boolean ignoreInvalidRules) {
      if(ignoreInvalidRules && !this.statusHolder.shouldIgnoreInvalidRules()) {
         this.ignoreInvalidRules(true);
         super.setSelectedItem(anObject);
         this.ignoreInvalidRules(false);
      } else {
         super.setSelectedItem(anObject);
      }

   }

   public void setSelectedItem(Object anObject) {
      this.setSelectedItem(anObject, false);
   }

   public void addItem(Object anObject) {
      if(this.statusHolder.shouldIgnoreInvalidRules()) {
         super.addItem(anObject);
      } else {
         this.ignoreInvalidRules(true);
         super.addItem(anObject);
         this.ignoreInvalidRules(false);
      }

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

   public void setSynchronizer(Synchronizer synchronizer) {
      this.synchronizer = synchronizer;
   }

   public Dimension getPreferredSize() {
      return this.synchronizer == null?super.getPreferredSize():this.synchronizer.getSize(this);
   }

   public Dimension getNormalSize() {
      return super.getPreferredSize();
   }

   public Object getOldValue() {
      return this.oldValue;
   }

   public void confirmNewValue() {
      this.oldValue = this.getSelectedItem();
      this.statusHolder.fireCheckRules();
   }

   public JComponent getComponent() {
      return this;
   }

   public boolean isDirty() {
      return this.oldValue != this.getSelectedItem();
   }

   public void resetToOldValue() {
      this.setSelectedItem(this.oldValue);
   }

   public void disableDirtyComponent(boolean disable) {
      this.statusHolder.ignoreStatus(StatusHolder.STATUSFLAG.MODIFIED, disable);
      this.statusHolder.fireCheckRules();
   }
}
