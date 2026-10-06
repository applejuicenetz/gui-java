package de.applejuicenet.client.fassade.tools;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * MD5 als Kleinbuchstaben-Hex. Das ist das Passwortformat des Core-Protokolls,
 * kein frei austauschbarer Algorithmus.
 */
public abstract class MD5Encoder {

	public static final String getMD5(String text) {
		try {
			// Standardzeichensatz wie bisher (seit Java 18 UTF-8), damit gespeicherte Hashes gleich bleiben
			return HexFormat.of().formatHex(MessageDigest.getInstance("MD5").digest(text.getBytes(Charset.defaultCharset())));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
}
