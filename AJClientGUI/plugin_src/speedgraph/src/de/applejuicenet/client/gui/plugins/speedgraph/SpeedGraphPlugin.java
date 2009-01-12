/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.speedgraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JScrollPane;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.gui.plugins.PluginConnector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/speedgraph/src/de/applejuicenet/client/gui/plugins/speedgraph/SpeedGraphPlugin.java,v 1.3 2009/01/12 10:00:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class SpeedGraphPlugin extends PluginConnector
{
   private static Logger logger;
   private GraphPanel    graphPanel = new GraphPanel();

   public SpeedGraphPlugin(Properties properties, Map<String, Properties> languageFiles, ImageIcon icon,
                           Map<String, ImageIcon> availableIcons)
   {
      super(properties, languageFiles, icon, availableIcons);
      logger = Logger.getLogger(getClass());
      try
      {
         setLayout(new BorderLayout());
         graphPanel.setBackground(Color.BLACK);
         add(new JScrollPane(graphPanel), BorderLayout.CENTER);
         setBackground(Color.BLACK);
         AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.SPEED_CHANGED);
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error("Unbehandelte Exception", e);
         }
      }
   }

   public void fireLanguageChanged()
   {
   }

   /*Wird automatisch aufgerufen, wenn neue Informationen vom Server eingegangen sind.
     Über den DataManger können diese abgerufen werden.*/
   @SuppressWarnings("unchecked")
   public void fireContentChanged(DATALISTENER_TYPE type, Object content)
   {
      if(type == DATALISTENER_TYPE.SPEED_CHANGED)
      {
         graphPanel.update((HashMap) content);
      }
   }

   public void registerSelected()
   {
   }
}
