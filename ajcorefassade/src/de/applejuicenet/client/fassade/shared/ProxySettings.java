package de.applejuicenet.client.fassade.shared;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/ProxySettings.java,v
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

public class ProxySettings {
	private String host;

	private int port;

	private String userpass;

	public ProxySettings(String host, int port, String user, String pass) {
		this.host = host;
		this.port = port;
		setUserpass(user, pass);
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUserpass() {
		return userpass;
	}

	private void setUserpass(String user, String passwort) {
		this.userpass = new sun.misc.BASE64Encoder()
				.encode((user + ":" + passwort).getBytes());
	}
}
