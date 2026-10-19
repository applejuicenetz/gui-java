/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.exception.CoreLostException;
import de.applejuicenet.client.facade.shared.HtmlLoader;
import de.applejuicenet.client.facade.shared.StringConstants;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;

/**
 *
 * Copyright: General Public License
 *
 */
public class SessionXMLHolder extends DefaultHandler
{
   private final CoreConnectionSettingsHolder coreHolder;
   private String                             xmlCommand;
   private XMLReader                          xr        = null;
   private String                             sessionId = "";

   @SuppressWarnings("unchecked")
   public SessionXMLHolder(CoreConnectionSettingsHolder coreHolder)
   {
      this.coreHolder = coreHolder;
      try
      {
         xmlCommand = "/xml/getsession.xml?password=";
         xr = SaxReaders.create();
         xr.setContentHandler(this);
      }
      catch(Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   private void checkSessionAttributes(Attributes attr)
   {
      int length = attr.getLength();

      for(int i = 0; i < length; i++)
      {
         if(attr.getLocalName(i).equals(StringConstants.ID))
         {
            sessionId = attr.getValue(i);
         }
      }
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      if(localName.equals(StringConstants.SESSION))
      {
         checkSessionAttributes(attr);
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

   public String getNewSessionId()
   {
      try
      {
         String xmlString = getXMLString();

         xr.parse(new InputSource(new StringReader(xmlString)));
         return sessionId;
      }
      catch(Exception ex)
      {
         throw new CoreLostException(ex);
      }
   }
}
