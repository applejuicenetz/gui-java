/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.text.SimpleDateFormat;

import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.shared.ReleaseInfo;
import de.applejuicenet.client.gui.AppleJuiceDialog;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.ProxyManagerImpl;
import de.applejuicenet.client.gui.start.HyperlinkAdapter;

public class ReleaseInfoDialog extends JDialog
{
   private static Logger     logger         = Logger.getLogger(ReleaseInfoDialog.class);
   private JTextPane         txtContent;
   private final ReleaseInfo releaseInfo;
   private final boolean     downloadOption;
   private final String      filename;
   private final Long        size;

   private ReleaseInfoDialog(ReleaseInfo releaseInfo, boolean downloadOption, String filename, Long size)
   {
      super(AppleJuiceDialog.getApp(), releaseInfo.getTitle(), true);
      this.releaseInfo    = releaseInfo;
      this.downloadOption = downloadOption;
      this.filename       = filename;
      this.size           = size;
      init();
      pack();
      setSize(500, 450);
      setLocationRelativeTo(AppleJuiceDialog.getApp());
      setVisible(true);
   }

   private void init()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();
      String           txtCategories  = languageSelector.getFirstAttrbuteByTagName("releaseinfo.categories");
      String           txtFormat      = languageSelector.getFirstAttrbuteByTagName("releaseinfo.format");
      String           txtViewed      = languageSelector.getFirstAttrbuteByTagName("releaseinfo.viewed");
      String           txtCount       = languageSelector.getFirstAttrbuteByTagName("releaseinfo.count");
      String           txtClicks      = languageSelector.getFirstAttrbuteByTagName("releaseinfo.clicks");
      String           txtPublished   = languageSelector.getFirstAttrbuteByTagName("releaseinfo.published");
      String           txtToday       = languageSelector.getFirstAttrbuteByTagName("releaseinfo.today");
      String           txtYesterday   = languageSelector.getFirstAttrbuteByTagName("releaseinfo.yesterday");
      String           txtLanguages   = languageSelector.getFirstAttrbuteByTagName("releaseinfo.languages");
      String           txtGenres      = languageSelector.getFirstAttrbuteByTagName("releaseinfo.genres");
      String           txtRatingVideo = languageSelector.getFirstAttrbuteByTagName("releaseinfo.rating.video");
      String           txtRatingAudio = languageSelector.getFirstAttrbuteByTagName("releaseinfo.rating.audio");
      String           txtFsk18       = languageSelector.getFirstAttrbuteByTagName("releaseinfo.fsk18");
      String           txtJa          = languageSelector.getFirstAttrbuteByTagName("releaseinfo.ja");
      String           txtNein        = languageSelector.getFirstAttrbuteByTagName("releaseinfo.nein");

      HyperlinkAdapter hyperlinkAdapter = new HyperlinkAdapter(getTxtContent());

      getTxtContent().addHyperlinkListener(hyperlinkAdapter);

      getContentPane().setLayout(new BorderLayout());

      StringBuilder theContent = new StringBuilder();

