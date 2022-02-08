/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.exception.CoreLostException;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.StringConstants;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.StringReader;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/xmlholder/SessionXMLHolder.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r <aj@tkl-soft.de>
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
         Class parser = SAXParser.class;

         xr = XMLReaderFactory.createXMLReader(parser.getName());
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
