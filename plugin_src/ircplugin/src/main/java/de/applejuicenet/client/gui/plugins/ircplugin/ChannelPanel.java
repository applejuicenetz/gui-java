/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.ircplugin;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Information;
import de.applejuicenet.client.fassade.entity.Server;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.shared.AJSettings;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.start.HyperlinkAdapter;

/**
 * $Header:
 * /cvsroot/applejuicejava/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/ChannelPanel.java,v
 * 1.18 2004/12/06 20:55:30 maj0r Exp $
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
public class ChannelPanel extends JPanel implements ActionListener
{
   public static final String HTTP_IDENTIFIER    = "http://";
   public static final String WWW_IDENTIFIER     = "www.";
   private static final Color JOIN_GREEN         = new Color(0, 128, 0);
   private String             name;
   private SortedListModel    usernameList       = new SortedListModel();
   private JList              userList           = new JList(usernameList);
   private JTextPane          textArea           = new JTextPane();
   private StyledDocument     document           = (StyledDocument) textArea.getStyledDocument();
   private JTextField         textField;
   private SimpleDateFormat   dateFormatter      = new SimpleDateFormat("HH:mm:ss");
   private ArrayList<String>  befehle            = new ArrayList<String>();
   private int                befehlPos          = -1;
   private JLabel             counter            = new JLabel("0 Ops, 0 Gesamt");
   private JTextPane          titleArea          = new JTextPane();
   private JButton            closeButton        = new JButton("X");
   private XdccIrc            parentPanel;
   private boolean            selected           = false;
   private boolean            marked             = false;
   private JTabbedPane        tabbedPane;
   private Logger             logger;
   private long               lastStatsPrinted   = 0;
   private long               lastVersionPrinted = 0;
   private String             counterText        = "Gesamt";

   public ChannelPanel(XdccIrc parentPanel, String name, JTabbedPane tabbedPane)
   {
      logger           = Logger.getLogger(getClass());
      this.name        = name;
      this.parentPanel = parentPanel;
      this.tabbedPane  = tabbedPane;
      makePanel();
   }

   public SortedListModel getUserNameList()
   {
      return usernameList;
   }

   public void selected()
   {
      selected = true;
      marked   = false;
   }

   public void unselected()
   {
      selected = false;
   }

   private void makePanel()
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
      textField.addActionListener(this);
      textArea.addKeyListener(new KeyAdapter()
         {
            public void keyTyped(KeyEvent ke)
            {
               textField.setText(textField.getText() + ke.getKeyChar());
               textField.requestFocus();
            }
         });
      textField.addKeyListener(new KeyAdapter()
         {
            public void keyReleased(KeyEvent ke)
            {
               super.keyReleased(ke);
               if(ke.getKeyCode() == KeyEvent.VK_TAB)
               {
                  String text = textField.getText();

                  if(text.length() > 0)
                  {
                     int index  = text.lastIndexOf(' ');
                     int index2 = text.lastIndexOf(',');

                     if(index2 > index)
                     {
                        index = index2;
                     }

                     String searchString;

                     if(index != -1)
                     {
                        searchString = text.substring(index + 1).toLowerCase();
                     }
                     else
                     {
                        searchString = text.toLowerCase();
                     }

                     String treffer = "";
                     int    count = 0;

                     synchronized(usernameList)
                     {
                        User[] users        = usernameList.getValues();
                        String compareValue;

                        for(int i = 0; i < users.length; i++)
                        {
                           if(users[i].getName().toLowerCase().startsWith(searchString))
                           {
                              treffer += users[i].getName() + " ";
                              count++;
                           }
                        }
                     }

                     if(treffer.length() > 0)
                     {
                        treffer = treffer.substring(0, treffer.length() - 1);
                        if(count == 1)
                        {
                           if(index != -1)
                           {
                              String newText = text.subSequence(0, index + 1) + treffer;

                              textField.setText(newText);
                           }
                           else
                           {
                              textField.setText(treffer);
                           }
                        }
                        else if(count > 1)
                        {
                           updateTextArea("\t" + treffer, false);
                        }
                     }
                  }
               }
               else if(befehlPos != -1)
               {
                  if(ke.getKeyCode() == KeyEvent.VK_UP)
                  {
                     textField.setText(befehle.get(befehlPos));
                     if(befehlPos > 0)
                     {
                        befehlPos--;
                     }
                  }
                  else if(ke.getKeyCode() == KeyEvent.VK_DOWN)
                  {
                     if(befehlPos < befehle.size() - 1)
                     {
                        befehlPos++;
                     }

                     textField.setText(befehle.get(befehlPos));
                  }
               }
            }
         });

      userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      userList.addMouseListener(new MouseAdapter()
         {
            public void mouseClicked(MouseEvent me)
            {
               if(me.getClickCount() == 2)
               {
                  User user = (User) userList.getSelectedValue();

                  if(!user.getName().equalsIgnoreCase(parentPanel.getNickname()))
                  {
                     JTabbedPane tabbedPane = parentPanel.getTabbedPane();

                     for(int i = 1; i < tabbedPane.getTabCount(); i++)
                     {
                        if(tabbedPane.getTitleAt(i).equalsIgnoreCase(user.getName()))
                        {
                           return;
                        }
                     }

                     parentPanel.addUser(tabbedPane, user.getName());
                     tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
                  }
               }
            }
         });
      userList.setCellRenderer(new UserListCellRenderer());

      JScrollPane sp1    = new JScrollPane(textArea);
      JScrollPane sp2    = new JScrollPane(userList);
      JPanel      panel1 = new JPanel(new BorderLayout());

      panel1.add(counter, BorderLayout.NORTH);
      panel1.add(sp2, BorderLayout.CENTER);

      sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      sp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp1, panel1);

      splitPane.setDividerLocation(AppleJuiceDialog.getApp().getSize().width - 200);
      add(splitPane, BorderLayout.CENTER);
      add(textField, BorderLayout.SOUTH);
      add(makeNorth(), BorderLayout.NORTH);
   }

   private Box makeNorth()
   {
      Box northBox = Box.createHorizontalBox();

      // let's add actions
      closeButton.addActionListener(this);

      northBox.add(closeButton);
      titleArea.setEditable(false);
      northBox.add(titleArea);

      return northBox;
   }

   private MutableAttributeSet setLink(String sUrl, SimpleAttributeSet attributeSet)
   {
      MutableAttributeSet a = new SimpleAttributeSet(attributeSet);

      try
      {
         if(!sUrl.toLowerCase().startsWith(ChannelPanel.HTTP_IDENTIFIER))
         {
            sUrl = ChannelPanel.HTTP_IDENTIFIER + sUrl;
         }

         URL url = new URL(sUrl);

         a.addAttribute(LinkEditorKit.LINK, new URL(sUrl));
      }
      catch(Exception e)
      {

         // nothing to do
      }

      a.addAttribute(StyleConstants.Bold, Boolean.TRUE);
      a.addAttribute(StyleConstants.Underline, Boolean.TRUE);
      return a;
   }

   public void actionPerformed(ActionEvent e)
   {
      Object source = e.getSource();

      // let's take care of textField
      if(source == textField)
      {
         String message = textField.getText();

         if(message.length() > 450)
         {
            message = message.substring(0, 450);
         }

         // let's send to server
         if(message.startsWith("/"))
         {

            // commands that start with "/"
            // message = message.substring(1);
            if(message.toLowerCase().compareTo("/ajstats") == 0 || message.toLowerCase().compareTo("/ajversinfo") == 0)
            {
               long currentTime = System.currentTimeMillis();

               if(message.toLowerCase().compareTo("/ajstats") == 0)
               {
                  if(currentTime > lastStatsPrinted + 60000)
                  {
                     lastStatsPrinted = currentTime;
                     ApplejuiceFassade ajFassade = AppleJuiceClient.getAjFassade();
                     Information       info = ajFassade.getInformation();
                     StringBuffer      buf  = new StringBuffer();

                     String            coreVersion      = AppleJuiceClient.getAjFassade().getCoreVersion().getVersion();
                     String            guiVersion       = AppleJuiceDialog.GUI_VERSION;
                     String            ircPluginVersion = parentPanel.getPlugin()
                                                          .getGeneralXMLAttributeByTagName(".root.general.version.value");

                     String version = "Java-GUI: " + guiVersion + " / Core: " + coreVersion + " / IRC-Plugin: " + ircPluginVersion;

                     buf.append(version + " | ");
                     String verbindungstatus = null;

                     switch(info.getVerbindungsStatus())
                     {

                        case Information.VERBUNDEN:
                        {
                           int serverId = ajFassade.getNetworkInfo().getConnectedWithServerId();

                           try
                           {
                              Server curServer = (Server) ajFassade.getObjectById(serverId);

                              verbindungstatus = "Server: " + curServer.getName();
                           }
                           catch(IllegalArgumentException e1)
                           {
                              verbindungstatus = "Verbindung: verbunden";
                           }

                           break;
                        }

                        case Information.NICHT_VERBUNDEN:
                        {
                           verbindungstatus = "Verbindung: nicht verbunden";
                           break;
                        }

                        case Information.VERSUCHE_ZU_VERBINDEN:
                        {
                           verbindungstatus = "Verbindung: versuche zu verbinden";
                           break;
                        }
                     }

                     if(null != verbindungstatus)
                     {
                        buf.append(verbindungstatus + " | ");
                     }

                     buf.append("Aktueller DL:" + info.getUpDownAsString() + " | ");
                     buf.append("Gesamt-DL:" + info.getUpDownSessionAsString() + " |");
                     buf.append(info.getCreditsAsString() + " | ");
                     buf.append("offene Verbindungen: " + info.getOpenConnections() + " | ");

                     AJSettings ajSettings = ajFassade.getCurrentAJSettings();

                     buf.append("erlaubte Verbindungen: " + ajSettings.getMaxConnections() + " | ");
                     buf.append("max. neue Verbindungen/10 Seks: " + ajSettings.getMaxNewConnectionsPerTurn() + " | ");
                     buf.append("max. DL: " + ajSettings.getMaxDownloadInKB() + " KB/s | ");
                     buf.append("max. UP: " + ajSettings.getMaxUploadInKB() + " KB/s | ");
                     buf.append("max. Quellen/Datei: " + ajSettings.getMaxSourcesPerFile() + " | ");
                     buf.append("max. KB/Uploadslot: " + ajSettings.getSpeedPerSlot() + " KB/s");

                     parentPanel.parseSendToCommand("PRIVMSG " + name + " :" + buf.toString());
                     updateTextArea(parentPanel.formatNickname("<" + parentPanel.getNickname() + "> ") + buf.toString());
                  }
               }
               else if(message.toLowerCase().compareTo("/ajversinfo") == 0)
               {
                  if(currentTime > lastVersionPrinted + 60000)
                  {
                     lastVersionPrinted = currentTime;
                     String coreVersion = AppleJuiceClient.getAjFassade().getCoreVersion().getVersion();
                     String guiVersion = AppleJuiceDialog.GUI_VERSION;
                     String text       = "Java-GUI: " + guiVersion + " / Core: " + coreVersion;

                     parentPanel.parseSendToCommand("PRIVMSG " + name + " :" + text);
                     updateTextArea(parentPanel.formatNickname("<" + parentPanel.getNickname() + "> ") + text);
                  }
               }
            }
            else
            {
               parentPanel.analyzeCommand(message);
            }

            textField.setText("");
         }
         else
         {
            // A private message to channel!
            parentPanel.parseSendToCommand("PRIVMSG " + name + " :" + message);

            // let's update textArea
            // textArea.append(textField.getText() + "\n");
            updateTextArea(parentPanel.formatNickname("<" + parentPanel.getNickname() + "> ") + textField.getText());
            textField.setText("");
         }

         if(message != null && message.length() > 0)
         {
            befehle.add(message);
            if(befehle.size() > 40)
            {
               befehle.remove(0);
            }

            befehlPos = befehle.size() - 1;
         }
      }
      else if(source == closeButton)
      {
         parentPanel.closeChannel(parentPanel.getTabbedPane(), name);

         // Let's send a sensible message
         parentPanel.parseSendToCommand("PART " + name);
      }
   }

   public void setTitleArea(String title)
   {
      StyledDocument     doc        = titleArea.getStyledDocument();
      SimpleAttributeSet attributes = new SimpleAttributeSet();

      StyleConstants.setBackground(attributes, Color.WHITE);
      try
      {
         doc.remove(0, doc.getLength());
         int startIndex = 0;

         for(int i = 0; i < title.length(); i++)
         {
            if(title.charAt(i) == 3 || i == title.length() - 1)
            {
               if(title.charAt(i) == 3)
               {
                  String toWrite = title.substring(startIndex, i);

                  if(toWrite.length() > 0)
                  {
                     attributes = writeString(doc, attributes, toWrite);
                  }

                  startIndex = i + 1;
                  i++;
               }
               else
               {
                  String toWrite = title.substring(startIndex);

                  if(toWrite.length() > 0)
                  {
                     attributes = writeString(doc, attributes, toWrite);
                  }
               }
            }
         }
      }
      catch(BadLocationException ex)
      {
         ex.printStackTrace();
      }
   }

   private SimpleAttributeSet writeString(StyledDocument doc, SimpleAttributeSet attributes, String toWrite)
   {
      boolean istNachkomma = false;
      boolean parsEnde = false;
      int     index    = 0;

      if(toWrite.length() > 1)
      {
         while(!parsEnde)
         {
            try
            {
               if(toWrite.charAt(index) == ',')
               {
                  istNachkomma = true;
                  index++;
                  continue;
               }

               int colorCode = Integer.parseInt(toWrite.substring(index, index + 2));

               index += 2;
               Color color = getColor(colorCode);

               if(!istNachkomma)
               {
                  StyleConstants.setForeground(attributes, color);
               }
            }
            catch(NumberFormatException nfE)
            {
               if(toWrite.charAt(index + 1) != ',')
               {
                  parsEnde = true;
               }

               try
               {
                  int colorCode = Integer.parseInt(toWrite.substring(index, index + 1));

                  index++;
                  Color color = getColor(colorCode);

                  if(!istNachkomma)
                  {
                     StyleConstants.setForeground(attributes, color);
                  }
               }
               catch(NumberFormatException nfE2)
               {
                  parsEnde = true;
                  StyleConstants.setForeground(attributes, Color.BLACK);
               }
            }
            catch(StringIndexOutOfBoundsException sioobE)
            {
               parsEnde = true;
               try
               {
                  int   colorCode = Integer.parseInt(toWrite.substring(index, index + 1));
                  Color color = getColor(colorCode);

                  if(!istNachkomma)
                  {
                     StyleConstants.setForeground(attributes, color);
                  }
               }
               catch(NumberFormatException nfE2)
               {
                  StyleConstants.setForeground(attributes, Color.BLUE);
               }
               catch(StringIndexOutOfBoundsException nfE2)
               {
                  StyleConstants.setForeground(attributes, Color.BLACK);
               }

               return attributes;
            }
         }
      }

      try
      {
         doc.insertString(doc.getLength(), toWrite.substring(index), attributes);
      }
      catch(BadLocationException ex)
      {
         ex.printStackTrace();
      }

      return attributes;
   }

   private Color getColor(int code)
   {
      switch(code)
      {

         case 0:
            return Color.BLACK;

         case 1:
            return Color.BLACK;

         case 2:
            return Color.BLUE;

         case 3:
            return Color.GREEN;

         case 4:
            return Color.RED;

         case 5:
            return Color.BLACK;

         case 6:
            return Color.PINK;

         case 7:
            return Color.ORANGE;

         case 8:
            return Color.YELLOW;

         case 9:
            return Color.GREEN;

         case 10:
            return Color.GREEN;

         case 11:
            return Color.CYAN;

         case 12:
            return Color.BLUE;

         case 13:
            return Color.PINK;

         case 14:
            return Color.GRAY;

         case 15:
            return Color.LIGHT_GRAY;

         default:
            return Color.BLACK;
      }
   }

   public void updateTextArea(String message, boolean withTimeStamp)
   {
      Document           doc              = textArea.getDocument();
      int                oldCaretPosition = textArea.getCaretPosition();
      SimpleAttributeSet attributes       = new SimpleAttributeSet();

      StyleConstants.setBackground(attributes, Color.WHITE);
      StyleConstants.setForeground(attributes, Color.BLACK);
      int     index        = message.indexOf('>');
      String  compareValue;
      boolean eigenerName = false;
      boolean doMark      = false;

      if(index != -1 && message.length() - 1 > index)
      {
         compareValue = message.substring(index + 1);
      }
      else
      {
         compareValue = message;
      }

      boolean isAction = false;

      if(compareValue.indexOf(parentPanel.getNickname()) != -1)
      {
         StyleConstants.setBackground(attributes, Color.ORANGE);
         eigenerName = true;
         doMark      = true;
      }
      else if(message.indexOf("---> JOIN:") != -1)
      {
         StyleConstants.setForeground(attributes, JOIN_GREEN);
      }
      else if(message.indexOf("<--- PART:") != -1)
      {
         StyleConstants.setForeground(attributes, Color.RED);
      }
      else if(message.indexOf("<--- QUIT:") != -1)
      {
         StyleConstants.setForeground(attributes, Color.RED);
      }
      else if(message.indexOf('>') == 0)
      {
         StyleConstants.setForeground(attributes, Color.MAGENTA);
      }
      else if(message.startsWith("* "))
      {
         isAction = true;
      }
      else if(!withTimeStamp || index == -1)
      {
         StyleConstants.setForeground(attributes, Color.GRAY);
      }
      else
      {
         StyleConstants.setForeground(attributes, Color.BLACK);
         doMark = true;
      }

      try
      {
         if(withTimeStamp)
         {
            String zeit = dateFormatter.format(new Date(System.currentTimeMillis()));

            doc.insertString(doc.getLength(), "[" + zeit + "]\t", attributes);
         }

         if(isAction)
         {
            Color color = StyleConstants.getForeground(attributes);

            StyleConstants.setForeground(attributes, Color.MAGENTA);
            doc.insertString(doc.getLength(), "\t*", attributes);
            StyleConstants.setForeground(attributes, color);
            message = message.substring(1).trim();
         }

         parseLinks(attributes, doc, message);
      }
      catch(BadLocationException blE)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error("Fehler im IrcPlugin", blE);
         }
      }

      if(!selected && doMark)
      {
         if(eigenerName)
         {
            for(int i = 0; i < tabbedPane.getTabCount(); i++)
            {
               Object tab = tabbedPane.getComponentAt(i);

               if(tab == this)
               {
                  tabbedPane.setForegroundAt(i, Color.GREEN);
               }
            }
         }

         if(!eigenerName && !marked)
         {
            for(int i = 0; i < tabbedPane.getTabCount(); i++)
            {
               Object tab = tabbedPane.getComponentAt(i);

               if(tab == this)
               {
                  tabbedPane.setForegroundAt(i, Color.RED);
               }
            }
         }

         marked = true;
      }

      int newCaretPosition = doc.getLength();

      if(newCaretPosition != oldCaretPosition)
      {
         textArea.setCaretPosition(newCaretPosition);
      }

      usernameList.reorder();
   }

   private void parseLinks(SimpleAttributeSet attributes, Document doc, String message)
                    throws BadLocationException
   {
      while(message.toLowerCase().indexOf(HTTP_IDENTIFIER) != -1 || message.toLowerCase().indexOf(WWW_IDENTIFIER) != -1)
      {
         String httpIdentifier;
         int    indexHttp = message.toLowerCase().indexOf(HTTP_IDENTIFIER);
         int    indexWww  = message.toLowerCase().indexOf(WWW_IDENTIFIER);

         if(indexHttp == -1)
         {
            httpIdentifier = WWW_IDENTIFIER;
         }
         else if(indexWww == -1)
         {
            httpIdentifier = HTTP_IDENTIFIER;
         }
         else
         {
            if(indexWww == indexHttp + HTTP_IDENTIFIER.length())
            {
               httpIdentifier = HTTP_IDENTIFIER;
            }
            else
            {
               httpIdentifier = WWW_IDENTIFIER;
            }
         }

         doc.insertString(doc.getLength(), message.substring(0, message.toLowerCase().indexOf(httpIdentifier)), attributes);
         message = message.substring(message.toLowerCase().indexOf(httpIdentifier));
         int index  = message.indexOf(" ");
         int index2 = message.indexOf(">");

         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         index2 = message.indexOf(")");
         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         index2 = message.indexOf("]");
         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         index2 = message.indexOf("}");
         if(index2 != -1 && index2 < index)
         {
            index = index2;
         }

         if(index != -1)
         {
            doc.insertString(doc.getLength(), message.substring(0, index), setLink(message.substring(0, index), attributes));
            message = message.substring(index);
            if(message.indexOf(httpIdentifier, index) == -1)
            {
               break;
            }
         }
         else
         {
            doc.insertString(doc.getLength(), message, setLink(message, attributes));
            message = "";
         }
      }

      doc.insertString(doc.getLength(), message + "\n", attributes);
   }

   public void updateTextArea(String message)
   {
      updateTextArea(message, true);
   }

   public void updateUserArea(String username, String command)
   {
      if(command.equals("add"))
      {
         User user;

         if(username.indexOf('!') == 0 || username.indexOf('@') == 0 || username.indexOf('%') == 0 || username.indexOf('+') == 0)
         {
            char mod = username.charAt(0);

            username = username.substring(1);
            user     = new User(username);
            switch(mod)
            {

               case '!':
               {
                  user.setAdmin(true);
                  break;
               }

               case '@':
               {
                  user.setOp(true);
                  break;
               }

               case '%':
               {
                  user.setHalfop(true);
                  break;
               }

               case '+':
               {
                  user.setVoice(true);
                  break;
               }
            }
         }
         else
         {
            user = new User(username);
         }

         usernameList.add(user);
      }

      else if(command.equals("remove"))
      {
         if(username.indexOf('!') == 0 || username.indexOf('@') == 0 || username.indexOf('%') == 0 || username.indexOf('+') == 0)
         {
            username = username.substring(1);
         }

         User[] users = usernameList.getValues();
         User   user = null;

         for(int i = 0; i < users.length; i++)
         {
            if(users[i].getName().equalsIgnoreCase(username))
            {
               user = users[i];
               break;
            }
         }

         if(user != null)
         {
            usernameList.remove(user);
         }
      }

      updateCounter();
   }

   public void userQuits(String nick, boolean hide)
   {
      User nickToRemove = null;

      synchronized(usernameList)
      {
         User[] users = usernameList.getValues();

         for(int i = 0; i < users.length; i++)
         {
            if(users[i].getName().equalsIgnoreCase(nick))
            {
               nickToRemove = users[i];
               break;
            }
         }
      }

      if(nickToRemove != null)
      {
         usernameList.remove(nickToRemove);
      }

      if(nickToRemove != null && !hide)
      {
         updateTextArea("<--- QUIT: " + nickToRemove);
      }
   }

   public void renameUser(String oldNick, String newNick)
   {
      User searchedNick = null;

      synchronized(usernameList)
      {
         User[] users        = usernameList.getValues();
         String compareValue;

         for(int i = 0; i < users.length; i++)
         {
            if(users[i].getName().compareToIgnoreCase(oldNick) == 0)
            {
               searchedNick = users[i];
               break;
            }
         }
      }

      if(searchedNick != null)
      {
         searchedNick.setName(newNick);
         usernameList.reorder();
         updateTextArea(oldNick + " changes nickname to " + newNick);
      }
   }

   private void updateCounter()
   {
      counter.setText(usernameList.getOpCount() + " Ops, " + usernameList.getSize() + " " + counterText);
   }

   public void fireLanguageChanged()
   {
      counterText = parentPanel.getPlugin().getLanguageString(".root.language.channel.gesamt");
      updateCounter();
   }
}
