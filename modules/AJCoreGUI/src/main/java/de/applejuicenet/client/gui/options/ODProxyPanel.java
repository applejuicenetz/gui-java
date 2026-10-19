/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.options;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.shared.ProxySettings;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import de.applejuicenet.client.shared.IconManager;
import de.applejuicenet.client.shared.NumberInputVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Copyright: General Public License
 */
public class ODProxyPanel extends JPanel implements OptionsRegister {
    private boolean dirty = false;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JTextField host = new JTextField();
    private JTextField port = new JTextField();
    private JTextField user = new JTextField();
    private JPasswordField password = new JPasswordField();
    private JCheckBox use = new JCheckBox();
    private ProxySettings proxySettings;
    private Logger logger;
    private Icon menuIcon;
    private String menuText;

    public ODProxyPanel() {
        logger = LoggerFactory.getLogger(getClass());
        try {
            init();
        } catch (Exception e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    private void init() {
        IconManager im = IconManager.getInstance();

        menuIcon = im.getIcon("opt_proxy");
        setLayout(new BorderLayout());
        JPanel panel1 = new JPanel(new GridBagLayout());
        FlowLayout flowL = new FlowLayout();

        flowL.setAlignment(FlowLayout.RIGHT);
        JPanel panel2 = new JPanel(flowL);

        LanguageSelector languageSelector = LanguageSelector.getInstance();

        proxySettings = ProxyManagerImpl.getInstance().getProxySettings();

        label1 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.proxy.host"));
        label2 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.proxy.port"));
        label3 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.proxy.username"));
        label4 = new JLabel(languageSelector.getFirstAttributeByTagName("javagui.options.proxy.password"));
        use.setText(languageSelector.getFirstAttributeByTagName("javagui.options.proxy.use"));
        menuText = languageSelector.getFirstAttributeByTagName("javagui.options.proxy.caption");
        host.setText(proxySettings.getHost());
        host.addFocusListener(new HostFocusListener());
        port.setDocument(new NumberInputVerifier());
        if (proxySettings.getPort() != -1) {
            port.setText(Integer.toString(proxySettings.getPort()));
        }

        port.addFocusListener(new PortFocusListener());
        user.addFocusListener(new UserFocusListener());
        password.addFocusListener(new PasswordFocusListener());
        use.setSelected(proxySettings.isUse());
        use.addChangeListener(e -> dirty = true);

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
        panel1.add(password, constraints);

        constraints.gridy = 4;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        JLabel explanation = new JLabel("<html>" + languageSelector.getFirstAttributeByTagName(
                "javagui.options.proxy.scope") + "</html>");
        explanation.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        panel1.add(explanation, constraints);
        constraints.gridy = 5;
        panel2.add(use);
        panel1.add(panel2, constraints);
        // Keep the explanatory text from setting the dialog width.
        explanation.setPreferredSize(new Dimension(0, explanation.getPreferredSize().height * 2));

        add(panel1, BorderLayout.NORTH);
    }

    public ProxySettings getProxySettings() {
        if (dirty) {
            proxySettings.setUse(use.isSelected());
            proxySettings.setHost(host.getText());
            String tmpPort = port.getText();

            if (tmpPort.length() == 0) {
                proxySettings.setPort(-1);
            } else {
                proxySettings.setPort(Integer.parseInt(tmpPort));
            }

            proxySettings.setUserpass(user.getText(), new String(password.getPassword()));
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

    class PasswordFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            dirty = true;
        }
    }


    class UserFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            dirty = true;
        }
    }


    class PortFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            if (Integer.toString(proxySettings.getPort()).compareTo(host.getText()) != 0) {
                dirty = true;
            }
        }
    }


    class HostFocusListener extends FocusAdapter {
        public void focusLost(FocusEvent e) {
            if (proxySettings.getHost().compareTo(host.getText()) != 0) {
                dirty = true;
            }
        }
    }
}
