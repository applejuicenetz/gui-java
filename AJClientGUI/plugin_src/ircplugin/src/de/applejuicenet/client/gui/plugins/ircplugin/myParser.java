package de.applejuicenet.client.gui.plugins.ircplugin;

import java.util.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/myParser.java,v 1.4 2004/10/15 13:34:47 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class myParser {
    String line;
    String prefix = "",
    command = "",
    params = "",
    middle = "",
    trailing = "",
    servername = "",
    nick = "",
    user = "",
    host = "";

    public myParser(String line) {
        this.line = line;
        initTags();
    }

    private void initTags() {
        StringTokenizer st = new StringTokenizer(line, " \r\n");
        int totalTokens = st.countTokens();

        if (line.startsWith(":") && totalTokens >= 3)
        {
            String temp = st.nextToken();
            int index = temp.indexOf(":");
            if (index != -1){
                prefix = temp.substring(index + 1);
            }

            temp = st.nextToken();
            command = temp;

            temp = st.nextToken("\n");
            params = temp;
        }
        else if (!line.startsWith(":") && totalTokens >= 2)
        {
            String temp = st.nextToken();
            command = temp;
            while (st.hasMoreTokens()){
                params = params + st.nextToken();
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String getCommand() {
        return command;
    }

    public String getParams() {
        return params;
    }

    public String getServer() {
        if (!prefix.equals(""))
        {
            int index = prefix.indexOf("!");
            if (index != -1)
            {
                String temp = prefix.substring(0, index);
                servername = temp;
            }
        }
        return servername;
    }

    public String getNick() {
        if (!prefix.equals(""))
        {
            int index = prefix.indexOf("!");
            if (index != -1)
            {
                String temp = prefix.substring(0, index);
                nick = temp;
            }
        }
        return nick;
    }

    public String getUser() {
        if (!prefix.equals(""))
        {
            int exMark = prefix.indexOf("!");
            int adMark = prefix.indexOf("@");
            if (exMark != -1 && adMark != -1 && (adMark > exMark))
            {
                user = prefix.substring(exMark + 1, adMark);
            }
        }
        return user;
    }

    public String getHost() {
        if (!prefix.equals(""))
        {
            int adMark = prefix.indexOf("@");
            if (adMark != -1 && adMark >= 0)
            {
                host = prefix.substring(adMark + 1);
            }
        }
        return host;
    }

    public String getTrailing() {
        if (!params.equals(""))
        {
            int index = params.indexOf(":");
            if (index != -1 && index >= 0)
            {
                trailing = params.substring(index + 1);
            }
        }
        return trailing;
    }

    public String getMiddle() {
        if (!params.equals(""))
        {
            int index = params.indexOf(":");
            if (index != -1 && index >= 0)
            {
                if (params.startsWith(" ") && index - 1 >= 1){
                    middle = params.substring(1, index - 1);
                }
                else{
                    middle = params.substring(0, index - 1);
                }
            }
        }
        return middle;
    }
}
