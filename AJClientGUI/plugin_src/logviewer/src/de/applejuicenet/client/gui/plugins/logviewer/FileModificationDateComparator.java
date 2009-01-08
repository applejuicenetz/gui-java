/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.plugins.logviewer;

import java.io.File;
import java.util.Comparator;

public class FileModificationDateComparator implements Comparator<File>
{
   public int compare(File o1, File o2)
   {
      return Long.valueOf(o2.lastModified()).compareTo(Long.valueOf(o1.lastModified()));
   }
}
