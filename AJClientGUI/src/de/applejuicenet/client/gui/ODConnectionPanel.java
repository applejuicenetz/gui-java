package de.applejuicenet.client.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ConnectionSettings;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/Attic/ODConnectionPanel.java,v 1.14 2004/03/10 17:04:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ODConnectionPanel
    extends JPanel
    implements OptionsRegister {
    private boolean dirty = false;
    private JLabel label1;
    private JLabel label3;
    private JLabel label4;
    private JTextField host = new JTextField();
    private JTextField port = new JTextField();
    private JPasswordField passwortNeu = new JPasswordField();
    private ConnectionSettings remote;
    private Logger logger;
    private boolean showPort = false;
    private QuickConnectionSettingsDialog quickConnectionSettingsDialog;
    private Icon menuIcon;
    private String menuText;

    public ODConnectionPanel(ConnectionSettings remote,
                             QuickConnectionSettingsDialog
                             quickConnectionSettingsDialog) {
        logger = Logger.getLogger(getClass());
        try {
            this.quickConnectionSettingsDialog = quickConnectionSettingsDialog;
            this.remote = remote;
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    public ODConnectionPanel(ConnectionSettings remote,
                             QuickConnectionSettingsDialog
                             quickConnectionSettingsDialog,
                             boolean showPort) {
        logger = Logger.getLogger(getClass());
        try {
            this.showPort = showPort;
            this.quickConnectionSettingsDialog = quickConnectionSettingsDialog;
            this.remote = remote;
            init();
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
        }
    }

    private void init() throws Exception {
        setLayout(new BorderLayout());
        IconManager im = IconManager.getInstance();
        menuIcon = im.getIcon("opt_passwort");
        JPanel panel1 = new JPanel(new GridBagLayout());
        FlowLayout flowL = new FlowLayout();
        flowL.setAlignment(FlowLayout.RIGHT);
        JPanel panel2 = new JPanel(flowL);

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.remote.host")));
        label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.remote.passwortNeu")));
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.einstform.pwsheet.caption"));
        label4 = new JLabel("Port");

        host.setText(remote.getHost());
        host.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (remote.getHost().compareTo(host.getText()) != 0) {
                    dirty = true;
                    remote.setHost(host.getText());
                }
            }
        });
        passwortNeu.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setNewPassword(new String(passwortNeu.getPassword()));
            }
        });
        if (quickConnectionSettingsDialog != null) {
            passwortNeu.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent ke) {
                    if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                        dirty = true;
                        remote.setNewPassword(new String(passwortNeu.
                            getPassword()));
                        quickConnectionSettingsDialog.pressOK();
                    }
                    else {
                        super.keyPressed(ke);
                    }
                }
            });
        }
        port.setDocument(new NumberInputVerifier());
        port.setText(Integer.toString(remote.getXmlPort()));
        port.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
                remote.setXmlPort(Integer.parseInt(port.getText()));
            }
        });

        enableControls(true);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.left = 5;

        panel1.add(label1, constraints);

        int gridy = 1;
        if (showPort) {
            constraints.gridy = gridy;
            gridy++;
            panel1.add(label4, constraints);
        }
        constraints.gridy = gridy;
        gridy++;
        panel1.add(label3, constraints);

        constraints.insets.right = 5;
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(host, constraints);

        gridy = 1;
        if (showPort) {
            constraints.gridy = gridy;
            gridy++;
            panel1.add(port, constraints);
        }
        constraints.gridy = gridy;
        gridy++;
        panel1.add(passwortNeu, constraints);

        constraints.gridy = gridy;
        gridy++;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        panel1.add(panel2, constraints);

        add(panel1, BorderLayout.NORTH);
        if (quickConnectionSettingsDialog != null) {
            label3.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
                getFirstAttrbuteByTagName(".root.einstform.pwsheet.caption")));
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void enableControls(boolean enable) {
        host.setEnabled(enable);
        passwortNeu.setEnabled(enable);
        label1.setEnabled(enable);
        label3.setEnabled(enable);
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }
}
