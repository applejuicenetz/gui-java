package de.applejuicenet.client.shared;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/ZLibUtils.java,v 1.2 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: ZLibUtils.java,v $
 * Revision 1.2  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.1  2004/02/02 15:12:03  maj0r
 * Hilfsklasse zum Entzippen eines byte-Arrays.
 *
 *
 */

public abstract class ZLibUtils {

    private static Logger logger = Logger.getLogger(ZLibUtils.class.getClass());

    public static byte[] compress(String s) {
        try {
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
            bos.flush();
            bos.close();
            return (bos.toByteArray());
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return new byte[0];
        }
    }

    public static String uncompress(byte[] b) {
        try {
            Inflater infl = new Inflater();
            infl.setInput(b);

            StringBuffer retval = new StringBuffer();
            boolean done = false;
            int bufnum;
            byte[] buf;
            while (!done) {
                buf = new byte[256];
                try {
                    bufnum = infl.inflate(buf);
                    retval.append(new String(buf, 0, bufnum));
                    if (bufnum < buf.length) {
                        done = true;
                    }
                }
                catch (DataFormatException dfe) {
                    done = true;
                }
            }
            return (retval.toString());
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error("Unbehandelte Exception", e);
            }
            return "";
        }
    }
}