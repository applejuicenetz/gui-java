/*
 * Created on 09.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.tklsoft.processnost.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author krallt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class ProcessNoSTProperties {
	
	private static ProcessNoSTProperties instance = null;
	
	private final String propertiesLocation;
	private final Properties properties;
	
	private final String PROPERTY_DRIVER = "db_driver";
	private final String PROPERTY_PORT = "db_port";
	private final String PROPERTY_HOST = "db_host";
	private final String PROPERTY_USER = "db_user";
	private final String PROPERTY_PASSWORD = "db_password";
	
	private ProcessNoSTProperties(){
		propertiesLocation = System.getProperty("user.home") + File.separator + "processnost.properties";
		properties = new Properties();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(propertiesLocation);
		} catch (FileNotFoundException e) {
			// Datei existiert noch nicht. Template wird angelegt
			properties.setProperty(PROPERTY_DRIVER, "org.gjt.mm.mysql.Driver");
			properties.setProperty(PROPERTY_PORT, "");
			properties.setProperty(PROPERTY_HOST, "");
			properties.setProperty(PROPERTY_USER, "");
			properties.setProperty(PROPERTY_PASSWORD, "");
			save();
			try {
				inputStream = new FileInputStream(propertiesLocation);
			} catch (FileNotFoundException e2) {
				// Nun kann es nicht mehr passieren.
				e2.printStackTrace();
			}
		}
		try {
			properties.load(inputStream);
			inputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static synchronized ProcessNoSTProperties getInstance(){
		if (instance == null){
			instance = new ProcessNoSTProperties();
		}
		return instance;
	}
	
	public String getDbDriver(){
		return properties.getProperty(PROPERTY_DRIVER);
	}

	public String getDbHost(){
		return properties.getProperty(PROPERTY_HOST);
	}
	
	public String getDbPort(){
		return properties.getProperty(PROPERTY_PORT);
	}
	public String getDbUser(){
		return properties.getProperty(PROPERTY_USER);
	}
	public String getDbPassword(){
		return properties.getProperty(PROPERTY_PASSWORD);
	}

	private void save(){
		File aFile = new File(propertiesLocation);
		try {
			aFile.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(aFile);
			properties.store(outputStream, "Property-File for ProcessNoST");
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
