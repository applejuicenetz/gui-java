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

    @Test
    public void rejectsDoctypeAndNeverLoadsExternalEntities() throws Exception {
        java.nio.file.Path secret = java.nio.file.Files.createTempFile("secret", ".txt");
        java.nio.file.Files.writeString(secret, "geheim");
        try {
            String xml = "<?xml version='1.0'?><!DOCTYPE a [<!ENTITY x SYSTEM '" + secret.toUri() + "'>]><a v='&x;'/>";
            XMLValueHolder holder = new XMLValueHolder();
            assertThrows(RuntimeException.class, () -> holder.parse(xml));
            XMLReader reader = SaxReaders.create();
            assertThrows(SAXException.class, () -> reader.parse(new InputSource(new StringReader(xml))));
        } finally {
            java.nio.file.Files.deleteIfExists(secret);
        }
    }

    @Test
    public void rejectsDoctypeInDomParser() throws Exception {
        javax.xml.parsers.DocumentBuilder builder = de.applejuicenet.client.fassade.shared.SecureXml.newDocumentBuilder();
        assertThrows(SAXException.class, () -> builder.parse(new InputSource(
                new StringReader("<!DOCTYPE a [<!ENTITY x 'y'>]><a>&x;</a>"))));
        assertEquals("a", builder.parse(new InputSource(new StringReader("<a/>"))).getDocumentElement().getNodeName());
    }

    @Test
    public void parsesConcurrentlyWithIndependentParsers() throws Exception {
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(8);
        try {
            java.util.List<java.util.concurrent.Future<String>> results = new java.util.ArrayList<>();
            for (int i = 0; i < 64; i++) {
                final int id = i;
                results.add(pool.submit(() -> {
                    javax.xml.parsers.DocumentBuilder builder = de.applejuicenet.client.fassade.shared.SecureXml.newDocumentBuilder();
                    return builder.parse(new InputSource(new StringReader("<a id='" + id + "'/>")))
                            .getDocumentElement().getAttribute("id");
                }));
            }
            for (int i = 0; i < 64; i++) {
                assertEquals(Integer.toString(i), results.get(i).get(10, java.util.concurrent.TimeUnit.SECONDS));
            }
        } finally {
            pool.shutdownNow();
        }
    }
}
