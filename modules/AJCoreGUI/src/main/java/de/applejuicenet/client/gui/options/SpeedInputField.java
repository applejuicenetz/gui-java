package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Locale;

/**
 * Eingabefeld fuer Geschwindigkeiten. Der Core arbeitet in kb/s; angezeigt
 * werden kann wahlweise kb/s oder MB/s (1 MB = 1024 kb). Die gewaehlte
 * Einheit wird je Feld gespeichert.
 */
public class SpeedInputField extends JPanel {
    private static final String[] UNITS = {"kb/s", "MB/s"};

    private final String name;
    private final JTextField text = new JTextField();
    private final JComboBox<String> unit = new JComboBox<>(UNITS);
    private boolean mb;
    private long exactKb;
    private String shown = "";

    public SpeedInputField(String name) {
        super(new BorderLayout(5, 0));
        this.name = name;
        text.setHorizontalAlignment(JTextField.RIGHT);
        mb = OptionsManagerImpl.getInstance().isSpeedUnitMb(name);
        unit.setSelectedIndex(mb ? 1 : 0);
        unit.addActionListener(e -> switchUnit(unit.getSelectedIndex() == 1));
        add(text, BorderLayout.CENTER);
        add(unit, BorderLayout.EAST);
    }

    public void addFocusLostListener(Runnable listener) {
        text.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                listener.run();
            }
        });
    }

    public void setKb(long kb) {
        exactKb = kb;
        shown = format(kb, mb);
        text.setText(shown);
    }

    /** Wert in kb/s; bei ungueltiger Eingabe der zuletzt gueltige Wert. */
    public long getKb() {
        String current = text.getText();
        if (current.equals(shown)) {
            return exactKb;
        }
        long kb = parseKb(current, mb, exactKb);
        setKb(kb);
        return kb;
    }

    private void switchUnit(boolean toMb) {
        if (toMb == mb) {
            return;
        }
        long kb = getKb();
        mb = toMb;
        OptionsManagerImpl.getInstance().setSpeedUnitMb(name, mb);
        setKb(kb);
    }

    static String format(long kb, boolean mb) {
        return mb ? String.format(Locale.ROOT, "%.2f", kb / 1024.0) : Long.toString(kb);
    }

    static long parseKb(String input, boolean mb, long fallback) {
        try {
            double value = Double.parseDouble(input.trim().replace(',', '.'));
            if (value < 0 || Double.isNaN(value) || Double.isInfinite(value)) {
                return fallback;
            }
            return Math.round(mb ? value * 1024 : value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
