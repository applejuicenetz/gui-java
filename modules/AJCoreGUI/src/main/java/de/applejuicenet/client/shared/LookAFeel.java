/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

public class LookAFeel
{
   private String name;
   private String className;

   public LookAFeel(String name, String className)
   {
      this.name = name;
      if(className.equals("com.jgoodies.plaf.windows.ExtWindowsLookAndFeel"))
      {
         className = "com.jgoodies.looks.windows.WindowsLookAndFeel";
      }
      else if(className.startsWith("com.jgoodies.plaf"))
      {
         className = className.replaceAll("plaf", "looks");
      }

      this.className = className;
   }

   public String getName()
   {
      return name;
   }

   public String getClassName()
   {
      return className;
   }
}
