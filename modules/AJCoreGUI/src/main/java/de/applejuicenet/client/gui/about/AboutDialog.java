/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.about;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.IconManager;
import de.tklsoft.gui.controls.TKLLabel;
import de.tklsoft.gui.controls.TKLPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/about/AboutDialog.java,v 1.14 2009/01/12 09:19:20 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class AboutDialog extends JDialog
{
   private Logger       logger;
   private WorkerThread worker    = null;
   private BackPanel    backPanel = new BackPanel();

   public AboutDialog(Frame parent, boolean modal)
   {
      super(parent, modal);
      logger = Logger.getLogger(getClass());
      try
      {
         addWindowListener(new WindowAdapter()
            {
               public void windowClosing(WindowEvent evt)
               {
                  if(worker != null)
                  {
                     worker.interrupt();
                     worker = null;
                  }
               }
            });
         init();
      }
      catch(Exception e)
      {
         logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
      }
   }

   public Dimension getPreferredSize()
   {
      if(backPanel != null)
      {
         return backPanel.getPreferredSize();
      }
      else
      {
         return super.getPreferredSize();
      }
   }

   private void init()
   {
      setResizable(false);
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      setTitle(languageSelector.getFirstAttrbuteByTagName("mainform.aboutbtn.caption"));
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(backPanel, BorderLayout.CENTER);
      pack();
   }

   class BackPanel extends TKLPanel
   {
      private Image              backgroundImage;
      private Image              flagge;
      private TKLLabel           version = new TKLLabel();
      private List<CreditsEntry> credits = new ArrayList<CreditsEntry>();
      private Logger             logger;

      public BackPanel()
      {
         super();
         logger = Logger.getLogger(getClass());
         try
         {
            init();
         }
         catch(Exception e)
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }

         worker = new WorkerThread(backgroundImage, this, credits);
         worker.start();
         addMouseListener(new MouseAdapter()
            {
               public void mouseClicked(MouseEvent me)
               {
                  if(worker != null)
                  {
                     worker.toggleRunStatus();
                  }
               }
            });
      }

      private void init()
      {
         credits.add(new CreditsEntry(true, "Programmierung"));
         credits.add(new CreditsEntry(false, "Maj0r"));
         credits.add(new CreditsEntry(false, "(tkrall@tkl-soft.de)"));
         credits.add(new CreditsEntry(false, "loevenwong"));
         credits.add(new CreditsEntry(false, "(tloevenich@tkl-soft.de)"));
         credits.add(new CreditsEntry(false, "red171"));
         credits.add(new CreditsEntry(false, "(red171@tr4sh.eu)"));
         credits.add(new CreditsEntry(true, "Besonderen Dank an"));
         credits.add(new CreditsEntry(false, "muhviehstarr"));
         credits.add(new CreditsEntry(true, "Banner & Bilder"));
         credits.add(new CreditsEntry(false, "saschxd"));
         credits.add(new CreditsEntry(true, "Übersetzung"));
         credits.add(new CreditsEntry(false, "BlueTiger"));
         credits.add(new CreditsEntry(false, "nurseppel"));
         credits.add(new CreditsEntry(true, "Kontakt"));
         credits.add(new CreditsEntry(false, "applejuicenet.cc"));

         backgroundImage = IconManager.getInstance().getIcon("applejuiceinfobanner").getImage();
         flagge          = IconManager.getInstance().getIcon("deutsch").getImage();
         MediaTracker mt = new MediaTracker(this);

         mt.addImage(backgroundImage, 0);
         try
         {
            mt.waitForAll();
         }
         catch(InterruptedException x)
         {

            //kein Bild da, dann kack drauf ;-)
         }

         version.setText("Version " + AppleJuiceDialog.getVersion());
         Font font = version.getFont();

         font = new Font(font.getName(), Font.PLAIN, font.getSize());
         version.setFont(font);
         setLayout(new BorderLayout());
         TKLPanel panel1 = new TKLPanel(new FlowLayout(FlowLayout.RIGHT));

         panel1.add(version);
         panel1.setOpaque(false);
         add(panel1, BorderLayout.SOUTH);
      }

      public void paintComponent(Graphics g)
      {
         super.paintComponent(g);

         Color saved = g.getColor();

         g.setColor(getBackground());
         g.fillRect(0, 0, getWidth(), getHeight());
         g.setColor(saved);

         if(backgroundImage != null)
         {
            int imageX = (getWidth() - backgroundImage.getWidth(this)) / 2;
            int imageY = (getHeight() - backgroundImage.getHeight(this)) / 2;

            g.drawImage(backgroundImage, imageX, imageY, this);
            if(flagge != null)
            {
               g.drawImage(flagge, backgroundImage.getWidth(this) - flagge.getWidth(this), 0, this);
            }
         }
      }

      public Dimension getPreferredSize()
      {
         if(backgroundImage != null)
         {
            int width  = backgroundImage.getWidth(this) + 3;
            int height = backgroundImage.getHeight(this) + 3;

            return new Dimension(width, height);
         }
         else
         {
            return super.getPreferredSize();
         }
      }
   }


   class CreditsEntry
   {
      private boolean ueberschrift;
      private String  ausgabetext;

      public CreditsEntry(boolean ueberschrift, String ausgabetext)
      {
         this.ueberschrift = ueberschrift;
         this.ausgabetext  = ausgabetext;
      }

      public boolean isUeberschrift()
      {
         return ueberschrift;
      }

      public void setUeberschrift(boolean ueberschrift)
      {
         this.ueberschrift = ueberschrift;
      }

      public String getAusgabetext()
      {
         return ausgabetext;
      }

      public void setAusgabetext(String ausgabetext)
      {
         this.ausgabetext = ausgabetext;
      }
   }


   private class WorkerThread extends Thread
   {
      private Image              backgroundImage;
      private BackPanel          backPanel;
      private List<CreditsEntry> credits;
      private boolean            run    = true;
      private Logger             logger;

      public WorkerThread(Image backgroundImage, BackPanel backPanel, List<CreditsEntry> credits)
      {
         logger               = Logger.getLogger(getClass());
         this.backgroundImage = backgroundImage;
         this.backPanel       = backPanel;
         this.credits         = credits;
      }

      public void toggleRunStatus()
      {
         run = !run;
      }

      public void run()
      {
         if(logger.isEnabledFor(Level.DEBUG))
         {
            logger.debug("About-Workerthread gestartet. " + this);
         }

         Image       new_img;
         Image       toDraw;
         ImageFilter filter        = new ImageFilter();
         int         creditsHoehe  = 60;
         int         creditsBreite = 135;
         int         imageX        = backgroundImage.getWidth(backPanel) / 2 + 20;
         int         imageY        = backgroundImage.getHeight(backPanel) / 2 - 15;

         filter  = new CropImageFilter(imageX, imageY, creditsBreite, creditsHoehe);
         new_img = createImage(new FilteredImageSource(backgroundImage.getSource(), filter));
         filter  = new CropImageFilter(0, 0, creditsBreite, creditsHoehe);
         int y   = creditsHoehe;

         try
         {
            sleep(1000);
            Graphics g = backPanel.getGraphics();

            g.setColor(Color.BLACK);
            Graphics    toDrawGraphics;
            FontMetrics fm;
            int         strWidth;
            Font        fontBold  = new Font("Arial", Font.BOLD, 12);
            Font        fontPlain = new Font("Arial", Font.PLAIN, 12);
            boolean     draw      = false;

            while(!isInterrupted())
            {
               if(run)
               {
                  toDraw         = createImage(creditsBreite, creditsHoehe);
                  toDrawGraphics = toDraw.getGraphics();
                  toDrawGraphics.drawImage(new_img, 0, 0, backPanel);
                  y--;
                  int abstand = -15;

                  for(CreditsEntry curEntry : credits)
                  {
                     if(curEntry.isUeberschrift())
                     {
                        abstand += 20;
                        toDrawGraphics.setFont(fontBold);
                        toDrawGraphics.setColor(Color.BLUE);
                     }
                     else
                     {
                        abstand += 15;
                        toDrawGraphics.setFont(fontPlain);
                        toDrawGraphics.setColor(Color.BLACK);
                     }

                     fm       = toDrawGraphics.getFontMetrics();
                     strWidth = fm.stringWidth(curEntry.getAusgabetext());
                     toDrawGraphics.drawString(curEntry.getAusgabetext(), (creditsBreite - strWidth) / 2, y + abstand);
                  }

                  if(draw)
                  {
                     g.drawImage(toDraw, imageX - 1, imageY - 11, backPanel);
                  }
                  else
                  {
                     draw = true;
                  }

                  if(y == -5 - credits.size() * 15)
                  {
                     y = creditsHoehe;
                  }
               }

               try
               {
                  sleep(100);
               }
               catch(InterruptedException iE)
               {
                  interrupt();
               }
            }
         }
         catch(Exception e)
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }

         if(logger.isEnabledFor(Level.DEBUG))
         {
            logger.debug("About-Workerthread beendet. " + this);
         }
      }
   }
}
