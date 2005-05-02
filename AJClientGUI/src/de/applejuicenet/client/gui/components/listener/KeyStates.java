package de.applejuicenet.client.gui.components.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyStates implements KeyListener {

    boolean[] keys = new boolean[256];

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() > 255) {
            return;
        }
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() > 255) {
            return;
        }
        keys[e.getKeyCode()] = false;
    }

    public void keyTyped(KeyEvent e) {
    }

    public boolean isKeyDown(int keycode) {
        if (keycode > 255) {
            return false;
        }
        return keys[keycode];
    }
}
