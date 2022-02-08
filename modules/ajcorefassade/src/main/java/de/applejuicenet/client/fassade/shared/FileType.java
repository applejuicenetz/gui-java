/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * $Header:
 * /cvsroot/applejuicejava/ajcorefassade/src/de/applejuicenet/client/fassade/shared/FileTypeHelper.java,v
 * 1.1 2004/12/03 07:57:12 maj0r Exp $
 *
 * <p>
 * Titel: AppleJuice Client-GUI
 * </p>
 * <p>
 * Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten
 * appleJuice-Core
 * </p>
 * <p>
 * Copyright: General Public License
 * </p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */
public enum FileType
{PDF("pdf"), IMAGE("image"), MOVIE("movie"), ISO("iso"), TEXT("text"), SOUND("sound"), ARCHIVE("archive"), UNKNOWN("treeRoot");FileType(String representation)
   {
      this.representation = representation;
   }

   @Override
   public String toString()
   {
      return representation;
   }

   private String                       representation;
   private static Map<String, FileType> associations = new HashMap<String, FileType>();

   static
   {
      associations.put("pdf", PDF);
      associations.put("bmp", IMAGE);
      associations.put("tif", IMAGE);
      associations.put("pcx", IMAGE);
      associations.put("jpe", IMAGE);
      associations.put("jpg", IMAGE);
      associations.put("png", IMAGE);
      associations.put("jpeg", IMAGE);

      associations.put("mpg", MOVIE);
      associations.put("mov", MOVIE);
      associations.put("jpeg", MOVIE);
      associations.put("dat", MOVIE);
      associations.put("vob", MOVIE);
      associations.put("avi", MOVIE);
      associations.put("mpeg", MOVIE);
      associations.put("ra", MOVIE);
      associations.put("rm", MOVIE);
      associations.put("divx", MOVIE);

      associations.put("iso", ISO);
      associations.put("tao", ISO);
      associations.put("cue", ISO);
      associations.put("img", ISO);
      associations.put("nrg", ISO);
      associations.put("bwt", ISO);
      associations.put("b5t", ISO);
      associations.put("ccd", ISO);
      associations.put("bin", ISO);
      associations.put("dao", ISO);
      associations.put("cif", ISO);
      associations.put("c2d", ISO);
      associations.put("pdi", ISO);
      associations.put("cdi", ISO);

      associations.put("txt", TEXT);
      associations.put("doc", TEXT);
      associations.put("nfo", TEXT);

      associations.put("mp3", SOUND);
      associations.put("mid", SOUND);
      associations.put("m2v", SOUND);
      associations.put("midi", SOUND);
      associations.put("mmf", SOUND);
      associations.put("wav", SOUND);
      associations.put("mp2", SOUND);
      associations.put("wma", SOUND);
      associations.put("ogg", SOUND);

      associations.put("zip", ARCHIVE);
      associations.put("ace", ARCHIVE);
      associations.put("gz2", ARCHIVE);
      associations.put("lzh", ARCHIVE);
      associations.put("gzip", ARCHIVE);
      associations.put("zip", ARCHIVE);
      associations.put("bz2", ARCHIVE);
      associations.put("rar", ARCHIVE);
      associations.put("arj", ARCHIVE);
      associations.put("cab", ARCHIVE);
      associations.put("tag", ARCHIVE);
      associations.put("uue", ARCHIVE);
      associations.put("jar", ARCHIVE);
   }

   public static FileType calculatePossibleFileType(String dateiname)
   {
      if(dateiname == null || dateiname.length() == 0)
      {
         return UNKNOWN;
      }

      String tmp   = dateiname.toLowerCase();
      int    index = tmp.lastIndexOf(StringConstants.POINT);

      if(index == -1 || index == tmp.length() - 1)
      {
         return UNKNOWN;
      }

      String   suffix             = tmp.substring(index + 1);
      FileType calculatedFileType = associations.get(suffix);

      return null == calculatedFileType ? UNKNOWN : calculatedFileType;

   }
}
