/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.controller.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLValueHolder extends DefaultHandler
{
   private XMLReader    xr     = null;
   private StringBuffer key    = new StringBuffer();
   protected Properties values = new Properties();

   public XMLValueHolder()
   {
   }

   public void parseProperties(File propertiesFile) throws IllegalArgumentException
   {
      try
      {
         FileInputStream fiS = new FileInputStream(propertiesFile);

         values.clear();
         values.load(fiS);
         fiS.close();
      }
      catch(Exception e)
      {
         throw new IllegalArgumentException(e);
      }
   }

   @SuppressWarnings("unchecked")
   public void parse(String xmlString) throws IllegalArgumentException
   {
      Class parser = SAXParser.class;

      try
      {
         xr = XMLReaderFactory.createXMLReader(parser.getName());
      }
      catch(SAXException e)
      {

         // Parter not found
         throw new RuntimeException(e);
      }

      xr.setContentHandler(this);

      try
      {
         values.clear();
         xr.parse(new InputSource(new StringReader(xmlString)));
      }
      catch(IOException e)
      {
         throw new IllegalArgumentException(e);
      }
      catch(SAXException e)
      {
         throw new IllegalArgumentException(e);
      }
   }

   @SuppressWarnings("unchecked")
   public void parse(File xmlFile) throws IllegalArgumentException
   {
      Class parser = SAXParser.class;

      try
      {
         xr = XMLReaderFactory.createXMLReader(parser.getName());
      }
      catch(SAXException e)
      {

         // Parter not found
         throw new RuntimeException(e);
      }

      xr.setContentHandler(this);
      try
      {
         values.clear();
         FileInputStream fiS = new FileInputStream(xmlFile);

         xr.parse(new InputSource(fiS));
         fiS.close();
      }
      catch(IOException e)
      {
         throw new IllegalArgumentException(e);
      }
      catch(SAXException e)
      {
         throw new IllegalArgumentException(e);
      }
   }

   public void startElement(String namespaceURI, String localName, String qName, Attributes attr)
                     throws SAXException
   {
      key.append(".");
      key.append(localName);
      for(int i = 0; i < attr.getLength(); i++)
      {
         values.put(key.toString() + "." + attr.getLocalName(i), attr.getValue(i));
      }
   }

   public void characters(char[] ch, int start, int length)
                   throws SAXException
   {
      values.put(key.toString(), new String(ch, start, length));
   }

   public void endElement(String namespaceURI, String localName, String qName)
                   throws SAXException
   {
      key.delete(key.length() - localName.length() - 1, key.length());
   }

   public String getXMLAttributeByTagName(String identifier)
   {
      if(values.containsKey(identifier))
      {
         return (String) values.get(identifier);
      }
      else
      {
         return "";
      }
   }
}
