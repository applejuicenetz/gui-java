package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.DelegationObject;
import de.tklsoft.gui.controls.InvalidRule;
import de.tklsoft.gui.controls.ModifyableComponent;
import de.tklsoft.gui.controls.StatusHolder;
import de.tklsoft.gui.layout.Synchronizable;
import de.tklsoft.gui.layout.Synchronizer;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TKLCheckBox extends JCheckBox implements ModifyableComponent, Synchronizable {

   private Synchronizer synchronizer;
   private StatusHolder statusHolder;
   private boolean oldValue;


   public TKLCheckBox(String text, boolean selected) {
      super(text, selected);
      this.synchronizer = null;
      this.oldValue = selected;
      this.init();
   }

   public TKLCheckBox() {
      this("", false);
   }

   public TKLCheckBox(String text) {
      this(text, false);
   }

   private void init() {
      this.statusHolder = new StatusHolder(this, 13);
      this.setBorderPainted(true);
      this.addChangeListener(new ChangeListener() {
         public void stateChanged(ChangeEvent ce) {
            TKLCheckBox.this.fireCheckRules();
         }
      });
      this.addKeyListener(new KeyAdapter() {
         public void keyReleased(KeyEvent keyEvent) {
            super.keyReleased(keyEvent);
            DelegationObject.notifyPapas(TKLCheckBox.this.getParent(), keyEvent);
            TKLCheckBox.this.fireCheckRules();
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
      return new Boolean(this.oldValue);
   }

   public void confirmNewValue() {
      this.oldValue = this.isSelected();
      this.statusHolder.fireCheckRules();
   }

   public JComponent getComponent() {
      return this;
   }

   public boolean isDirty() {
      return this.oldValue != this.isSelected();
   }

   public void resetToOldValue() {
      this.setSelected(this.oldValue);
   }

   public void disableDirtyComponent(boolean disable) {
      this.statusHolder.ignoreStatus(StatusHolder.STATUSFLAG.MODIFIED, disable);
      this.statusHolder.fireCheckRules();
   }
}
