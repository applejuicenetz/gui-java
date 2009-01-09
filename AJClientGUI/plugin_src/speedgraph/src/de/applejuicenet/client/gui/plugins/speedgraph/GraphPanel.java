/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.speedgraph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.applejuicenet.client.AppleJuiceClient;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/speedgraph/src/de/applejuicenet/client/gui/plugins/speedgraph/GraphPanel.java,v 1.7 2009/01/09 14:21:36 maj0r Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class GraphPanel extends JPanel
{
   private String                uploadSpeedKey        = "uploadspeed";
   private String                downloadSpeedKey      = "downloadspeed";
   private int                   leftBorder            = 33;
   private int                   width;
   private int                   imageHeight;
   private int                   bottomHeight          = 30;
   private int                   topHeight             = 30;
   private int                   minImageHeight        = bottomHeight + 280;
   private long                  time;
   private SimpleDateFormat      formatter             = new SimpleDateFormat("HH:mm");
   private Font                  timeFont;
   private Image                 image                 = null;
   private UpdateThread          updateThread;
   private Map<Integer, Integer> pendingDownloadSpeeds = new HashMap<Integer, Integer>();
   private Map<Integer, Integer> pendingUploadSpeeds   = new HashMap<Integer, Integer>();
   private double                pxPerKb;
   private int                   lastUploadY;
   private int                   lastDownloadY;
   private Integer               curX = 0;

   public GraphPanel()
   {
      updateThread = new UpdateThread();
      updateThread.start();
   }

   @Override
   protected void paintComponent(Graphics g)
   {
      if(null != image)
      {
         Dimension size = getSize();

         // wird ggf. skaliert
         g.drawImage(image, 0, 0, size.width, size.height, null);
      }
      else
      {
         super.paintComponent(g);
      }
   }

   @SuppressWarnings("unchecked")
   public synchronized void update(HashMap speeds)
   {
      curX++;
      synchronized(pendingDownloadSpeeds)
      {
         pendingDownloadSpeeds.put(curX, ((Long) speeds.get(downloadSpeedKey)).intValue());
      }

      synchronized(pendingDownloadSpeeds)
      {
         pendingUploadSpeeds.put(curX, ((Long) speeds.get(uploadSpeedKey)).intValue());
      }
   }

   private class UpdateThread extends Thread
   {
      public UpdateThread()
      {
         super("SpeedGraphPaintThread");
         setDaemon(true);
      }

      @Override
      public void run()
      {
         while(true)
         {
            try
            {
               sleep(2000);
            }
            catch(InterruptedException e)
            {

               // nix zu tun
               ;
            }

            if(null == image)
            {
               width = getWidth();
               if(getHeight() < minImageHeight)
               {
                  imageHeight = minImageHeight;
               }
               else
               {
                  imageHeight = getHeight();
               }

               if(width == 0 || imageHeight == 0)
               {

                  /*
                   Da kamen schneller Daten rein, als das Plugin angezeigt werden konnte.
                   Wir nehmen folglich erst den naechsten Durchgang.
                    */
                  continue;
               }

               long maxDownKb = AppleJuiceClient.getAjFassade().getCurrentAJSettings().getMaxDownloadInKB();

               int  remainingHeight = imageHeight - bottomHeight - topHeight;

               pxPerKb = ((double) remainingHeight) / (maxDownKb + 10);

               int yAbstaende  = 30;
               int anzahlMarks = (remainingHeight / yAbstaende);

               image = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_ARGB);
               Graphics g = image.getGraphics();

               // erstmal schwaerzen
               g.setColor(Color.BLACK);
               g.fillRect(0, 0, width, imageHeight);

               g.setColor(Color.WHITE);
               // senkrechte Linie
               g.drawLine(leftBorder, imageHeight - bottomHeight, leftBorder, topHeight);
               FontMetrics fm        = g.getFontMetrics();
               String      markText;
               int         strWidth;
               int         strHeight = fm.getHeight();

               for(int i = 0; i <= anzahlMarks; i++)
               {
                  markText = "" + (int) (((double) (maxDownKb + 10)) / anzahlMarks * i);
                  strWidth = fm.stringWidth(markText);

                  g.drawString(markText, leftBorder - strWidth - 4, imageHeight - bottomHeight - i * yAbstaende);
                  g.drawLine(leftBorder - 2, imageHeight - bottomHeight - i * yAbstaende, width,
                             imageHeight - bottomHeight - i * yAbstaende);
               }

               strWidth = fm.stringWidth("kb/s");
               g.drawString("kb/s", leftBorder - (strWidth / 2), topHeight / 2);
               g.setColor(Color.GREEN);
               g.drawLine(70, topHeight / 2, 85, topHeight / 2);
               g.setColor(Color.WHITE);
               g.drawString("Download", 92, topHeight / 4 * 3);

               g.setColor(Color.YELLOW);
               g.drawLine(170, topHeight / 2, 185, topHeight / 2);
               g.setColor(Color.WHITE);
               g.drawString("Upload", 192, topHeight / 4 * 3);

               Font gFont = g.getFont();

               timeFont = new Font(gFont.getName(), gFont.getStyle(), 9);
               g.setFont(timeFont);
               fm       = g.getFontMetrics();
               time     = System.currentTimeMillis();
               String actualTime = formatter.format(new Date(time));

               strWidth = fm.stringWidth(actualTime);
               g.drawString(actualTime, leftBorder - strWidth / 2, imageHeight - (bottomHeight / 2));
            }

            Map<Integer, Integer> copyPendingDownloadSpeeds = new HashMap<Integer, Integer>();
            Map<Integer, Integer> copyPendingUploadSpeeds = new HashMap<Integer, Integer>();

            synchronized(pendingDownloadSpeeds)
            {
               copyPendingDownloadSpeeds.putAll(pendingDownloadSpeeds);
               pendingDownloadSpeeds.clear();
            }

            synchronized(pendingDownloadSpeeds)
            {
               copyPendingUploadSpeeds.putAll(pendingUploadSpeeds);
               pendingUploadSpeeds.clear();
            }

            Graphics g = image.getGraphics();

            g.setColor(Color.GREEN);
            Integer speed;

            int     curLastX = -1;

            for(Integer actualX : copyPendingDownloadSpeeds.keySet())
            {
               speed = copyPendingDownloadSpeeds.get(actualX);
               int curY = (int) (speed * pxPerKb);

               if(actualX > 0)
               {
                  g.drawLine(leftBorder + actualX - 1, imageHeight - bottomHeight - lastDownloadY, leftBorder + actualX,
                             imageHeight - bottomHeight - curY);
               }

               lastDownloadY = curY;
               curLastX      = actualX;
            }

            g.setColor(Color.YELLOW);
            for(Integer actualX : copyPendingUploadSpeeds.keySet())
            {
               speed = copyPendingUploadSpeeds.get(actualX);
               int curY = (int) (speed * pxPerKb);

               if(actualX > 0)
               {
                  g.drawLine(leftBorder + actualX - 1, imageHeight - bottomHeight - lastUploadY, leftBorder + actualX,
                             imageHeight - bottomHeight - curY);
               }

               lastUploadY = curY;
               curLastX    = actualX;
            }

            long curTime = System.currentTimeMillis();

            if(curLastX != -1 && time - curTime > 300000)
            {
               g.setColor(Color.WHITE);
               Font gFont = g.getFont();

               timeFont = new Font(gFont.getName(), gFont.getStyle(), 9);
               g.setFont(timeFont);
               FontMetrics fm = g.getFontMetrics();

               fm = g.getFontMetrics();
               String actualTime = formatter.format(new Date(curTime));

               int    strWidth = fm.stringWidth(actualTime);

               g.drawString(actualTime, leftBorder + curLastX - strWidth / 2, imageHeight - (bottomHeight / 2));
               time = curTime;
            }

            SwingUtilities.invokeLater(new Runnable()
               {
                  public void run()
                  {
                     repaint();
                     System.out.println("paint");
                  }
               });
         }
      }
   }
}
