package de.applejuicenet.client.fassade.shared;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerFactory;

/**
 * Parser- und Transformer-Fabriken ohne DTD und externe Entitaeten. Die Core-Antworten und die
 * Serverliste enthalten kein DOCTYPE; ein solches Dokument wird abgelehnt.
 */
public final class SecureXml
{
   private static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";

   private SecureXml()
   {
   }

   public static XMLReader newSaxReader() throws SAXException
   {
      try
      {
         SAXParserFactory factory = SAXParserFactory.newDefaultNSInstance();

         factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
         factory.setFeature(DISALLOW_DOCTYPE, true);
         factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
         factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
         factory.setXIncludeAware(false);
         return factory.newSAXParser().getXMLReader();
      }
      catch(ParserConfigurationException e)
      {
         throw new SAXException(e);
      }
   }

   public static DocumentBuilder newDocumentBuilder() throws ParserConfigurationException
   {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
      factory.setFeature(DISALLOW_DOCTYPE, true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      factory.setXIncludeAware(false);
      factory.setExpandEntityReferences(false);
      return factory.newDocumentBuilder();
   }

   public static TransformerFactory newTransformerFactory()
   {
      TransformerFactory factory = TransformerFactory.newInstance();

      try
      {
         factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
         factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
         factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
      }
      catch(Exception e)
      {
         throw new IllegalStateException(e);
      }

      return factory;
   }
}
