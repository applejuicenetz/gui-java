package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.InvalidRule;
import de.tklsoft.gui.controls.ModifyableComponent;
import de.tklsoft.gui.controls.border.ModifyableComponentBorder;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class StatusHolder {

   private final ModifyableComponent parentComponent;
   private HashSet ignoreStati = new HashSet();
   private STATUSFLAG status;
   private ModifyableComponentBorder dirtyBorder;
   private Set listeners;
   private Set rules;
   private boolean ignoreRules;


   StatusHolder(ModifyableComponent component, int borderWidth) {
      this.status = STATUSFLAG.NORMAL;
      this.listeners = new HashSet();
      this.rules = new HashSet();
      this.ignoreRules = true;
      this.parentComponent = component;
      this.dirtyBorder = new ModifyableComponentBorder(component.getComponent(), borderWidth);
      component.getComponent().setBorder(this.dirtyBorder);
      this.addStatusPropertyChangeListener(this.dirtyBorder);
   }

   public void setStatus(STATUSFLAG newStatus) {
      PropertyChangeEvent ev = new PropertyChangeEvent(this.parentComponent, "border", this.status, newStatus);
      this.status = newStatus;
      this.informStatusPropertyChangeListeners(ev);
   }

   public STATUSFLAG getStatus() {
      return this.status;
   }

   public void addStatusPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
      this.listeners.add(propertyChangeListener);
   }

   public void removeStatusPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
      this.listeners.remove(propertyChangeListener);
   }

   private void informStatusPropertyChangeListeners(PropertyChangeEvent ev) {
      Iterator i$ = this.listeners.iterator();

      while(i$.hasNext()) {
         PropertyChangeListener curListener = (PropertyChangeListener)i$.next();
         curListener.propertyChange(ev);
      }

   }

   public void addInvalidRule(InvalidRule rule) {
      this.rules.add(rule);
   }

   public void removeInvalidRule(InvalidRule rule) {
      this.rules.remove(rule);
   }

   public void ignoreInvalidRules(boolean ignore) {
      this.ignoreRules = ignore;
   }

   public boolean shouldIgnoreInvalidRules() {
      return this.ignoreRules;
   }

   public void resetValidStatus() {
      this.setStatus(STATUSFLAG.NORMAL);
   }

   public void ignoreStatus(STATUSFLAG status, boolean ignore) {
      if(ignore) {
         this.ignoreStati.add(status);
      } else {
         this.ignoreStati.remove(status);
      }

   }

   public void fireCheckRules() {
      if(this.ignoreRules) {
         if(!this.ignoreStati.contains(STATUSFLAG.MODIFIED) && this.parentComponent.isDirty()) {
            this.setStatus(STATUSFLAG.MODIFIED);
         } else {
            this.setStatus(STATUSFLAG.NORMAL);
         }

      } else if(this.ignoreStati.contains(STATUSFLAG.INVALID)) {
         this.setStatus(STATUSFLAG.NORMAL);
      } else {
         if(this.isInvalid()) {
            this.setStatus(STATUSFLAG.INVALID);
         } else if(!this.ignoreStati.contains(STATUSFLAG.MODIFIED) && this.parentComponent.isDirty()) {
            this.setStatus(STATUSFLAG.MODIFIED);
         } else {
            this.setStatus(STATUSFLAG.NORMAL);
         }

      }
   }

   public boolean isInvalid() {
      if(this.ignoreRules) {
         return false;
      } else {
         Iterator i$ = this.rules.iterator();

         InvalidRule curRule;
         do {
            if(!i$.hasNext()) {
               return false;
            }

            curRule = (InvalidRule)i$.next();
         } while(!curRule.isInvalid(this.parentComponent));

         return true;
      }
   }

   public static enum STATUSFLAG {

      NORMAL("NORMAL", 0),
      MODIFIED("MODIFIED", 1),
      INVALID("INVALID", 2);
      // $FF: synthetic field
      private static final STATUSFLAG[] $VALUES = new STATUSFLAG[]{NORMAL, MODIFIED, INVALID};


      private STATUSFLAG(String var1, int var2) {}

   }
}
