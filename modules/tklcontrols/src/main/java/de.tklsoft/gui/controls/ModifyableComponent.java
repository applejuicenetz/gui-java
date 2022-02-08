package de.tklsoft.gui.controls;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public interface ModifyableComponent {

   void addStatusPropertyChangeListener(PropertyChangeListener var1);

   void removeStatusPropertyChangeListener(PropertyChangeListener var1);

   void addInvalidRule(InvalidRule var1);

   void removeInvalidRule(InvalidRule var1);

   void ignoreInvalidRules(boolean var1);

   void resetValidStatus();

   void fireCheckRules();

   boolean isInvalid();

   void ignoreStatus(StatusHolder.STATUSFLAG var1, boolean var2);

   Object getOldValue();

   void confirmNewValue();

   JComponent getComponent();

   boolean isDirty();

   void resetToOldValue();

   void disableDirtyComponent(boolean var1);
}
