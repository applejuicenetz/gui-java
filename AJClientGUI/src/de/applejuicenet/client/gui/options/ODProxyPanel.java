package de.applejuicenet.client.gui.options;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberInputVerifier;
import de.tklsoft.gui.controls.TKLCheckBox;
import de.tklsoft.gui.controls.TKLTextField;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODProxyPanel.java,v 1.4 2005/03/07 14:23:13 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class ODProxyPanel
    extends JPanel
    implements OptionsRegister {
	
	private boolean dirty = false;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private TKLTextField host = new TKLTextField();
    private TKLTextField port = new TKLTextField();
    private TKLTextField user = new TKLTextField();
    private JPasswordField passwort = new JPasswordField();
    private TKLCheckBox use = new TKLCheckBox();
    private ProxySettings proxySettings;
    private Logger logger;
    private Icon menuIcon;
    private String menuText;

    public ODProxyPanel() {
        logger = Logger.getLogger(getClass());
        try {
            init();
        }
        catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void init() throws Exception {
        IconManager im = IconManager.getInstance();
        menuIcon = im.getIcon("opt_proxy");
        setLayout(new BorderLayout());
        JPanel panel1 = new JPanel(new GridBagLayout());
        FlowLayout flowL = new FlowLayout();
        flowL.setAlignment(FlowLayout.RIGHT);
        JPanel panel2 = new JPanel(flowL);

        LanguageSelector languageSelector = LanguageSelector.getInstance();
        proxySettings = ProxyManagerImpl.getInstance().getProxySettings();

        label1 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.proxy.host")));
        label2 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.proxy.port")));
        label3 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.proxy.benutzername")));
        label4 = new JLabel(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.proxy.passwort")));
        use.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.proxy.verwenden")));
        menuText = ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.options.proxy.caption"));
        host.setText(proxySettings.getHost());
        host.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (proxySettings.getHost().compareTo(host.getText()) != 0) {
                    dirty = true;
                }
            }
        });
        port.setDocument(new NumberInputVerifier());
        if (proxySettings.getPort() != -1) {
            port.setText(Integer.toString(proxySettings.getPort()));
        }
        port.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                if (Integer.toString(proxySettings.getPort()).compareTo(host.
                    getText()) != 0) {
                    dirty = true;
                }
            }
        });
        user.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
            }
        });
        passwort.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                dirty = true;
            }
        });
        use.setSelected(proxySettings.isUse());
        use.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                dirty = true;
            }
        });

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.top = 5;
        constraints.insets.left = 5;

        panel1.add(label1, constraints);

        constraints.gridy = 1;
        panel1.add(label2, constraints);

        constraints.gridy = 2;
        panel1.add(label3, constraints);

        constraints.gridy = 3;
        panel1.add(label4, constraints);

        constraints.insets.right = 5;
        constraints.gridy = 0;
        constraints.gridx = 1;
        constraints.weightx = 1;
        panel1.add(host, constraints);

        constraints.gridy = 1;
        panel1.add(port, constraints);

        constraints.gridy = 2;
        panel1.add(user, constraints);

        constraints.gridy = 3;
        panel1.add(passwort, constraints);

        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        panel2.add(use);
        panel1.add(panel2, constraints);

        add(panel1, BorderLayout.NORTH);
        
        host.confirmNewValue();
        port.confirmNewValue();
        user.confirmNewValue();
        use.confirmNewValue();
    }

    public ProxySettings getProxySettings() {
        if (dirty) {
            proxySettings.setUse(use.isSelected());
            proxySettings.setHost(host.getText());
            String tmpPort = port.getText();
            if (tmpPort.length() == 0) {
                proxySettings.setPort( -1);
            }
            else {
                proxySettings.setPort(Integer.parseInt(tmpPort));
            }
            proxySettings.setUserpass(user.getText(), new String(passwort.getPassword()));
        }
        return proxySettings;
    }

    public boolean isDirty() {
        return dirty;
    }

    public Icon getIcon() {
        return menuIcon;
    }

    public String getMenuText() {
        return menuText;
    }

    public void reloadSettings() {
        // nothing to do...
    }
}
