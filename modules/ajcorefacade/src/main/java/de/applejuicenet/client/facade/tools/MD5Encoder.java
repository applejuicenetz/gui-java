package de.applejuicenet.client.facade.tools;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * MD5 as lowercase hex. This is the password format of the core protocol, not a freely exchangeable
 * algorithm.
 */
public abstract class MD5Encoder {

	public static final String getMD5(String text) {
		try {
			// Default charset as before (UTF-8 since Java 18) so stored hashes stay the same
			return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(text.getBytes(Charset.defaultCharset())));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
