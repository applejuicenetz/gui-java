package de.applejuicenet.client.fassade.shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public abstract class ZLibUtils {

	public static byte[] compress(String s) {
		Deflater defl = new Deflater(Deflater.BEST_COMPRESSION);
		defl.setInput(s.getBytes());
		defl.finish();
		boolean done = false;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf;
		int bufnum;
		while (!done) {
			buf = new byte[256];
			bufnum = defl.deflate(buf);
			bos.write(buf, 0, bufnum);
			if (bufnum < buf.length) {
				done = true;
			}
		}
		try {
			bos.flush();
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return (bos.toByteArray());
	}

	public static StringBuffer uncompress(byte[] b) {
		StringBuffer retval = new StringBuffer();
		Inflater infl = new Inflater();
		infl.setInput(b);
		boolean done = false;
		int bufnum;
		byte[] buf;
		while (!done) {
			buf = new byte[256];
			try {
				bufnum = infl.inflate(buf);
				char[] tmp = new char[bufnum];
				for (int i = 0; i < bufnum; i++) {
					tmp[i] = (char) buf[i];
				}
				retval.append(tmp);
				if (bufnum < buf.length) {
					done = true;
				}
			} catch (DataFormatException dfe) {
				done = true;
			}
		}
		return retval;
	}
}