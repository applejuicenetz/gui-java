package de.applejuicenet.client.gui.plugins.ircplugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Document;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.text.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/InitPanel.java,v 1.2 2004/05/14 19:48:28 maj0r Exp $
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
    final JTextPane textArea = new JTextPane();
    final JTextField textField = new JTextField();
    private JTextField titleArea;
    private XdccIrc parentPanel;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    private Logger logger;

    public InitPanel(XdccIrc parentPanel) {
        logger = Logger.getLogger(getClass());
        this.parentPanel = parentPanel;
        makePanel();
    }

    private void makePanel() {
        setLayout(new BorderLayout());

        // let's add actionListener
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
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
                    updateTextArea(message);
                }
                message = message.substring(1);
                textField.setText("");

                parentPanel.parseSendToCommand(message);
            }
            else {
                // update textArea
                updateTextArea(message);
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
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setBackground(attributes, Color.WHITE);
        StyleConstants.setForeground(attributes, Color.BLACK);
        Document doc = textArea.getDocument();
        String zeit = dateFormatter.format(new Date(System.
            currentTimeMillis()));
        try {
            doc.insertString(doc.getLength(),
                             "[" + zeit + "]\t" + message + "\n",
                             attributes);
        }
        catch (BadLocationException ex) {
            if (logger.isEnabledFor(Level.ERROR)){
                logger.error("Fehler im IrcPlugin", ex);
            }
        }
        int newCaretPosition = textArea.getDocument().getLength();
        if (newCaretPosition != oldCaretPosition) {
            textArea.setCaretPosition(newCaretPosition);
        }
    }
}

