package de.applejuicenet.client.fassade.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MD5Encoder {

	public static final String getMD5(String text) {
		byte[] intext = text.getBytes();
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] md5rslt = md5.digest(intext);
		StringBuffer verifyMsg = new StringBuffer();
		for (int i = 0; i < md5rslt.length; i++) {
			int hexChar = 0xFF & md5rslt[i];
			String hexString = Integer.toHexString(hexChar);
			hexString = (hexString.length() == 1) ? "0" + hexString : hexString;
			verifyMsg.append(hexString);
		}
		return verifyMsg.toString();
	}
}
