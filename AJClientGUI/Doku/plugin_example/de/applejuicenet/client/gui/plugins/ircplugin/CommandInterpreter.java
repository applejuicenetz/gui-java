package de.applejuicenet.client.gui.plugins.ircplugin;

/*
 * @(#) jqIRC	0.4	08/12/2001
 *
 * Copyright (c), 2000 by jqIRC, Inc. ^-^
 *
 * License: We grant you this piece of source code to play with
 * as you wish provided that 1) you buy us a drink when we meet
 * somewhere someday. 2) Incase you don't want to fullfill the
 * first condition, you just buy something for one of your
 * beloved friends. 3) Attach below messages somewhere. ^-^
 *
 *                To some people, a friend
 *            is practically anyone they know.
 *                To me, friendship means
 *              a much closer relationship,
 *            one in which you take the time
 *            to really understand each other.
 *         A friend is someone you trust enough
 *              to share a part of yourself
 *         the rest of the world may never see.
 *               That kind of friendship
 *            doesn't come along everyday...
 *            but that's the way it should be.
 *
 */

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/Doku/plugin_example/de/applejuicenet/client/gui/plugins/ircplugin/Attic/CommandInterpreter.java,v 1.2 2003/08/28 15:53:02 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: CommandInterpreter.java,v $
 * Revision 1.2  2003/08/28 15:53:02  maj0r
 * NullPointer behoben und Header eingefuegt.
 *
 *
 */

import java.util.*;

public class CommandInterpreter {
    String lineToServer;
    String command = "",
    param1 = "",
    message = "";

    public CommandInterpreter(String lineToServer) {
        this.lineToServer = lineToServer;
        init_all();
    }

    private void init_all() {
        if (lineToServer.startsWith("/"))
        {
            StringTokenizer st = new StringTokenizer(lineToServer, " \r\n");
            int totalTokens = st.countTokens();
            if (totalTokens >= 3)
            {
                String temp = st.nextToken();

                temp = temp.toUpperCase();
                temp = temp.substring(1);
                command = temp;

                temp = st.nextToken();
                param1 = temp;

                temp = st.nextToken("\n");
                if (temp.startsWith(" "))
                {
                    temp = temp.substring(1);

                }
                message = temp;
            }
            else if (totalTokens == 2)
            {
                String temp = st.nextToken();

                temp = temp.toUpperCase();
                temp = temp.substring(1);
                command = temp;

                temp = st.nextToken();
                param1 = temp;
            }
        }
    }

    public String getCommand() {
        return command;
    }

    public String getParam1() {
        return param1;
    }

    public String getMessage() {
        return message;
    }
}
