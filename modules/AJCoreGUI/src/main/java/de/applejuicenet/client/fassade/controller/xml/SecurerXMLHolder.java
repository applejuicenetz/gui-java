/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import java.io.StringReader;

import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.shared.HtmlLoader;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/SecurerXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */
public class SecurerXMLHolder extends DefaultHandler
{
   private final CoreConnectionSettingsHolder coreHolder;
   private String                             xmlCommand;
   private XMLReader                          xr          = null;
   private InformationDO                      information = null;

   @SuppressWarnings("unchecked")
   public SecurerXMLHolder(CoreConnectionSettingsHolder coreHolder)
   {
      this.coreHolder = coreHolder;
      try
      {
         xmlCommand = "/xml/modified.xml?filter=informations&password=";
         Class parser = SAXParser.class;

         xr = XMLReaderFactory.createXMLReader(parser.getName());
         xr.setContentHandler(this);
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      if(localName.equals("information"))
      {
         checkInformationAttributes(attr);
      }
   }

   private String getXMLString() throws Exception
   {
      String xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                                                    xmlCommand + coreHolder.getCorePassword());

      if(xmlData.length() == 0)
      {
         throw new IllegalArgumentException();
      }

      return xmlData;
   }

   public synchronized boolean secure(String sessionKontext, InformationDO information)
   {
      try
      {
         if(information == null)
         {
            return false;
         }

         this.information = information;
         String xmlString = getXMLString();

         xr.parse(new InputSource(new StringReader(xmlString)));
         return true;
      }
      catch(WebSiteNotFoundException wsnfE)
      {

         // Verbindung zum Core ueberlastet.
         return false;
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
         if(attr.getLocalName(i).equals("credits"))
         {
            information.setCredits(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals("sessionupload"))
         {
            information.setSessionUpload(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals("sessiondownload"))
         {
            information.setSessionDownload(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals("uploadspeed"))
         {
            information.setUploadSpeed(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals("downloadspeed"))
         {
            information.setDownloadSpeed(Long.parseLong(attr.getValue(i)));
         }
         else if(attr.getLocalName(i).equals("openconnections"))
         {
            information.setOpenConnections(Long.parseLong(attr.getValue(i)));
         }
      }
   }
}
