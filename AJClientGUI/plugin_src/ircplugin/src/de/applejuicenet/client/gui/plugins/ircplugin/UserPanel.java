package de.applejuicenet.client.gui.plugins.ircplugin;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import java.awt.Color;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/UserPanel.java,v 1.3 2004/05/13 15:28:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class UserPanel
    extends JPanel
    implements ActionListener {
    private String name;
    private final JTextArea textArea = new JTextArea();
    private final JTextField textField = new JTextField();
    private JTextField titleArea = new JTextField();
    private JButton closeButton = new JButton("X");
    private XdccIrc parentPanel;
    private boolean selected = false;
    private boolean marked = false;
    private JTabbedPane tabbedPane;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

    public UserPanel(XdccIrc parentPanel, String name, JTabbedPane tabbedPane) {
        this.name = name;
        this.parentPanel = parentPanel;
        this.tabbedPane = tabbedPane;
        makePanel();
    }

    public void selected(){
        selected = true;
    }

    public void unselected(){
        selected = false;
        marked = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitleArea(String title) {
        titleArea.setText("");
        titleArea.setText(title);
    }

    private void makePanel() {
        setLayout(new BorderLayout());

        // let's add actionListener
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textField.addActionListener(this);
        textArea.setEditable(false);
        titleArea.setEditable(false);

        JScrollPane sp1 = new JScrollPane(textArea);

        sp1.setVerticalScrollBarPolicy(JScrollPane.
                                       VERTICAL_SCROLLBAR_ALWAYS);

        add(sp1, BorderLayout.CENTER);
        add(textField, BorderLayout.SOUTH);
        add(makeNorth(), BorderLayout.NORTH);
    }

    private Box makeNorth() {
        Box northBox = Box.createHorizontalBox();
        // let's add actions
        closeButton.addActionListener(this);
        northBox.add(closeButton);
        northBox.add(titleArea);
        return northBox;
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // let's take care of textField
        if (source == textField) {
            String message = textField.getText();

            // let's send to the server
            if (message.startsWith("/")) {
                // A command
                parentPanel.analyzeCommand(message);
                textField.setText("");
            }
            else {
                // A normal private message to send to the user
                parentPanel.parseSendToCommand("PRIVMSG " + name + " :" + message);

                // let's update textArea
                updateTextArea(parentPanel.formatNickname("<" + parentPanel.getNickname() + "> ") +
                               textField.getText());
                resetTextField();
            }
        }
        else if (source == closeButton) {
            parentPanel.closeChannel(parentPanel.getTabbedPane(), name);
        }
    }

    public void resetTextField() {
        textField.setText("");
    }

    public void updateTextArea(String message) {
        int oldCaretPosition = textArea.getCaretPosition();
        String zeit = dateFormatter.format(new Date(System.
            currentTimeMillis()));
        textArea.append("[" + zeit + "]\t" + message + "\n");

        int newCaretPosition = textArea.getCaretPosition();
        if (newCaretPosition == oldCaretPosition) {
            textArea.setCaretPosition(oldCaretPosition +
                                      (message + "\n").length());
        }
        if (!selected && !marked){
            marked = true;
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Object tab = tabbedPane.getComponentAt(i);
                if (tab == this) {
                    tabbedPane.setForegroundAt(i, Color.GREEN);
                }
            }
        }
    }
}
