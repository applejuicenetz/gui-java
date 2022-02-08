package de.tklsoft.gui.controls.calendar;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DayLabel extends FlatButton implements ActionListener {

   private boolean _today;
   private JMonthPanel _monthPanel;
   private int _day;
   private boolean _selected = false;


   public DayLabel(int day, boolean today, JMonthPanel monthPanel) {
      super(Integer.toString(day));
      this._today = today;
      if(this._today) {
         super.setBorder(BorderFactory.createEtchedBorder());
      }

      this.setBackground(JMonthPanel.BACKGROUND_COLOR);
      this.addActionListener(this);
      this._day = day;
      this._monthPanel = monthPanel;
      this.setHorizontalAlignment(4);
   }

   public void setBorder(Border border) {
      if(this._today) {
         super.setBorder(BorderFactory.createEtchedBorder());
      } else {
         super.setBorder(border);
      }

   }

   public void setSelected(boolean selected) {
      this._selected = selected;
      if(this._selected) {
         this.setOpaque(true);
         this.setBackground(JMonthPanel.SELECTED_BACKGROUND_COLOR);
         this.setForeground(JMonthPanel.SELECTED_FONT_COLOR);
      } else {
         this.setOpaque(false);
         this.setBackground(JMonthPanel.BACKGROUND_COLOR);
         this.setForeground(JMonthPanel.FONT_COLOR);
      }

   }

   public boolean isSelected() {
      return this._selected;
   }

   public void actionPerformed(ActionEvent e) {
      this._monthPanel.setSelectedDayOfMonth(this._day);
      this.requestFocus();
   }
}
