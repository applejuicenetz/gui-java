/*
 * Decompiled with CFR 0.150.
 */
package de.tklsoft.gui.controls;

import de.tklsoft.gui.controls.calendar.JCalendarPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicArrowButton;

public class TKLCalendarComboBox
        extends JPanel
        implements ActionListener,
        AncestorListener,
        ChangeListener,
        SwingConstants {
    private int _popupLocation = 2;
    private boolean _calendarWindowFocusLost = false;
    private Calendar _selected;
    private BasicArrowButton _button;
    private JSpinner _spinner = new JSpinner();
    private JWindow _calendarWindow;
    private JCalendarPanel _calendarPanel;
    private List<ChangeListener> _changeListener = new ArrayList<ChangeListener>();
    private boolean _fireingChangeEvent = false;
    private boolean _changed = false;

    public TKLCalendarComboBox() {
        this._calendarPanel = new JCalendarPanel();
        this.createGUI();
    }

    public TKLCalendarComboBox(Calendar cal) {
        this._calendarPanel = new JCalendarPanel(cal);
        this.createGUI();
    }

    private void createGUI() {
        this._calendarPanel.setListenerModus(2);
        this._selected = (Calendar)this._calendarPanel.getCalendar().clone();
        this._calendarPanel.addChangeListener(this);
        this._calendarPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        this.setLayout(new BorderLayout());
        this._spinner.setModel(new SpinnerDateModel());
        this._spinner.setEditor(new JSpinner.DateEditor(this._spinner, ((SimpleDateFormat)this._calendarPanel.getDateFormat()).toPattern()));
        this._spinner.getModel().setValue(this._selected.getTime());
        this._spinner.setBorder(null);
        ((JSpinner.DefaultEditor)this._spinner.getEditor()).getTextField().addFocusListener(new FocusAdapter(){

            public void focusLost(FocusEvent e) {
                if (!TKLCalendarComboBox.this._calendarPanel.getCalendar().getTime().equals(TKLCalendarComboBox.this._spinner.getModel().getValue())) {
                    Date date = (Date)TKLCalendarComboBox.this._spinner.getModel().getValue();
                    TKLCalendarComboBox.this._selected.setTime(date);
                    TKLCalendarComboBox.this._calendarPanel.setCalendar(TKLCalendarComboBox.this._selected);
                    TKLCalendarComboBox.this.fireChangeEvent();
                }
            }
        });
        this._button = new BasicArrowButton(5);
        Insets insets = new Insets(this._button.getMargin().top, 0, this._button.getMargin().bottom, 0);
        this._button.setMargin(insets);
        this._button.addActionListener(this);
        this._button.setEnabled(true);
        this._button.addFocusListener(new FocusAdapter(){

            public void focusGained(FocusEvent e) {
                JComponent opposite;
                if (e.getOppositeComponent() != null && e.getOppositeComponent() instanceof JComponent && (opposite = (JComponent)e.getOppositeComponent()).getTopLevelAncestor() != TKLCalendarComboBox.this._calendarWindow && !TKLCalendarComboBox.this._calendarWindowFocusLost) {
                    TKLCalendarComboBox.this._calendarWindowFocusLost = false;
                }
            }
        });
        this.add((Component)this._spinner, "Center");
        this.add((Component)this._button, "East");
        this.setBorder(new JTextField().getBorder());
    }

    public Dimension getPreferredSize() {
        return new Dimension(120, super.getPreferredSize().height);
    }

    private void createCalendarWindow() {
        Window ancestor = (Window)this.getTopLevelAncestor();
        this._calendarWindow = new JWindow(ancestor);
        JPanel contentPanel = (JPanel)this._calendarWindow.getContentPane();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(this._calendarPanel);
        ((JComponent)((RootPaneContainer)((Object)ancestor)).getContentPane()).addAncestorListener(this);
        ((JComponent)((RootPaneContainer)((Object)ancestor)).getContentPane()).addMouseListener(new MouseAdapter(){

            public void mouseClicked(MouseEvent e) {
                TKLCalendarComboBox.this.hideCalendar();
            }
        });
        this._calendarWindow.addWindowListener(new WindowAdapter(){

            public void windowDeactivated(WindowEvent e) {
                TKLCalendarComboBox.this.hideCalendar();
            }
        });
        this._calendarWindow.addWindowFocusListener(new WindowAdapter(){

            public void windowLostFocus(WindowEvent e) {
                if (TKLCalendarComboBox.this._button.isSelected()) {
                    TKLCalendarComboBox.this._calendarWindowFocusLost = true;
                }
                TKLCalendarComboBox.this.hideCalendar();
            }
        });
        ancestor.addComponentListener(new ComponentAdapter(){

            public void componentResized(ComponentEvent e) {
                TKLCalendarComboBox.this.hideCalendar();
            }

            public void componentMoved(ComponentEvent e) {
                TKLCalendarComboBox.this.hideCalendar();
            }

            public void componentShown(ComponentEvent e) {
                TKLCalendarComboBox.this.hideCalendar();
            }

            public void componentHidden(ComponentEvent e) {
                TKLCalendarComboBox.this.hideCalendar();
            }
        });
        this._calendarWindow.pack();
    }

    public Calendar getCalendar() {
        return this._calendarPanel.getCalendar();
    }

    public void setTime(long time) {
        Calendar calendar = this._calendarPanel.getCalendar();
        calendar.setTimeInMillis(time);
        this.setCalendar(calendar);
    }

    public void setCalendar(Calendar cal) {
        this._calendarPanel.setCalendar(cal);
        this._spinner.getModel().setValue(this._calendarPanel.getCalendar().getTime());
    }

    public JCalendarPanel getCalendarPanel() {
        return this._calendarPanel;
    }

    public void setPopUpLocation(int location) {
        this._popupLocation = location;
    }

    public int getPopUpLocation() {
        return this._popupLocation;
    }

    public void setVerticalAlignment(int value) {
    }

    public void setHorizontalAlignment(int value) {
        ((JSpinner.DefaultEditor)this._spinner.getEditor()).getTextField().setHorizontalAlignment(value);
    }

    public void actionPerformed(ActionEvent e) {
        if (this._calendarWindow != null && this._calendarWindow.isVisible()) {
            this.hideCalendar();
        } else {
            this.showCalender();
        }
    }

    public void hideCalendar() {
        if (this._calendarWindow.isVisible()) {
            this._calendarWindow.setVisible(false);
            if (!this._calendarPanel.getCalendar().getTime().equals(this._spinner.getModel().getValue())) {
                this._changed = true;
            }
            if (this._changed) {
                this._spinner.getModel().setValue(this._calendarPanel.getCalendar().getTime());
                this._selected = (Calendar)this._calendarPanel.getCalendar().clone();
                this._changed = false;
                this.fireChangeEvent();
            }
        }
    }

    public void showCalender() {
        Window ancestor = (Window)this.getTopLevelAncestor();
        if (this._calendarWindow == null || ancestor != this._calendarWindow.getOwner()) {
            this.createCalendarWindow();
        }
        Date date = (Date)this._spinner.getModel().getValue();
        this._selected.setTime(date);
        this._calendarPanel.setCalendar(this._selected);
        Point location = this.getLocationOnScreen();
        int x = this._popupLocation == 4 ? (int)location.getX() + this._button.getSize().width - this._calendarWindow.getSize().width : (this._popupLocation == 0 ? (int)location.getX() + (this._button.getSize().width - this._calendarWindow.getSize().width) / 2 : (int)location.getX());
        int y = (int)location.getY() + this._button.getHeight();
        Rectangle screenSize = this.getDesktopBounds();
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x + this._calendarWindow.getWidth() > screenSize.width) {
            x = screenSize.width - this._calendarWindow.getWidth();
        }
        if (y + 30 + this._calendarWindow.getHeight() > screenSize.height) {
            y = (int)location.getY() - this._calendarWindow.getHeight();
        }
        this._calendarWindow.setBounds(x, y, this._calendarWindow.getWidth(), this._calendarWindow.getHeight());
        this._calendarWindow.setVisible(true);
    }

    private Rectangle getDesktopBounds() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        Rectangle[] screenDeviceBounds = new Rectangle[gd.length];
        Rectangle desktopBounds = new Rectangle();
        for (int i = 0; i < gd.length; ++i) {
            GraphicsConfiguration gc = gd[i].getDefaultConfiguration();
            screenDeviceBounds[i] = gc.getBounds();
            desktopBounds = desktopBounds.union(screenDeviceBounds[i]);
        }
        return desktopBounds;
    }

    public void ancestorAdded(AncestorEvent event) {
        this.hideCalendar();
    }

    public void ancestorMoved(AncestorEvent event) {
        this.hideCalendar();
    }

    public void ancestorRemoved(AncestorEvent event) {
        this.hideCalendar();
    }

    public void stateChanged(ChangeEvent e) {
        this._changed = true;
        this.hideCalendar();
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

    public void setEnabled(boolean enabled) {
        this._spinner.setEnabled(enabled);
        this._button.setEnabled(enabled);
    }

    public boolean isEnabled() {
        return this._button.isEnabled();
    }

    public int getPopupLocation() {
        return this._popupLocation;
    }

    public void setPopupLocation(int location) {
        this._popupLocation = location;
    }

    public SpinnerDateModel getModel() {
        return (SpinnerDateModel)this._spinner.getModel();
    }

    public void setSpinnerDateModel(SpinnerDateModel model) {
        this._spinner.setModel(model);
    }
}

