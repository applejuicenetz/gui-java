/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.shared;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 *
 * Copyright: General Public License
 *
 */
public abstract class XMLDecoder
{
   protected Document document;
   private String     filePath;
   protected boolean  webXML = false;

   protected XMLDecoder()
   {
   }

   protected XMLDecoder(String filePath)
   {
      File xmlFile = new File(filePath);

      reload(xmlFile);
   }

   protected XMLDecoder(File xmlFile)
   {
      reload(xmlFile);
   }

   protected void reload(String filePath)
   {
      File xmlFile = new File(filePath);

      this.reload(xmlFile);
   }

   protected void reload(File xmlFile)
   {
      try(InputStreamReader reader = new InputStreamReader(new FileInputStream(xmlFile), StandardCharsets.UTF_8))
      {
         DocumentBuilder builder = SecureXml.newDocumentBuilder();
         Document        parsed  = builder.parse(new InputSource(reader));

         document = parsed;
         this.filePath = xmlFile.getPath();
      }
      catch(SAXException | IOException | ParserConfigurationException e)
      {
         throw new IllegalStateException("XML file could not be read: " + xmlFile, e);
      }
   }

   public Document getDocument()
   {
      return document;
   }

   public String getFirstAttributeByTagName(String[] attributePath, boolean lastIsElement)
   {
      if(!webXML)
      {
         return getFirstAttributeByTagName(attributePath);
      }
      else
      {
         NodeList nodes             = document.getChildNodes();
         int      attributePathSize = attributePath.length;
         String   result            = null;
         Element  e                 = null;
         int      nodesSize;

         for(int i = 0; i < attributePathSize; i++)
         {
            nodesSize = nodes.getLength();
            for(int x = 0; x < nodesSize; x++)
            {
               if(nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i]))
               {
                  if(i == attributePathSize - 1 && lastIsElement)
                  {
                     e      = (Element) nodes.item(x);
                     nodes  = e.getChildNodes();
                     result = nodes.item(0).getNodeValue();
                     return result;
                  }
                  else if(i == attributePathSize - 2 && !lastIsElement)
                  {
                     e      = (Element) nodes.item(x);
                     result = e.getAttribute(attributePath[attributePathSize - 1]);
                     return result;
                  }
                  else
                  {
                     nodes = nodes.item(x).getChildNodes();
                     break;
                  }
               }
            }
         }

         return "";
      }
   }

   public String getFirstAttributeByTagName(String[] attributePath)
   {
      NodeList nodes             = document.getChildNodes();
      Node     rootNode = nodes.item(0); // Element "root"

      nodes = rootNode.getChildNodes();
      Element e                 = null;
      int     attributePathSize = attributePath.length;
      int     nodesSize;

      for(int i = 0; i < attributePathSize - 1; i++)
      {
         nodesSize = nodes.getLength();
         for(int x = 0; x < nodesSize; x++)
         {
            if(nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i]))
            {
               if(i == attributePathSize - 2)
               {
                  e = (Element) nodes.item(x);
                  return e.getAttribute(attributePath[attributePathSize - 1]);
               }
               else
               {
                  nodes = nodes.item(x).getChildNodes();
                  break;
               }
            }
         }
      }

      return ""; // not found
   }

   protected void setAttributeByTagName(String[] attributePath, int newValue)
   {
      setAttributeByTagName(attributePath, Integer.toString(newValue));
   }

   protected void setAttributeByTagName(String[] attributePath, String newValue)
   {
      NodeList nodes             = document.getChildNodes();
      Node     rootNode = nodes.item(0); // Element "root"

      nodes = rootNode.getChildNodes();
      Element       e                 = null;
      int           attributePathSize = attributePath.length;
      int           nodesSize;

      for(int i = 0; i < attributePathSize - 1; i++)
      {
         nodesSize = nodes.getLength();
         for(int x = 0; x < nodesSize; x++)
         {
            if(nodes.item(x).getNodeName().equalsIgnoreCase(attributePath[i]))
            {
               if(i == attributePathSize - 2)
               {
                  e = (Element) nodes.item(x);
                  e.setAttribute(attributePath[attributePathSize - 1], newValue);
                  try
                  {
                     Transformer transformer = SecureXml.newTransformerFactory().newTransformer();

                     transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                     transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                     try(Writer writer = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))
                     {
                        transformer.transform(new DOMSource(document), new StreamResult(writer));
                     }
                     return;
                  }
                  catch(IOException | TransformerException ioE)
                  {
                     return;
                  }
               }
               else
               {
                  nodes = nodes.item(x).getChildNodes();
                  break;
               }
            }
         }
      }

      return; // not found
   }
}
