package de.applejuicenet.client.gui.wizard;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.applejuicenet.client.gui.WizardDialog;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.AJSettings;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/Schritt3Panel.java,v 1.7 2004/07/09 14:31:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [maj0r@applejuicenet.de]
 *
 */

public class Schritt3Panel
    extends WizardPanel {
    private JTextArea label1 = new JTextArea();
    private JTextField nickname = new JTextField();
    private WizardDialog parent;

    public Schritt3Panel(WizardDialog parent, AJSettings settings) {
        super();
        this.parent = parent;
        if (settings != null){
	        String nick = settings.getNick();
	        nickname.setText(nick);
	        if (!isValidNickname()){
	        	nickname.setText("nonick");
	        }
        }
        init();
    }

    private void init() {
        label1.setWrapStyleWord(true);
        label1.setLineWrap(true);
        label1.setBackground(Color.WHITE);
        label1.setEditable(false);
        nickname.setColumns(20);
        nickname.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (isValidNickname()) {
                    parent.setWeiterEnabled(true);
                }
                else {
                    parent.setWeiterEnabled(false);
                }
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets.top = 5;
        constraints.insets.left = 5;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        add(label1, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.insets.top = 10;
        add(nickname, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        add(new JLabel(), constraints);
        constraints.weightx = 0;
        constraints.gridy = 2;
        constraints.weighty = 1;
        add(new JLabel(), constraints);
    }

    public boolean isValidNickname() {
        return (nickname.getText().startsWith("nonick") ||
                nickname.getText().length() == 0) ? false : true;
    }

    public void fireLanguageChanged() {
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.wizard.schritt3.label1")));
    }

    public String getNickname() {
        return nickname.getText();
    }

}
