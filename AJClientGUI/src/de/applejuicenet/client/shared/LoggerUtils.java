package de.applejuicenet.client.shared;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Attic/LoggerUtils.java,v 1.1 2003/07/01 14:50:01 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI f?r den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: LoggerUtils.java,v $
 * Revision 1.1  2003/07/01 14:50:01  maj0r
 * Hilfsklasse für Log4j
 *
 *
 */

public abstract class LoggerUtils {
    public static final int DEFAULT = -1;
    public static final int EINTRITT = 0;
    public static final int AUSTRITT = 1;

    private static final SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss,SSS");

    public static String getTimeForLogger(){
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    public static String createDebugMessage(String prefix, int methodStatus){
        String nachricht = prefix;
        switch (methodStatus){
            case EINTRITT:{
                nachricht += " Eintritt: ";
                break;
            }
            case AUSTRITT:{
                nachricht += " Austritt: ";
                break;
            }
            default:{
                nachricht += ": ";
                break;
            }
        }
        nachricht += formatter.format(new Date(System.currentTimeMillis()));
        return nachricht;
    }

}
