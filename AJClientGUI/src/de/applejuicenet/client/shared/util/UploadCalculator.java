/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared.util;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.shared.IconManager;

public abstract class UploadCalculator
{
   private static JProgressBar progress                    = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
   private static JProgressBar wholeLoadedProgress         = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
   private static JLabel       progressbarLabel            = new JLabel();
   private static JLabel       versionLabel                = new JLabel();
   private static JLabel       wholeLoadedProgressbarLabel = new JLabel();

   static
   {
      progress.setStringPainted(true);
      progress.setOpaque(false);
      progressbarLabel = new JLabel();
      progressbarLabel.setOpaque(true);
      versionLabel.setOpaque(true);
      wholeLoadedProgress.setStringPainted(true);
      wholeLoadedProgress.setOpaque(false);
      wholeLoadedProgressbarLabel.setOpaque(true);
   }

   public static Component getProgressbarComponent(Upload upload)
   {
      if(upload.getStatus() == Upload.AKTIVE_UEBERTRAGUNG)
      {
         String prozent = upload.getDownloadPercentAsString();

         progress.setString(prozent + " %");
         int pos = prozent.indexOf(',');

         if(pos != -1)
         {
            prozent = prozent.substring(0, pos);
         }
         else
         {
            pos = prozent.indexOf('.');
            if(pos != -1)
            {
               prozent = prozent.substring(0, pos);
            }
         }

         progress.setValue(Integer.parseInt(prozent));
         return progress;
      }
      else
      {
         return progressbarLabel;
      }
   }

   public static Component getVersionComponent(Upload upload, JTable table)
   {
      versionLabel.setFont(table.getFont());
      if(upload.getVersion() == null)
      {
         versionLabel.setIcon(null);
         versionLabel.setText("");
      }
      else
      {
         versionLabel.setIcon(getVersionIcon(upload));
         versionLabel.setText(upload.getVersion().getVersion());
      }

      return versionLabel;
   }

   public static Component getWholeLoadedProgressbarComponent(Upload upload)
   {
      if(upload.getLoaded() != -1)
      {
         String prozent = upload.getLoadedPercentAsString();

         wholeLoadedProgress.setString(prozent + " %");
         int pos = prozent.indexOf(',');

         if(pos != -1)
         {
            prozent = prozent.substring(0, pos);
         }
         else
         {
            pos = prozent.indexOf('.');
            if(pos != -1)
            {
               prozent = prozent.substring(0, pos);
            }
         }

         wholeLoadedProgress.setValue(Integer.parseInt(prozent));
         return wholeLoadedProgress;
      }
      else
      {
         return wholeLoadedProgressbarLabel;
      }
   }

   public static ImageIcon getVersionIcon(Upload upload)
   {
      switch(upload.getVersion().getBetriebsSystem())
      {

         case Version.WIN32:
            return IconManager.getInstance().getIcon("winsymbol");

         case Version.LINUX:
            return IconManager.getInstance().getIcon("linuxsymbol");

         case Version.FREEBSD:
            return IconManager.getInstance().getIcon("freebsdsymbol");

         case Version.MACINTOSH:
            return IconManager.getInstance().getIcon("macsymbol");

         case Version.SOLARIS:
            return IconManager.getInstance().getIcon("sunossymbol");

         case Version.NETWARE:
            return IconManager.getInstance().getIcon("netwaresymbol");

         case Version.OS2:
            return IconManager.getInstance().getIcon("os2symbol");

         default:
            return IconManager.getInstance().getIcon("unbekanntsymbol");
      }
   }
}
