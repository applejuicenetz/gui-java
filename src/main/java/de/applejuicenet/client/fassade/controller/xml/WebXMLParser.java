package de.applejuicenet.client.fassade.controller.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.applejuicenet.client.fassade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.fassade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.fassade.shared.HtmlLoader;
import de.applejuicenet.client.fassade.shared.XMLDecoder;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/controller/WebXMLParser.java,v
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
 * @author: Maj0r <aj@tkl-soft.de>
 * 
 */

public abstract class WebXMLParser extends XMLDecoder {

	private String xmlCommand;

	private long timestamp = 0;

	private boolean useTimestamp = false;

	private String zipMode = "";

	private static DocumentBuilder builder;

	private CoreConnectionSettingsHolder coreHolder;

	static {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException ex) {
			;
		}
	}

	public WebXMLParser(CoreConnectionSettingsHolder coreHolder,
			String xmlCommand, String parameters) {
		this(coreHolder, xmlCommand, parameters, false);
	}

	public WebXMLParser(CoreConnectionSettingsHolder coreHolder,
			String xmlCommand, String parameters, boolean useTimestamp) {
		super();
		init(coreHolder, xmlCommand, useTimestamp);
	}

	private void init(CoreConnectionSettingsHolder coreHolder,
			String xmlCommand, boolean useTimestamp) {
		this.useTimestamp = useTimestamp;
		if (!coreHolder.isLocalhost()) {
			zipMode = "mode=zip&";
		}
		this.xmlCommand = xmlCommand;
		webXML = true;
		this.coreHolder = coreHolder;
	}

	private String getCommand(String parameters) {
		String command = xmlCommand + "?";
		if (parameters.indexOf("mode=zip") == -1) {
			command += zipMode;
		}
		if (useTimestamp) {
			command += "password=" + coreHolder.getCorePassword()
					+ "&timestamp=" + timestamp + parameters;
		} else {
			if (parameters.length() != 0) {
				command += "password=" + coreHolder.getCorePassword() + "&"
						+ parameters;
			} else {
				command += "password=" + coreHolder.getCorePassword();
			}
		}
		return command;
	}

	public void reload(String parameters, boolean throwWebSiteNotFoundException)
			throws Exception {
		String xmlData = null;
		try {
			String command = getCommand(parameters);
			xmlData = HtmlLoader.getHtmlXMLContent(coreHolder.getCoreHost(),
					coreHolder.getCorePort(), HtmlLoader.GET, command);
			if (xmlData == null || xmlData.length() == 0
					|| xmlData.startsWith("error:")) {
				throw new IllegalArgumentException();
			}
		} catch (WebSiteNotFoundException ex) {
			if (throwWebSiteNotFoundException) {
				throw ex;
			} else {
				return;
			}
		}
		try {
			document = builder
					.parse(new InputSource(new StringReader(xmlData)));
			if (useTimestamp) {
				timestamp = Long.parseLong(getFirstAttrbuteByTagName(
						new String[] { "applejuice", "time" }, true));
			}
		} catch (Exception e) {
			Exception x = e;
			if (e.getClass() == SAXException.class) {
				if (((SAXException) e).getException() != null) {
					x = ((SAXException) e).getException();
				}
			}
			String zeit = Long.toString(System.currentTimeMillis());
			String path = System.getProperty("user.dir") + File.separator
					+ "logs";
			File aFile = new File(path);
			if (!aFile.exists()) {
				aFile.mkdir();
			}
			FileWriter fileWriter = null;
			String dateiname = path + File.separator + zeit + ".exc";
			try {
				fileWriter = new FileWriter(dateiname);
				fileWriter.write(xmlData);
				fileWriter.close();
			} catch (IOException ioE) {
				throw new RuntimeException(ioE);
			}
			throw new RuntimeException("SAX-Exception -> content saved in "
					+ dateiname, x);
		}
	}

	public abstract void update();
}
