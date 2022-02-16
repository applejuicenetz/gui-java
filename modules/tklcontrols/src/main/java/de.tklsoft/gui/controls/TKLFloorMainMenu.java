/*
 * Decompiled with CFR 0.150.
 */
package de.tklsoft.gui.controls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class TKLFloorMainMenu {
    private final JButton northButton;
    private final JButton southButton;
    private final MenuPanel menuPanel;
    private final String name;
    private final TKLFloorTab parent;

    public TKLFloorMainMenu(TKLFloorTab parent, String name) {
        this.name = name.toLowerCase();
        this.parent = parent;
        this.northButton = new JButton(name);
        this.northButton.setBorderPainted(false);
        this.northButton.setBackground(Color.LIGHT_GRAY);
        this.southButton = new JButton(name);
        this.southButton.setBorderPainted(false);
        this.southButton.setBackground(Color.LIGHT_GRAY);
        MenuButtonListener actionListener = new MenuButtonListener(name, this);
        this.northButton.addActionListener(actionListener);
        this.southButton.addActionListener(actionListener);
        this.menuPanel = new MenuPanel();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String newName) {
        this.northButton.setText(newName);
        this.southButton.setText(newName);
    }

    JButton getNorthButton() {
        return this.northButton;
    }

    JButton getSouthButton() {
        return this.southButton;
    }

    MenuPanel getMenuPanel() {
        return this.menuPanel;
    }

    public TklFloorButton addIcon(String name, Icon icon, ActionListener actionListener) {
        if (this.menuPanel != null) {
            return this.menuPanel.addButton(name, icon, actionListener);
        }
        return null;
    }

    private class MenuPanel
            extends JPanel {
        private final GridBagConstraints gridconst = new GridBagConstraints();
        private int currentMaxY = -1;
        private final HashMap<String, Object> buttons = new HashMap<String, Object>();

        public MenuPanel() {
            this.setLayout(new GridBagLayout());
            this.gridconst.anchor = 11;
            this.gridconst.fill = 2;
            this.gridconst.insets = new Insets(0, 0, 0, 0);
            this.gridconst.weightx = 1.0;
            this.gridconst.gridx = 0;
            this.gridconst.weighty = 1.0;
            this.gridconst.gridy = 251;
            this.add(new JLabel(), this.gridconst);
            this.gridconst.weighty = 0.0;
        }

        void renameButton(String oldValue, String newValue) {
            Object obj = this.buttons.get(oldValue);
            if (obj != null) {
                this.buttons.remove(oldValue);
                this.buttons.put(newValue, obj);
            }
        }

        public TklFloorButton addButton(String name, Icon icon, ActionListener actionListener) {
            if (name == null || name.length() == 0) {
                throw new RuntimeException("Ung\u00fcltiger Name!");
            }
            if (icon == null) {
                throw new RuntimeException("Ung\u00fcltiges Icon!");
            }
            if (actionListener == null) {
                throw new RuntimeException("Ung\u00fcltiger ActionListener!");
            }
            if (this.buttons.containsKey(name.toLowerCase())) {
                throw new RuntimeException("Buttonname '" + name + "' bereits definiert!");
            }
            ButtonPanel buttonPanel = new ButtonPanel(name, icon, actionListener);
            this.buttons.put(name, buttonPanel);
            if (this.currentMaxY >= 249) {
                throw new RuntimeException("Maximal 250 Men\u00fceintr\u00e4ge m\u00f6glich!");
            }
            ++this.currentMaxY;
            this.gridconst.gridy = this.currentMaxY;
            this.add(buttonPanel, this.gridconst);
            return new TklFloorButton(this, buttonPanel);
        }
    }

    private class MenuButtonListener implements ActionListener {
        private final TKLFloorMainMenu tKLFloorMainMenu;

        public MenuButtonListener(String name, TKLFloorMainMenu tKLFloorMainMenu2) {
            String name1 = name.toLowerCase();
            this.tKLFloorMainMenu = tKLFloorMainMenu2;
        }

        public void actionPerformed(ActionEvent e) {
            TKLFloorMainMenu.this.parent.showMenu(this.tKLFloorMainMenu);
        }
    }

    private class ButtonPanel extends JPanel {
        private final JLabel buttonLabel;

        ButtonPanel(String name, Icon icon, ActionListener actionListener) {
            super(new GridBagLayout());
            JLabel button = new JLabel(icon);
            button.setPreferredSize(new Dimension(48, 48));
            button.setMinimumSize(new Dimension(48, 48));
            button.addMouseListener(new MouseOverAdapter(actionListener, name));
            button.setBorder(BorderFactory.createLineBorder(button.getBackground()));
            GridBagConstraints gridconstint = new GridBagConstraints();
            gridconstint.anchor = 11;
            gridconstint.fill = 2;
            gridconstint.insets = new Insets(10, 5, 0, 5);
            gridconstint.weightx = 1.0;
            gridconstint.weighty = 0.0;
            gridconstint.gridx = 0;
            this.add(new JLabel(), gridconstint);
            gridconstint.weightx = 0.0;
            gridconstint.gridx = 1;
            this.add(button, gridconstint);
            gridconstint.gridx = 2;
            gridconstint.weightx = 1.0;
            this.add(new JLabel(), gridconstint);
            gridconstint.weightx = 0.0;
            gridconstint.gridx = 0;
            gridconstint.gridy = 1;
            gridconstint.gridwidth = 3;
            gridconstint.insets.top = 0;
            this.buttonLabel = new JLabel(name, SwingConstants.CENTER);
            this.add(this.buttonLabel, gridconstint);
        }

        String getButtonText() {
            return this.buttonLabel.getText();
        }

        void setButtonText(String newText) {
            this.buttonLabel.setText(newText);
        }
    }

    private class MouseOverAdapter extends MouseAdapter {
        private final ActionListener actionListener;
        private final String name;

        public MouseOverAdapter(ActionListener actionListener, String name) {
            this.actionListener = actionListener;
            this.name = name;
        }

        public void mouseEntered(MouseEvent e) {
            JLabel source = (JLabel)e.getSource();
            source.setBorder(BorderFactory.createLineBorder(Color.black));
        }

        public void mouseClicked(MouseEvent e) {
            JLabel source = (JLabel)e.getSource();
            if (this.actionListener != null) {
                this.actionListener.actionPerformed(new ActionEvent(source, 0, this.name));
            }
        }

        public void mouseExited(MouseEvent e) {
            JLabel source = (JLabel)e.getSource();
            source.setBorder(BorderFactory.createLineBorder(source.getBackground()));
        }
    }

    public class TklFloorButton {
        private final ButtonPanel buttonPanel;
        private final MenuPanel parent;

        TklFloorButton(MenuPanel parent, ButtonPanel buttonPanel) {
            this.buttonPanel = buttonPanel;
            this.parent = parent;
        }

        public void setText(String newText) {
            this.parent.renameButton(this.buttonPanel.getButtonText(), newText);
            this.buttonPanel.setButtonText(newText);
        }
    }
}

