package de.applejuicenet.client.gui.download.table;

import de.applejuicenet.client.fassade.controller.dac.DownloadDO;
import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.shared.util.DownloadDOCalculator;
import de.applejuicenet.client.shared.util.DownloadSourceCalculator;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadColumnValue.java,v 1.2 2005/01/18 17:35:25 maj0r Exp $
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
    	if (obj.getClass() == DownloadDirectoryNode.class){
    		return ((DownloadDirectoryNode)obj).getVerzeichnis();
    	}
    	else if (obj.getClass() == DownloadDO.class){
    		return ((DownloadDO)obj).getFilename();
    	}
    	else if (obj.getClass() == DownloadSourceDO.class){
    		return ((DownloadSourceDO)obj).getFilename();
    	}
    	return "";
    }

    public static String getColumn1(Object obj){
    	if (obj.getClass() == DownloadDO.class){
    		return DownloadDOCalculator.getStatusAsString((DownloadDO)obj);
    	}
    	else if (obj.getClass() == DownloadSourceDO.class){
    		return DownloadSourceCalculator.getStatusAsString((DownloadSourceDO)obj);
    	}
    	return "";
    }

    public static String getColumn2(Object obj){
    	if (obj.getClass() == DownloadDO.class){
    		return DownloadModel.parseGroesse(((DownloadDO)obj).getGroesse());
    	}
    	else if (obj.getClass() == DownloadSourceDO.class){
    		int size = ((DownloadSourceDO)obj).getSize();
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
    	if (obj.getClass() == DownloadDO.class){
    		return DownloadModel.parseGroesse(((DownloadDO)obj).getBereitsGeladen());
    	}
    	else if (obj.getClass() == DownloadSourceDO.class){
    		int bereitsGeladen = ((DownloadSourceDO)obj).getBereitsGeladen();
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
    	if (obj.getClass() == DownloadDO.class
    			&& ((DownloadDO)obj).getStatus() == DownloadDO.SUCHEN_LADEN){
    		return DownloadModel.parseGroesse(((DownloadDO)obj).getSpeedInBytes());
    	}
    	else if (obj.getClass() == DownloadSourceDO.class
    			&& ((DownloadSourceDO)obj).getStatus() == DownloadSourceDO.UEBERTRAGUNG){
    		return DownloadModel.getSpeedAsString( (long) ((DownloadSourceDO)obj).getSpeed());
    	}
    	return "";
    }

    public static String getColumn5(Object obj){
    	if (obj.getClass() == DownloadDO.class
    			&& ((DownloadDO)obj).getStatus() == DownloadDO.SUCHEN_LADEN){
    		return ((DownloadDO)obj).getRestZeitAsString();
    	}
    	else if (obj.getClass() == DownloadSourceDO.class){
    		return ((DownloadSourceDO)obj).getRestZeitAsString();
    	}
    	return "";
    }

    public static String getColumn6(Object obj){
    	return "";
    }

    public static String getColumn7(Object obj){
    	if (obj.getClass() == DownloadDO.class
    			&& (((DownloadDO)obj).getStatus() == DownloadDO.SUCHEN_LADEN
				|| ((DownloadDO)obj).getStatus() == DownloadDO.PAUSIERT)){
    		return DownloadModel.parseGroesse(
    				((DownloadDO)obj).getGroesse()-((DownloadDO)obj).getBereitsGeladen());
    	}
    	else if (obj.getClass() == DownloadSourceDO.class){
    		int nochZuLaden = ((DownloadSourceDO)obj).getNochZuLaden();
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
    	if (obj.getClass() == DownloadDO.class
    			&& (((DownloadDO)obj).getStatus() == DownloadDO.SUCHEN_LADEN
				|| ((DownloadDO)obj).getStatus() == DownloadDO.PAUSIERT)){
    		return DownloadModel.powerdownload(((DownloadDO)obj).getPowerDownload());
    	}
    	else if (obj.getClass() == DownloadSourceDO.class){
    		return DownloadModel.powerdownload(((DownloadSourceDO)obj).getPowerDownload());
    	}
    	return "";
    }

    public static String getColumn9(Object obj){
    	if (obj.getClass() == DownloadSourceDO.class
    			&& ((DownloadSourceDO)obj).getVersion() != null){
    		return ((DownloadSourceDO)obj).getVersion().getVersion();
    	}
    	return "";
    }
    
    
}