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
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;

import java.util.Date;
import java.net.URL;
import java.text.SimpleDateFormat;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.start.HyperlinkAdapter;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/InitPanel.java,v
 * 1.6 2004/11/22 16:25:25 maj0r Exp $
 * 
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 * 
 * @author: Maj0r [aj@tkl-soft.de]
 * 
 */

public class InitPanel extends JPanel implements ActionListener {
	private final String name = "Init Window";

	private final JTextPane textArea = new JTextPane();
	private final JTextField textField = new JTextField();
	private StyledDocument document = (StyledDocument) textArea
			.getStyledDocument();
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
		textArea.setEditorKit(new LinkEditorKit());
		textArea.setDocument(document);
		textArea.setEditable(false);
		textArea.addHyperlinkListener(new HyperlinkAdapter(textArea));

		setLayout(new BorderLayout());

		textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		textArea.setBackground(Color.WHITE);
		textField.addActionListener(this);

		JScrollPane sp1 = new JScrollPane(textArea);

		sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		add(sp1, BorderLayout.CENTER);
		add(textField, BorderLayout.SOUTH);
		add(makeNorth(), BorderLayout.NORTH);
	}

	private MutableAttributeSet setLink(String sUrl) {
		MutableAttributeSet a = new SimpleAttributeSet();
		try {
			if (!sUrl.startsWith(ChannelPanel.HTTP_IDENTIFIER)){
				sUrl = ChannelPanel.HTTP_IDENTIFIER + sUrl;
			}
			URL url = new URL(sUrl);
			a.addAttribute(LinkEditorKit.LINK, new URL(sUrl));
		} catch (Exception e) {
			// nothing to do
		}
		a.addAttribute(StyleConstants.Bold, Boolean.TRUE);
		a.addAttribute(StyleConstants.Underline, Boolean.TRUE);
		return a;
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
				if (message.toLowerCase().indexOf("nickserv identify ") == -1) {
					updateTextArea(message);
				}
				message = message.substring(1);
				textField.setText("");

				parentPanel.parseSendToCommand(message);
			} else {
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
		String zeit = dateFormatter.format(new Date(System.currentTimeMillis()));
		try {
			doc.insertString(doc.getLength(), "[" + zeit + "]\t", attributes);
			while (message.indexOf(ChannelPanel.HTTP_IDENTIFIER) != -1 || 
					message.indexOf(ChannelPanel.WWW_IDENTIFIER) != -1){
				String httpIdentifier;
				int indexHttp = message.indexOf(ChannelPanel.HTTP_IDENTIFIER);
				int indexWww = message.indexOf(ChannelPanel.WWW_IDENTIFIER);
				if (indexHttp == -1){
					httpIdentifier = ChannelPanel.WWW_IDENTIFIER;
				}
				else if (indexWww == -1){
					httpIdentifier = ChannelPanel.HTTP_IDENTIFIER;
				}
				else{
					if (indexWww == indexHttp + ChannelPanel.HTTP_IDENTIFIER.length()){
						httpIdentifier = ChannelPanel.HTTP_IDENTIFIER;
					}
					else{
						httpIdentifier = ChannelPanel.WWW_IDENTIFIER;
					}
				}
			
				doc.insertString(doc.getLength(), message.substring(0, message.indexOf(httpIdentifier)), 
						attributes);
				message = message.substring(message.indexOf(httpIdentifier));
				int index = message.indexOf(" ");
				int index2 = message.indexOf(">");
				if (index2 != -1 && index2 < index){
					index = index2;
				}
				index2 = message.indexOf(")");
				if (index2 != -1 && index2 < index){
					index = index2;
				}
				index2 = message.indexOf("]");
				if (index2 != -1 && index2 < index){
					index = index2;
				}
				index2 = message.indexOf("}");
				if (index2 != -1 && index2 < index){
					index = index2;
				}
				if (index != -1){
					doc.insertString(doc.getLength(), message.substring(0, index), 
							setLink(message.substring(0, index)));
					message = message.substring(index);
				}
				else{
					doc.insertString(doc.getLength(), message, setLink(message));
					message = "";
				}
			}
			doc.insertString(doc.getLength(), message + "\n", attributes);
		} catch (BadLocationException ex) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error("Fehler im IrcPlugin", ex);
			}
		}
		int newCaretPosition = textArea.getDocument().getLength();
		if (newCaretPosition != oldCaretPosition) {
			textArea.setCaretPosition(newCaretPosition);
		}
	}
}
