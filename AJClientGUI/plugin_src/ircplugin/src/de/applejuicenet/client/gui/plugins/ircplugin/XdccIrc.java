package de.applejuicenet.client.gui.plugins.ircplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.StringTokenizer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.plugins.IrcPlugin;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/XdccIrc.java,v 1.10 2004/05/12 12:31:39 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class XdccIrc
    extends JPanel {
    private JButton connectButton;
    private JButton cancelButton;
    private Logger logger;

    private Thread ircWorker;

    private String host = "irc.p2pchat.net";
    private int port = 6667;
    private JTabbedPane tabbedPane;
    private String nickname;
    private String realname;

    // Action to join a Channel
    private JButton joinChannelAction;
    private JButton createConnection;
    private JButton changeNickAction;
    private JButton newUserAction;

    private String connectionInfo = "Connection Info";
    private String nicknameString = "Enter nickname";
    private String channelNameString = "Enter nickname";
    private String verbinden;
    private String trennen;

    private IrcPlugin parent;

    private JDialog dialog;
    private JTextField nickJTextField1;

    private Socket chatSocket;
    private BufferedReader fromServer;
    private PrintWriter toServer;

    private AppleJuiceDialog theApp = AppleJuiceDialog.getApp();
    private DisconnectActionListener disconnectActionListener = new DisconnectActionListener();
    private ConnectActionListener connectActionListener = new ConnectActionListener();

    public XdccIrc(IrcPlugin parent) {
        this.parent = parent;
        logger = Logger.getLogger(getClass());
        try {
            setLayout(new BorderLayout());
            String propHost = parent.getProperties().getProperty("host");
            if (propHost != null && propHost.length()>0){
                host = propHost;
            }
            String propPort = parent.getProperties().getProperty("port");
            if (propPort != null && propPort.length()>0){
                try{
                    int tmpPort = Integer.parseInt(propPort);
                    port = tmpPort;
                }
                catch(NumberFormatException nfE){
                    //ungueltiger Port
                }
            }

            createConnection = new JButton("Connect");
            createConnection.addActionListener(connectActionListener);
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

            connectButton = new JButton("Connect");
            cancelButton = new JButton("Cancel");

            tabbedPane = createTabbedPane();

            JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            // Let's create a ToolBar
            panel1.add(createConnection);
            panel1.add(newUserAction);
            panel1.add(joinChannelAction);
            panel1.add(changeNickAction);

            add(panel1, BorderLayout.NORTH);
            add(tabbedPane, BorderLayout.CENTER);
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
        }
    }

    public void fireLanguageChanged() {
        verbinden = parent.getLanguageString(
            ".root.language.buttons.connect");
        trennen = parent.getLanguageString(
            ".root.language.buttons.disconnect");
        createConnection.removeActionListener(disconnectActionListener);
        ActionListener[] listener = createConnection.getActionListeners();
        boolean found = false;
        for (int i=0; i<listener.length; i++){
            if (listener[i]==connectActionListener){
                found = true;
                createConnection.setText(verbinden);
                break;
            }
        }
        if (!found){
            createConnection.setText(trennen);
        }
        newUserAction.setText(parent.getLanguageString(
            ".root.language.buttons.private"));
        joinChannelAction.setText(parent.getLanguageString(
            ".root.language.buttons.join"));
        changeNickAction.setText(parent.getLanguageString(
            ".root.language.buttons.nick"));
        connectButton.setText(parent.getLanguageString(
            ".root.language.buttons.connect"));
        cancelButton.setText(parent.getLanguageString(
            ".root.language.buttons.cancel"));
        connectionInfo = parent.getLanguageString(
            ".root.language.title.newconnection");
        nicknameString = parent.getLanguageString(".root.language.nick.newnick");
        channelNameString = parent.getLanguageString(
            ".root.language.channel.newchannel");
    }

    public void connectStartRegister() {
        try {
            connect();
            start();
            register();
        }
        catch (IOException e) {
            tabUpdate("Init Window", "Connection refused to: " + host + ":" + port);
        }
    }

    private void makeConnectionInfo() {
        dialog = new JDialog(AppleJuiceDialog.getApp(), connectionInfo, true);
        Container dialogContentPane = dialog.getContentPane();

        // Ok, let's make the UserInfo
        JPanel userInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label1 = new JLabel("Nickname: ");

        nickJTextField1 = new JTextField(25);
        nickJTextField1.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
                super.keyReleased(ke);
                if (nickJTextField1.getText().length() != 0) {
                    connectButton.setEnabled(true);
                    if (ke.getKeyChar()==KeyEvent.VK_ENTER){
                        connectButton.doClick();
                    }
                }
                else {
                    connectButton.setEnabled(false);
                }
            }
        });
        userInfo.add(label1);
        userInfo.add(nickJTextField1);
        String nick = parent.getProperties().getProperty("nick");
        if (nick != null){
            nickname = nick;
        }

        if (nickname != null && nickname.length()>0) {
            nickJTextField1.setText(nickname);
        }
        else {
            Random random = new Random();
            nickJTextField1.setText("ajPluginUser" + Integer.toString(random.nextInt(99999)));
        }
        connectButton.setEnabled(true);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // Action Listener
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent
                                        e) {
                // OK, let's set the nickname if user has mentioned it
                if (nickJTextField1.getText().length() == 0) {
                    dialog.dispose();
                    return;
                }

                if (nickJTextField1.getText() != null) {
                    setNickname(nickJTextField1.getText());
                }
                if (nickJTextField1.getText() == null) {
                    Random random = new Random();
                    setNickname("ajPlugin" +
                                Integer.toString(random.nextInt(99999)));
                }
                connectButton.setEnabled(false);
                createConnection.setEnabled(false);
                tabUpdate("Init Window", " Connecting to: " + host);
                connectStartRegister();
                createConnection.setText(trennen);
                createConnection.removeActionListener(connectActionListener);
                createConnection.addActionListener(disconnectActionListener);
                createConnection.setEnabled(true);
                dialog.dispose();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (dialog != null){
                    dialog.dispose();
                    dialog = null;
                }
            }
        });

        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);

        dialogContentPane.add("North", userInfo);
        dialogContentPane.add("South", buttonPanel);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.show();
    }

    private class ConnectActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            makeConnectionInfo();
        }
    }

    private class DisconnectActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            createConnection.setEnabled(false);
            createConnection.setText(verbinden);
            createConnection.removeActionListener(disconnectActionListener);
            createConnection.addActionListener(connectActionListener);
            closeAll();
            createConnection.setEnabled(true);
            tabUpdate("Init Window", "Disconnected from: " + host + ":" + port);
        }
    }

    private void connect() throws IOException{
        chatSocket = new Socket(host, port);
        fromServer = new BufferedReader(new InputStreamReader(
            chatSocket.getInputStream()));
        toServer = new PrintWriter(new OutputStreamWriter(
            chatSocket.getOutputStream()));
        Component aComponent = tabbedPane.getComponentAt(0);
        ( (InitPanel) aComponent).setTitleArea(nickname + " connected to: " +
                                               host + ":" + port);
    }

    private boolean pong = false;

    private void register() {

        realname = nickname;

        parseSendToCommand("PASS test");
        parseSendToCommand("NICK " + nickname);
        parseSendToCommand("USER " + nickname + " 0 * :" + realname);

        new Thread(){
            public void run(){
                int count = 0;
                while (!isInterrupted() && count <60){
                    try {
                        sleep(500);
                    }
                    catch (InterruptedException ex) {
                        interrupt();
                    }
                    if (pong){
                        if (toServer != null) {
                            joinChannel("test");
                            String onJoin = parent.getProperties().getProperty("onjoin");
                            if (onJoin != null && onJoin.length()>0){
                                if (onJoin.charAt(0) == '/'){
                                    onJoin = onJoin.substring(1);
                                }
                                parseSendToCommand(onJoin);
                            }
                            String onJoinChannels = parent.getProperties().getProperty("channels");
                            if (onJoinChannels != null && onJoinChannels.length()>0){
                                onJoinChannels(onJoinChannels);
                            }
                        }
                        pong = false;
                        break;
                    }
                    count++;
                }
            }
        }.start();
    }

    private void onJoinChannels(String onJoinChannels){
        String[] channels = onJoinChannels.split(",");
        for (int i=0; i<channels.length; i++){
            parseSendToCommand("JOIN " + channels[i]);
        }
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

        for (int i = 0; i < totalTabs; i++) {
            Component aComponent = tabbedPane.getComponentAt(i);

            if (aComponent instanceof ChannelPanel) {
                // Do channel processing
                // At first remove the old nickname from the userNameBox
                ( (ChannelPanel) aComponent).updateUserArea(oldNickname,
                    "remove");
                ( (ChannelPanel) aComponent).updateUserArea(newNickname, "add");

                // Now let's post a sensible comment on the channel Panel
                ( (ChannelPanel) aComponent).updateTextArea(oldNickname +
                    " changes nickname to " +
                    newNickname);
            }
            else if (aComponent instanceof UserPanel) {
                ;
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

        if (len >= formatlen) {
            return nickname;
        }
        else {
            for (int i = 0; i < (formatlen - len); i++) {
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
                try {
                    while (!isInterrupted()) {
                        if (fromServer == null) {
                            try {
                                sleep(5000);
                            }
                            catch (InterruptedException e) {
                                interrupt();
                            }
                            continue;
                        }
                        String line = fromServer.readLine();
                        if (line != null) {
                            parseFromServer(line);
                        }
                        else {
                            // a null line indicates that our server has
                            // closed the connection, right?
                            tabUpdate("Init Window",
                                      "Server closed the connection or network is fucked.");
                            closeAll();
                            createConnection.setText(verbinden);
                            createConnection.removeActionListener(disconnectActionListener);
                            createConnection.addActionListener(connectActionListener);
                            createConnection.setEnabled(true);
                            break;
                        }
                    }
                }

                catch (IOException e) {
                    if (logger.isEnabledFor(Level.ERROR)) {
                        logger.error("Unbehandelte Exception", e);
                    }
                }
            }
        };
        ircWorker.start();
    }

    public void closeAll() {
        // So, let's shutdown everything
        try {
            chatSocket.close();
            fromServer.close();
            toServer.close();
        }
        catch (Exception e) {
            //egal, weg ist weg...
        }

        chatSocket = null;
        fromServer = null;
        toServer = null;
        closeAllChannels();
    }

    public void parseFromServer(String lineFromServer) throws IOException {
        myParser parser = new myParser(lineFromServer);
        String command = parser.getCommand();
        if (command.equals("PING")) {
            parseSendToCommand("PONG :" + parser.getTrailing());
            pong = true;
        }
        else if (command.equals("JOIN")) {
            String channelName = parser.getTrailing();
            if (channelName.startsWith("#")) {
                channelName = channelName.substring(1);
                int indexOfChannel = findTab(tabbedPane, "#" + channelName);
                if (indexOfChannel == -1) { // Channel Tab doesn't exist
                    ChannelPanel channel = addChannel(tabbedPane, channelName);
                    indexOfChannel = findTab(tabbedPane, "#" + channelName);
                    tabbedPane.setSelectedIndex(indexOfChannel);
                    channel.updateTextArea("*** Now talking in: " +
                                           parser.getTrailing());
                    if (tabbedPane.getSelectedIndex() !=
                        findTab(tabbedPane, "#" + channelName)) {
                        tabbedPane.setBackgroundAt(findTab(tabbedPane,
                            "#" + channelName),
                            Color.red);
                    }
                    tabbedPane.revalidate();
                }
                else { // channelName tab exists!
                    ChannelPanel channel = (ChannelPanel) tabbedPane.
                        getComponentAt(
                        indexOfChannel);
                    channel.updateTextArea("---> JOIN: " + parser.getNick() +
                                           " (" +
                                           parser.getUser() + "@" +
                                           parser.getHost() + ")");
                    if (tabbedPane.getSelectedIndex() != indexOfChannel) {
                        tabbedPane.setBackgroundAt(indexOfChannel, Color.red);
                    }
                    tabbedPane.revalidate();

                    channel.updateUserArea(parser.getNick(), "add");
                }
            }
        }
        else if (command.equals("PRIVMSG")) {
            // handle PRIVMSG
            String destination = parser.getMiddle();
            if (destination.startsWith("#")) {
                // it's a channel!
                String channelName = destination.substring(1);

// MSG Received....!

                int indexOfChannel = findTab(tabbedPane, destination);
                if (indexOfChannel != -1) { // A connection/tab already exists!
                    Component aComponent = tabbedPane.getComponentAt(
                        indexOfChannel);
                    if (aComponent instanceof ChannelPanel) {
                        String trailing;
                        String nick;
                        nick = parser.getNick();
                        trailing = parser.getTrailing();
                        if (trailing.indexOf(1) != -1) { //is this a CTCP
                            //remove \001 from Beginning and End of CTCP msg
                            trailing = trailing.substring(1,
                                trailing.length() - 1);
                            ( (ChannelPanel) aComponent).updateTextArea(
                                formatNickname("[" + nick + "] ") + trailing);
                        }
                        if (nick.equals(nickname)) {
                            //((ChannelPanel)aComponent).textArea.setDisabledTextColor(new Color(255,0,0));
                            ( (ChannelPanel) aComponent).updateTextArea(
                                formatNickname("<" + nick + "> ") + trailing);
                        }
                        else {
                            //((ChannelPanel)aComponent).textArea.setDisabledTextColor(new Color(0,0,255));
                            ( (ChannelPanel) aComponent).updateTextArea(
                                formatNickname("<" + nick + "> ") + trailing);
                        }
                        if (tabbedPane.getSelectedIndex() != indexOfChannel) {
                            tabbedPane.setBackgroundAt(indexOfChannel,
                                Color.red);
                        }
                        tabbedPane.revalidate();
                    }
                }
                else { // A connection/tab doesn't exists, so create one!
                    ChannelPanel channel = addChannel(tabbedPane, channelName);
                    channel.updateTextArea(
                        formatNickname("<" + parser.getNick() + "> ") + "> " +
                        parser.getTrailing());
                    if (tabbedPane.getSelectedIndex() !=
                        findTab(tabbedPane, "#" + channelName)) {
                        tabbedPane.setBackgroundAt(findTab(tabbedPane,
                            "#" + channelName),
                            Color.red);
                    }
                    tabbedPane.revalidate();
                }

            } //if (destination.startsWith("#"))
            else { //if (!destination.equalsIgnoreCase(getNickname()))
                // it's from a user

                int indexOfUser = findTab(tabbedPane, parser.getNick());

                if (indexOfUser == -1) {
                    UserPanel userPanel = addUser(tabbedPane, parser.getNick()); //if tab doesnt exist
                    // Add the real username@hostname in title of tabbedPane
                    userPanel.setTitleArea(parser.getNick() + "!" +
                                           parser.getUser() +
                                           "@" + parser.getHost());
                }

                indexOfUser = findTab(tabbedPane, parser.getNick());

                if (indexOfUser != -1) { // A user connection/tab already exists!
                    Component aComponent = tabbedPane.getComponentAt(
                        indexOfUser);
                    UserPanel userPanel = (UserPanel) aComponent;

                    if (aComponent instanceof UserPanel) {
                        String ctcpCommand;
                        String trailing;
                        char firstOne = 1;

                        trailing = parser.getTrailing();
                        if (trailing.indexOf(1) != -1) { //is this a CTCP
                            if (trailing.indexOf(" ") != -1) {
                                ctcpCommand = trailing.substring(1,
                                    trailing.indexOf(" "));
                            }
                            else {
                                ctcpCommand = trailing.substring(1,
                                    trailing.indexOf(1, 2));
                            }

                            if (ctcpCommand.equalsIgnoreCase("PING")) {
                                parseSendToCommand("NOTICE " + parser.getNick() +
                                    " " +
                                    trailing);
                            }

                            else if (ctcpCommand.equalsIgnoreCase("VERSION")) {
                                String versionReply = "NOTICE " +
                                    parser.getNick() + " " +
                                    firstOne + ctcpCommand +
                                    " Using ajPlugin IRC:v" + parent.getVersion() + ":Java" +
                                    firstOne;
                                parseSendToCommand(versionReply);

                            }

                            userPanel.updateTextArea(
                                formatNickname("••• ¢ CTCP " + ctcpCommand +
                                               " received from " +
                                               parser.getNick()));
                        } // if (trailing.indexOf(1) != -1)  //is this a CTCP

                        else { // regular privmsg
                            userPanel.updateTextArea(
                                formatNickname("<" + parser.getNick() + "> ") +
                                trailing);
                        }
                        if (tabbedPane.getSelectedIndex() != indexOfUser) {
                            tabbedPane.setBackgroundAt(indexOfUser, Color.red);
                        }
                        tabbedPane.revalidate();
                    } //if (aComponent instanceof UserPanel)
                } // if (indexOfUser != -1)
            } // else (Msg from a user..)
        } //else if (command.equals("PRIVMSG"))

        else if (command.equals("PART")) {
            // Let's grab the channel name
            String channelName = parser.getParams();
            int index = channelName.indexOf("#");
            int index2 = channelName.indexOf(":");
            if (index2 != -1) {
                channelName = channelName.substring(index, index2 - 1);
            }
            else {
                channelName = channelName.substring(index);
            }

            if (channelName.startsWith("#")) {
                channelName = channelName.substring(1);

                int indexOfChannel = findTab(tabbedPane, "#" + channelName);
                if (indexOfChannel != -1) { // Tab doesn't exist. Do nothing
                    Component aComponent = tabbedPane.getComponentAt(
                        indexOfChannel);
                    if (aComponent instanceof ChannelPanel) {
                        ( (ChannelPanel) aComponent).updateTextArea(
                            "<--- PART: " +
                            parser.getNick());
                        ( (ChannelPanel) aComponent).updateUserArea(parser.
                            getNick(),
                            "remove");
                    }

                    // Ok, let's remove the tab if the user who left the channel is you
                    if (parser.getNick().equals(getNickname())) {
                        tabbedPane.removeTabAt(indexOfChannel);
                    }
                }
            }

        }
        else if (command.equals("QUIT")) {
            //??????????????????????????
            //Find what channel user is on and direct Quit msg to correct channel(s)
            tabUpdate("Init Window",
                      parser.getNick() + " has quit (" + parser.getTrailing() +
                      ")");
        }
        else if (command.equals("NICK")) {
            // handle NICK --- When a user changes its nickname
            int index = findTab(tabbedPane, parser.getNick());
            if (index != -1) {
                // setting the user tab title to changed nickname
                tabbedPane.setTitleAt(index, parser.getTrailing());

                // Let's change the name of the UserPanel alsow
                UserPanel temp = (UserPanel) tabbedPane.getComponentAt(index);
                temp.setName(parser.getTrailing());
            }

            if (parser.getNick().equalsIgnoreCase(getNickname())) {
                setNickname(parser.getTrailing());
            }

            // When a user changes his/her name, his name in every
            // UserPanel or ChannelPanel should be changed too.
            // So, we are doing it now --- here
            String oldNickname = parser.getNick();
            String newNickname = parser.getTrailing();

            changeNickname(oldNickname, newNickname);
        }
        else if (command.equals("NOTICE")) {
            // handle NOTICE (à arranger: savoir de qui elle vient et la mettre au bon endroit)
            tabUpdate("Init Window",
                      "NOTICE FROM: " + parser.getNick() + " ---> " +
                      parser.getTrailing());
        }
        else if (command.equals("KICK")) {
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
            if (! (kickedNick.equals(getNickname()))) {
                channel.updateTextArea("<- KICK: " + kickedNick + " by " +
                                       parser.getNick() + " (" +
                                       parser.getTrailing() +
                                       ")");
                channel.updateUserArea(kickedNick, "remove");
            }
            else { // You have been kicked...
                tabbedPane.removeTabAt(indexOfChannel);
                tabUpdate("Init Window",
                          "You have been kicked from " + kickedChannel + " by " +
                          parser.getNick() + " because " + parser.getTrailing());
            }
        }
        else if (command.equals("001") || // RPL_WELCOME
                 command.equals("002") || // RPL_YOURHOST
                 command.equals("003") || // RPL_CREATED
                 command.equals("004")) { // RPL_MYINFO
            // successful registration
            tabUpdate("Init Window", parser.getTrailing());
        }
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
                 ) {
            tabUpdate("Init Window", parser.getTrailing());
        }

//252 & 254 = num IRCOP and Total Channels
        else if (command.equals("252")) {
            String numThing;
            numThing = lineFromServer.substring(lineFromServer.indexOf("252") +
                                                5 +
                                                nickname.length(),
                                                lineFromServer.indexOf(":", 2));
            tabUpdate("Init Window", numThing + " " + parser.getTrailing());
        }

        else if (command.equals("254")) {
            String numThing;
            numThing = lineFromServer.substring(lineFromServer.indexOf("254") +
                                                5 +
                                                nickname.length(),
                                                lineFromServer.indexOf(":", 2));
            tabUpdate("Init Window", numThing + " " + parser.getTrailing());
        }

//channel full, invite only or need a key
        else if (command.equals("471") ||
                 command.equals("473") ||
                 command.equals("475")
                 ) {
            String channel;
            channel = lineFromServer.substring(lineFromServer.indexOf("#"),
                                               lineFromServer.indexOf(":", 2));
            tabUpdate("Init Window",
                      parser.getTrailing() + " (" + channel + ")");
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
                 ) {
            // Ok, let's see how we can process PROCESS
            StringTokenizer st = new StringTokenizer(parser.getParams(),
                " \r\n");
            //StringTokenizer st = new StringTokenizer(parser.getParams(), " ");
            String whoNick = "",
                whoUser = "",
                whoHost = "",
                whoMode = "",
                whoRealName = "";

            for (int i = 0; st.hasMoreTokens(); ) {
                String aToken = st.nextToken();
                if (i == 1) {
                    whoNick = aToken;
                }
                else if (i == 2) {
                    whoUser = aToken;
                }
                else if (i == 3) {
                    whoHost = aToken;
                }
                else if (i == 4) {
                    whoMode = aToken;
                }
                else if (i == 5) {
                    //à vérifier!!!!!!!!!!!!!!
                    String paramsWhois = parser.getParams();
                    whoRealName = paramsWhois.substring(paramsWhois.indexOf(
                        aToken) + 1,
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
        else if (command.equals("312")) {
            String whoNick = "",
                whoServer = "",
                whoServerInfo = "";

            StringTokenizer st = new StringTokenizer(parser.getParams(),
                " \r\n");
            for (int i = 0; st.hasMoreTokens(); ) {
                String aToken = st.nextToken();
                if (i == 1) {
                    whoNick = aToken;
                }
                else if (i == 2) {
                    whoServer = aToken;
                }
                else if (i == 3) {
                    //à vérifier!!!!!!!!!!!!!! ainsi que whois realname
                    String paramsWhois = parser.getParams();
                    whoServerInfo = paramsWhois.substring(paramsWhois.indexOf(
                        aToken) + 1,
                        paramsWhois.length());
                    //whoServerInfo = aToken.substring(aToken.length());
                }

                i++;
            }

            // Let's update the Init Window
            tabUpdate("Init Window",
                      "  Server: " + whoServer + " " + whoServerInfo);
        }
        else if (command.equals("313")) {
            tabUpdate("Init Window", "  Operator: " + lineFromServer);
        }
        else if (command.equals("317")) {
            String seconds = "",
                trailing = "";
            trailing = parser.getTrailing();
            StringTokenizer st = new StringTokenizer(parser.getParams(),
                " \r\n");
            for (int i = 0; st.hasMoreTokens(); ) {
                String aToken = st.nextToken();
                if (i == 2) {
                    seconds = aToken;
                    break;
                }

                i++;
            }
            tabUpdate("Init Window", " Idle time: " + seconds + " " + trailing);
        }
        else if (command.equals("318")) {
            tabUpdate("Init Window", parser.getTrailing());
        }
        else if (command.equals("319")) {
            tabUpdate("Init Window", "  Channels: " + parser.getTrailing());
        }
        else if (command.equals("301")) { // RPL_AWAY
            StringTokenizer st = new StringTokenizer(parser.getParams(),
                " \r\n");
            String awayNickname = "";
            for (int i = 0; st.hasMoreTokens(); ) {
                String aToken = st.nextToken();
                if (i == 1) {
                    awayNickname = aToken;
                    break;
                }

                i++;
            }
            tabUpdate("Init Window",
                      awayNickname + " is away for \"" + parser.getTrailing() +
                      "\"");
        }
        else if (command.equals("305")) { // RPL_UNAWAY
            ;
        }

        else if (command.equals("333")) { // RPL_UNAWAY
            ;
        }

// used or not ???????
        else if (command.equals("332") // RPL_TOPIC
                 ) {
            String topicChannelName = "";

            StringTokenizer st = new StringTokenizer(parser.getParams(),
                " \r\n");
            for (int i = 0; st.hasMoreTokens(); ) {
                String aToken = st.nextToken();
                if (i == 1) {
                    topicChannelName = aToken;
                    break;
                }

                i++;
            }

            int indexOfChannel = findTab(tabbedPane, topicChannelName);
            if (indexOfChannel != -1) {
                Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                if (aComponent instanceof ChannelPanel) {
                    ( (ChannelPanel) aComponent).setTitleArea(parser.
                        getTrailing());
                }
            }
        }
        else if (command.equals("TOPIC")
                 ) {
            String topicChannelName = "";

            StringTokenizer st = new StringTokenizer(parser.getParams(),
                " \r\n");
            if (st.hasMoreTokens()) {
                topicChannelName = st.nextToken();

            }
            int indexOfChannel = findTab(tabbedPane, topicChannelName);
            if (indexOfChannel != -1) {
                Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                if (aComponent instanceof ChannelPanel) {
                    ( (ChannelPanel) aComponent).setTitleArea(parser.
                        getTrailing());
                    ( (ChannelPanel) aComponent).updateTextArea("TOPIC: " +
                        parser.getTrailing() + "(" + parser.getNick() + ")");
                }
            }
        }
        else if (command.equals("353") //
                 ) {
            String channelName = parser.getMiddle();
            int index = channelName.lastIndexOf("#");
            channelName = channelName.substring(index);

            // Find the tab of channelName
            int indexOfChannel = findTab(tabbedPane, channelName);

            // If the channelName tab exists!
            if (indexOfChannel != -1) {
                Component aComponent = tabbedPane.getComponentAt(indexOfChannel);
                if (aComponent instanceof ChannelPanel) {
                    // All the usernames are in trailing!
                    String trailing = parser.getTrailing();

                    // let's split those usernames with StringTokenizer
                    StringTokenizer st = new StringTokenizer(trailing, " \r\n");

                    // How many users are there?
                    int totalTokens = st.countTokens();

                    for (int i = 0; i < totalTokens; i++) {
                        // a username/nickname
                        String tempnickname = st.nextToken();

                        // Add that username to channelPanel's usernameBox
                        ( (ChannelPanel) aComponent).updateUserArea(
                            tempnickname, "add");
                    }
                }
            }
            else { // channelName tab doesn't exist!
                ;
            }
        }

        else if (command.equals("404")) {
            tabUpdate("Init Window",
                      "ERROR: Cannot send msg to channel! " + lineFromServer);
        }

        else if (command.equals("421")) {
            tabUpdate("Init Window",
                      "ERROR: Command not found! " + lineFromServer);
        }

        else if (command.equals("442")) {
            tabUpdate("Init Window",
                      "ERROR: You're not on that channel! " + lineFromServer);
        }

        else if (command.equals("443")) {
            tabUpdate("Init Window",
                      "ERROR: User is already on that channel! " +
                      lineFromServer);
        }

        else if (command.equals("461")) {
            tabUpdate("Init Window",
                      "ERROR: Need more params for this command! " +
                      lineFromServer);
        }

        else if (command.equals("482")) {
            tabUpdate("Init Window",
                      "ERROR: You must be channel operator for this! " +
                      lineFromServer);
        }

        else if (command.equals("433")) {
            tabUpdate("Init Window",
                      nickname + " already in use " + parser.getTrailing());

            String newNickname;
            newNickname = JOptionPane.showInputDialog(nicknameString,
                nickname);

            if (newNickname != null) {
                nickname = newNickname;
                Component aComponent = tabbedPane.getComponentAt(0);
                ( (InitPanel) aComponent).setTitleArea(newNickname +
                    " connected to: " +
                    host + ":" + port);
                parseSendToCommand("NICK " + newNickname);
            }
        }
        else {
            if (lineFromServer.indexOf("MODE") != -1) {
                String nickCommand;
                String modeReceiver;
                String modeReceived;
                String temp;
                String channelsMode;
                temp = lineFromServer.substring(1, lineFromServer.indexOf(" "));
                nickCommand = temp;
                if (temp.indexOf("!") != -1) {
                    nickCommand = temp.substring(0, temp.indexOf("!"));
                }
                temp = lineFromServer.substring(lineFromServer.indexOf(" ",
                    lineFromServer.indexOf(" ") + 1));
                temp = temp.substring(temp.indexOf(" "));
// User mode
                if (temp.indexOf(":") != -1) {
                    modeReceiver = temp.substring(0, temp.indexOf(":") - 1);
                    modeReceived = temp.substring(temp.indexOf(":") + 1);
                    tabUpdate("Init Window",
                              nickCommand + " sets MODE: " + modeReceived + " " +
                              modeReceiver);
                }
// Channel mode
                else {
                    channelsMode = temp.substring(1, temp.indexOf(" ", 1));
                    modeReceived = temp.substring(temp.indexOf(" ", 1) + 1,
                                                  temp.length());

                    int indexOfChannel = findTab(tabbedPane, channelsMode);
                    if (indexOfChannel != -1) {
                        Component aComponent = tabbedPane.getComponentAt(
                            indexOfChannel);
                        if (aComponent instanceof ChannelPanel) {
                            ChannelPanel channel = (ChannelPanel) aComponent;
                            channel.updateTextArea(nickCommand + " sets MODE: " +
                                modeReceived);
                            String tmp = modeReceived.substring(0, 1);
                            if (tmp.compareToIgnoreCase("-") == 0
                                || tmp.compareToIgnoreCase("+") == 0) {
                                String name = modeReceived.substring(3);
                                if (channel.usernameList.contains("!" + name)) {
                                    channel.updateUserArea("!" + name, "remove");
                                }
                                else if (channel.usernameList.contains("@" +
                                    name)) {
                                    channel.updateUserArea("@" + name, "remove");
                                }
                                else if (channel.usernameList.contains("%" +
                                    name)) {
                                    channel.updateUserArea("%" + name, "remove");
                                }
                                else if (channel.usernameList.contains("+" +
                                    name)) {
                                    channel.updateUserArea("+" + name, "remove");
                                }
                                else if (channel.usernameList.contains(name)) {
                                    channel.updateUserArea(name, "remove");
                                }
                                if (tmp.compareToIgnoreCase("+") == 0) {
                                    tmp = modeReceived.substring(1, 2);
                                    if (tmp.compareToIgnoreCase("v") == 0) {
                                        channel.updateUserArea("+" + name,
                                            "add");
                                    }
                                    else if (tmp.compareToIgnoreCase("a") == 0) {
                                        channel.updateUserArea("!" + name,
                                            "add");
                                    }
                                    else if (tmp.compareToIgnoreCase("o") == 0) {
                                        channel.updateUserArea("@" + name,
                                            "add");
                                    }
                                    else if (tmp.compareToIgnoreCase("h") == 0) {
                                        channel.updateUserArea("%" + name,
                                            "add");
                                    }
                                }
                                else if (tmp.compareToIgnoreCase("-") == 0) {
                                    channel.updateUserArea(name, "add");
                                }
                            }
                        }
                    }
                }
            }
            else if (lineFromServer.indexOf(" 366 ") != -1) {
                ;
            }
            else {
                tabUpdate("Init Window", lineFromServer);
            }
        }
    }

    private int findTab(final JTabbedPane tabbedPane, String title) {
        int totalTabs = tabbedPane.getTabCount();

        for (int i = 0; i < totalTabs; i++) {
            String tabTitle = tabbedPane.getTitleAt(i);

            // Let's see whether tabbedPane title and title matches!
            if (tabTitle.equalsIgnoreCase(title)) {
                return i;
            }
        }

        // Not found anything? Return -1
        return -1;
    }

    private int tabUpdate(String tabTitle, String message) {
        int indexOfTab = findTab(tabbedPane, tabTitle);

        if (indexOfTab != -1) {
            Component aComponent = tabbedPane.getComponentAt(indexOfTab);
            if (aComponent instanceof ChannelPanel) {
                ( (ChannelPanel) aComponent).updateTextArea(message);
                if (tabbedPane.getSelectedIndex() != indexOfTab) {
                    tabbedPane.setBackgroundAt(indexOfTab, Color.red);
                }
                tabbedPane.revalidate();
            }
            else if (aComponent instanceof UserPanel) {
                ( (UserPanel) aComponent).updateTextArea(message);
                if (tabbedPane.getSelectedIndex() != indexOfTab) {
                    tabbedPane.setBackgroundAt(indexOfTab, Color.red);
                }
                tabbedPane.revalidate();
            }
            else if (aComponent instanceof InitPanel) {
                ( (InitPanel) aComponent).updateTextArea(message);
                if (tabbedPane.getSelectedIndex() != indexOfTab) {
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
        if (toServer != null) {
            toServer.print(lineToServer + "\r\n");
            toServer.flush();
        }
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
        if (tabbedPane.getModel().isSelected()) {
            int index = tabbedPane.getSelectedIndex();

            // setting a background color to null makes
            // a tab's background to it's default background color
            tabbedPane.setBackgroundAt(index, null);

            tabbedPane.revalidate();
        }
    }

    private void newUser() {
        String userName;

        userName = JOptionPane.showInputDialog(theApp,
                                               nicknameString);

        if (userName != null) {
            addUser(tabbedPane, userName);
        }
    }

    private void joinChan() {
        String channelName;

        channelName = JOptionPane.showInputDialog(theApp, channelNameString);
        joinChannel(channelName);
    }

    private void joinChannel(String channel) {
        if (channel != null) {
            if (channel.startsWith("#")) {
                tabUpdate("Init Window", "Trying to join: " + channel);
                if (toServer != null) {
                    parseSendToCommand("JOIN " + channel);
                }
            }
            else {
                tabUpdate("Init Window", "Trying to join: #" + channel);
                if (toServer != null) {
                    parseSendToCommand("JOIN #" + channel);
                }
            }
        }
        else {
            tabUpdate("Init Window", "No channel name specified.");
        }
    }

    private void changeNick() {
        String newNickname;

        newNickname = JOptionPane.showInputDialog(theApp, nicknameString);

        if (newNickname != null) {
            if (toServer != null) {
                Component aComponent = tabbedPane.getComponentAt(0);
                ( (InitPanel) aComponent).setTitleArea(newNickname +
                    " connected to: " +
                    host + ":" + port);
                parseSendToCommand("NICK " + newNickname);
                nickname = newNickname;
            }
        }
        else {
            tabUpdate("Init Window", "No nickname specified.");
        }
    }

    private void whois() {
        String whoisNickname;

        whoisNickname = JOptionPane.showInputDialog(theApp, nicknameString);

        if (whoisNickname != null && toServer != null) {
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

        if (name.startsWith("#")) {
            channel = new ChannelPanel(name);
            tabbedPane.add(channel, name);
        }
        else {
            channel = new ChannelPanel("#" + name);
            tabbedPane.add(channel, "#" + name);
        }
        tabbedPane.revalidate();

        return channel;
    }

    public JTabbedPane closeChannel(final JTabbedPane tabbedPane, String name) {
        int index;

        index = tabbedPane.indexOfTab(name);

        if (index != -1 && index >= 0) {
            tabbedPane.removeTabAt(index);
        }

        tabbedPane.revalidate();
        return tabbedPane;
    }

    public JTabbedPane closeAllChannels() {
        int count = tabbedPane.getTabCount();
        for (int i=count-1; i>0; i--){
            tabbedPane.removeTabAt(i);
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
            if (keystroke != null) {
                putValue(ACCELERATOR_KEY, keystroke);
            }
        }

        public void actionPerformed(ActionEvent e) {
            ;
        }
    }

    public class InitPanel
        extends JPanel
        implements ActionListener {
        final String name = "Init Window";
        final JTextArea textArea = new JTextArea();
        final JTextField textField = new JTextField();
        private JTextField titleArea;

        public InitPanel() {
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

                    parseSendToCommand(message);
                }
                else {
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
            if (newCaretPosition == oldCaretPosition) {
                textArea.setCaretPosition(oldCaretPosition +
                                          (message + "\n").length());
            }
        }
    }

    private void analyzeCommand(String message) {
        CommandInterpreter cmdI = new CommandInterpreter(message);

        if (cmdI.getCommand().equals("JOIN")) {
            parseSendToCommand(cmdI.getCommand() + " " +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("PART") ||
                 cmdI.getCommand().equals("LEAVE")) {
            parseSendToCommand("PART " +
                               cmdI.getParam1() + " :" +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("QUIT")) {
            parseSendToCommand("QUIT :" +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("WHOIS")) {
            parseSendToCommand("WHOIS " +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("MSG")) {
            parseSendToCommand("PRIVMSG " +
                               cmdI.getParam1() + " :" +
                               cmdI.getMessage());
        }
        else if (cmdI.getCommand().equals("CTCP")) {
            parseSendToCommand("PRIVMSG " +
                               cmdI.getParam1() + " :" + 1 +
                               cmdI.getMessage() + 1);
        }
        else if (cmdI.getCommand().equals("NICK")) {
            parseSendToCommand("NICK " + cmdI.getParam1());
        }
        else if (cmdI.getCommand().equals("TOPIC")) {
            if (cmdI.getMessage().equals("")) {
                parseSendToCommand(cmdI.getCommand() + " " +
                                   cmdI.getParam1() + " :");
            }
            else {
                parseSendToCommand(cmdI.getCommand() + " " +
                                   cmdI.getParam1() + " :" +
                                   cmdI.getMessage());
            }
        }
        else {
            parseSendToCommand(cmdI.getCommand() + " :" +
                               cmdI.getParam1() + " " +
                               cmdI.getMessage());
        }
    }

    public class ChannelPanel
        extends JPanel
        implements ActionListener {
        private String name;
        private SortedListModel usernameList = new SortedListModel();
        private JList userList = new JList(usernameList);
        private JTextArea textArea = new JTextArea();
        private JTextField textField = new JTextField();

        private JTextPane titleArea = new JTextPane();

        private JButton closeButton = new JButton("X");

        public ChannelPanel(String name) {
            this.name = name;

            makePanel();
        }

        private void makePanel() {
            setLayout(new BorderLayout());

            textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            textArea.setEditable(false);
            textArea.setBackground(Color.WHITE);
            textField.addActionListener(this);

            userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            userList.addMouseListener(new MouseAdapter(){
                public void mouseClicked(MouseEvent me){
                    if (me.getClickCount()==2){
                        String name = (String)userList.getSelectedValue();
                        if (name.charAt(0)=='!' || name.charAt(0)=='%' || name.charAt(0)=='@' || name.charAt(0)=='+'){
                            name = name.substring(1);
                        }
                        if (name.compareToIgnoreCase(nickname) != 0){
                            for (int i = 1; i < tabbedPane.getTabCount(); i++) {
                                if (tabbedPane.getTitleAt(i).
                                    compareToIgnoreCase(name) == 0) {
                                    return;
                                }
                            }
                            addUser(tabbedPane, name);
                            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() -
                                1);
                        }
                    }
                }
            });
            userList.setCellRenderer(new UserListCellRenderer());

            JScrollPane sp1 = new JScrollPane(textArea);
            JScrollPane sp2 = new JScrollPane(userList);

            sp1.setVerticalScrollBarPolicy(JScrollPane.
                                           VERTICAL_SCROLLBAR_ALWAYS);
            sp2.setVerticalScrollBarPolicy(JScrollPane.
                                           VERTICAL_SCROLLBAR_ALWAYS);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                  sp1,
                                                  sp2);
//            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerLocation(theApp.getSize().width - 200);
            add(splitPane, BorderLayout.CENTER);
            add(textField, BorderLayout.SOUTH);
            add(makeNorth(), BorderLayout.NORTH);
        }

        private Box makeNorth() {
            Box northBox = Box.createHorizontalBox();

            // let's add actions
            closeButton.addActionListener(this);

            northBox.add(closeButton);
            titleArea.setEditable(false);
            northBox.add(titleArea);

            return northBox;
        }

        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            // let's take care of textField
            if (source == textField) {
                String message = textField.getText();
                if (message.length()>450){
                    message = message.substring(0, 450);
                }
                // let's send to server
                if (message.startsWith("/")) {
                    // commands that start with "/"
                    // message = message.substring(1);
                    analyzeCommand(message);
                    textField.setText("");
                }
                else {
                    // A private message to channel!
                    parseSendToCommand("PRIVMSG " + name + " :" + message);

                    // let's update textArea
                    // textArea.append(textField.getText() + "\n");
                    updateTextArea(formatNickname("<" + getNickname() + "> ") +
                                   textField.getText());
                    textField.setText("");
                }
            }
            else if (source == closeButton) {
                closeChannel(tabbedPane, name);

                // Let's send a sensible message
                parseSendToCommand("PART " + name);
            }
        }

        public void setTitleArea(String title) {
            StyledDocument doc = titleArea.getStyledDocument();
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setBackground(attributes, Color.WHITE);
            try {
                doc.remove(0, doc.getLength());
                int startIndex = 0;
                for (int i=0; i<title.length(); i++){
                    if (title.charAt(i) == 3 || i==title.length()-1){
                        if (title.charAt(i) == 3){
                            String toWrite = title.substring(startIndex, i);
                            if (toWrite.length()>0){
                                attributes = writeString(doc, attributes,
                                    toWrite);
                            }
                            startIndex = i+1;
                            i++;
                        }
                        else{
                            String toWrite = title.substring(startIndex);
                            if (toWrite.length()>0){
                                attributes = writeString(doc, attributes,
                                    toWrite);
                            }
                        }
                    }
                }
            }
            catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        }

        private SimpleAttributeSet writeString(StyledDocument doc, SimpleAttributeSet attributes, String toWrite){
            boolean istNachkomma = false;
            boolean parsEnde = false;
            int index = 0;
            if (toWrite.length()>1){
                while (!parsEnde) {
                    if (toWrite.charAt(index) == ',') {
                        istNachkomma = true;
                        index++;
                        continue;
                    }
                    try {
                        int colorCode = Integer.parseInt(toWrite.substring(
                            index, index+2));
                        index += 2;
                        Color color = getColor(colorCode);
                        if (!istNachkomma) {
                            StyleConstants.setForeground(attributes, color);
                        }
                    }
                    catch (NumberFormatException nfE) {
                        if (toWrite.charAt(index + 1) != ',') {
                            parsEnde = true;
                        }
                        try {
                            int colorCode = Integer.parseInt(toWrite.substring(
                                index, index+1));
                            index++;
                            Color color = getColor(colorCode);
                            if (!istNachkomma) {
                                StyleConstants.setForeground(attributes, color);
                            }
                        }
                        catch (NumberFormatException nfE2) {
                            parsEnde = true;
                            StyleConstants.setForeground(attributes,
                                Color.BLACK);
                        }
                    }
                    catch(StringIndexOutOfBoundsException sioobE){
                        parsEnde = true;
                        int colorCode = Integer.parseInt(toWrite.substring(
                            index, index+1));
                        Color color = getColor(colorCode);
                        if (!istNachkomma) {
                            StyleConstants.setForeground(attributes, color);
                        }
                        return attributes;
                    }
                }
            }
            try {
                doc.insertString(doc.getLength(),
                                 toWrite.substring(index),
                                 attributes);
            }
            catch (BadLocationException ex) {
                ex.printStackTrace();
            }
            return attributes;
        }

        private Color getColor(int code){
            switch (code){
                case 0: return Color.BLACK;
                case 1: return Color.BLACK;
                case 2: return Color.BLUE;
                case 3: return Color.GREEN;
                case 4: return Color.RED;
                case 5: return Color.BLACK;
                case 6: return Color.PINK;
                case 7: return Color.ORANGE;
                case 8: return Color.YELLOW;
                case 9: return Color.GREEN;
                case 10: return Color.GREEN;
                case 11: return Color.CYAN;
                case 12: return Color.BLUE;
                case 13: return Color.PINK;
                case 14: return Color.GRAY;
                case 15: return Color.LIGHT_GRAY;
                default: return null;
            }
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

        public void updateUserArea(String username, String command) {
            if (command.equals("add")) {
                usernameList.add(username);
            }

            else if (command.equals("remove")) {
                usernameList.remove(username);
            }
        }
    }

    public class UserPanel
        extends JPanel
        implements ActionListener {
        private String name;
        private final JTextArea textArea = new JTextArea();
        private final JTextField textField = new JTextField();
        private JTextField titleArea = new JTextField();

        private JButton closeButton = new JButton("X");

        public UserPanel(String name) {
            this.name = name;

            makePanel();
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
                    analyzeCommand(message);
                    textField.setText("");
                }
                else {
                    // A normal private message to send to the user
                    parseSendToCommand("PRIVMSG " + name + " :" + message);

                    // let's update textArea
                    updateTextArea(formatNickname("<" + getNickname() + "> ") +
                                   textField.getText());
                    resetTextField();
                }
            }
            else if (source == closeButton) {
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
            if (newCaretPosition == oldCaretPosition) {
                textArea.setCaretPosition(oldCaretPosition +
                                          (message + "\n").length());
            }
        }
    }
}
