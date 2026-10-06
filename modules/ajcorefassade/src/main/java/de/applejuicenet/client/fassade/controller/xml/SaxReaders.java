package de.applejuicenet.client.fassade.controller.xml;

import de.applejuicenet.client.fassade.shared.SecureXml;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

final class SaxReaders {
    private SaxReaders() {
    }

    static XMLReader create() throws SAXException {
        return SecureXml.newSaxReader();
    }
}
