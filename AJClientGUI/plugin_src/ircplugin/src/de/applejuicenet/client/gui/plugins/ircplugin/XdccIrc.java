package de.applejuicenet.client.gui.plugins.ircplugin;

import java.io.*;
import java.net.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.shared.SwingWorker;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/XdccIrc.java,v 1.4 2003/10/27 16:01:24 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: XdccIrc.java,v $
 * Revision 1.4  2003/10/27 16:01:24  maj0r
 * Benutzerliste wird nun bei Veränderung aktualisiert und halbwegs richtig sortiert (Status wird noch nicht berücksichtigt).
 *
 * Revision 1.3  2003/10/27 14:48:36  maj0r
 * Bug #833 fixed (Danke an plutoman2):
 * Threadverwendung korrigiert.
 *
 * Revision 1.2  2003/10/27 14:08:39  maj0r
 * Bug #1030 fixed (Danke an plutoman2):
 * Nullpointer behoben.
 *
 * Revision 1.1  2003/09/12 06:32:17  maj0r
 * Nur verschoben.
 *
 * Revision 1.5  2003/08/29 06:49:32  maj0r
 * NullPointer behoben.
 * Version 1.21
 *
 * Revision 1.4  2003/08/28 16:19:29  maj0r
 * Auf SwingWorker umgebaut und Hauptklasse umbenannt.
 *
 * Revision 1.3  2003/08/28 15:53:02  maj0r
 * NullPointer behoben und Header eingefuegt.
 *
 *
 */

