package de.applejuicenet.client.gui.connect;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.OptionsManagerImpl;
import de.applejuicenet.client.gui.options.ODConnectionPanel;
import de.applejuicenet.client.shared.ConnectionSettings;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/connect/QuickConnectionSettingsDialog.java,v 1.7 2009/01/12 09:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class QuickConnectionSettingsDialog
    extends JDialog {
	
    public static final int ABGEBROCHEN = 1;

    private ODConnectionPanel remotePanel;
    private ConnectionSettings remote;
    private ConnectionSettings[] connectionSet;
    private JButton ok = new JButton("OK");
    private JButton abbrechen = new JButton("Abbrechen");
    private JCheckBox cmbNieWiederZeigen = new JCheckBox();
    private JComboBox connectionListe = new JComboBox();
    private Logger logger;
    private boolean dirty = false;

    private int result = 0;

    public QuickConnectionSettingsDialog(Frame parent) {
        super(parent, true);
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }
    
    private void init() {
        remote = OptionsManagerImpl.getInstance().getRemoteSettings();
        connectionSet = OptionsManagerImpl.getInstance().getConnectionsSet();
        for (int i = 0; i < connectionSet.length; i++) {
            this.connectionListe.addItem(connectionSet[i]);
            if (remote.getHost() != null && remote.getHost().equals(connectionSet[i].getHost())) {
                this.connectionListe.setSelectedItem(connectionSet[i]);
            }
        }
        remotePanel = new ODConnectionPanel(remote, this, true);
        setTitle("appleJuice Client");

        getContentPane().setLayout(new BorderLayout());

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        String nachricht = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName("javagui.startup.ueberpruefeEinst"));
        cmbNieWiederZeigen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.
            getFirstAttrbuteByTagName("javagui.startup.showdialog")));
        cmbNieWiederZeigen.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                dirty = true;
            }
        });

        connectionListe.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                selChanged(itemEvent);
            }
        });

        cmbNieWiederZeigen.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    pressOK();
                }
                else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                	pressAbbrechen();
                }
                else {
                    super.keyPressed(ke);
                }
            }
        });
        connectionListe.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    pressOK();
                }
                else if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
                	pressAbbrechen();
                }
                else {
                    super.keyPressed(ke);
                }
            }
        });
        JPanel panel2 = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.insets.bottom = 5;

        panel2.add(new JLabel(nachricht), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.insets.right = 5;
        panel2.add(new JLabel("           "), constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        panel2.add(connectionListe, constraints);

        getContentPane().add(panel2, BorderLayout.NORTH);
        getContentPane().add(remotePanel, BorderLayout.CENTER);

        JPanel panel3 = new JPanel(new BorderLayout());
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel1.add(ok);
        panel1.add(abbrechen);

        JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel4.add(cmbNieWiederZeigen);
        panel3.add(panel4, BorderLayout.NORTH);
        panel3.add(panel1, BorderLayout.SOUTH);
        getContentPane().add(panel3, BorderLayout.SOUTH);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                speichereEinstellungen();
                result = 0;
                setVisible(false);
            }
        });

        abbrechen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                result = ABGEBROCHEN;
                setVisible(false);
            }
        });
        addWindowListener(
            new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                result = ABGEBROCHEN;
                setVisible(false);
            }
        });
        pack();
        Dimension appDimension = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation( (screenSize.width - appDimension.width) / 2,
                    (screenSize.height - appDimension.height) / 2);
       remotePanel.setFocusOnPassword();
    }

    public void pressOK() {
        ok.doClick();
    }
    
    public void setNieWiederAnzeigen(){
    	cmbNieWiederZeigen.setSelected(true);
    	dirty = true;
    }

    public void pressAbbrechen() {
        abbrechen.doClick();
    }

    public int getResult() {
        return result;
    }

    private void selChanged(ItemEvent itemEvent) {
        ConnectionSettings con = (ConnectionSettings) itemEvent.getItem();
        remotePanel.setHost(con.getHost());
        remotePanel.setXMLPort(Integer.toString(con.getXmlPort()));
        remotePanel.updateUI();
    }

    private void speichereEinstellungen() {
        try {
            CoreConnectionSettingsHolder ajConn = AppleJuiceClient.getCoreConnectionSettingsHolder();
            if (ajConn != null) {
                ajConn.setCoreHost(remotePanel.getHost());
                ajConn.setCorePort(remotePanel.getPort());
                ajConn.setCorePassword(remotePanel.getPassword(), true);
            }
            
            OptionsManagerImpl.getInstance().setConnectionsSet(getNeueConfigs(remote));
            OptionsManagerImpl.getInstance().onlySaveRemote(remote);
            if (dirty) {
                OptionsManagerImpl.getInstance().
                    showConnectionDialogOnStartup(!cmbNieWiederZeigen.
                                                  isSelected());
            }
        }
        catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private ConnectionSettings[] getNeueConfigs(ConnectionSettings remote) {
        for (int i = 0; i < this.connectionSet.length; i++) {
            if (remote.getHost().equals(connectionSet[i].getHost()) && (remote.getXmlPort() == connectionSet[i].getXmlPort())) {
                connectionSet[i] = remote;
                return connectionSet;
            }
        }
        ArrayList<ConnectionSettings> targets2Save = new ArrayList<ConnectionSettings>();
        targets2Save.add(remote);
        for (ConnectionSettings curVal : connectionSet) {
            targets2Save.add(curVal);
        }
        return targets2Save.toArray(new ConnectionSettings[]{});
    }
}
