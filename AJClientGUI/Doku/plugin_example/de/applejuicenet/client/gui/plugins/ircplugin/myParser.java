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

public class myParser
{
   String line;
   String prefix     = "",
          command    = "",
	  params     = "",
	  middle     = "",
	  trailing   = "",
          servername = "",
	  nick       = "",
	  user       = "",
	  host       = "";

   public myParser(String line)
   {
      this.line = line;

      initTags();
   }

   /**
    * Main initializing method!
    */
   private void initTags()
   {
      StringTokenizer st = new StringTokenizer(line, " \r\n");
      int totalTokens = st.countTokens();

      // At first let us grep prefix
      if (line.startsWith(":") && totalTokens >= 3)
      {
         String temp = st.nextToken();
				 int index = temp.indexOf(":");
				 if (index != -1)
				    prefix = temp.substring(index + 1);

				 // now let's grep command
				 temp = st.nextToken();
				 command = temp;

				 // now let's grep params
				 temp = st.nextToken("\n");
				 params = temp;
      }
      else if (!line.startsWith(":") && totalTokens >= 2)
      {
	 String temp = st.nextToken();

	 // now let's grep command
	 command = temp;

        // now let's grep params
	while(st.hasMoreTokens())
      	  params = params + st.nextToken();
     }
   }

   public String getPrefix()
   {
      return prefix;
   }

   public String getCommand()
   {
      return command;
   }

   public String getParams()
   {
      return params;
   }

	 public String getServer()
	 {
	   if (!prefix.equals("")) // prefix is not empty
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

	 public String getNick()
	 {
	   if (!prefix.equals("")) // prefix is not empty
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

	 public String getUser()
	 {
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

	 public String getHost()
	 {
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

	 public String getTrailing()
	 {
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

	 public String getMiddle()
	 {
	 	  if (!params.equals(""))
			{
	 	    int index = params.indexOf(":");
			  if (index != -1 && index >= 0)
			  {
				  if (params.startsWith(" ") && index - 1 >= 1)
				    middle = params.substring(1, index - 1);
				  else
				    middle = params.substring(0, index - 1);
			  }
			}

			return middle;
	 }
}
