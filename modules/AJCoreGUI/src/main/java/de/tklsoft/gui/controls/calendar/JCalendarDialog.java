package de.tklsoft.gui.controls.calendar;

import de.tklsoft.gui.controls.TKLCalendarComboBox;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JCalendarDialog {

   private Component _parentComponent;
   private String _title;
   private String _message;
   private TKLCalendarComboBox _calendarBox;
   private int _optionType;
   private int _messageType;


   public JCalendarDialog(Component parentComponent, String title, String message) {
      this._parentComponent = parentComponent;
      this._title = title;
      this._message = message;
      this._calendarBox = new TKLCalendarComboBox();
      this._optionType = 2;
      this._messageType = 3;
   }

   public JCalendarDialog(Component parentComponent, String title, String message, TKLCalendarComboBox calendarBox) {
      this._parentComponent = parentComponent;
      this._title = title;
      this._message = message;
      this._calendarBox = calendarBox;
      this._optionType = 2;
      this._messageType = 3;
   }

   public JCalendarDialog(Component parentComponent, String title, String message, TKLCalendarComboBox calendarBox, int optionType, int messageType) {
      this._parentComponent = parentComponent;
      this._title = title;
      this._message = message;
      this._calendarBox = calendarBox;
      this._optionType = optionType;
      this._messageType = messageType;
   }

   public JCalendarDialog(Component parentComponent, String title, String message, int optionType, int messageType) {
      this._parentComponent = parentComponent;
      this._title = title;
      this._message = message;
      this._optionType = optionType;
      this._messageType = messageType;
      this._calendarBox = new TKLCalendarComboBox();
   }

   public Calendar getCalendar() {
      return this.getCalendar(0);
   }

   public Calendar getCalendar(int messageOption) {
      return this.showConfirmDialog() == messageOption?this._calendarBox.getCalendar():null;
   }

   private int showConfirmDialog() {
      new JTextField();
      JPanel calPanel = new JPanel();
      FlowLayout layout = new FlowLayout();
      layout.setAlignment(0);
      layout.setHgap(0);
      layout.setVgap(0);
      calPanel.setLayout(layout);
      calPanel.add(this._calendarBox);
      Object[] msg = new Object[]{this._message, calPanel};
      int result = JOptionPane.showConfirmDialog(this._parentComponent, msg, this._title, this._optionType, this._messageType);
      return result;
   }
}
