/*
 * Created on 09.09.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.tklsoft.processnost.server.database;

import de.mathema.pride.DatabaseFactory;
import de.mathema.pride.ResourceAccessor;
import de.mathema.pride.ResourceAccessorJ2SE;
import de.tklsoft.processnost.shared.ProcessNoSTProperties;

/**
 * @author krallt
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class DatabaseLocator {
	private static DatabaseLocator instance = null;
	
	private DatabaseLocator(){
		ProcessNoSTProperties props = ProcessNoSTProperties.getInstance();
		ResourceAccessor accessor = new ResourceAccessorJ2SE(null);
		DatabaseFactory.setResourceAccessor(accessor);
//		DatabaseFactory.setDatabaseName(props.get.getProperty("pride.db"));	
	}

	public static synchronized DatabaseLocator getInstance(){
		if (instance == null){
			instance = new DatabaseLocator();
		}
		return instance;
	}
	
}
