package de.applejuicenet.client.fassade.controller.xml;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class SaxReadersTest {
    @Test
    public void preservesUnqualifiedElementAndAttributeNames() {
        XMLValueHolder holder = new XMLValueHolder();
        holder.parse("<applejuice><share id='1' filename='Größe &amp; Spaß.bin'/></applejuice>");
        assertEquals("1", holder.getXMLAttributeByTagName(".applejuice.share.id"));
        assertEquals("Größe & Spaß.bin", holder.getXMLAttributeByTagName(".applejuice.share.filename"));
    }

    @Test
    public void preservesNamespacedElementAndAttributeLocalNames() {
        XMLValueHolder holder = new XMLValueHolder();
        holder.parse("<aj:applejuice xmlns:aj='urn:applejuice'><aj:share aj:id='1'/></aj:applejuice>");
        assertEquals("1", holder.getXMLAttributeByTagName(".applejuice.share.id"));
    }

    @Test
    public void rejectsMalformedXml() throws Exception {
        XMLReader reader = SaxReaders.create();
        assertThrows(SAXException.class,
                () -> reader.parse(new InputSource(new StringReader("<applejuice><share></applejuice>"))));
    }
}
