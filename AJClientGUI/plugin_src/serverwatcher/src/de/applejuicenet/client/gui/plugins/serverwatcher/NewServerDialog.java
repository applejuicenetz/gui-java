package de.applejuicenet.client.gui.plugins.serverwatcher;

import de.applejuicenet.client.shared.NumberInputVerifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/serverwatcher/src/de/applejuicenet/client/gui/plugins/serverwatcher/NewServerDialog.java,v 1.1 2003/09/12 11:15:49 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: NewServerDialog.java,v $
 * Revision 1.1  2003/09/12 11:15:49  maj0r
 * Server lassen sich nun speichern und entfernen.
 * Version 1.1
 *
 *
 */

public class NewServerDialog extends JDialog {
    private JButton ok = new JButton("OK");
    private JButton abbrechen = new JButton("Abbrechen");
    private JTextField dyn = new JTextField();
    private JTextField port = new JTextField();
    private JTextField user = new JTextField();
    private JTextField pass = new JTextField();
    private boolean save = false;
    private ServerConfig serverConfig;

    public NewServerDialog(Frame parent, boolean modal) {
        super(parent, modal);
        init();
    }

    private void init() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                close();
            }
        });
        setTitle("Neuer Server");
        dyn.setColumns(15);
        port.setColumns(5);
        port.setDocument(new NumberInputVerifier());
        user.setColumns(15);
        pass.setColumns(15);
        getContentPane().setLayout(new BorderLayout());
        JPanel panel2 = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets.left = 5;
        panel2.add(new JLabel("Ip: "), constraints);
        constraints.gridy = 1;
        panel2.add(new JLabel("Port: "), constraints);
        constraints.gridy = 2;
        panel2.add(new JLabel("User: "), constraints);
        constraints.gridy = 3;
        panel2.add(new JLabel("Passwort: "), constraints);
        constraints.insets.left = 0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel2.add(dyn, constraints);
        constraints.gridy = 1;
        panel2.add(port, constraints);
        constraints.gridy = 2;
        panel2.add(user, constraints);
        constraints.gridy = 3;
        panel2.add(pass, constraints);
        getContentPane().add(panel2, BorderLayout.CENTER);
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel1.add(ok);
        panel1.add(abbrechen);
        getContentPane().add(panel1, BorderLayout.SOUTH);
        pack();
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (dyn.getText().length() > 0 && port.getText().length() > 0
                        && user.getText().length() > 0 && pass.getText().length() > 0)
                {
                    serverConfig = new ServerConfig(dyn.getText(), port.getText(), user.getText() + ":" + pass.getText());
                    save = true;
                    close();
                }
            }
        });
        abbrechen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                close();
            }
        });
    }

    private void close() {
        dispose();
    }

    public boolean isSave() {
        return save;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }
}
