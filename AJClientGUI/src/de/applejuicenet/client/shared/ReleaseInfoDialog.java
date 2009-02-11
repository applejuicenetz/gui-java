/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.awt.BorderLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JDialog;
import javax.swing.JTextPane;

import de.applejuicenet.client.fassade.shared.ReleaseInfo;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.start.HyperlinkAdapter;

public class ReleaseInfoDialog extends JDialog
{
   private JTextPane         txtContent;
   private final ReleaseInfo releaseInfo;

   public ReleaseInfoDialog(ReleaseInfo releaseInfo)
   {
      super(AppleJuiceDialog.getApp(), releaseInfo.getTitle(), true);
      this.releaseInfo = releaseInfo;
      init();
      pack();
      setSize(500, 450);
      setLocationRelativeTo(AppleJuiceDialog.getApp());
      setVisible(true);
   }

   private void init()
   {
      HyperlinkAdapter hyperlinkAdapter = new HyperlinkAdapter(getTxtContent());

      getTxtContent().addHyperlinkListener(hyperlinkAdapter);

      getContentPane().setLayout(new BorderLayout());

      StringBuilder theContent = new StringBuilder();

      theContent.append("<html>");
      theContent.append("  <div align=\"center\"><b>");
      if (null != releaseInfo.getDescriptionURL())
      {
          theContent.append("    <a href=\"" + releaseInfo.getDescriptionURL().toString() + "\">" + releaseInfo.getTitle() + "</a>");
      }
      else
      {
          theContent.append(releaseInfo.getTitle());
      }
      theContent.append("  </b></div>");
      theContent.append("  <table>");
      theContent.append("    <tr>");
      theContent.append("      <td>Kategorien:</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getCategories())
      {
         int x = 0;

         for(String curCategorie : releaseInfo.getCategories())
         {
            if(x > 0)
            {
               theContent.append(", ");
            }

            theContent.append(curCategorie);
            x++;
         }
      }

      theContent.append("      </td>");
      theContent.append("      <td rowspan=\"8\">");
      if(null != releaseInfo.getImageURL())
      {
         String link = releaseInfo.getImageURL().toString();

         theContent.append("<IMG SRC=\"" + link + "\">");
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>Format:</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getFormat())
      {
         theContent.append(releaseInfo.getFormat());
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>Aufgerufen:</td>");
      theContent.append("      <td>Gesamt:");
      if(null != releaseInfo.getViewsTotal())
      {
         theContent.append(releaseInfo.getViewsTotal());
      }

      if(null != releaseInfo.getViewsCurrentMonth())
      {
         theContent.append(" (aktueller Monat: " + releaseInfo.getViewsCurrentMonth() + ")");
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>Klicks:</td>");
      theContent.append("      <td>Gesamt:");
      if(null != releaseInfo.getClicksTotal())
      {
         theContent.append(releaseInfo.getClicksTotal());
      }

      if(null != releaseInfo.getClicksCurrentMonth())
      {
         theContent.append(" (aktueller Monat: " + releaseInfo.getClicksCurrentMonth() + ")");
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>Veröffentlicht:</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getReleaseDate())
      {
         Calendar calHeute   = Calendar.getInstance();
         Calendar calRelease = Calendar.getInstance();

         calRelease.setTime(releaseInfo.getReleaseDate());
         if(calHeute.get(Calendar.YEAR) == calRelease.get(Calendar.YEAR) &&
               calHeute.get(Calendar.DAY_OF_YEAR) == calRelease.get(Calendar.DAY_OF_YEAR))
         {
            theContent.append("<b>Heute</b> - ");
         }
         else
         {
            calHeute.add(Calendar.DATE, -1);
            if(calHeute.get(Calendar.YEAR) == calRelease.get(Calendar.YEAR) &&
                  calHeute.get(Calendar.DAY_OF_YEAR) == calRelease.get(Calendar.DAY_OF_YEAR))
            {
               theContent.append("<b>Gestern</b> - ");
            }
         }

         SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy HH:mm");

         theContent.append(formater.format(releaseInfo.getReleaseDate()));
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>Sprachen:</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getLanguageImages())
      {
         for(URL curLanguageURL : releaseInfo.getLanguageImages())
         {
            String link = curLanguageURL.toString();

            theContent.append("<IMG SRC=\"" + link + "\">");
         }
      }

      if(null != releaseInfo.getLanguages())
      {
         int x = 0;

         for(String curLanguage : releaseInfo.getLanguages())
         {
            theContent.append(x > 0 ? ", " : "&nbsp;");
            theContent.append(curLanguage);
            x++;
         }
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>Genres:</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getGenres())
      {
         int x = 0;

         for(String curGenre : releaseInfo.getGenres())
         {
            if(x > 0)
            {
               theContent.append(", ");
            }

            theContent.append(curGenre);
            x++;
         }
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      File imageFileGood = new File(System.getProperty("user.dir") + File.separator + "icons" + File.separator + "led_green.gif");
      File imageFileBad = new File(System.getProperty("user.dir") + File.separator + "icons" + File.separator + "led_gray.gif");

      if(null != releaseInfo.getRatingVideoOutOf10())
      {
         theContent.append("    <tr>");
         theContent.append("      <td>Bild-Bewertung:</td>");
         theContent.append("      <td>");
         Long   count     = releaseInfo.getRatingVideoOutOf10();
         String imageGood = null;
         String imagebad  = null;

         try
         {
            imageGood = imageFileGood.toURI().toURL().toString();
            imagebad  = imageFileBad.toURI().toURL().toString();
            for(int i = 0; i < count; i++)
            {
               theContent.append("<IMG SRC=\"" + imageGood + "\">");
            }

            for(int i = count.intValue(); i < 10; i++)
            {
               theContent.append("<IMG SRC=\"" + imagebad + "\">");
            }
         }
         catch(MalformedURLException e)
         {
            e.printStackTrace();
         }

         theContent.append("      </td>");
         theContent.append("    </tr>");
      }

      if(null != releaseInfo.getRatingAudioOutOf10())
      {
         imageFileGood = new File(System.getProperty("user.dir") + File.separator + "icons" + File.separator + "led_red.gif");
         theContent.append("    <tr>");
         theContent.append("      <td>Ton-Bewertung:</td>");
         theContent.append("      <td>");
         Long   count     = releaseInfo.getRatingAudioOutOf10();
         String imageGood = null;
         String imagebad  = null;

         try
         {
            imageGood = imageFileGood.toURI().toURL().toString();
            imagebad  = imageFileBad.toURI().toURL().toString();
            for(int i = 0; i < count; i++)
            {
               theContent.append("<IMG SRC=\"" + imageGood + "\">");
            }

            for(int i = count.intValue(); i < 10; i++)
            {
               theContent.append("<IMG SRC=\"" + imagebad + "\">");
            }
         }
         catch(MalformedURLException e)
         {
            e.printStackTrace();
         }

         theContent.append("      </td>");
         theContent.append("    </tr>");
      }

      theContent.append("    <tr>");
      theContent.append("      <td>FSK 18:</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getFsk18())
      {
         theContent.append(releaseInfo.getFsk18() ? "ja" : "nein");
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("  </table>");
      theContent.append("</html>");
      getTxtContent().setContentType("text/html");
      getTxtContent().setText(theContent.toString());
      getTxtContent().setEditable(false);
      getTxtContent().setBackground(getBackground());
      getContentPane().add(getTxtContent());
   }

   private JTextPane getTxtContent()
   {
      if(null == txtContent)
      {
         txtContent = new JTextPane();
      }

      return txtContent;
   }
}
