/*
 * Decompiled with CFR 0.150.
 */
package de.tklsoft.gui.controls.calendar;

import de.tklsoft.gui.controls.calendar.FlatButton;
import de.tklsoft.gui.controls.calendar.JMonthPanel;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JCalendarPanel
        extends JPanel
        implements ItemListener,
        ChangeListener {
    public static final int FIRE_EVERYTIME = 1;
    public static final int FIRE_DAYCHANGES = 2;
    private int _listenermode = 1;
    private boolean _updating = false;
    private Calendar _cal;
    private DateFormat _format;
    private Locale _locale;
    private JComboBox _month;
    private JComboBox _year;
    private JMonthPanel _monthPanel;
    private ArrayList<ChangeListener> _changeListener = new ArrayList();
    private boolean _fireingChangeEvent = false;

    public JCalendarPanel() {
        this.createGUI(Calendar.getInstance(), Locale.getDefault(), DateFormat.getDateInstance(2, Locale.getDefault()), true);
    }

    public JCalendarPanel(Calendar cal) {
        this.createGUI(cal, Locale.getDefault(), DateFormat.getDateInstance(2, Locale.getDefault()), true);
    }

    public JCalendarPanel(Locale locale) {
        this.createGUI(Calendar.getInstance(locale), locale, DateFormat.getDateInstance(2, locale), true);
    }

    public JCalendarPanel(Calendar cal, Locale locale) {
        this.createGUI(cal, locale, DateFormat.getDateInstance(2, locale), true);
    }

    public JCalendarPanel(Calendar cal, Locale locale, DateFormat dateFormat) {
        this.createGUI(cal, locale, dateFormat, true);
    }

    public JCalendarPanel(Calendar cal, Locale locale, DateFormat dateFormat, boolean flat) {
        this.createGUI(cal, locale, dateFormat, flat);
    }

    private void createGUI(Calendar cal, Locale locale, DateFormat dateFormat, boolean flat) {
        this._locale = locale;
        this._cal = Calendar.getInstance(locale);
        this._cal.set(5, cal.get(5));
        this._cal.set(2, cal.get(2));
        this._cal.set(1, cal.get(1));
        this._format = dateFormat;
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        this._month = this.createMonth();
        this._month.addItemListener(this);
        this.add((Component)this._month, c);
        this._year = this.createYear();
        this._year.addItemListener(this);
        c.gridwidth = 0;
        this.add((Component)this._year, c);
        c.anchor = 10;
        c.fill = 1;
        c.insets = new Insets(1, 1, 1, 1);
        this._monthPanel = new JMonthPanel(this._cal, this._locale);
        this._monthPanel.addChangeListener(this);
        this.add((Component)this._monthPanel, c);
        c.insets = new Insets(0, 0, 1, 0);
        this.add((Component)this.createButtonPanel(flat), c);
        this._monthPanel.grabFocus();
    }

    private JPanel createButtonPanel(boolean flat) {
        JButton yearRight;
        JButton dayRight;
        JButton today;
        JButton dayLeft;
        JButton yearLeft;
        JPanel buttonpanel = new JPanel();
        if (flat) {
            yearLeft = new FlatButton("<<");
            dayLeft = new FlatButton("<");
            today = new FlatButton("Heute");
            dayRight = new FlatButton(">");
            yearRight = new FlatButton(">>");
        } else {
            yearLeft = new JButton("<<");
            yearLeft.setMargin(new Insets(1, 1, 1, 1));
            dayLeft = new JButton("<");
            dayLeft.setMargin(new Insets(1, 1, 1, 1));
            today = new JButton("Heute");
            today.setMargin(new Insets(2, 2, 2, 2));
            dayRight = new JButton(">");
            dayRight.setMargin(new Insets(1, 1, 1, 1));
            yearRight = new JButton(">>");
            yearRight.setMargin(new Insets(1, 1, 1, 1));
        }
        buttonpanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 0;
        c.insets = new Insets(0, 0, 0, 5);
        yearLeft.setMargin(new Insets(1, 1, 1, 1));
        yearLeft.setToolTipText("Letztes Jahr");
        yearLeft.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                if (JCalendarPanel.this._year.getSelectedIndex() > 0) {
                    int month = JCalendarPanel.this._cal.get(2);
                    JCalendarPanel.this._cal.set(1, JCalendarPanel.this._cal.get(1) - 1);
                    if (JCalendarPanel.this._cal.get(2) != month) {
                        JCalendarPanel.this._cal.set(2, month);
                    }
                    JCalendarPanel.this.setCalendar(JCalendarPanel.this._cal);
                }
            }
        });
        buttonpanel.add((Component)yearLeft, c);
        dayLeft.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                int monthIndex = JCalendarPanel.this._cal.get(2);
                JCalendarPanel.this._cal.set(2, monthIndex - 1);
                if (JCalendarPanel.this._cal.get(2) == monthIndex) {
                    JCalendarPanel.this._cal.set(5, 0);
                }
                JCalendarPanel.this.setCalendar(JCalendarPanel.this._cal);
            }
        });
        dayLeft.setMargin(new Insets(1, 1, 1, 1));
        dayLeft.setToolTipText("N\u00e4chstes Jahr");
        buttonpanel.add((Component)dayLeft, c);
        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = 2;
        c2.weightx = 1.0;
        today.setMargin(new Insets(2, 2, 2, 2));
        today.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                JCalendarPanel.this.setCalendar(Calendar.getInstance());
            }
        });
        buttonpanel.add((Component)today, c2);
        c.insets = new Insets(0, 5, 0, 0);
        dayRight.setMargin(new Insets(1, 1, 1, 1));
        dayRight.setToolTipText("N\u00e4chster Monate");
        dayRight.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                int monthIndex = JCalendarPanel.this._cal.get(2);
                JCalendarPanel.this._cal.set(2, monthIndex + 1);
                if (JCalendarPanel.this._cal.get(2) != (monthIndex + 1) % 12) {
                    JCalendarPanel.this._cal.set(5, 0);
                }
                JCalendarPanel.this.setCalendar(JCalendarPanel.this._cal);
            }
        });
        buttonpanel.add((Component)dayRight, c);
        yearRight.setMargin(new Insets(1, 1, 1, 1));
        yearRight.setToolTipText("N\u00e4chstes Jahr");
        yearRight.addActionListener(new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                if (JCalendarPanel.this._year.getSelectedIndex() < JCalendarPanel.this._year.getItemCount() - 1) {
                    int month = JCalendarPanel.this._cal.get(2);
                    JCalendarPanel.this._cal.set(1, JCalendarPanel.this._cal.get(1) + 1);
                    if (JCalendarPanel.this._cal.get(2) != month) {
                        JCalendarPanel.this._cal.set(2, month);
                    }
                    JCalendarPanel.this.setCalendar(JCalendarPanel.this._cal);
                }
            }
        });
        buttonpanel.add((Component)yearRight, c);
        return buttonpanel;
    }

    private JComboBox createYear() {
        JComboBox<String> year = new JComboBox<String>();
        for (int i = 1900; i <= 2100; ++i) {
            year.addItem("" + i);
        }
        year.setSelectedIndex(this._cal.get(1) - 1900);
        return year;
    }

    private JComboBox createMonth() {
        JComboBox<String> month = new JComboBox<String>();
        SimpleDateFormat format = new SimpleDateFormat("MMMMM", this._locale);
        Calendar currentCal = Calendar.getInstance(this._locale);
        currentCal.set(5, 1);
        for (int i = 0; i < 12; ++i) {
            currentCal.set(2, i);
            currentCal.set(1, this._cal.get(1));
            String myString = format.format(currentCal.getTime());
            month.addItem(myString);
        }
        month.setSelectedIndex(this._cal.get(2));
        return month;
    }

    private void updateCalendar() {
        if (!this._updating) {
            this._updating = true;
            this._cal.set(2, this._month.getSelectedIndex());
            this._cal.set(1, this._year.getSelectedIndex() + 1900);
            this._cal.set(5, this._monthPanel.getSelectedDayOfMonth());
            this._monthPanel.setCalendar(this._cal);
            this._monthPanel.grabFocus();
            this._updating = false;
        }
    }

    public Calendar getCalendar() {
        this.updateCalendar();
        return this._cal;
    }

    public void setCalendar(Calendar cal) {
        this._updating = true;
        this._cal.set(5, cal.get(5));
        this._cal.set(2, cal.get(2));
        this._cal.set(1, cal.get(1));
        this._monthPanel.setCalendar(this._cal);
        this._year.setSelectedIndex(this._cal.get(1) - 1900);
        this._month.setSelectedIndex(this._cal.get(2));
        this._monthPanel.grabFocus();
        this._updating = false;
    }

    public String toString() {
        this.updateCalendar();
        return this._format.format(this._cal.getTime());
    }

    public String toString(DateFormat format) {
        this.updateCalendar();
        return format.format(this._cal.getTime());
    }

    public void itemStateChanged(ItemEvent e) {
        this.updateCalendar();
        if (this._listenermode == 1) {
            this.fireChangeEvent();
        }
    }

    public void stateChanged(ChangeEvent e) {
        this.updateCalendar();
        this.fireChangeEvent();
    }

    public void addChangeListener(ChangeListener listener) {
        this._changeListener.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        this._changeListener.remove(listener);
    }

    public ChangeListener[] getChangeListener() {
        return (ChangeListener[])this._changeListener.toArray();
    }

    protected void fireChangeEvent() {
        if (!this._fireingChangeEvent) {
            this._fireingChangeEvent = true;
            ChangeEvent event = new ChangeEvent(this);
            for (ChangeListener cl : this._changeListener) {
                cl.stateChanged(event);
            }
            this._fireingChangeEvent = false;
        }
    }

    public void setListenerModus(int mode) {
        this._listenermode = mode;
    }

    public void setEnabled(boolean enabled) {
        this._month.setEnabled(enabled);
        this._year.setEnabled(enabled);
        this._monthPanel.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return this._month.isEnabled();
    }

    public DateFormat getDateFormat() {
        return this._format;
    }
}

