package de.applejuicenet.client.fassade.controller;

import de.applejuicenet.client.fassade.exception.IllegalArgumentException;
import de.applejuicenet.client.fassade.tools.MD5Encoder;

public final class CoreConnectionSettingsHolder {

	private final String coreHost;

	private final Integer corePort;

	private String corePassword;

	public CoreConnectionSettingsHolder(String coreHost, Integer corePort,
			String corePassword, boolean passwordIsPlaintext)
			throws IllegalArgumentException {
		if (coreHost == null) {
			throw new IllegalArgumentException("illegal host");
		}
		if (corePort == null) {
			throw new IllegalArgumentException("illegal port");
		}
		if (corePassword == null) {
			throw new IllegalArgumentException("illegal password");
		}
		this.coreHost = coreHost;
		this.corePort = corePort;
		if (passwordIsPlaintext) {
			this.corePassword = MD5Encoder.getMD5(corePassword);
		} else {
			if (corePassword.length() == 0 || corePassword.trim().length() == 0) {
				throw new IllegalArgumentException("illegal password");
			}
			this.corePassword = corePassword;
		}
	}

	public String getCorePassword() {
		return corePassword;
	}

	public void setCorePassword(String corePassword) {
		this.corePassword = corePassword;
	}

	public String getCoreHost() {
		return coreHost;
	}

	public Integer getCorePort() {
		return corePort;
	}

	public boolean isLocalhost() {
		return (coreHost.equalsIgnoreCase("127.0.0.1") || coreHost
				.equalsIgnoreCase("localhost"));
	}
}
