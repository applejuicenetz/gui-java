/*
 * Created on 02.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.applejuicenet.client.gui.plugins;

import java.awt.Color;
import java.awt.Image;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Zab
 * <p>
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SpeedGraphSettings extends JPanel {

    private javax.swing.JButton btnUpColor = null;
    private javax.swing.JButton btnDownColor = null;
    private javax.swing.JLabel lblUpColor = null;
    private javax.swing.JLabel lblDownColor = null;

    private final javax.swing.JSlider jSlider = null;
    private javax.swing.JSlider JSUpdateTime = null;  //
    private javax.swing.JLabel jLabel = null;
    private javax.swing.JCheckBox jCheckBox = null;
    private javax.swing.JButton btnBGColorBegin = null;
    private javax.swing.JButton btnBGColorEnd = null;
    private javax.swing.JLabel lblBGColorBegin = null;
    private javax.swing.JLabel lblBGColorEnd = null;

    private javax.swing.JLabel LblValue = null;
    private javax.swing.JComboBox CmbBGDirection = null;

    public SpeedGraphPlugin parent = null;

    public SpeedGraphSettings() {
    }

    private void initialize() {
        java.awt.GridBagConstraints consGridBagConstraints19 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints18 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints17 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints16 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints15 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints14 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints13 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints12 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints11 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints10 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints9 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints8 = new java.awt.GridBagConstraints();

        java.awt.GridBagConstraints consGridBagConstraints7 = new java.awt.GridBagConstraints();

        this.setLayout(new java.awt.GridBagLayout());  // Generated
        setSize(455, 300);
        consGridBagConstraints7.gridx = 0;  // Generated
        consGridBagConstraints7.gridy = 0;  // Generated
        consGridBagConstraints7.ipadx = 10;  // Generated
        consGridBagConstraints7.ipady = 0;  // Generated
        consGridBagConstraints7.insets = new java.awt.Insets(10, 25, 10, 25);  // Generated
        consGridBagConstraints7.fill = java.awt.GridBagConstraints.BOTH;  // Generated
        consGridBagConstraints8.gridx = 2;  // Generated
        consGridBagConstraints8.gridy = 0;  // Generated
        consGridBagConstraints8.ipadx = 20;  // Generated
        consGridBagConstraints8.ipady = 0;  // Generated
        consGridBagConstraints8.insets = new java.awt.Insets(5, 0, 12, 40);  // Generated
        consGridBagConstraints8.fill = java.awt.GridBagConstraints.NONE;  // Generated
        consGridBagConstraints9.gridx = 0;  // Generated
        consGridBagConstraints9.gridy = 1;  // Generated
        consGridBagConstraints9.ipadx = 10;  // Generated
        consGridBagConstraints9.ipady = 0;  // Generated
        consGridBagConstraints9.insets = new java.awt.Insets(0, 25, 10, 25);  // Generated
        consGridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;  // Generated
        consGridBagConstraints10.gridx = 2;  // Generated
        consGridBagConstraints10.gridy = 1;  // Generated
        consGridBagConstraints10.ipadx = 20;  // Generated
        consGridBagConstraints10.ipady = 0;  // Generated
        consGridBagConstraints10.insets = new java.awt.Insets(0, 0, 10, 40);  // Generated
        consGridBagConstraints11.gridx = 0;  // Generated
        consGridBagConstraints11.gridy = 7;  // Generated
        consGridBagConstraints11.gridwidth = 3;  // Generated
        consGridBagConstraints11.weightx = 1.0;  // Generated
        consGridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;  // Generated
        consGridBagConstraints11.ipadx = 0;  // Generated
        consGridBagConstraints11.ipady = 0;  // Generated
        consGridBagConstraints11.insets = new java.awt.Insets(2, 25, 50, 25);  // Generated
        consGridBagConstraints12.gridx = 0;  // Generated
        consGridBagConstraints12.gridy = 6;  // Generated
        consGridBagConstraints12.gridwidth = 2;  // Generated
        consGridBagConstraints12.ipadx = 0;  // Generated
        consGridBagConstraints12.ipady = 0;  // Generated
        consGridBagConstraints12.insets = new java.awt.Insets(5, 25, 2, 25);  // Generated
        consGridBagConstraints12.fill = java.awt.GridBagConstraints.NONE;  // Generated
        consGridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;  // Generated
        consGridBagConstraints13.gridx = 0;  // Generated
        consGridBagConstraints13.gridy = 2;  // Generated
        consGridBagConstraints13.ipadx = 70;  // Generated
        consGridBagConstraints13.ipady = -1;  // Generated
        consGridBagConstraints13.insets = new java.awt.Insets(10, 25, 0, 10);  // Generated
        consGridBagConstraints13.anchor = java.awt.GridBagConstraints.CENTER;  // Generated
        consGridBagConstraints14.gridx = 2;  // Generated
        consGridBagConstraints14.gridy = 3;  // Generated
        consGridBagConstraints14.insets = new java.awt.Insets(5, 0, 10, 40);  // Generated
        consGridBagConstraints14.ipadx = 20;  // Generated
        consGridBagConstraints15.gridx = 2;  // Generated
        consGridBagConstraints15.gridy = 4;  // Generated
        consGridBagConstraints15.insets = new java.awt.Insets(0, 0, 10, 40);  // Generated
        consGridBagConstraints15.ipadx = 20;  // Generated
        consGridBagConstraints16.gridx = 0;  // Generated
        consGridBagConstraints16.gridy = 3;  // Generated
        consGridBagConstraints16.insets = new java.awt.Insets(5, 25, 10, 25);  // Generated
        consGridBagConstraints16.fill = java.awt.GridBagConstraints.BOTH;  // Generated
        consGridBagConstraints17.gridx = 0;  // Generated
        consGridBagConstraints17.gridy = 4;  // Generated
        consGridBagConstraints17.insets = new java.awt.Insets(0, 25, 10, 25);  // Generated
        consGridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;  // Generated
        consGridBagConstraints18.gridx = 2;  // Generated
        consGridBagConstraints18.gridy = 6;  // Generated
        consGridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;  // Generated
        consGridBagConstraints18.insets = new java.awt.Insets(5, 0, 2, 25);  // Generated
        consGridBagConstraints19.gridx = 2;  // Generated
        consGridBagConstraints19.gridy = 2;  // Generated
        consGridBagConstraints19.weightx = 1.0;  // Generated
        consGridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;  // Generated
        consGridBagConstraints19.insets = new java.awt.Insets(10, 0, 0, 25);  // Generated
        this.add(getLblUpColor(), consGridBagConstraints7);  // Generated
        this.add(getBtnUpColor(), consGridBagConstraints8);  // Generated
        this.add(getLblDownColor(), consGridBagConstraints9);  // Generated
        this.add(getBtnDownColor(), consGridBagConstraints10);  // Generated
        this.add(getJSUpdateTime(), consGridBagConstraints11);  // Generated
        this.add(getJLabel(), consGridBagConstraints12);  // Generated
        this.add(getJCheckBox(), consGridBagConstraints13);  // Generated
        this.add(getBtnBGColorBegin(), consGridBagConstraints14);  // Generated
        this.add(getBtnBGColorEnd(), consGridBagConstraints15);  // Generated
        this.add(getLblBGColorBegin(), consGridBagConstraints16);  // Generated
        this.add(getLblBGColorEnd(), consGridBagConstraints17);  // Generated
        this.add(getLblValue(), consGridBagConstraints18);  // Generated
        this.add(getCmbBGDirection(), consGridBagConstraints19);  // Generated

    }

    private JLabel getLblUpColor() {
        if (lblUpColor == null) {
            lblUpColor = new JLabel();
            lblUpColor.setText("Upload Color");
            lblUpColor.setOpaque(true);

            lblUpColor.setBackground(parent.getPropertyColor("UploadColor", Color.red));
        }
        return lblUpColor;
    }

    private javax.swing.JButton getBtnUpColor() {
        if (btnUpColor == null) {
            btnUpColor = new javax.swing.JButton();
            btnUpColor.setText("Wählen");
            btnUpColor.setMnemonic(java.awt.event.KeyEvent.VK_W);

            btnUpColor.addActionListener(e -> {
                Color col = JColorChooser.showDialog(getPanel(), "Upload Farbe wählen",
                        parent.getPropertyColor("UploadColor", Color.red));
                if (col != null) {
                    parent.setPropertyColor("UploadColor", col);
                    getLblUpColor().setBackground(col);
                }
            });
        }
        return btnUpColor;
    }

    private JLabel getLblDownColor() {
        if (lblDownColor == null) {
            lblDownColor = new JLabel();
            lblDownColor.setText("Download Color");
            lblDownColor.setOpaque(true);

            lblDownColor.setBackground(parent.getPropertyColor("DownloadColor", Color.blue));
        }
        return lblDownColor;
    }

    private javax.swing.JButton getBtnDownColor() {
        if (btnDownColor == null) {
            btnDownColor = new javax.swing.JButton();
            btnDownColor.setText("Wählen");
            btnDownColor.setMnemonic(java.awt.event.KeyEvent.VK_H);

            btnDownColor.addActionListener(e -> {
                Color col = JColorChooser.showDialog(getPanel(), "Download Farbe wählen",
                        parent.getPropertyColor("DownloadColor", Color.blue));
                if (col != null) {
                    parent.setPropertyColor("DownloadColor", col);
                    getLblDownColor().setBackground(col);
                }
            });
        }
        return btnDownColor;
    }

    public JPanel getPanel() {
        initialize();
        return this;
    }

    /**
     * @return Icon with 18x18 from given color
     */
    private Icon getColorIcon(Color color) {
        Image ico = createVolatileImage(18, 18);

        try {
            ico.getGraphics().setColor(color);
            ico.getGraphics().fillRect(0, 0, 17, 17);
            //System.out.println("Icon drawn");
        } catch (NullPointerException e) {
            //System.out.println("Error in getColorIcon() "); e.printStackTrace();
            //System.out.println("Icon not drawn");
            return null;
        }

        return new ImageIcon(ico);
    }

    private Color chooseColor(Color color) {
        try {
            getBtnUpColor().setIcon(getColorIcon(parent.getPropertyColor("UploadColor", Color.red)));
            getBtnDownColor().setIcon(getColorIcon(parent.getPropertyColor("DownloadColor", Color.blue)));
        } catch (NullPointerException e) {
            parent.setPropertyColor("UploadColor", Color.red);
            parent.setPropertyColor("DownloadColor", Color.blue);
        }
        return Color.BLACK;
    }

    /**
     * This method initializes jSlider1
     *
     * @return javax.swing.JSlider
     */
    private javax.swing.JSlider getJSUpdateTime() {
        if (JSUpdateTime == null) {
            try {
                JSUpdateTime = new javax.swing.JSlider();
                JSUpdateTime.setMaximum(5000);  // Generated
                JSUpdateTime.setMinimum(500);  // Generated
                JSUpdateTime.setMinorTickSpacing(100);  // Generated
                JSUpdateTime.setName("JSUpdateTime");  // Generated
                JSUpdateTime.setPaintLabels(true);  // Generated
                JSUpdateTime.setPaintTicks(true);  // Generated
                JSUpdateTime.setValue(Integer.parseInt(parent.getProperties().getProperty("UpdateTime", "2000")));  // Generated
                getLblValue().setText(Integer.toString((JSUpdateTime.getValue())));
                JSUpdateTime.addChangeListener(e -> {
                    getLblValue().setText(Integer.toString((JSUpdateTime.getValue())));
                    parent.getProperties().put("UpdateTime", Integer.toString(JSUpdateTime.getValue()));
                    parent.saveProperties();
                    firePropertyChange("UpdateTime", 0, JSUpdateTime.getValue());
                });

            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return JSUpdateTime;
    }

    /**
     * This method initializes jLabel
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabel() {
        if (jLabel == null) {
            try {
                jLabel = new javax.swing.JLabel();
                jLabel.setText("Update time [ms]");  // Generated
                jLabel.setDisplayedMnemonic(java.awt.event.KeyEvent.VK_U);  // Generated
            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return jLabel;
    }

    /**
     * This method initializes jCheckBox
     *
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getJCheckBox() {
        if (jCheckBox == null) {
            try {
                jCheckBox = new javax.swing.JCheckBox();
                jCheckBox.setText("Farbverlauf benutzen");  // Generated

                // prüfen,ob Gradient verwendet wird.
                try {
                    jCheckBox.setSelected(Boolean.getBoolean(parent.getProperties().getProperty("UseGradient", "false")));
                } catch (NullPointerException e) {
                    jCheckBox.setSelected(false);
                    parent.getProperties().put("UseGradient", Boolean.toString(false));
                }

                jCheckBox.addChangeListener(e -> {
                    boolean state = jCheckBox.isSelected();
                    getBtnBGColorEnd().setEnabled(state);
                    getLblBGColorEnd().setEnabled(state);
                    getCmbBGDirection().setEnabled(state);
                    parent.getProperties().put("UseGradient", Boolean.toString(state));
                });

            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return jCheckBox;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getBtnBGColorBegin() {
        if (btnBGColorBegin == null) {
            try {
                btnBGColorBegin = new javax.swing.JButton();
                btnBGColorBegin.setText("Wählen");  // Generated
                btnBGColorBegin.setMnemonic(java.awt.event.KeyEvent.VK_L);
                btnBGColorBegin.addActionListener(e -> {
                    Color col = null;
                    try {
                        col = JColorChooser.showDialog(getPanel(), "Hintergrund Beginn Farbe wählen",
                                parent.getPropertyColor("GradientStart", Color.white));
                    } catch (NullPointerException er) {
                        col = JColorChooser.showDialog(getPanel(), "Hintergrund Beginn Farbe wählen",
                                Color.white);
                    }

                    if (col != null) {
                        parent.setPropertyColor("GradientStart", col);
                        getLblBGColorBegin().setBackground(col);
                    }
                });

            } catch (java.lang.Throwable e) {

            }
        }
        return btnBGColorBegin;
    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getBtnBGColorEnd() {
        if (btnBGColorEnd == null) {
            try {
                btnBGColorEnd = new javax.swing.JButton();
                btnBGColorEnd.setText("Wählen");  // Generated
                btnBGColorEnd.setMnemonic(java.awt.event.KeyEvent.VK_N);  // Generated
                btnBGColorEnd.addActionListener(e -> {
                    Color col = null;
                    try {
                        col = JColorChooser.showDialog(getPanel(), "Hintergrund Ende Farbe wählen",
                                parent.getPropertyColor("GradientEnd", Color.blue));
                    } catch (NullPointerException er) {
                        col = JColorChooser.showDialog(getPanel(), "Hintergrund Ende Farbe wählen",
                                Color.blue);
                    }
                    if (col != null) {
                        parent.setPropertyColor("GradientEnd", col);
                        getLblBGColorEnd().setBackground(col);
                    }
                });

            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return btnBGColorEnd;
    }

    /**
     * This method initializes jLabel1
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getLblBGColorBegin() {
        if (lblBGColorBegin == null) {
            try {
                lblBGColorBegin = new javax.swing.JLabel();
                lblBGColorBegin.setText("Hintergrund Start");  // Generated
                lblBGColorBegin.setOpaque(true);
                try {
                    lblBGColorBegin.setBackground(parent.getPropertyColor("GradientStart", Color.white));
                } catch (NullPointerException e) {
                    lblBGColorBegin.setBackground(Color.white);
                }
            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return lblBGColorBegin;
    }

    /**
     * This method initializes jLabel2
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getLblBGColorEnd() {
        if (lblBGColorEnd == null) {
            try {
                lblBGColorEnd = new javax.swing.JLabel();
                lblBGColorEnd.setText("Hintergrund Ende");  // Generated
                lblBGColorEnd.setOpaque(true);
                try {
                    lblBGColorEnd.setBackground(parent.getPropertyColor("GradientEnd", Color.blue));
                } catch (NullPointerException e) {
                    lblBGColorEnd.setBackground(Color.blue);
                }
            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return lblBGColorEnd;
    }

    /**
     * This method initializes jLabel1
     *
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getLblValue() {
        if (LblValue == null) {
            try {
                LblValue = new javax.swing.JLabel();
                LblValue.setText("");  // Generated
            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return LblValue;
    }

    /**
     * This method initializes jComboBox
     *
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getCmbBGDirection() {
        if (CmbBGDirection == null) {
            try {
                CmbBGDirection = new javax.swing.JComboBox();
                CmbBGDirection.addItem(makeObj("Links - Rechts", 0));
                CmbBGDirection.addItem(makeObj("Rechts - Links", 1));
                CmbBGDirection.addItem(makeObj("Oben - Unten", 2));
                CmbBGDirection.addItem(makeObj("Unten - Oben", 3));
                CmbBGDirection.addItem(makeObj("ObenLinks - UntenRechts", 4));
                CmbBGDirection.addItem(makeObj("UntenRechts - ObenLinks", 5));
                CmbBGDirection.addItem(makeObj("ObenRechts - UntenLinks", 6));
                CmbBGDirection.addItem(makeObj("UntenLinks - ObenRechts", 7));

                try {
                    CmbBGDirection.setSelectedIndex(Integer.parseInt(parent.getProperties().getProperty("GradientDirection"), UpDownChart.BG_UP_TO_DOWN));
                } catch (NullPointerException e) {
                    CmbBGDirection.setSelectedIndex(UpDownChart.BG_UP_TO_DOWN);
                }

                CmbBGDirection.addActionListener(e -> parent.getProperties().put("GradientDirection", Integer.toString(CmbBGDirection.getSelectedIndex())));

            } catch (java.lang.Throwable e) {
                // TODO: Something
            }
        }
        return CmbBGDirection;
    }

    private Object makeObj(final String item, final int index) {
        return new Object() {
            public String toString() {
                return item;
            }

            public int getIndex() {
                return index;
            }
        };
    }
}	
