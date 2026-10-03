package de.applejuicenet.client.fassade.controller.xml;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

final class SaxReaders {
    private SaxReaders() {
    }

    static XMLReader create() throws SAXException {
        try {
            return SAXParserFactory.newDefaultNSInstance().newSAXParser().getXMLReader();
        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
    }
}
