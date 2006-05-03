package de.applejuicenet.client.gui.options;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.shared.ZeichenErsetzer;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.plugins.PluginConnector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/ODPluginOptionsDialog.java,v 1.4 2006/05/03 14:52:00 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class ODPluginOptionsDialog extends JDialog
{
   private PluginConnector pluginConnector;
   private JButton         schliessen = new JButton();
   private Logger          logger;

   public ODPluginOptionsDialog(JDialog parent, PluginConnector pluginConnector)
   {
      super(parent, true);
      try
      {
         this.pluginConnector = pluginConnector;
         logger = Logger.getLogger(getClass());
         init();
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   private void init()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      schliessen.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent ae)
            {
               ODPluginOptionsDialog.this.dispose();
            }
         });

      String title = pluginConnector.getTitle() + " - ";

      title += ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(
            ".root.javagui.options.plugins.einstellungen"));
      schliessen.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.getFirstAttrbuteByTagName(
               ".root.javagui.options.plugins.schliessen")));
      setTitle(title);
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(pluginConnector.getOptionPanel(), BorderLayout.CENTER);
      JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

      southPanel.add(schliessen);
      getContentPane().add(southPanel, BorderLayout.SOUTH);
      pack();
      Dimension appDimension = getSize();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

      setLocation((screenSize.width - appDimension.width) / 2, (screenSize.height - appDimension.height) / 2);
   }
}
