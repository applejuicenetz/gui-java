package de.applejuicenet.client.gui.memorymonitor;

import de.applejuicenet.client.gui.controller.GuiText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Non-modal dialog "JavaGUI Memory Monitor". Shows the RAM usage of the JavaGUI. */
public class MemoryMonitorDialog
    extends JDialog {

    private MemoryMonitor memoryMonitorPanel;
    private final JLabel note = new JLabel() {
        @Override
        public void updateUI() {
            super.updateUI();
            // The dialog instance is long-lived, so follow theme changes.
            setForeground(UIManager.getColor("Label.disabledForeground"));
        }
    };

    public MemoryMonitorDialog(Dialog parent) {
        super(parent, false);
        init();
    }

    public MemoryMonitorDialog(Frame parent) {
        super(parent, false);
        init();
    }

    private void init() {
        memoryMonitorPanel = new MemoryMonitor();
        note.setFont(note.getFont().deriveFont(note.getFont().getSize2D() - 1f));
        note.setBorder(BorderFactory.createEmptyBorder(6, 14, 10, 14));

        JPanel root = new JPanel(new BorderLayout());
        root.add(memoryMonitorPanel, BorderLayout.CENTER);
        root.add(note, BorderLayout.SOUTH);
        setContentPane(root);

        refreshTexts();
        GuiText.onLanguageChange(getRootPane(), this::refreshTexts);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                memoryMonitorPanel.stopMemoryMonitor();
            }
        });
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setMinimumSize(new Dimension(420, 300));
        setSize(new Dimension(560, 380));
    }

    private void refreshTexts() {
        setTitle(GuiText.text("javagui.memory.title"));
        note.setText(GuiText.text("javagui.memory.note"));
    }

    @Override
    public void setVisible(boolean display) {
        super.setVisible(display);
        if (display) {
            memoryMonitorPanel.startMemoryMonitor();
        } else {
            memoryMonitorPanel.stopMemoryMonitor();
        }
    }
}
