package de.applejuicenet.client.fassade.tools;

import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;

import static org.junit.Assert.assertEquals;

public class MD5EncoderTest {
    @Test
    public void matchesKnownVectors() {
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", MD5Encoder.getMD5(""));
        assertEquals("5d41402abc4b2a76b9719d911017c592", MD5Encoder.getMD5("hello"));
    }

    @Test
    public void keepsLeadingZeroBytesAndLowerCase() throws Exception {
        // "a" ergibt 0cc175b9c0f1b6a831c399e269772661 (fuehrende Null)
        assertEquals("0cc175b9c0f1b6a831c399e269772661", MD5Encoder.getMD5("a"));
        for (int i = 0; i < 500; i++) {
            String text = "pw" + i;
            byte[] digest = MessageDigest.getInstance("MD5").digest(text.getBytes());
            String legacy = String.format("%032x", new BigInteger(1, digest));
            assertEquals(legacy, MD5Encoder.getMD5(text));
        }
    }

    @Test
    public void umlautsHashLikeThePreviousImplementation() throws Exception {
        String text = "Pässwörd ß€";
        StringBuilder legacy = new StringBuilder();
        for (byte b : MessageDigest.getInstance("MD5").digest(text.getBytes())) {
            String hex = Integer.toHexString(0xFF & b);
            legacy.append(hex.length() == 1 ? "0" + hex : hex);
        }
        assertEquals(legacy.toString(), MD5Encoder.getMD5(text));
    }
}
