package de.applejuicenet.client.facade.controller.xml;

import de.applejuicenet.client.facade.controller.CoreConnectionSettingsHolder;
import de.applejuicenet.client.facade.exception.WebSiteNotFoundException;
import de.applejuicenet.client.facade.shared.HtmlLoader;
import de.applejuicenet.client.facade.shared.SecureXml;
import de.applejuicenet.client.facade.shared.XMLDecoder;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 *
 * Copyright: General Public License
 *
 */

public abstract class WebXMLParser extends XMLDecoder {

	private String xmlCommand;

	private long timestamp = 0;

	private boolean useTimestamp = false;

	private String zipMode = "";

	private CoreConnectionSettingsHolder coreHolder;

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
			document = SecureXml.newDocumentBuilder()
					.parse(new InputSource(new StringReader(xmlData)));
			if (useTimestamp) {
				timestamp = Long.parseLong(getFirstAttributeByTagName(
						new String[] { "applejuice", "time" }, true));
			}
		} catch (Exception e) {
			Exception x = e;
			if (e.getClass() == SAXException.class) {
				if (((SAXException) e).getException() != null) {
					x = ((SAXException) e).getException();
				}
			}
			String time = Long.toString(System.currentTimeMillis());
			File directory = new File(System.getProperty("user.dir"), "logs");
			File dump = new File(directory, time + ".exc");
			try {
				Files.createDirectories(directory.toPath());
				Files.writeString(dump.toPath(), xmlData, StandardCharsets.UTF_8);
			} catch (IOException ioE) {
				x.addSuppressed(ioE);
				throw new RuntimeException("XML parse error; the dump could not be written to " + dump, x);
			}
			throw new RuntimeException("XML parse error; content saved in " + dump, x);
		}
	}

	public abstract void update();
}
