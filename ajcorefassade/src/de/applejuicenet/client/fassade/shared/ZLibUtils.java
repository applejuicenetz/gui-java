package de.applejuicenet.client.fassade.shared;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.fassade.ApplejuiceFassade;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/shared/ZLibUtils.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
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
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            return new byte[0];
        }
    }

    public static StringBuffer uncompress(byte[] b) {
        StringBuffer retval = new StringBuffer();
        try {
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
                    for(int i=0; i<bufnum; i++){
                    	tmp[i] = (char)buf[i];
                    }
                    retval.append(tmp);
                    if (bufnum < buf.length) {
                        done = true;
                    }
                }
                catch (DataFormatException dfe) {
                    done = true;
                }
            }
            return retval;
        }
        catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR)) {
                logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            }
            return retval;
        }
    }
}