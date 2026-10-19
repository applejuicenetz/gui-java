/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.facade.entity.PartList;
import de.applejuicenet.client.facade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.facade.shared.HtmlLoader;
import de.applejuicenet.client.facade.shared.StringConstants;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.CharArrayWriter;
import java.io.StringReader;

/**
 *
 * Copyright: General Public License
 *
 */
public class PartListXMLHolder extends DefaultHandler
{
   private final CoreConnectionSettingsHolder coreHolder;
   private String                             xmlCommand;
   private XMLReader                          xr         = null;
   private CharArrayWriter                    contents   = new CharArrayWriter();
   private PartListDO                         partListDO;
   private String                             zipMode    = "";

   @SuppressWarnings("unchecked")
   public PartListXMLHolder(CoreConnectionSettingsHolder coreHolder)
   {
      this.coreHolder = coreHolder;
      try
      {
         if(!coreHolder.isLocalhost())
         {
            zipMode = StringConstants.MODE_ZIP_AND;
         }

         xr = SaxReaders.create();
         xr.setContentHandler(this);
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void checkInformationAttributes(Attributes attr)
   {
      for(int i = 0; i < attr.getLength(); i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.FILESIZE))
         {
            partListDO.setSize(Long.parseLong(attr.getValue(i)));
         }
      }
   }

   private void checkPartAttributes(Attributes attr)
   {
      long startPosition = -1;
      int  type = -1;

      for(int i = 0; i < attr.getLength(); i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.FROMPOSITION))
         {
            startPosition = Long.parseLong(attr.getValue(i));
         }
         else if(attr.getLocalName(i).equals(StringConstants.TYPE))
         {
            type = Integer.parseInt(attr.getValue(i));
         }
      }

      partListDO.addPart(partListDO.new PartDO(startPosition, type));
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      contents.reset();
      if(localName.equals(StringConstants.FILEINFORMATION))
      {
         checkInformationAttributes(attr);
      }
      else if(localName.equals(StringConstants.PART))
      {
         checkPartAttributes(attr);
      }
   }

   public void characters(char[] ch, int start, int length)
                   throws SAXException
   {
      contents.write(ch, start, length);
   }

   private String getXMLString(String parameters) throws WebSiteNotFoundException
   {
      String        xmlData = null;
      StringBuilder command = new StringBuilder();

      command.append(xmlCommand);
      command.append(zipMode);
      command.append(StringConstants.PASSWORD_EQUALS);
      command.append(coreHolder.getCorePassword());
      command.append(parameters);

      xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET, command.toString());
      if(xmlData.length() == 0)
      {
         throw new IllegalArgumentException();
      }

      return xmlData;
   }

   public PartList getPartList(Object object) throws WebSiteNotFoundException
   {
      String xmlString = null;

      try
      {
         if(object.getClass() == DownloadSourceDO.class)
         {
            xmlCommand = StringConstants.XML_USERPARTLIST_XML_DOWNLOADSOURCE;
            xmlString  = getXMLString(StringConstants.AND_ID + ((DownloadSourceDO) object).getId());
            partListDO = new PartListDO((DownloadSource) object);
         }
         else
         {
            xmlCommand = StringConstants.XML_USERPARTLIST_XML_DOWNLOAD;
            xmlString  = getXMLString(StringConstants.AND_ID + ((DownloadDO) object).getId());
            partListDO = new PartListDO((Download) object);
         }

         // workaround for current core bug #584
         if(xmlString.indexOf("java.lang.ClassCastException") != -1 || xmlString.indexOf("java.lang.NullPointerException") != -1)
         {
            throw new WebSiteNotFoundException(WebSiteNotFoundException.INPUT_ERROR);
         }

         xr.parse(new InputSource(new StringReader(xmlString)));
         PartList resultPartList = partListDO;

         partListDO = null;
         return resultPartList;
      }
      catch(WebSiteNotFoundException wnfE)
      {
         throw wnfE;
      }
      catch(Exception e)
      {
         return null;
      }
   }
}
