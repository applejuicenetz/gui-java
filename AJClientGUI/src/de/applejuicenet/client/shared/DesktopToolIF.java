/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.shared;

import java.io.File;

import java.net.URI;

public interface DesktopToolIF
{
   void browse(URI uri);

   void open(File toOpen);
}