public class XdccIrc
        extends JPanel {
    private JButton connectButton;
    private JButton cancelButton;
    private JButton editButton;
    private JButton removeButton;

    private Thread ircWorker;

    String host = "localhost";
    int port = 6667;
    JTabbedPane tabbedPane;
    //XdccTree xdccTree;
    // 5 = # of DCC arguments and 128 = max number of DCCs
    String[][] xdccInfos = new String[128][5];
    //0 = port
    //1 = address
    //2 = Filename
    //3 = File Size
    //4 = Resume Position
    int numDcc = 0;
    String nickname;
    String realname;

    // Action to join a Channel
    private JButton joinChannelAction;
    private JButton createConnection;
    private JButton changeNickAction;
    private JButton whoisAction;
    private JButton newUserAction;

    private JDialog dialog;
    JTextField nickJTextField1;
    JTextField nickJTextField2;
    JTextField nickJTextField3;

    String nickname1, nickname2, nickname3;

    // For communicating purposes. You write to 'toServer' to
    // send something to the IRC server. For example:
    //       toServer.print("A dummy message")
    // should send a message to IRC server
    // 'fromServer' reads something from the IRC server.
    private Socket chatSocket;
    BufferedReader fromServer;
    PrintWriter toServer;

    // For debugging purpose only
    PrintWriter debugOut;

    /**
     * Main Constructor. Here,
     *   host: the IRC hostname. Should be a valid hostname
     *   port: the port where IRC server is listening
     *         generally this is port 6667
     */
    public XdccIrc() {
        setLayout(new BorderLayout());

        createConnection = new JButton("Connect");
        createConnection.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                makeConnectionInfo();
            }
        });
        newUserAction = new JButton("Talk Private");
        newUserAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                newUser();
            }
        });
        joinChannelAction = new JButton("Join a Channel");
        joinChannelAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                joinChan();
            }
        });
        changeNickAction = new JButton("Change nick");
        changeNickAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                changeNick();
            }
        });
        whoisAction = new JButton("Nick Info");
        whoisAction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                whois();
            }
        });

        tabbedPane = createTabbedPane();

        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Let's create a ToolBar
        panel1.add(createConnection);
        panel1.add(newUserAction);
        panel1.add(joinChannelAction);
        panel1.add(changeNickAction);
        panel1.add(whoisAction);

        add(panel1, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // mandatory things to do!
        //setBounds(50, 50, 600, 400);
        Toolkit theKit = java.awt.Toolkit.getDefaultToolkit();
        Dimension dm = theKit.getScreenSize();
        setBounds(dm.width / 6, dm.height / 6,
                  (dm.width * 5) / 8, // width
                  (dm.height * 2) / 3 // height
        );

        setVisible(true);
    }

    class WindowCloser
            extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            Window win = e.getWindow();
            win.setVisible(false);
            if (toServer != null)
            {
                parseSendToCommand("QUIT ConnectionClosed.");
            }
            System.exit(0);
        }
    }

    public void connectStartRegister() {
        connect();
        start();
        register();
    }

    private void makeConnectionInfo() {
        dialog = new JDialog(AppleJuiceDialog.getApp(), " Connection Info ", true);
        Border etched = BorderFactory.createEtchedBorder();

        Container dialogContentPane = dialog.getContentPane();

        // Ok, let's make the UserInfo
        JPanel userInfo = new JPanel();
        userInfo.setBorder(makeBorder(" User Info "));
        JLabel label1 = new JLabel("Nickname1: ");
        JLabel label2 = new JLabel("Nickname2: ");
        JLabel label3 = new JLabel("Nickname3: ");

        nickJTextField1 = new JTextField(25);
        nickJTextField1.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                if (nickJTextField1.getText().length() != 0)
                {
                    connectButton.setEnabled(true);
                }
                else
                {
                    connectButton.setEnabled(false);
                }
            }
        });
        nickJTextField2 = new JTextField(25);
        nickJTextField3 = new JTextField(25);

        Box box1 = Box.createHorizontalBox();
        box1.add(label1);
        box1.add(nickJTextField1);

        Box box2 = Box.createHorizontalBox();
        box2.add(label2);
        box2.add(nickJTextField2);

        Box box3 = Box.createHorizontalBox();
        box3.add(label3);
        box3.add(nickJTextField3);

        Box box4 = Box.createVerticalBox();
        box4.add(box1);
        box4.add(box2);
        box4.add(box3);
        Random random = new Random();
        if (nickname != null)
            nickJTextField1.setText(nickname);
        else
            nickJTextField1.setText("");
        if (nickname2 != null)
            nickJTextField2.setText(nickname2);
        else
            nickJTextField2.setText("");
        if (nickname3 != null)
            nickJTextField3.setText(nickname3);
        else
            nickJTextField3.setText("");

        userInfo.add("Center", box4);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black, 1),
                BorderFactory.createRaisedBevelBorder()
        ));

        connectButton = new JButton("Connect");
        cancelButton = new JButton("Cancel");
        editButton = new JButton("Edit");
        removeButton = new JButton("Remove");

        connectButton.setBorder(makeButtonBorder());
        connectButton.setEnabled(false);
        cancelButton.setBorder(makeButtonBorder());
        editButton.setBorder(makeButtonBorder());
        removeButton.setBorder(makeButtonBorder());

        // Action Listener
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // OK, let's set the nickname if user has mentioned it
                if (nickJTextField1.getText().length() == 0)
                {
                    dialog.dispose();
                    return;
                }

                if (nickJTextField1.getText() != null)
                {
                    setNickname(nickJTextField1.getText());
                }
                if (nickJTextField1.getText() == null)
                {
                    Random random = new Random();
                    setNickname("XDCC" + Integer.toString(random.nextInt(99999)));
                }
                if (nickJTextField2.getText() != null)
                {
                    nickname2 = nickJTextField2.getText();
                }

                if (nickJTextField3.getText() != null)
                {
                    nickname3 = nickJTextField3.getText();
                }

                host = "irc.bongster.de";
                tabUpdate("Init Window", " Connecting to: irc.bongster.de");
                System.out.println("Connecting to: irc.bongster.de");
                connectStartRegister();
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                dialog = null;
            }
        });

        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(editButton);

        dialogContentPane.add("North", userInfo);
        dialogContentPane.add("South", buttonPanel);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.show();
    }

    private Border makeButtonBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(2, 4, 2, 4)
        );
    }

    private Border makeBorder(String title) {
        Border etched = BorderFactory.createEtchedBorder();
        Border border = BorderFactory.createTitledBorder
                (
                        etched, title, TitledBorder.CENTER,
                        TitledBorder.TOP
                );
        return border;
    }

    /**
     * Connect tries to establish a connection to host in port
     */
    private void connect() {
        try
        {
            chatSocket = new Socket(host, port);
            fromServer = new BufferedReader(new InputStreamReader(
                    chatSocket.getInputStream()));
            toServer = new PrintWriter(new OutputStreamWriter(
                    chatSocket.getOutputStream()));
            Component aComponent = tabbedPane.getComponentAt(0);
            ((InitPanel) aComponent).setTitleArea(nickname + " connected to: " +
                                                  host + ":" + port);
            //debugOut = new PrintWriter(new FileWriter(new File("Debug.txt")));
        }
        catch (IOException e)
        {
            System.err.println("Connection refused to: " + host + ":" + port);
        }
    }

    private void register() {

        realname = nickname;

        parseSendToCommand("PASS test");
        parseSendToCommand("NICK " + nickname);
        parseSendToCommand("USER " + nickname + " 0 * :" + realname);
    }

    // sends back your current nickname
    public String getNickname() {
        return nickname;
    }

    // setNickname sets your current nickname
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void changeNickname(String oldNickname, String newNickname) {
        int totalTabs = tabbedPane.getTabCount();

        for (int i = 0; i < totalTabs; i++)
        {
            Component aComponent = tabbedPane.getComponentAt(i);

            if (aComponent instanceof ChannelPanel)
            {
                // Do channel processing
                // At first remove the old nickname from the userNameBox
                ((ChannelPanel) aComponent).updateUserArea(oldNickname, "remove");
                ((ChannelPanel) aComponent).updateUserArea(newNickname, "add");

                // Now let's post a sensible comment on the channel Panel
                ((ChannelPanel) aComponent).updateTextArea(oldNickname +
                                                           " changes nickname to " +
                                                           newNickname);
            }
            else if (aComponent instanceof UserPanel)
            {
                // Do UserPanel processing
                // Actually this process was done in the NICK handling part....
            }
        }
    }

    // A ad-hoc method to format all the nicknames!
    // I wanted to format this as 'printf' in C. However,
    // it fails miserably when output to 'TextArea' ---
    // console output is OK, though.
    public String formatNickname(String nickname) {
        int formatlen = 12;
        String blank = "";
        int len = nickname.length();

        if (len >= formatlen)
        {
            return nickname;
        }
        else
        {
            for (int i = 0; i < (formatlen - len); i++)
            {
                blank = blank + " ";
            }
        }

        return nickname + blank;
    }

    // send back realname. Did I need it really?
    public String getRealname() {
        return realname;
    }

    public void start() { //synchronized void start()
        ircWorker = new Thread() {
            public void run() {
                try
                {
                    while (!isInterrupted())
                    {
                        String line = fromServer.readLine();
                        if (line != null)
                        {
                            parseFromServer(line);
                        }
                        else
                        {
                            // a null line indicates that our server has
                            // closed the connection, right?
                            tabUpdate("Init Window",
                                      "READ a null line, server closed the connection or network is fucked.");
                            closeAll();

                            try
                            {
                                sleep(5000);
                            }
                            catch (InterruptedException e)
                            {
                                interrupt();
                            }
                        }
                    }
                }

                catch (IOException e)
                {
                    System.out.println(
                            "Read Exception from Server. Exception is something like:");
                    System.out.println(e);
                }
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        connectStartRegister();
                    }
                });
            }
        };
        ircWorker.start();
    }

    // Close everything and sleeps for few seconds
    public void closeAll() {
        // So, let's shutdown everything
        try
        {
            chatSocket.close();
            fromServer.close();
            toServer.close();
        }
        catch (IOException e)
        {
            System.out.println("chatSocket.close() thrown an IOException or");
            System.out.println("fromServer.close() thrown an IOException or");
            System.out.println("toServer.close() thrown an IOException");
        }

        chatSocket = null;
        fromServer = null;
        toServer = null;
    }

    // Well this method is the most responsible party in
    // processing IRC server 'replies' or 'queries'.
    public void parseFromServer(String lineFromServer) throws IOException {
        myParser parser = new myParser(lineFromServer);
        String command = parser.getCommand();
        if (command.equals("PING"))
        {
            parseSendToCommand("PONG :" + parser.getTrailing());
        }
        else if (command.equals("JOIN"))
        {
            String channelName = parser.getTrailing();
            if (channelName.startsWith("#"))
            {
                channelName = channelName.substring(1);
                int indexOfChannel = findTab(tabbedPane, "#" + channelName);
                if (indexOfChannel == -1)
                { // Channel Tab doesn't exist
                    ChannelPanel channel = addChannel(tabbedPane, channelName);
                    indexOfChannel = findTab(tabbedPane, "#" + channelName);
                    tabbedPane.setSelectedIndex(indexOfChannel);
                    channel.updateTextArea("*** Now talking in: " + parser.getTrailing());
                    if (tabbedPane.getSelectedIndex() !=
                            findTab(tabbedPane, "#" + channelName))
                    {
                        tabbedPane.setBackgroundAt(findTab(tabbedPane, "#" + channelName),
                                                   Color.red);
                    }
                    tabbedPane.revalidate();

                    // channel.updateUserArea(parser.getNick(), "add");
                }
                else
                { // channelName tab exists!
                    ChannelPanel channel = (ChannelPanel) tabbedPane.getComponentAt(
                            indexOfChannel);
                    channel.updateTextArea("---> JOIN: " + parser.getNick() + " (" +
                                           parser.getUser() + "@" +
                                           parser.getHost() + ")");
                    if (tabbedPane.getSelectedIndex() != indexOfChannel)
                    {
                        tabbedPane.setBackgroundAt(indexOfChannel, Color.red);
                    }
                    tabbedPane.revalidate();

                    channel.updateUserArea(parser.getNick(), "add");
                }
            }
        }
        else if (command.equals("PRIVMSG"))
        {
            // handle PRIVMSG
            String destination = parser.getMiddle();
            if (destination.startsWith("#"))
            {
                // it's a channel!
                String channelName = destination.substring(1);

// MSG Received....!

                int indexOfChannel = findTab(tabbedPane, destination);
                if (indexOfChannel != -1)
                { // A connection/tab already exists!
                    Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                    if (aComponent instanceof ChannelPanel)
                    {
                        String trailing;
                        String nick;
                        nick = parser.getNick();
                        trailing = parser.getTrailing();
                        if (trailing.indexOf(1) != -1)
                        { //is this a CTCP
                            //remove \001 from Beginning and End of CTCP msg
                            trailing = trailing.substring(1, trailing.length() - 1);
                            ((ChannelPanel) aComponent).updateTextArea(
                                    formatNickname("[" + nick + "] ") + trailing);
                        }
                        String bracketFermeture = "a";
                        if (trailing.indexOf("[") != -1)
                        {
                            bracketFermeture = String.valueOf(trailing.charAt(trailing.
                                                                              indexOf("[") + 5));
                            //System.out.println("bracketFermeture = " + bracketFermeture + " trailing = " + trailing);
                        }
                        if (nick.equals(nickname))
                        {
                            //System.out.println("Color ReD!");
                            //((ChannelPanel)aComponent).textArea.setDisabledTextColor(new Color(255,0,0));
                            ((ChannelPanel) aComponent).updateTextArea(
                                    formatNickname("<" + nick + "> ") + trailing);
                        }
                        else
                        {
                            //System.out.println("Color GreeN!");
                            //((ChannelPanel)aComponent).textArea.setDisabledTextColor(new Color(0,0,255));
                            ((ChannelPanel) aComponent).updateTextArea(
                                    formatNickname("<" + nick + "> ") + trailing);
                        }
                        if (tabbedPane.getSelectedIndex() != indexOfChannel)
                        {
                            tabbedPane.setBackgroundAt(indexOfChannel, Color.red);
                        }
                        tabbedPane.revalidate();
                    }
                }
                else
                { // A connection/tab doesn't exists, so create one!
                    ChannelPanel channel = addChannel(tabbedPane, channelName);
                    channel.updateTextArea(
                            formatNickname("<" + parser.getNick() + "> ") + "> " +
                            parser.getTrailing());
                    if (tabbedPane.getSelectedIndex() !=
                            findTab(tabbedPane, "#" + channelName))
                    {
                        tabbedPane.setBackgroundAt(findTab(tabbedPane, "#" + channelName),
                                                   Color.red);
                    }
                    tabbedPane.revalidate();
                }

            } //if (destination.startsWith("#"))
            else
            { //if (!destination.equalsIgnoreCase(getNickname()))
                // it's from a user

                int indexOfUser = findTab(tabbedPane, parser.getNick());

                if (indexOfUser == -1)
                {
                    UserPanel userPanel = addUser(tabbedPane, parser.getNick()); //if tab doesnt exist
                    // Add the real username@hostname in title of tabbedPane
                    userPanel.setTitleArea(parser.getNick() + "!" + parser.getUser() +
                                           "@" + parser.getHost());
                }

                indexOfUser = findTab(tabbedPane, parser.getNick());

                if (indexOfUser != -1)
                { // A user connection/tab already exists!
                    Component aComponent = tabbedPane.getComponentAt(indexOfUser);
                    UserPanel userPanel = (UserPanel) aComponent;

                    if (aComponent instanceof UserPanel)
                    {
                        String ctcpCommand;
                        String trailing;
                        String dccPort;
                        String dccType;

                        StringTokenizer st;
                        char firstOne = 1;

                        trailing = parser.getTrailing();
                        if (trailing.indexOf(1) != -1)
                        { //is this a CTCP
                            if (trailing.indexOf(" ") != -1)
                            {
                                ctcpCommand = trailing.substring(1, trailing.indexOf(" "));
                            }
                            else
                            {
                                ctcpCommand = trailing.substring(1, trailing.indexOf(1, 2));
                            }

                            if (ctcpCommand.equalsIgnoreCase("PING"))
                            {
                                parseSendToCommand("NOTICE " + parser.getNick() + " " +
                                                   trailing);
                            }

                            else if (ctcpCommand.equalsIgnoreCase("VERSION"))
                            {
                                String versionReply = "NOTICE " + parser.getNick() + " " +
                                        firstOne + ctcpCommand + " Using Xdcc IRC:v0.1b:Java" +
                                        firstOne;
                                parseSendToCommand(versionReply);

                            }

                            userPanel.updateTextArea(
                                    formatNickname("••• ¢ CTCP " + ctcpCommand +
                                                   " received from " + parser.getNick()));
                        } // if (trailing.indexOf(1) != -1)  //is this a CTCP

                        else
                        { // regular privmsg
                            userPanel.updateTextArea(
                                    formatNickname("<" + parser.getNick() + "> ") + trailing);

                            //update le tout
                        }
                        if (tabbedPane.getSelectedIndex() != indexOfUser)
                        {
                            tabbedPane.setBackgroundAt(indexOfUser, Color.red);
                        }
                        tabbedPane.revalidate();
                    } //if (aComponent instanceof UserPanel)
                } // if (indexOfUser != -1)
            } // else (Msg from a user..)
        } //else if (command.equals("PRIVMSG"))

        else if (command.equals("PART"))
        {
            // Let's grab the channel name
            String channelName = parser.getParams();
            int index = channelName.indexOf("#");
            int index2 = channelName.indexOf(":");
            if (index2 != -1)
            {
                channelName = channelName.substring(index, index2 - 1);
            }
            else
            {
                channelName = channelName.substring(index);
            }

            if (channelName.startsWith("#"))
            {
                channelName = channelName.substring(1);

                int indexOfChannel = findTab(tabbedPane, "#" + channelName);
                if (indexOfChannel != -1)
                { // Tab doesn't exist. Do nothing
                    Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                    if (aComponent instanceof ChannelPanel)
                    {
                        ((ChannelPanel) aComponent).updateTextArea("<--- PART: " +
                                                                   parser.getNick());
                        ((ChannelPanel) aComponent).updateUserArea(parser.getNick(),
                                                                   "remove");
                    }

                    // Ok, let's remove the tab if the user who left the channel is you
                    if (parser.getNick().equals(getNickname()))
                    {
                        tabbedPane.removeTabAt(indexOfChannel);
                    }
                }
            }

        }
        else if (command.equals("QUIT"))
        {
            //??????????????????????????
            //Find what channel user is on and direct Quit msg to correct channel(s)
            tabUpdate("Init Window",
                      parser.getNick() + " has quit (" + parser.getTrailing() + ")");
        }
        else if (command.equals("NICK"))
        {
            // handle NICK --- When a user changes its nickname
            int index = findTab(tabbedPane, parser.getNick());
            if (index != -1)
            {
                // setting the user tab title to changed nickname
                tabbedPane.setTitleAt(index, parser.getTrailing());

                // Let's change the name of the UserPanel alsow
                UserPanel temp = (UserPanel) tabbedPane.getComponentAt(index);
                temp.setName(parser.getTrailing());
            }

            if (parser.getNick().equalsIgnoreCase(getNickname()))
            {
                setNickname(parser.getTrailing());
            }

            // When a user changes his/her name, his name in every
            // UserPanel or ChannelPanel should be changed too.
            // So, we are doing it now --- here
            String oldNickname = parser.getNick();
            String newNickname = parser.getTrailing();

            changeNickname(oldNickname, newNickname);
        }
        else if (command.equals("NOTICE"))
        {
            // handle NOTICE (à arranger: savoir de qui elle vient et la mettre au bon endroit)
            tabUpdate("Init Window",
                      "NOTICE FROM: " + parser.getNick() + " ---> " +
                      parser.getTrailing());
        }
        else if (command.equals("KICK"))
        {
            // handle KICK
            String kickedNick;
            String kickedChannel;
            String temp;
            temp = parser.getMiddle();
            kickedChannel = temp.substring(0, temp.indexOf(" "));
            kickedNick = temp.substring(temp.indexOf(" ") + 1, temp.length());
            int indexOfChannel = findTab(tabbedPane, kickedChannel);
            ChannelPanel channel = (ChannelPanel) tabbedPane.getComponentAt(
                    indexOfChannel);
            if (!(kickedNick.equals(getNickname())))
            {
                channel.updateTextArea("<- KICK: " + kickedNick + " by " +
                                       parser.getNick() + " (" + parser.getTrailing() +
                                       ")");
                channel.updateUserArea(kickedNick, "remove");
            }
            else
            { // You have been kicked...
                tabbedPane.removeTabAt(indexOfChannel);
                tabUpdate("Init Window",
                          "You have been kicked from " + kickedChannel + " by " +
                          parser.getNick() + " because " + parser.getTrailing());
            }
        }
        else if (command.equals("001") || // RPL_WELCOME
                command.equals("002") || // RPL_YOURHOST
                command.equals("003") || // RPL_CREATED
                command.equals("004"))
        { // RPL_MYINFO
            // successful registration
            tabUpdate("Init Window", parser.getTrailing());
        }
/*        else if (command.equals("005"))
        {
            // RPL_BOUNCE (Server settings)
            tabUpdate("Init Window", "005 " + lineFromServer);
            tabUpdate("Init Window", "005 " + parser.getTrailing());
            System.out.println(parser.getTrailing());
        }*/
        else if (command.equals("250") ||
                command.equals("251") ||
                command.equals("253") ||
                command.equals("255") ||
                command.equals("256") ||
                command.equals("257") ||
                command.equals("258") ||
                command.equals("259") ||
                command.equals("265") ||
                command.equals("266")
        )
        {
            tabUpdate("Init Window", parser.getTrailing());
        }

//252 & 254 = num IRCOP and Total Channels
        else if (command.equals("252"))
        {
            String numThing;
            numThing = lineFromServer.substring(lineFromServer.indexOf("252") + 5 +
                                                nickname.length(),
                                                lineFromServer.indexOf(":", 2));
            tabUpdate("Init Window", numThing + " " + parser.getTrailing());
        }

        else if (command.equals("254"))
        {
            String numThing;
            numThing = lineFromServer.substring(lineFromServer.indexOf("254") + 5 +
                                                nickname.length(),
                                                lineFromServer.indexOf(":", 2));
            tabUpdate("Init Window", numThing + " " + parser.getTrailing());
        }

//channel full, invite only or need a key
        else if (command.equals("471") ||
                command.equals("473") ||
                command.equals("475")
        )
        {
            String channel;
            channel = lineFromServer.substring(lineFromServer.indexOf("#"),
                                               lineFromServer.indexOf(":", 2));
            tabUpdate("Init Window", parser.getTrailing() + " (" + channel + ")");
        }

/*        else if (command.equals("371") ||
                command.equals("372") ||
                command.equals("374") ||
                command.equals("375") ||
                command.equals("376")
        )
        {
            tabUpdate("Init Window", "371-376 " + parser.getTrailing());
            tabUpdate("Init Window", "Skipping MOTD!");
        }*/
        else if (command.equals("311") // RPL_WHOISUSER
        )
        {
            // Ok, let's see how we can process PROCESS
            StringTokenizer st = new StringTokenizer(parser.getParams(), " \r\n");
            //StringTokenizer st = new StringTokenizer(parser.getParams(), " ");
            String whoNick = "",
                    whoUser = "",
                    whoHost = "",
                    whoMode = "",
                    whoRealName = "";

            for (int i = 0; st.hasMoreTokens();)
            {
                String aToken = st.nextToken();
                if (i == 1)
                {
                    whoNick = aToken;
                }
                else if (i == 2)
                {
                    whoUser = aToken;
                }
                else if (i == 3)
                {
                    whoHost = aToken;
                }
                else if (i == 4)
                {
                    whoMode = aToken;
                }
                else if (i == 5)
                {
                    //à vérifier!!!!!!!!!!!!!!
                    String paramsWhois = parser.getParams();
                    whoRealName = paramsWhois.substring(paramsWhois.indexOf(aToken) + 1,
                                                        paramsWhois.length());
                    //whoRealName = aToken.substring(1);
                }

                i++;
            }

            // Let's show the messages in the Init Window
            tabUpdate("Init Window", "Whois info for " + whoNick);
            tabUpdate("Init Window",
                      "  " + whoNick + "!" + whoUser + "@" + whoHost + " " +
                      whoRealName);
        }
        else if (command.equals("312"))
        {
            String whoNick = "",
                    whoServer = "",
                    whoServerInfo = "";

            StringTokenizer st = new StringTokenizer(parser.getParams(), " \r\n");
            for (int i = 0; st.hasMoreTokens();)
            {
                String aToken = st.nextToken();
                if (i == 1)
                {
                    whoNick = aToken;
                }
                else if (i == 2)
                {
                    whoServer = aToken;
                }
                else if (i == 3)
                {
                    //à vérifier!!!!!!!!!!!!!! ainsi que whois realname
                    String paramsWhois = parser.getParams();
                    whoServerInfo = paramsWhois.substring(paramsWhois.indexOf(aToken) + 1,
                                                          paramsWhois.length());
                    //whoServerInfo = aToken.substring(aToken.length());
                }

                i++;
            }

            // Let's update the Init Window
            tabUpdate("Init Window", "  Server: " + whoServer + " " + whoServerInfo);
        }
        else if (command.equals("313"))
        {
            tabUpdate("Init Window", "  Operator: " + lineFromServer);
        }
        else if (command.equals("317"))
        {
            String seconds = "",
                    trailing = "";
            trailing = parser.getTrailing();
            StringTokenizer st = new StringTokenizer(parser.getParams(), " \r\n");
            for (int i = 0; st.hasMoreTokens();)
            {
                String aToken = st.nextToken();
                if (i == 2)
                {
                    seconds = aToken;
                    break;
                }

                i++;
            }
            tabUpdate("Init Window", " Idle time: " + seconds + " " + trailing);
        }
        else if (command.equals("318"))
        {
            tabUpdate("Init Window", parser.getTrailing());
        }
        else if (command.equals("319"))
        {
            tabUpdate("Init Window", "  Channels: " + parser.getTrailing());
        }
        else if (command.equals("301"))
        { // RPL_AWAY
            StringTokenizer st = new StringTokenizer(parser.getParams(), " \r\n");
            String awayNickname = "",
                    awayReason = "";
            for (int i = 0; st.hasMoreTokens();)
            {
                String aToken = st.nextToken();
                if (i == 1)
                {
                    awayNickname = aToken;
                    break;
                }

                i++;
            }
            tabUpdate("Init Window",
                      awayNickname + " is away for \"" + parser.getTrailing() + "\"");
        }
        else if (command.equals("305"))
        { // RPL_UNAWAY
            System.out.println("305: " + lineFromServer);
        }

        else if (command.equals("333"))
        { // RPL_UNAWAY
            System.out.println(lineFromServer);
        }

// used or not ???????
        else if (command.equals("332") // RPL_TOPIC
        )
        {
            String topicChannelName = "",
                    topicTopic = "";

            StringTokenizer st = new StringTokenizer(parser.getParams(), " \r\n");
            for (int i = 0; st.hasMoreTokens();)
            {
                String aToken = st.nextToken();
                if (i == 1)
                {
                    topicChannelName = aToken;
                    break;
                }

                i++;
            }

            int indexOfChannel = findTab(tabbedPane, topicChannelName);
            if (indexOfChannel != -1)
            {
                Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                if (aComponent instanceof ChannelPanel)
                {
                    ((ChannelPanel) aComponent).setTitleArea(parser.getTrailing());
                }
            }
        }
        else if (command.equals("TOPIC")
        )
        {
            String topicChannelName = "";

            StringTokenizer st = new StringTokenizer(parser.getParams(), " \r\n");
            if (st.hasMoreTokens())
            {
                topicChannelName = st.nextToken();

            }
            int indexOfChannel = findTab(tabbedPane, topicChannelName);
            if (indexOfChannel != -1)
            {
                Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                if (aComponent instanceof ChannelPanel)
                {
                    ((ChannelPanel) aComponent).setTitleArea(parser.getTrailing());
                    ((ChannelPanel) aComponent).updateTextArea("TOPIC: " +
                                                               parser.getTrailing() + "(" + parser.getNick() + ")");
                }
            }
        }
        else if (command.equals("353") //
        )
        {
            String channelName = parser.getMiddle();
            int index = channelName.lastIndexOf("#");
            channelName = channelName.substring(index);

            // Find the tab of channelName
            int indexOfChannel = findTab(tabbedPane, channelName);

            // If the channelName tab exists!
            if (indexOfChannel != -1)
            {
                Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                if (aComponent instanceof ChannelPanel)
                {
                    // All the usernames are in trailing!
                    String trailing = parser.getTrailing();

                    // let's split those usernames with StringTokenizer
                    StringTokenizer st = new StringTokenizer(trailing, " \r\n");

                    // How many users are there?
                    int totalTokens = st.countTokens();

                    for (int i = 0; i < totalTokens; i++)
                    {
                        // a username/nickname
                        String tempnickname = st.nextToken();

                        // Add that username to channelPanel's usernameBox
                        ((ChannelPanel) aComponent).updateUserArea(tempnickname, "add");
                    }
                }
            }
            else
            { // channelName tab doesn't exist!
            }
        }

        else if (command.equals("404"))
        {
            tabUpdate("Init Window",
                      "ERROR: Cannot send msg to channel! " + lineFromServer);
        }

        else if (command.equals("421"))
        {
            tabUpdate("Init Window", "ERROR: Command not found! " + lineFromServer);
        }

        else if (command.equals("442"))
        {
            tabUpdate("Init Window",
                      "ERROR: You're not on that channel! " + lineFromServer);
        }

        else if (command.equals("443"))
        {
            tabUpdate("Init Window",
                      "ERROR: User is already on that channel! " + lineFromServer);
        }

        else if (command.equals("461"))
        {
            tabUpdate("Init Window",
                      "ERROR: Need more params for this command! " + lineFromServer);
        }

        else if (command.equals("482"))
        {
            tabUpdate("Init Window",
                      "ERROR: You must be channel operator for this! " + lineFromServer);
        }

        else if (command.equals("433"))
        {
            tabUpdate("Init Window",
                      nickname + " already in use " + parser.getTrailing());

            String newNickname;
            newNickname = JOptionPane.showInputDialog(" Change current nickname: " +
                                                      getNickname() + " to: ",
                                                      nickname);

            if (newNickname != null)
            {
                nickname = newNickname;
                Component aComponent = tabbedPane.getComponentAt(0);
                ((InitPanel) aComponent).setTitleArea(newNickname + " connected to: " +
                                                      host + ":" + port);
                parseSendToCommand("NICK " + newNickname);
            }
        }
        else
        {
            if (lineFromServer.indexOf("MODE") != -1)
            {
                String nickCommand;
                String modeReceiver;
                String modeReceived;
                String temp;
                String channelsMode;
                temp = lineFromServer.substring(1, lineFromServer.indexOf(" "));
                nickCommand = temp;
                if (temp.indexOf("!") != -1)
                {
                    nickCommand = temp.substring(0, temp.indexOf("!"));
                }
                temp = lineFromServer.substring(lineFromServer.indexOf(" ",
                                                                       lineFromServer.indexOf(" ") + 1));
                temp = temp.substring(temp.indexOf(" "));
// User mode
                if (temp.indexOf(":") != -1)
                {
                    modeReceiver = temp.substring(0, temp.indexOf(":") - 1);
                    modeReceived = temp.substring(temp.indexOf(":") + 1);
                    tabUpdate("Init Window",
                              nickCommand + " sets MODE: " + modeReceived + " " +
                              modeReceiver);
                }
// Channel mode
                else
                {
                    channelsMode = temp.substring(1, temp.indexOf(" ", 1));
                    modeReceived = temp.substring(temp.indexOf(" ", 1) + 1, temp.length());

                    int indexOfChannel = findTab(tabbedPane, channelsMode);
                    if (indexOfChannel != -1)
                    {
                        Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                        if (aComponent instanceof ChannelPanel)
                        {
                            ChannelPanel channel = (ChannelPanel)aComponent;
                            channel.updateTextArea(nickCommand +" sets MODE: " + modeReceived);
                            String tmp = modeReceived.substring(0,1);
                            if (tmp.compareToIgnoreCase("-")==0
                                || tmp.compareToIgnoreCase("+")==0){
                                String name = modeReceived.substring(3);
                                if (channel.usernameList.contains("!" + name)){
                                    channel.updateUserArea("!" + name, "remove");
                                }
                                else if (channel.usernameList.contains("@" + name)){
                                    channel.updateUserArea("@" + name, "remove");
                                }
                                else if (channel.usernameList.contains("%" + name)){
                                    channel.updateUserArea("%" + name, "remove");
                                }
                                else if (channel.usernameList.contains("+" + name)){
                                    channel.updateUserArea("+" + name, "remove");
                                }
                                else if (channel.usernameList.contains(name)){
                                    channel.updateUserArea(name, "remove");
                                }
                                if (tmp.compareToIgnoreCase("+")==0){
                                    tmp = modeReceived.substring(1,2);
                                    if (tmp.compareToIgnoreCase("v")==0){
                                        channel.updateUserArea("+" + name, "add");
                                    }
                                    else if (tmp.compareToIgnoreCase("a")==0){
                                        channel.updateUserArea("!" + name, "add");
                                    }
                                    else if (tmp.compareToIgnoreCase("o")==0){
                                        channel.updateUserArea("@" + name, "add");
                                    }
                                    else if (tmp.compareToIgnoreCase("h")==0){
                                        channel.updateUserArea("%" + name, "add");
                                    }
                                }
                                else if (tmp.compareToIgnoreCase("-")==0){
                                    channel.updateUserArea(name, "add");
                                }
                            }
                        }
                    }
                }
            }
            else if (lineFromServer.indexOf(" 366 ") != -1)
            {
                ;
            }
            //System.out.println("END of Channel name list.");
            else
            {
                tabUpdate("Init Window",
                          "Command not found (lineFromServer): " + lineFromServer);
            }
        }
    }

    private int findTab(final JTabbedPane tabbedPane, String title) {
        int totalTabs = tabbedPane.getTabCount();

        for (int i = 0; i < totalTabs; i++)
        {
            String tabTitle = tabbedPane.getTitleAt(i);

            // Let's see whether tabbedPane title and title matches!
            if (tabTitle.equalsIgnoreCase(title))
            {
                return i;
            }
        }

        // Not found anything? Return -1
        return -1;
    }

    private int tabUpdate(String tabTitle, String message) {
        int indexOfTab = findTab(tabbedPane, tabTitle);

        if (indexOfTab != -1)
        {
            Component aComponent = tabbedPane.getComponentAt(indexOfTab);
            if (aComponent instanceof ChannelPanel)
            {
                ((ChannelPanel) aComponent).updateTextArea(message);
                if (tabbedPane.getSelectedIndex() != indexOfTab)
                {
                    tabbedPane.setBackgroundAt(indexOfTab, Color.red);
                }
                tabbedPane.revalidate();
            }
            else if (aComponent instanceof UserPanel)
            {
                ((UserPanel) aComponent).updateTextArea(message);
                if (tabbedPane.getSelectedIndex() != indexOfTab)
                {
                    tabbedPane.setBackgroundAt(indexOfTab, Color.red);
                }
                tabbedPane.revalidate();
            }
            else if (aComponent instanceof InitPanel)
            {
                ((InitPanel) aComponent).updateTextArea(message);
                if (tabbedPane.getSelectedIndex() != indexOfTab)
                {
                    tabbedPane.setBackgroundAt(indexOfTab, Color.red);
                }
                tabbedPane.revalidate();
            }
        }

        return indexOfTab;
    }

    public void parseSendToCommand(String lineToServer) {
        // At first let's see whether the connection is still alive or
        // not.
        if (toServer != null)
        {
            toServer.print(lineToServer + "\r\n");
            toServer.flush();
        }
    }

    private JMenuItem addMenuItem(JMenu menu, Action action) {
        JMenuItem item = menu.add(action);

        KeyStroke keystroke = (KeyStroke) action.getValue(action.ACCELERATOR_KEY);
        if (keystroke != null)
        {
            item.setAccelerator(keystroke);

        }
        return item;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Adding the init window
        tabbedPane.add(new InitPanel(), "Init Window");

        tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setSelectedTab();
            }
        });

        return tabbedPane;
    }

    private void setSelectedTab() {
        // to return a selected tab to it's original color
        if (tabbedPane.getModel().isSelected())
        {
            int index = tabbedPane.getSelectedIndex();

            // setting a background color to null makes
            // a tab's background to it's default background color
            tabbedPane.setBackgroundAt(index, null);

            tabbedPane.revalidate();
        }
    }

    private void newUser() {
        String userName;

        userName = JOptionPane.showInputDialog(
                " Input a nickname to talk privately ");

        if (userName != null)
        {
            addUser(tabbedPane, userName);
        }
        else
        {
            System.out.println("You can do /msg nickname your message");
            System.out.println("to send a private message to nickname");
        }
    }

    private void joinChan() {
        String channelName;

        channelName = JOptionPane.showInputDialog(" Please Enter a Channel Name ");

        if (channelName != null)
        {
            if (channelName.startsWith("#"))
            {
                tabUpdate("Init Window", "Trying to join: " + channelName);
                if (toServer != null)
                {
                    parseSendToCommand("JOIN " + channelName);
                }
            }
            else
            {
                tabUpdate("Init Window", "Trying to join: #" + channelName);
                if (toServer != null)
                {
                    parseSendToCommand("JOIN #" + channelName);
                }
            }
        }
        else
        {
            tabUpdate("Init Window", "No channel name specified.");
        }
    }

    private void changeNick() {
        String newNickname;

        newNickname = JOptionPane.showInputDialog(" Change current nickname: " +
                                                  getNickname() + " to: ");

        if (newNickname != null)
        {
            if (toServer != null)
            {
                Component aComponent = tabbedPane.getComponentAt(0);
                ((InitPanel) aComponent).setTitleArea(nickname + " connected to: " +
                                                      host + ":" + port);
                parseSendToCommand("NICK " + newNickname);
            }
        }
        else
        {
            tabUpdate("Init Window", "No nickname specified.");
        }
    }

    private void whois() {
        String whoisNickname;

        whoisNickname = JOptionPane.showInputDialog(
                " Check Info of(Input nickname): ");

        if (whoisNickname != null && toServer!=null)
        {
            parseSendToCommand("WHOIS " + whoisNickname);
        }
    }

    private UserPanel addUser(final JTabbedPane tabbedPane, String name) {
        UserPanel userPanel = new UserPanel(name);
        tabbedPane.add(userPanel, name);
        tabbedPane.revalidate();

        return userPanel;
    }

    private ChannelPanel addChannel(final JTabbedPane tabbedPane, String name) {
        ChannelPanel channel;

        if (name.startsWith("#"))
        {
            channel = new ChannelPanel(name);
            tabbedPane.add(channel, name);
        }
        else
        {
            channel = new ChannelPanel("#" + name);
            tabbedPane.add(channel, "#" + name);
        }
        tabbedPane.revalidate();

        return channel;
    }

    public JTabbedPane closeChannel(final JTabbedPane tabbedPane, String name) {
        int index;

        index = tabbedPane.indexOfTab(name);

        if (index != -1 && index >= 0)
        {
            tabbedPane.removeTabAt(index);
        }

        tabbedPane.revalidate();
        return tabbedPane;
    }

    public class ConnectionAction
            extends AbstractAction {
        ConnectionAction(String name) {
            super(name);
        }

        ConnectionAction(String name, KeyStroke keystroke) {
            this(name);
            if (keystroke != null)
            {
                putValue(ACCELERATOR_KEY, keystroke);
            }
        }

        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
        }
    }

    public class InitPanel
            extends JPanel
            implements ActionListener {
        final String name = "Init Window";
        final JTextArea textArea = new JTextArea();
        final JTextField textField = new JTextField();
        JTextField titleArea;

        public InitPanel() {
            makePanel();
        }

        private void makePanel() {
            setLayout(new BorderLayout());

            // let's add actionListener
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textField.addActionListener(this);

            JScrollPane sp1 = new JScrollPane(textArea);

            sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            add(sp1, BorderLayout.CENTER);
            add(textField, BorderLayout.SOUTH);
            add(makeNorth(), BorderLayout.NORTH);
        }

        private Box makeNorth() {
            Box northBox = Box.createHorizontalBox();
            titleArea = new JTextField("Not connected yet");
            northBox.add(titleArea);
            return northBox;
        }

        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            // let's take care of textField
            if (source == textField)
            {
                String message = textField.getText();

                if (message.startsWith("/"))
                {
                    // commands that start with "/"
                    message = message.substring(1);
                    textArea.append(message + "\n");
                    textField.setText("");

                    parseSendToCommand(message);
                }
                else
                {
                    // update textArea
                    textArea.append(message + "\n");
                    textField.setText("");

                    parseSendToCommand(message);
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
            if (newCaretPosition == oldCaretPosition)
            {
                textArea.setCaretPosition(oldCaretPosition + (message + "\n").length());
            }
        }
    }

    private void analyzeCommand(String message) {
        CommandInterpreter cmdI = new CommandInterpreter(message);

        if (cmdI.getCommand().equals("JOIN"))
        {
            parseSendToCommand(cmdI.getCommand() + " " +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("PART") ||
                cmdI.getCommand().equals("LEAVE"))
        {
            parseSendToCommand("PART " +
                               cmdI.getParam1() + " :" +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("QUIT"))
        {
            parseSendToCommand("QUIT :" +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("WHOIS"))
        {
            parseSendToCommand("WHOIS " +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("MSG"))
        {
            parseSendToCommand("PRIVMSG " +
                               cmdI.getParam1() + " :" +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("CTCP"))
        {
            parseSendToCommand("PRIVMSG " +
                               cmdI.getParam1() + " :" + 1 +
                               cmdI.getMessage() + 1);
        }
        else if (cmdI.getCommand().equals("NICK"))
        {
            parseSendToCommand("NICK " + cmdI.getParam1());
        }
        else if (cmdI.getCommand().equals("TOPIC"))
        {
            if (cmdI.getMessage().equals(""))
            {
                parseSendToCommand(cmdI.getCommand() + " " +
                                   cmdI.getParam1() + " :");
            }
            else
            {
                System.out.println("Sending a message: " + cmdI.getCommand() + " " +
                                   cmdI.getParam1() + " :" + cmdI.getMessage());
                parseSendToCommand(cmdI.getCommand() + " " +
                                   cmdI.getParam1() + " :" +
                                   cmdI.getMessage());
            }
        }
        else
        {
            parseSendToCommand(cmdI.getCommand() + " :" +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
    }

    public class ChannelPanel
            extends JPanel
            implements ActionListener, ListSelectionListener {
        final String name;
        final SortedListModel usernameList = new SortedListModel();
        final JList userList = new JList(usernameList);
        final JTextArea textArea = new JTextArea();
        final JTextField textField = new JTextField();

        JTextField titleArea = new JTextField();

        JButton closeButton = new JButton("X");

        public ChannelPanel(String name) {
            this.name = name;

            makePanel();
        }

        private void makePanel() {
            setLayout(new BorderLayout());

            // let's add actionListener
            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            //textArea.setEditable(false);
            textField.addActionListener(this);

            userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //userList.setSelectedIndex(0);
            userList.addListSelectionListener(this);
            userList.setCellRenderer(new UserListCellRenderer());

            JScrollPane sp1 = new JScrollPane(textArea);
            JScrollPane sp2 = new JScrollPane(userList);

            sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            sp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                  sp1,
                                                  sp2);
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerLocation(600);
            add(splitPane, BorderLayout.CENTER);
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
            if (source == textField)
            {
                String message = textField.getText();

                // let's send to server
                if (message.startsWith("/"))
                {
                    // commands that start with "/"
                    // message = message.substring(1);
                    analyzeCommand(message);
                    textField.setText("");
                }
                else
                {
                    // A private message to channel!
                    parseSendToCommand("PRIVMSG " + name + " :" + message);

                    // let's update textArea
                    // textArea.append(textField.getText() + "\n");
                    updateTextArea(formatNickname("<" + getNickname() + "> ") +
                                   textField.getText());
                    resetTextField();
                }
            }
            else if (source == closeButton)
            {
                closeChannel(tabbedPane, name);

                // Let's send a sensible message
                parseSendToCommand("PART " + name);
            }
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting() == false)
            {
                Object obj = userList.getSelectedValue();
                if (obj != null)
                {
                    tabUpdate("Init Window",
                              "Nickname: " + obj.toString() +
                              " was selected.");
                }
            }
        }

        public void resetTextField() {
            textField.setText("");
        }

        public void setTitleArea(String title) { // Actually, it's user@host
            titleArea.setText("");
            titleArea.setText(title);
        }

        public void updateTextArea(String message) {
            int oldCaretPosition = textArea.getCaretPosition();
            textArea.append(message + "\n");

            int newCaretPosition = textArea.getCaretPosition();
            if (newCaretPosition == oldCaretPosition)
            {
                textArea.setCaretPosition(oldCaretPosition + (message + "\n").length());
            }
        }

        public void updateUserArea(String username, String command) {
            if (command.equals("add"))
            {
                usernameList.add(username);
            }

            else if (command.equals("remove"))
            {
                usernameList.remove(username);
            }
        }
    }

    public class UserPanel
            extends JPanel
            implements ActionListener {
        String name;
        final JTextArea textArea = new JTextArea();
        final JTextField textField = new JTextField();
        JTextField titleArea = new JTextField("Title goes over here");

        JButton closeButton = new JButton("X");

        public UserPanel(String name) {
            this.name = name;

            makePanel();
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setTitleArea(String title) { // Actually, it's user@host
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

            JScrollPane sp1 = new JScrollPane(textArea);

            sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

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
            if (source == textField)
            {
                String message = textField.getText();

                // let's send to the server
                if (message.startsWith("/"))
                {
                    // A command
                    analyzeCommand(message);
                    textField.setText("");
                }
                else
                {
                    // A normal private message to send to the user
                    parseSendToCommand("PRIVMSG " + name + " :" + message);

                    // let's update textArea
                    updateTextArea(formatNickname("<" + getNickname() + "> ") +
                                   textField.getText());
                    resetTextField();
                }
            }
            else if (source == closeButton)
            {
                closeChannel(tabbedPane, name);
            }
        }

        public void resetTextField() {
            textField.setText("");
        }

        public void updateTextArea(String message) {
            int oldCaretPosition = textArea.getCaretPosition();
            textArea.append(message + "\n");

            int newCaretPosition = textArea.getCaretPosition();
            if (newCaretPosition == oldCaretPosition)
            {
                textArea.setCaretPosition(oldCaretPosition + (message + "\n").length());
            }
        }
    }
}
