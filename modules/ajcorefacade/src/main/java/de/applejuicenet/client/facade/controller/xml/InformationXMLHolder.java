/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
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
public class InformationXMLHolder extends DefaultHandler
{
   private static InformationXMLHolder  instance    = null;
   private VersionDO                    version     = null;
   private XMLReader                    xr          = null;
   private String                       xmlCommand;
   private CharArrayWriter              contents    = new CharArrayWriter();
   private String                       coreVersion;
   private CoreConnectionSettingsHolder coreHolder;

   @SuppressWarnings("unchecked")
   public InformationXMLHolder(CoreConnectionSettingsHolder coreHolder)
   {
      this.coreHolder = coreHolder;
      try
      {
         xmlCommand = StringConstants.XML_INFORMATION_XML_PASSWORD;
         xr = SaxReaders.create();
         xr.setContentHandler(this);
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public VersionDO getCoreVersion()
   {
      if(version == null)
      {
         try
         {
            String xmlString = getXMLString();

            xr.parse(new InputSource(new StringReader(xmlString)));
            version = new VersionDO(coreVersion, VersionDO.getOSTypeByOSName((String) System.getProperties().get("os.name")));
         }
         catch(Exception ex)
         {
            throw new RuntimeException(ex);
         }
      }

      return version;
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      contents.reset();
      if(localName.equals(StringConstants.FILESYSTEM))
      {
         int length = attr.getLength();

         for(int i = 0; i < length; i++)
         {
            if(attr.getLocalName(i).equals(StringConstants.SEPARATOR))
            {
               ApplejuiceFacade.separator = attr.getValue(i);
               break;
            }
         }
      }
   }

   public void characters(char[] ch, int start, int length)
                   throws SAXException
   {
      contents.write(ch, start, length);
   }

   public void endElement(String namespaceURI, String localName, String qName)
                   throws SAXException
   {
      if(localName.equals(StringConstants.VERSION))
      {
         coreVersion = contents.toString();
      }
   }

   private String getXMLString() throws Exception
   {
      String xmlData = null;

      xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(), coreHolder.getCorePort(), HtmlLoader.GET,
                                             xmlCommand + coreHolder.getCorePassword());
      if(xmlData.length() == 0)
      {
         throw new IllegalArgumentException();
      }

      return xmlData;
   }
}
