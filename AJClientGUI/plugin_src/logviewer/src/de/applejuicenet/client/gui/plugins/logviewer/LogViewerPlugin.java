/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import java.awt.BorderLayout;

import java.io.File;
import java.io.FilenameFilter;

import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.controller.xml.XMLValueHolder;
import de.applejuicenet.client.gui.plugins.PluginConnector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/logviewer/src/de/applejuicenet/client/gui/plugins/logviewer/LogViewerPlugin.java,v 1.3 2009/01/08 09:45:59 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: GPL</p>
 *
 * @author: loevenwong <timo@loevenwong.de>
 *
 */
public class LogViewerPlugin extends PluginConnector
{
   private static Logger         logger;
   private static String         path      = AppleJuiceClient.getPath() + File.separator + "logs";
   private JSplitPane            splitPane = null;
   private JTextPane             logPane   = new JTextPane();
   private SortedStringListModel listModel = new SortedStringListModel();
   private JList                 list      = new JList(listModel);

   public LogViewerPlugin(XMLValueHolder pluginsPropertiesXMLHolder, Map<String, XMLValueHolder> languageFiles, ImageIcon icon,
                          Map<String, ImageIcon> availableIcons)
   {
      super(pluginsPropertiesXMLHolder, languageFiles, icon, availableIcons);
      logger = Logger.getLogger(getClass());
      try
      {
         setLayout(new BorderLayout());
         logPane.setBackground(getBackground());
         logPane.setContentType("text/html");
         logPane.setEditable(false);
         logPane.setBorder(null);
         list.setCellRenderer(new FileNameListCellRenderer());
         splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(list), new JScrollPane(logPane));
         add(splitPane, BorderLayout.CENTER);
         readLogDir();
         list.addListSelectionListener(new ListSelectionListener()
            {
               public void valueChanged(ListSelectionEvent e)
               {
                  if(!e.getValueIsAdjusting())
                  {
                     doDisplayLogfile();
                  }
               }
            });
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error("Unbehandelte Exception", e);
         }
      }
   }

   private void doDisplayLogfile()
   {
      try
      {
         File selectedLog = (File) list.getSelectedValue();

         logPane.setPage("file://localhost/" + selectedLog.getAbsolutePath());
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error("Unbehandelte Exception", e);
         }
      }
   }

   private void readLogDir()
   {
      File logPath = new File(path);

      if(!logPath.isDirectory())
      {
         return;
      }

      File[] htmlFiles = logPath.listFiles(new FilenameFilter()
         {
            public boolean accept(File dir, String name)
            {
               return name.endsWith(".html");
            }
         });

      listModel.setData(htmlFiles);
   }

   public void fireLanguageChanged()
   {
   }

   public void registerSelected()
   {
   }

   public void fireContentChanged(de.applejuicenet.client.fassade.listener.DataUpdateListener.DATALISTENER_TYPE arg0, Object arg1)
   {
   }
}
