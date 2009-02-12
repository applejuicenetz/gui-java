/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import java.io.StringReader;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import de.applejuicenet.client.fassade.shared.ProxySettings;
import de.applejuicenet.client.fassade.shared.ReleaseInfo;
import de.applejuicenet.client.fassade.shared.WebsiteContentLoader;

public class ReleaseInfoXMLHolder
{
   private static final Map<String, ReleaseCacheObject> releasesCache = new HashMap<String, ReleaseCacheObject>();
   private static final long                            CACHE_TIMEOUT = 300000;
   private final ReleaseInfo                            releaseInfo   = new ReleaseInfo();
   private final ProxySettings                          proxy;
   private final String                                 hash;

   public ReleaseInfoXMLHolder(String hash, ProxySettings proxy)
   {
      this.hash = hash;
      releaseInfo.setMd5(hash);
      this.proxy = proxy;
   }

   public ReleaseInfo getReleaseInfo() throws Exception
   {
      ReleaseCacheObject releaseCacheObject = releasesCache.get(hash);

      if(null != releaseCacheObject)
      {
         if(releaseCacheObject.getTimestamp() + CACHE_TIMEOUT > System.currentTimeMillis())
         {
            return releaseCacheObject.getReleaseInfo();
         }
         else
         {
            releasesCache.remove(hash);
         }
      }

      String releaseDataTmp = WebsiteContentLoader.getWebsiteContent(proxy, "http://appledocs.to", 80, "/JavaGUI/" + hash);

      if("no result".equals(releaseDataTmp))
      {
         releaseCacheObject = new ReleaseCacheObject(System.currentTimeMillis(), null);
         releasesCache.put(hash, releaseCacheObject);
         return null;
      }

      DOMParser parser = new DOMParser();

      parser.parse(new InputSource(new StringReader(releaseDataTmp)));
      Document doc = parser.getDocument();

      NodeList nodes = doc.getElementsByTagName("Title");
      Node     node  = nodes.item(0);

      releaseInfo.setTitle(node.getTextContent());

      nodes = doc.getElementsByTagName("Link");
      String tmp;
      URL    url;

      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            url = new URL(tmp);
            releaseInfo.setDescriptionURL(url);
         }
         catch(MalformedURLException mUE)
         {
         }
      }

      nodes = doc.getElementsByTagName("Image");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            url = new URL(tmp);
            releaseInfo.setImageURL(url);
         }
         catch(MalformedURLException mUE)
         {
         }
      }

      nodes = doc.getElementsByTagName("Format");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         releaseInfo.setFormat(tmp);
      }

      nodes = doc.getElementsByTagName("Views");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            Long longValue = Long.parseLong(tmp);

            releaseInfo.setViewsTotal(longValue);
         }
         catch(NumberFormatException nfE)
         {
         }
      }

      nodes = doc.getElementsByTagName("ViewsMonth");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            Long longValue = Long.parseLong(tmp);

            releaseInfo.setViewsCurrentMonth(longValue);
         }
         catch(NumberFormatException nfE)
         {
         }
      }

      nodes = doc.getElementsByTagName("Clicks");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            Long longValue = Long.parseLong(tmp);

            releaseInfo.setClicksTotal(longValue);
         }
         catch(NumberFormatException nfE)
         {
         }
      }

      nodes = doc.getElementsByTagName("ClicksMonth");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            Long longValue = Long.parseLong(tmp);

            releaseInfo.setClicksCurrentMonth(longValue);
         }
         catch(NumberFormatException nfE)
         {
         }
      }

      nodes = doc.getElementsByTagName("Language");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         releaseInfo.getLanguages().add(tmp);
      }

      nodes = doc.getElementsByTagName("LanguagePicture");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            url = new URL(tmp);
            releaseInfo.getLanguageImages().add(url);
         }
         catch(MalformedURLException mUE)
         {
         }
      }

      SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy HH:mm");

      nodes = doc.getElementsByTagName("AddDate");

      String time = null;

      if(null != nodes && (node = nodes.item(0)) != null)
      {
         time = node.getTextContent();
      }

      nodes = doc.getElementsByTagName("AddTime");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         time += " " + tmp;
      }

      if(null != time)
      {
         try
         {
            releaseInfo.setReleaseDate(formater.parse(time));
         }
         catch(ParseException pE)
         {
            pE.printStackTrace();
         }
      }

      nodes = doc.getElementsByTagName("fsk18");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         releaseInfo.setFsk18("true".equalsIgnoreCase(tmp));
      }

      nodes = doc.getElementsByTagName("Video");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            releaseInfo.setRatingVideoOutOf10(Long.parseLong(tmp));
         }
         catch(NumberFormatException nfE)
         {
         }
      }

      nodes = doc.getElementsByTagName("Sound");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         tmp = node.getTextContent();
         try
         {
            releaseInfo.setRatingAudioOutOf10(Long.parseLong(tmp));
         }
         catch(NumberFormatException nfE)
         {
         }
      }

      nodes = doc.getElementsByTagName("Category");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         NodeList nodes2 = doc.getElementsByTagNameNS(node.getNamespaceURI(), "Name");

         if(null != nodes2 && (node = nodes2.item(0)) != null)
         {
            tmp = node.getTextContent();
            releaseInfo.getCategories().add(tmp);
         }
      }

      nodes = doc.getElementsByTagName("Genres");
      if(null != nodes && (node = nodes.item(0)) != null)
      {
         nodes = node.getChildNodes();
         if(null != nodes)
         {
            int count = nodes.getLength();

            for(int x = 0; x < count; x++)
            {
               node = nodes.item(x);
               tmp  = node.getTextContent();
               releaseInfo.getGenres().add(tmp);
            }
         }
      }

      releaseCacheObject = new ReleaseCacheObject(System.currentTimeMillis(), releaseInfo);
      releasesCache.put(hash, releaseCacheObject);

      return releaseInfo;

   }

   private class ReleaseCacheObject
   {
      private long        timestamp;
      private ReleaseInfo releaseInfo;

      public ReleaseCacheObject(long timestamp, ReleaseInfo releaseInfo)
      {
         super();
         this.timestamp   = timestamp;
         this.releaseInfo = releaseInfo;
      }

      public ReleaseInfo getReleaseInfo()
      {
         return releaseInfo;
      }

      public long getTimestamp()
      {
         return timestamp;
      }
   }
}
