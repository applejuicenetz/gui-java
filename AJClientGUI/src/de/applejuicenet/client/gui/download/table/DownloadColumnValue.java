package de.applejuicenet.client.gui.download.table;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.shared.util.DownloadCalculator;
import de.applejuicenet.client.shared.util.DownloadSourceCalculator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadColumnValue.java,v 1.5 2005/05/05 18:33:10 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class DownloadColumnValue {
    public static String getColumn0(Object obj){
    	if (obj.getClass() == DownloadNode.class){
    		return ((DownloadNode)obj).getPath();
    	}
    	else if (obj instanceof Download){
    		return ((Download)obj).getFilename();
    	}
    	else if (obj instanceof DownloadSource){
    		return ((DownloadSource)obj).getFilename();
    	}
    	return "";
    }

    public static String getColumn1(Object obj){
    	if (obj instanceof Download){
    		return DownloadCalculator.getStatusAsString((Download)obj);
    	}
    	else if (obj instanceof DownloadSource){
    		return DownloadSourceCalculator.getStatusAsString((DownloadSource)obj);
    	}
    	return "";
    }

    public static String getColumn2(Object obj){
    	if (obj instanceof Download){
    		return DownloadModel.parseGroesse(((Download)obj).getGroesse());
    	}
    	else if (obj instanceof DownloadSource){
    		int size = ((DownloadSource)obj).getSize();
            String sizeAsString = DownloadModel.parseGroesse(size);
            if (sizeAsString == null){
                return "";
            }
            else{
                return sizeAsString;
            }
    	}
    	return "";
    }

    public static String getColumn3(Object obj){
    	if (obj instanceof Download){
    		return DownloadModel.parseGroesse(((Download)obj).getBereitsGeladen());
    	}
    	else if (obj instanceof DownloadSource){
    		int bereitsGeladen = ((DownloadSource)obj).getBereitsGeladen();
            String bereitsGeladenAsString = DownloadModel.parseGroesse(bereitsGeladen);
            if (bereitsGeladenAsString == null){
                return "";
            }
            else{
                return bereitsGeladenAsString;
            }
    	}
    	return "";
    }

    public static String getColumn4(Object obj){
    	if (obj instanceof Download
    			&& ((Download)obj).getStatus() == Download.SUCHEN_LADEN){
    		return DownloadModel.parseGroesse(((Download)obj).getSpeedInBytes());
    	}
    	else if (obj instanceof DownloadSource
    			&& ((DownloadSource)obj).getStatus() == DownloadSource.UEBERTRAGUNG){
    		return DownloadModel.getSpeedAsString( (long) ((DownloadSource)obj).getSpeed());
    	}
    	return "";
    }

    public static String getColumn5(Object obj){
    	if (obj instanceof Download
    			&& ((Download)obj).getStatus() == Download.SUCHEN_LADEN){
    		return ((Download)obj).getRestZeitAsString();
    	}
    	else if (obj instanceof DownloadSource){
    		return ((DownloadSource)obj).getRestZeitAsString();
    	}
    	return "";
    }

    public static String getColumn6(Object obj){
    	return "";
    }

    public static String getColumn7(Object obj){
    	if (obj instanceof Download
    			&& (((Download)obj).getStatus() == Download.SUCHEN_LADEN
				|| ((Download)obj).getStatus() == Download.PAUSIERT)){
    		return DownloadModel.parseGroesse(
    				((Download)obj).getGroesse()-((Download)obj).getBereitsGeladen());
    	}
    	else if (obj instanceof DownloadSource){
    		int nochZuLaden = ((DownloadSource)obj).getNochZuLaden();
            String nochZuLadenAsString = DownloadModel.parseGroesse(nochZuLaden);
            if (nochZuLadenAsString == null){
                return "";
            }
            else{
                return nochZuLadenAsString;
            }
    	}
    	return "";
    }

    public static String getColumn8(Object obj){
    	if (obj instanceof Download
    			&& (((Download)obj).getStatus() == Download.SUCHEN_LADEN
				|| ((Download)obj).getStatus() == Download.PAUSIERT)){
    		return DownloadModel.powerdownload(((Download)obj).getPowerDownload());
    	}
    	else if (obj instanceof DownloadSource){
    		return DownloadModel.powerdownload(((DownloadSource)obj).getPowerDownload());
    	}
    	return "";
    }

    public static String getColumn9(Object obj){
    	if (obj instanceof DownloadSource
    			&& ((DownloadSource)obj).getVersion() != null){
    		return ((DownloadSource)obj).getVersion().getVersion();
    	}
    	return "";
    }
    
    
}