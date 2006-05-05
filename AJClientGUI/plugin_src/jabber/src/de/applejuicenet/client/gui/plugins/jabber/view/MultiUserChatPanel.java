package de.applejuicenet.client.gui.plugins.jabber.view;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

import de.applejuicenet.client.gui.plugins.jabber.control.LinkEditorKit;
import de.applejuicenet.client.gui.start.HyperlinkAdapter;

import de.tklsoft.gui.controls.TKLPanel;

public class MultiUserChatPanel extends TKLPanel
{
   private JTextPane      textArea = new JTextPane();
   private StyledDocument document = (StyledDocument) textArea.getStyledDocument();
   private JTextField     textField;
   private JTextPane      titleArea = new JTextPane();
   private JButton        closeButton = new JButton("X");
   private Logger         logger = Logger.getLogger(MultiUserChatPanel.class);

   public MultiUserChatPanel()
   {
      initGUI();
   }

   private void initGUI()
   {
      setLayout(new BorderLayout());
      textArea.setEditorKit(new LinkEditorKit());
      textArea.setDocument(document);
      textArea.setEditable(false);
      textArea.addHyperlinkListener(new HyperlinkAdapter(textArea));
      textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      textArea.setBackground(Color.WHITE);
      textField = new JTextField();
      Set<AWTKeyStroke> set = new HashSet<AWTKeyStroke>(1);

      set.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_JAPANESE_HIRAGANA, 0));
      textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, set);

      JScrollPane sp1 = new JScrollPane(textArea);
      JPanel      panel1 = new JPanel(new BorderLayout());

      sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      add(sp1, BorderLayout.CENTER);
      add(textField, BorderLayout.SOUTH);
      add(makeNorth(), BorderLayout.NORTH);
   }

   private Box makeNorth()
   {
      Box northBox = Box.createHorizontalBox();

      northBox.add(closeButton);
      titleArea.setEditable(false);
      northBox.add(titleArea);

      return northBox;
   }

   public JTextPane getTextArea()
   {
      return textArea;
   }

   public JTextField getTextField()
   {
      return textField;
   }

   public JTextPane getTitleArea()
   {
      return titleArea;
   }

   public JButton getCloseButton()
   {
      return closeButton;
   }
}
