package de.tklsoft.gui.controls.calendar;

import de.tklsoft.gui.controls.calendar.DayLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JMonthPanel extends JPanel {

   private boolean _enabled = true;
   private Calendar _cal;
   private Locale _locale;
   private ArrayList _days;
   private ArrayList _changeListener = new ArrayList();
   private boolean _fireingChangeEvent = false;
   public static final Color BACKGROUND_COLOR = UIManager.getColor("TextField.background");
   public static final Color FONT_COLOR = UIManager.getColor("TextField.foreground");
   public static final Color SELECTED_BACKGROUND_COLOR = UIManager.getColor("TextField.selectionBackground");
   public static final Color SELECTED_FONT_COLOR = UIManager.getColor("TextField.selectionForeground");
   public static final Color HEADER_BACKGROUND_COLOR = UIManager.getColor("TextField.inactiveForeground");
   public static final Color HEADER_FONT_COLOR = UIManager.getColor("TextField.inactiveBackground");


   public JMonthPanel() {
      this.init(Calendar.getInstance(), Locale.getDefault());
   }

   public JMonthPanel(Calendar cal) {
      this.init(cal, Locale.getDefault());
   }

   public JMonthPanel(Locale locale) {
      this.init(Calendar.getInstance(locale), locale);
   }

   public JMonthPanel(Calendar cal, Locale locale) {
      this.init(cal, locale);
   }

   private void init(Calendar cal, Locale loc) {
      this._cal = Calendar.getInstance(loc);
      this._cal.set(5, cal.get(5));
      this._cal.set(2, cal.get(2));
      this._cal.set(1, cal.get(1));
      this._locale = loc;
      this.createGUI();
   }

   private void createGUI() {
      this.setLayout(new BorderLayout());
      this.add(this.createHeader(), "North");
      this.add(this.createTable(), "Center");
   }

   private JPanel createHeader() {
      JPanel header = new JPanel();
      header.setLayout(new GridLayout(1, 7, 1, 1));
      header.setBackground(HEADER_BACKGROUND_COLOR);
      SimpleDateFormat format = new SimpleDateFormat("E", this._locale);
      Calendar cal = (Calendar)this._cal.clone();
      char[] letters = new char[7];

      int pos;
      for(pos = 0; pos < 7; ++pos) {
         letters[cal.get(7) - 1] = format.format(cal.getTime()).charAt(0);
         cal.set(5, cal.get(5) + 1);
      }

      pos = cal.getFirstDayOfWeek() - 1;

      for(int i = 0; i < 7; ++i) {
         JLabel empty = new JLabel(String.valueOf(letters[pos]));
         ++pos;
         if(pos > 6) {
            pos = 0;
         }

         empty.setHorizontalAlignment(4);
         empty.setForeground(HEADER_FONT_COLOR);
         header.add(empty);
      }

      return header;
   }

   private JPanel createTable() {
      this._days = new ArrayList();
      JPanel table = new JPanel();
      table.setBackground(BACKGROUND_COLOR);
      table.setLayout(new GridLayout(6, 7, 1, 1));
      int position = 0;
      Calendar today = Calendar.getInstance();
      Calendar cal = (Calendar)this._cal.clone();
      cal.set(5, 1);
      int month = cal.get(2);
      int firstDay = cal.get(7);
      if(firstDay == 0) {
         --firstDay;
      } else {
         firstDay -= cal.getFirstDayOfWeek();
      }

      if(firstDay < 0) {
         firstDay += 7;
      }

      while(position < firstDay) {
         JLabel curDay = new JLabel();
         table.add(curDay);
         ++position;
      }

      int var10 = this._cal.get(5);

      while(position < 42 && cal.get(2) == month) {
         boolean empty = false;
         if(cal.get(1) == today.get(1) && cal.get(2) == today.get(2) && cal.get(5) == today.get(5)) {
            empty = true;
         }

         DayLabel day = new DayLabel(cal.get(5), empty, this);
         table.add(day);
         this._days.add(day);
         if(var10 == cal.get(5)) {
            day.setSelected(true);
         }

         ++position;
         cal.set(5, cal.get(5) + 1);
      }

      while(position < 42) {
         JLabel var11 = new JLabel();
         table.add(var11);
         ++position;
      }

      return table;
   }

   public void setCalendar(Calendar cal) {
      this._cal.set(5, cal.get(5));
      this._cal.set(2, cal.get(2));
      this._cal.set(1, cal.get(1));
      this.removeAll();
      this.createGUI();
      this.updateUI();
      DayLabel dayLabel = (DayLabel)this._days.get(cal.get(5) - 1);
      dayLabel.grabFocus();
      this.setBackground(BACKGROUND_COLOR);
   }

   public void grabFocus() {
      super.grabFocus();
      DayLabel dayLabel = (DayLabel)this._days.get(this._cal.get(5) - 1);
      dayLabel.grabFocus();
   }

   public Calendar getCalendar() {
      return this._cal;
   }

   public void setSelectedDayOfMonth(int day) {
      if(this._enabled && day > 0 && day <= this._days.size()) {
         int oldday = this._cal.get(5);
         DayLabel dayLabel = (DayLabel)this._days.get(oldday - 1);
         dayLabel.setSelected(false);
         this._cal.set(5, day);
         dayLabel = (DayLabel)this._days.get(day - 1);
         dayLabel.setSelected(true);
         this.updateUI();
         this.fireChangeEvent();
      }

   }

   public int getSelectedDayOfMonth() {
      return this._cal.get(5);
   }

   public void addChangeListener(ChangeListener listener) {
      this._changeListener.add(listener);
   }

   public void removeChangeListener(ChangeListener listener) {
      this._changeListener.remove(listener);
   }

   public ChangeListener[] getChangeListener() {
      return (ChangeListener[])((ChangeListener[])this._changeListener.toArray());
   }

   protected void fireChangeEvent() {
      if(!this._fireingChangeEvent) {
         this._fireingChangeEvent = true;
         ChangeEvent event = new ChangeEvent(this);
         Iterator i$ = this._changeListener.iterator();

         while(i$.hasNext()) {
            ChangeListener cl = (ChangeListener)i$.next();
            cl.stateChanged(event);
         }

         this._fireingChangeEvent = false;
      }

   }

   public void setEnabled(boolean enabled) {
      this._enabled = enabled;
   }

   public boolean isEnabled() {
      return this._enabled;
   }

}
