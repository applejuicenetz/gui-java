package de.applejuicenet.client.gui.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;

public class PropertyHandler {
	private Logger logger;
	
	private HashSet<PropertyChangeListener> listener = null;
	private String path;
	private Properties props;
	private String beschreibung;
	private boolean inform = true;
	
	public PropertyHandler(String propertiesLocation, String beschreibung, boolean load) 
		throws IllegalArgumentException{
		logger = Logger.getLogger(getClass());
		try{
			path = propertiesLocation;
			if (beschreibung == null){
				this.beschreibung = "";
			}
			else{
				this.beschreibung = beschreibung;
			}
			if (load){
				reload();
			}
			else{
				props = new Properties();
			}
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}
	
	private void informListener(String identifier, String oldValue, String newValue){
		if (listener != null && inform){
			PropertyChangeEvent propertyChangeEvent = 
				new PropertyChangeEvent(this, identifier, oldValue, newValue);
			Iterator it = listener.iterator();
			while (it.hasNext()){
				((PropertyChangeListener)it.next()).propertyChange(propertyChangeEvent);
			}
		}
	}
	
	public void allowInform(boolean shouldInform){
		inform = shouldInform;
	}
	
	public void put(String identifier, String value){
		try{
			String oldValue = get(identifier);
			props.put(identifier, value);
			informListener(identifier, oldValue, value);
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public void put(String identifier, int value){
		put(identifier, Integer.toString(value));
	}

	public void put(String identifier, boolean value){
		put(identifier, Boolean.toString(value));
	}
	
	public String get(String identifier){
		Object obj = props.get(identifier);
		if (obj == null){
			return "";
		}
		else{
			return (String)obj;
		}
	}

	public Boolean getAsBoolean(String identifier){
		String obj = (String)props.get(identifier);
		if (obj == null){
			return null;
		}
		return Boolean.valueOf(obj);
	}

	public Integer getAsInt(String identifier){
		String obj = (String)props.get(identifier);
		try{
			return new Integer(Integer.parseInt(obj));
		}
		catch(NumberFormatException nfE){
			return null;
		}
	}

	public void reload() throws IllegalArgumentException{
		try{
			props = new Properties();
			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(path);
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("PropertyDatei konnte nicht gefunden werden.");
	/*			File aFile = new File(path);
				try {
					aFile.createNewFile();
					inputStream = new FileInputStream(path);
				} catch (IOException e1) {*(
					throw new RuntimeException("PropertyDatei konnte nicht angelegt werden.");
				}*/
			}
			props = new Properties();
			try {
				props.load(inputStream);
				inputStream.close();
			} catch (IOException e2) {
				throw new IllegalArgumentException("Ungueltige PropertyDatei.");
			}
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}
	
	public void save() throws IllegalArgumentException{
		try{
			File aFile = new File(path);
			try {
				aFile.createNewFile();
				FileOutputStream outputStream = new FileOutputStream(aFile);
				props.store(outputStream, beschreibung);
				outputStream.close();
			} catch (IOException e) {
				throw new IllegalArgumentException("PropertyDatei konnte nicht gespeichert werden.");
			}
		} catch (Exception e) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
			}
		}
	}

	public boolean addPropertyChangeListener(PropertyChangeListener propertyChangeListener){
		if (listener == null){
			listener = new HashSet<PropertyChangeListener>();
		}
		return listener.add(propertyChangeListener);
	}

	public boolean removePropertyChangeListener(PropertyChangeListener propertyChangeListener){
		if (listener == null){
			return false;
		}
		return listener.remove(propertyChangeListener);
	}
}
