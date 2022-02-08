package de.tklsoft.gui.controls;

import javax.swing.*;

public interface InvalidRule {

   boolean isInvalid(ModifyableComponent var1);

   public static class NotEmptyInvalidRule implements InvalidRule {

      private static NotEmptyInvalidRule instance = null;


      public static InvalidRule getInstance() {
         if(instance == null) {
            instance = new NotEmptyInvalidRule();
         }

         return instance;
      }

      public boolean isInvalid(ModifyableComponent aComponent) {
         JComponent component = aComponent.getComponent();
         return component instanceof JTextField?((JTextField)component).getText().length() > 0:(component instanceof JComboBox?((JComboBox)component).getSelectedItem() != null:true);
      }

   }

   public static class EmptyInvalidRule implements InvalidRule {

      private static EmptyInvalidRule instance = null;


      public static InvalidRule getInstance() {
         if(instance == null) {
            instance = new EmptyInvalidRule();
         }

         return instance;
      }

      public boolean isInvalid(ModifyableComponent aComponent) {
         JComponent component = aComponent.getComponent();
         return component instanceof JTextField?((JTextField)component).getText().length() == 0:(component instanceof JComboBox?((JComboBox)component).getSelectedItem() == null:true);
      }

   }
}
