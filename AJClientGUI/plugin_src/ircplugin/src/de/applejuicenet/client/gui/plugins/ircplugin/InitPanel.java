package de.applejuicenet.client.gui.plugins.ircplugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/InitPanel.java,v 1.1 2004/05/13 13:55:16 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class InitPanel
    extends JPanel
    implements ActionListener {
    final String name = "Init Window";
    final JTextArea textArea = new JTextArea();
    final JTextField textField = new JTextField();
    private JTextField titleArea;
    private XdccIrc parentPanel;

    public InitPanel(XdccIrc parentPanel) {
        this.parentPanel = parentPanel;
        makePanel();
    }

    private void makePanel() {
        setLayout(new BorderLayout());

        // let's add actionListener
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        textField.addActionListener(this);

        JScrollPane sp1 = new JScrollPane(textArea);

        sp1.setVerticalScrollBarPolicy(JScrollPane.
                                       VERTICAL_SCROLLBAR_ALWAYS);

        add(sp1, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
        add(makeNorth(), BorderLayout.NORTH);
    }

    private Box makeNorth() {
        Box northBox = Box.createHorizontalBox();
        titleArea = new JTextField("Not connected yet");
        titleArea.setEditable(false);
        northBox.add(titleArea);
        return northBox;
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // let's take care of textField
        if (source == textField) {
            String message = textField.getText();

            if (message.startsWith("/")) {
                // commands that start with "/"
                if (message.toLowerCase().indexOf("nickserv identify ")==-1){
                    textArea.append(message + "\n");
                }
                message = message.substring(1);
                textField.setText("");

                parentPanel.parseSendToCommand(message);
            }
            else {
                // update textArea
                textArea.append(message + "\n");
                textField.setText("");

                parentPanel.parseSendToCommand(message);
            }
        }
    }

    public void resetTextField() {
        textField.setText("");
    }

    public void setTitleArea(String message) {
        titleArea.setText(message);
    }

    public void updateTextArea(String message) {
        int oldCaretPosition = textArea.getCaretPosition();
        textArea.append(message + "\n");

        int newCaretPosition = textArea.getCaretPosition();
        if (newCaretPosition == oldCaretPosition) {
            textArea.setCaretPosition(oldCaretPosition +
                                      (message + "\n").length());
        }
    }
}

