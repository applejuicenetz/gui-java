package de.tklsoft.gui.controls.calendar;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FlatButton extends JButton implements FocusListener, MouseListener {

   private final Border _focus = BorderFactory.createEtchedBorder();
   private final Border _withoutfocus = BorderFactory.createEmptyBorder(2, 2, 2, 2);


   public FlatButton(String text) {
      super(text);
      this.setBorder(this._withoutfocus);
      if(UIManager.getLookAndFeel().getClass().toString().startsWith("class com.jgoodies")) {
         this.setOpaque(false);
      } else {
         this.setContentAreaFilled(false);
      }

      this.addFocusListener(this);
      this.addMouseListener(this);
   }

   public void focusGained(FocusEvent e) {
      this.setBorder(this._focus);
   }

   public void focusLost(FocusEvent e) {
      this.setBorder(this._withoutfocus);
   }

   public void mouseClicked(MouseEvent e) {}

   public void mouseEntered(MouseEvent e) {
      this.setBorder(this._focus);
   }

   public void mouseExited(MouseEvent e) {
      this.setBorder(this._withoutfocus);
   }

   public void mousePressed(MouseEvent e) {}

   public void mouseReleased(MouseEvent e) {}
}
