package de.applejuicenet.client.gui.components.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyStates implements KeyListener
{
   boolean [] keys = new boolean[256];

   public void keyPressed(KeyEvent e)
   {
      keys[e.getKeyCode()] = true;
   }
   public void keyReleased(KeyEvent e)
   {
      keys[e.getKeyCode()] = false;
   }

   public void keyTyped(KeyEvent e){}

   public boolean isKeyDown(int keycode) {
       return keys[keycode];
   }
}
