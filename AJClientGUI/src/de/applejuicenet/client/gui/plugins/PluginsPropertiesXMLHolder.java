package de.applejuicenet.client.gui.plugins;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.CharArrayWriter;
import org.xml.sax.InputSource;
import java.io.StringReader;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.xml.sax.XMLReader;
import java.util.HashMap;
import org.xml.sax.helpers.XMLReaderFactory;
import org.apache.xerces.parsers.SAXParser;

public class PluginsPropertiesXMLHolder extends DefaultHandler{
    private Logger logger;
    private XMLReader xr = null;
    private HashMap xmlContents = new HashMap();
    private StringBuffer key = new StringBuffer();

    public PluginsPropertiesXMLHolder(String xmlString) {
        logger = Logger.getLogger(getClass());
        try {
            Class parser = SAXParser.class;
            xr = XMLReaderFactory.createXMLReader(parser.getName());
            xr.setContentHandler(this);
            xr.parse(new InputSource(
                new StringReader(xmlString)));
            int i=0;
        }
        catch (Exception ex) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", ex);
            }
        }
    }

    public void startElement(String namespaceURI,
                             String localName,
                             String qName,
                             Attributes attr) throws SAXException {
        key.append(".");
        key.append(localName);
        for (int i = 0; i < attr.getLength(); i++) {
            xmlContents.put(key.toString() + "." + attr.getLocalName(i), attr.getValue(i));
        }
    }

    public void characters(char[] ch, int start, int length) throws
        SAXException {
        xmlContents.put(key.toString(), ch);
    }

    public void endElement(String namespaceURI,
                           String localName,
                           String qName) throws SAXException {
        key.delete(key.length() - localName.length() - 1, key.length());
    }

    public String getXMLAttributeByTagName(String identifier){
        if (xmlContents.containsKey(identifier)){
            return (String)xmlContents.get(identifier);
        }
        else{
            return "";
        }
    }
}