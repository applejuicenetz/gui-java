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

import java.io.*;
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
    if (lineToServer.startsWith("/")) {
      StringTokenizer st = new StringTokenizer(lineToServer, " \r\n");
      int totalTokens = st.countTokens();
      if (totalTokens >= 3) {
        String temp = st.nextToken();

        // command
        temp = temp.toUpperCase();
        temp = temp.substring(1);
        command = temp;

        // param1
        temp = st.nextToken();
        param1 = temp;

        // message
        temp = st.nextToken("\n");
        if (temp.startsWith(" ")) {
          temp = temp.substring(1);

        }
        message = temp;
      }
      else if (totalTokens == 2) {
        String temp = st.nextToken();

        // command
        temp = temp.toUpperCase();
        temp = temp.substring(1);
        command = temp;

        // param1
        temp = st.nextToken();
        param1 = temp;
      }
      else {
        // not a valid command
      }
    }
    else {
      // not a command
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
