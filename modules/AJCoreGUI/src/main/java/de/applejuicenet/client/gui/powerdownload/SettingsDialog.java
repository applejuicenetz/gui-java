package de.applejuicenet.client.gui.powerdownload;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.gui.DialogLocation;

import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownload.Order;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberAndSpecialCharsInputVerifier;
import de.applejuicenet.client.shared.NumberInputVerifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SettingsDialog extends JDialog implements ActionListener {

    private static String PERCENT_LOADED = "prozentGeladen";
    private static String SIZE = "groesse";
    private static String SOURCE_COUNT = "quellenAnzahl";

    private int powerDownload = 12;
    private int downloadCount = 2;
    private int sleepTime = 30;
    private boolean confirmed;
    private JTextField tfPowerdownloadValue = new JTextField();
    private JTextField tfPowerdownloadCount = new JTextField();
    private JTextField tfSleepTime = new JTextField();
    private JButton btnOk = new JButton("Ok");
    private JList listSorting = new JList();
    private DefaultListModel listSortingModel = new DefaultListModel();
    private JLabel btnUp;
    private JLabel btnDown;

    public SettingsDialog(Frame parent, int powerDownload,
            int downloadCount, int sleepTime) {
        super(parent, true);
        this.powerDownload = powerDownload;
        this.downloadCount = downloadCount;
        this.sleepTime = sleepTime;
        listSorting.setModel(listSortingModel);
        initGui();
        initData();
    }

    public SettingsDialog(Frame parent) {
        super(parent, true);
        listSorting.setModel(listSortingModel);
        initGui();
        initData();
    }

    private void initData() {
        listSortingModel.addElement("prozentGeladen");
        listSortingModel.addElement("groesse");
        listSortingModel.addElement("quellenAnzahl");
    }

    private void initGui() {
        IconManager im = IconManager.getInstance();
        btnUp = new JLabel(im.getIcon("increase"));
        btnDown = new JLabel(im.getIcon("decrease"));
        tfPowerdownloadValue.setMinimumSize(new Dimension(40, tfPowerdownloadValue.getMinimumSize().height));
        tfPowerdownloadValue.setPreferredSize(new Dimension(40, tfPowerdownloadValue.getPreferredSize().height));
        setResizable(false);
        setTitle(GuiText.text("javagui.powerdownload.settings.title"));
        NumberAndSpecialCharsInputVerifier verifier = new NumberAndSpecialCharsInputVerifier(
                ",.");
        verifier.setSpecialPattern("^([1-4]?[0-9]|50$)([,.][0-9]{0,2})?$");
        tfPowerdownloadValue.setDocument(verifier);
        tfPowerdownloadCount.setDocument(new NumberInputVerifier(1, 99));
        tfSleepTime.setDocument(new NumberInputVerifier(0,
                Integer.MAX_VALUE / 1000));

        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints constr = new GridBagConstraints();
        constr.anchor = GridBagConstraints.NORTHWEST;
        constr.insets = new Insets(5, 5, 5, 5);
        constr.fill = GridBagConstraints.BOTH;
        constr.gridheight = 1;
        constr.gridwidth = 1;
        constr.gridx = 0;
        constr.gridy = 0;
        getContentPane().add(new JLabel(GuiText.text("javagui.powerdownload.settings.value")), constr);
        constr.gridy++;
        getContentPane().add(new JLabel(GuiText.text("javagui.powerdownload.settings.count")), constr);
        constr.gridy++;
        getContentPane().add(new JLabel(GuiText.text("javagui.powerdownload.settings.interval")), constr);
        constr.gridy++;
        constr.gridheight = 2;
        getContentPane().add(listSorting, constr);
        constr.gridheight = 1;
        constr.gridy++;
        constr.gridy++;
        constr.gridwidth = 2;
        getContentPane().add(btnOk, constr);
        constr.gridwidth = 1;

        constr.gridy = 0;
        constr.gridx++;
        getContentPane().add(tfPowerdownloadValue, constr);
        constr.gridy++;
        getContentPane().add(tfPowerdownloadCount, constr);
        constr.gridy++;
        getContentPane().add(tfSleepTime, constr);
        constr.gridy++;
        getContentPane().add(btnUp, constr);
        constr.gridy++;
        getContentPane().add(btnDown, constr);

        listSorting.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tfSleepTime.setText(Integer.toString(sleepTime));
        tfPowerdownloadValue.setText(Double.toString((powerDownload + 10) / 10.0));
        tfPowerdownloadCount.setText(Integer.toString(downloadCount));

        initListeners();
        pack();
    }

    private void initListeners() {
        btnOk.addActionListener(this);
        tfPowerdownloadCount.addActionListener(this);
        tfPowerdownloadValue.addActionListener(this);
        tfSleepTime.addActionListener(this);
        btnUp.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                int index = listSorting.getSelectedIndex();
                if (index > 0) {
                    moveListEntry(index, index - 1);
                }
            }
        });
        btnDown.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                int index = listSorting.getSelectedIndex();
                if (index < listSorting.getModel().getSize() - 1 && index != -1) {
                    moveListEntry(index, index + 1);
                }
            }
        });
    }

    private void moveListEntry(int from, int to) {
        Object objFrom = listSortingModel.getElementAt(from);
        Object objTo = listSortingModel.getElementAt(to);
        listSortingModel.setElementAt(objFrom, to);
        listSortingModel.setElementAt(objTo, from);
        listSorting.setSelectedIndex(to);
    }

    public void actionPerformed(ActionEvent e) {
        if (checkInput()) {
            confirmed = true;
            dispose();
        }
    }

    private boolean checkInput() {
        boolean returnValue = true;
        String result = tfPowerdownloadValue.getText();
        result = result.replace(',', '.');
        double value;

        try {
            value = Double.parseDouble(result) - 1;
        } catch (NumberFormatException e) {
            tfPowerdownloadValue.setText(Double.toString((powerDownload + 10) / 10.0));
            return false;
        }

        value = ((double) Math.round(value * 100.0)) / 100.0;
        int tempValue = (int) ((value) * 10);
        if (tempValue < 12 || tempValue > 490) {
            returnValue = false;
            tfPowerdownloadValue.setText(Integer.toString(powerDownload));
        } else {
            powerDownload = tempValue;
        }
        try {
            downloadCount = Integer.parseInt(tfPowerdownloadCount.getText());
        } catch (NumberFormatException e) {
            returnValue = false;
        }
        try {
            int slt = Integer.parseInt(tfSleepTime.getText());
            if (slt >= 30) {
                sleepTime = slt;
            } else {
                tfSleepTime.setText(Integer.toString(sleepTime));
                returnValue = false;
            }
        } catch (NumberFormatException e) {
            tfSleepTime.setText(Integer.toString(sleepTime));
            returnValue = false;
        }
        return returnValue;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public int getPowerDownload() {
        return powerDownload;
    }

    public int getSleepTime() {
        return sleepTime * 1000;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setVisible(boolean visible) {
        if (visible) {
            confirmed = false;
        }
        DialogLocation.center(this);
        super.setVisible(visible);
    }

    public Order[] getOrder() {
        Order[] order = new Order[4];
        for (int i = 0; i < listSortingModel.size(); i++) {
            String cur = (String) listSortingModel.elementAt(i);
            if (cur == PERCENT_LOADED) {
                order[i] = Order.PERCENT_LOADED;
            } else if (cur == SIZE) {
                order[i] = Order.SIZE;
            } else if (cur == SOURCE_COUNT) {
                order[i] = Order.SOURCES;
            }
        }
        order[3] = Order.ID;
        return order;
    }
}
