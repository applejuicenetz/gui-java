package de.applejuicenet.client.fassade.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/ajcorefassade/src/de/applejuicenet/client/fassade/shared/Attic/FileTypeHelper.java,v 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public abstract class FileTypeHelper {

    public static final String TYPE_PDF = "pdf";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_MOVIE = "movie";
    public static final String TYPE_ISO = "iso";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_SOUND = "sound";
    public static final String TYPE_ARCHIVE = "archive";
    public static final String TYPE_UNKNOWN = "treeRoot";

    private static final String[] allTypes = new String[]
       {TYPE_PDF, TYPE_IMAGE, TYPE_MOVIE, TYPE_ISO, TYPE_TEXT, TYPE_SOUND, TYPE_ARCHIVE, TYPE_UNKNOWN};

    public static String[] getAllTypes(){
    	return allTypes;
    }

    public static String calculatePossibleFileType(String dateiname){
    	if (dateiname == null || dateiname.length() == 0){
    		return "";
    	}
    	String calculatedFileType = "";
        String lower = dateiname.toLowerCase();
        if (lower.endsWith(".pdf")){
            calculatedFileType = FileTypeHelper.TYPE_PDF;
        }
        else if (lower.endsWith(".bmp") || lower.endsWith(".jpg")
                 || lower.endsWith(".tif") || lower.endsWith(".png")
                 || lower.endsWith(".pcx") || lower.endsWith(".jpeg")
                 || lower.endsWith(".jpe")){
        	calculatedFileType = FileTypeHelper.TYPE_IMAGE;
        }
        else if (lower.endsWith(".mpg") || lower.endsWith(".avi")
                 || lower.endsWith(".mov") || lower.endsWith(".mpeg")
                 || lower.endsWith(".dat") || lower.endsWith(".ra")
                 || lower.endsWith(".vob") || lower.endsWith(".rm")
                 || lower.endsWith(".divx")){
        	calculatedFileType = FileTypeHelper.TYPE_MOVIE;
        }
        else if (lower.endsWith(".iso") || lower.endsWith(".bin")
                 || lower.endsWith(".cue") || lower.endsWith(".dao")
                 || lower.endsWith(".img") || lower.endsWith(".cif")
                 || lower.endsWith(".nrg") || lower.endsWith(".c2d")
                 || lower.endsWith(".bwt") || lower.endsWith(".pdi")
                 || lower.endsWith(".b5t") || lower.endsWith(".cdi")
                 || lower.endsWith(".ccd") || lower.endsWith(".tao")){
        	calculatedFileType = FileTypeHelper.TYPE_ISO;
        }
        else if (lower.endsWith(".txt") || lower.endsWith(".nfo")
                 || lower.endsWith(".doc")){
        	calculatedFileType = FileTypeHelper.TYPE_TEXT;
        }
        else if (lower.endsWith(".mp3") || lower.endsWith(".wav")
                 || lower.endsWith(".mid") || lower.endsWith(".mp2")
                 || lower.endsWith(".m2v") || lower.endsWith(".wma")
                 || lower.endsWith(".midi") || lower.endsWith(".ogg")
                 || lower.endsWith(".mmf")){
        	calculatedFileType = FileTypeHelper.TYPE_SOUND;
        }
        else if (lower.endsWith(".zip") || lower.endsWith(".rar")
                 || lower.endsWith(".ace") || lower.endsWith(".arj")
                 || lower.endsWith(".gz2") || lower.endsWith(".cab")
                 || lower.endsWith(".lzh") || lower.endsWith(".tag")
                 || lower.endsWith(".gzip") || lower.endsWith(".uue")
                 || lower.endsWith(".bz2") || lower.endsWith(".jar")){
        	calculatedFileType = FileTypeHelper.TYPE_ARCHIVE;
        }
        return calculatedFileType;
    }
}
