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
      int byDate = Long.compare(o2.lastModified(), o1.lastModified());

      return byDate != 0 ? byDate : o1.getName().compareTo(o2.getName());
   }
}