      theContent.append("<html>");
      theContent.append("  <div align=\"center\"><b>");
      if(null != releaseInfo.getDescriptionURL())
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
      theContent.append("      <td>" + txtCategories + "</td>");
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
      theContent.append("      <td>" + txtFormat + "</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getFormat())
      {
         theContent.append(releaseInfo.getFormat());
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>" + txtViewed + "</td>");
      String tmp = txtCount;

      if(null != releaseInfo.getViewsTotal())
      {
         tmp = tmp.replaceAll("%all", "" + releaseInfo.getViewsTotal());
      }
      else
      {
         tmp = tmp.replaceAll("%all", "-");
      }

      if(null != releaseInfo.getViewsCurrentMonth())
      {
         tmp = tmp.replaceAll("%month", "" + releaseInfo.getViewsCurrentMonth());
      }
      else
      {
         tmp = tmp.replaceAll("%month", "-");
      }

      theContent.append("      <td>" + tmp);
      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>" + txtClicks + "</td>");

      tmp = txtCount;
      if(null != releaseInfo.getClicksTotal())
      {
         tmp = tmp.replaceAll("%all", "" + releaseInfo.getClicksTotal());
      }
      else
      {
         tmp = tmp.replaceAll("%all", "-");
      }

      if(null != releaseInfo.getClicksCurrentMonth())
      {
         tmp = tmp.replaceAll("%month", "" + releaseInfo.getClicksCurrentMonth());
      }
      else
      {
         tmp = tmp.replaceAll("%month", "-");
      }

      theContent.append("      <td>" + tmp);
      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>" + txtPublished + "</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getReleaseDate())
      {
         Calendar calHeute   = Calendar.getInstance();
         Calendar calRelease = Calendar.getInstance();

         calRelease.setTime(releaseInfo.getReleaseDate());
         if(calHeute.get(Calendar.YEAR) == calRelease.get(Calendar.YEAR) &&
               calHeute.get(Calendar.DAY_OF_YEAR) == calRelease.get(Calendar.DAY_OF_YEAR))
         {
            theContent.append("<b>" + txtToday + "</b> - ");
         }
         else
         {
            calHeute.add(Calendar.DATE, -1);
            if(calHeute.get(Calendar.YEAR) == calRelease.get(Calendar.YEAR) &&
                  calHeute.get(Calendar.DAY_OF_YEAR) == calRelease.get(Calendar.DAY_OF_YEAR))
            {
               theContent.append("<b>" + txtYesterday + "</b> - ");
            }
         }

         SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy HH:mm");

         theContent.append(formater.format(releaseInfo.getReleaseDate()));
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("    <tr>");
      theContent.append("      <td>" + txtLanguages + "</td>");
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
      theContent.append("      <td>" + txtGenres + "</td>");
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
         theContent.append("      <td>" + txtRatingVideo + "</td>");
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
         theContent.append("      <td>" + txtRatingAudio + "</td>");
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
      theContent.append("      <td>" + txtFsk18 + "</td>");
      theContent.append("      <td>");
      if(null != releaseInfo.getFsk18())
      {
         theContent.append(releaseInfo.getFsk18() ? txtJa : txtNein);
      }
      else
      {
         theContent.append("-");
      }

      theContent.append("      </td>");
      theContent.append("    </tr>");
      theContent.append("  </table>");
      theContent.append("</html>");
      getTxtContent().setContentType("text/html");
      getTxtContent().setText(theContent.toString());
      getTxtContent().setEditable(false);
      getTxtContent().setBackground(getBackground());
      getContentPane().add(getTxtContent(), BorderLayout.CENTER);

      JPanel      southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      IconManager im = IconManager.getInstance();

      JButton     btnClose = new JButton();

      btnClose.setIcon(im.getIcon("abbrechen"));
      btnClose.setText(languageSelector.getFirstAttrbuteByTagName("assistant.closebutton.caption"));
      btnClose.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               setVisible(false);
            }
         });
      southPanel.add(btnClose);

      JButton btnDownloadImage = new JButton();

      btnDownloadImage.setIcon(im.getIcon("download"));
      btnDownloadImage.setText(languageSelector.getFirstAttrbuteByTagName("releaseinfo.downloadimage"));
      btnDownloadImage.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               downloadImage();
            }
         });
      southPanel.add(btnDownloadImage);

      if(downloadOption)
      {
         JButton btnDownload = new JButton();

         btnDownload.setIcon(im.getIcon("download"));
         btnDownload.setText(languageSelector.getFirstAttrbuteByTagName("mainform.Getlink3.caption"));
         btnDownload.addActionListener(new ActionListener()
            {
               public void actionPerformed(ActionEvent ae)
               {
                  StringBuilder toCopy = new StringBuilder();

                  toCopy.append("ajfsp://file|");
                  toCopy.append(filename + "|" + releaseInfo.getMd5() + "|" + size + "/");
                  final String link = toCopy.toString();

                  new Thread()
                     {
                        public void run()
                        {
                           try
                           {
                              final String result = AppleJuiceClient.getAjFassade().processLink(link, "");

                              SoundPlayer.getInstance().playSound(SoundPlayer.LADEN);
                              if(result.indexOf("ok") != 0)
                              {
                                 SwingUtilities.invokeLater(new Runnable()
                                    {
                                       public void run()
                                       {
                                          String           message          = null;
                                          LanguageSelector languageSelector = LanguageSelector.getInstance();

                                          if(result.indexOf("already downloaded") != -1)
                                          {
                                             message = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.bereitsgeladen");
                                             message = message.replaceAll("%s", link);
                                          }
                                          else if(result.indexOf("incorrect link") != -1)
                                          {
                                             message = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.falscherlink");
                                             message = message.replaceAll("%s", link);
                                          }
                                          else if(result.indexOf("failure") != -1)
                                          {
                                             message = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.sonstigerlinkfehlerlang");
                                          }

                                          if(message != null)
                                          {
                                             JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message,
                                                                           languageSelector.getFirstAttrbuteByTagName("mainform.caption"),
                                                                           JOptionPane.OK_OPTION | JOptionPane.INFORMATION_MESSAGE);
                                          }
                                       }
                                    });
                              }
                           }
                           catch(IllegalArgumentException e)
                           {
                              logger.error(e);
                           }
                        }
                     }.start();
                  setVisible(false);
               }
            });
         southPanel.add(btnDownload);
      }

      getContentPane().add(southPanel, BorderLayout.SOUTH);
   }

   private JTextPane getTxtContent()
   {
      if(null == txtContent)
      {
         txtContent = new JTextPane();
      }

      return txtContent;
   }

   public void downloadImage()
   {
      if(null == releaseInfo.getImageURL())
      {
         return;
      }

      try
      {
         final JFileChooser fileChooser = new JFileChooser();

         fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         String defaultFileName = releaseInfo.getImageURL().getFile();
         int    index = defaultFileName.lastIndexOf("/");

         if(index != -1)
         {
            defaultFileName = defaultFileName.substring(index + 1);
         }

         fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory().getAbsolutePath() + "\\" + defaultFileName));

         int result = fileChooser.showSaveDialog(this);

         if(result != JFileChooser.APPROVE_OPTION)
         {
            return;
         }

         File          destFile = fileChooser.getSelectedFile();

         byte[]        buf = new byte[1024];

         URLConnection urlconnection = releaseInfo.getImageURL().openConnection();

         urlconnection.setUseCaches(true);
         InputStream      urlStream = urlconnection.getInputStream();

         FileOutputStream fos = new FileOutputStream(destFile);

         int              bytesread = 0;

         while((bytesread = urlStream.read(buf)) > 0)
         {
            fos.write(buf, 0, bytesread);
         }

         fos.close();
         urlStream.close();
         LanguageSelector languageSelector = LanguageSelector.getInstance();
         String           message = languageSelector.getFirstAttrbuteByTagName("releaseinfo.downloadimage.ok");

         JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message);
      }
      catch(IOException ioe)
      {
         logger.info(ioe.getMessage(), ioe);
      }
   }

   public static void showReleaseInfo(String hash)
   {
      showReleaseInfo(hash, false, null, null);
   }

   public static void showReleaseInfo(String hash, boolean downloadOption, String filename, Long size)
   {
      ReleaseInfo releaseInfo = null;

      try
      {
         releaseInfo = AppleJuiceClient.getAjFassade().getReleaseInfo(hash, ProxyManagerImpl.getInstance().getProxySettings());
      }
      catch(Exception e)
      {
         logger.info(e.getMessage(), e);
      }

      if(null == releaseInfo)
      {
         LanguageSelector languageSelector = LanguageSelector.getInstance();
         String           message = languageSelector.getFirstAttrbuteByTagName("releaseinfo.na");

         JOptionPane.showMessageDialog(AppleJuiceDialog.getApp(), message);
      }
      else
      {
         new ReleaseInfoDialog(releaseInfo, downloadOption, filename, size);
      }
   }
}
