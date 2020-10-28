package de.tklsoft.gui.controls;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public abstract class DelegationObject {

   public static void notifyPapas(Component ersterPapa, KeyEvent keyEvent) {
      for(Object moeglicherFrame = ersterPapa; moeglicherFrame != null; moeglicherFrame = ((Component)moeglicherFrame).getParent()) {
         KeyListener[] listeners = ((Component)moeglicherFrame).getKeyListeners();

         for(int i = 0; i < listeners.length; ++i) {
            listeners[i].keyReleased(keyEvent);
         }
      }

   }
}
