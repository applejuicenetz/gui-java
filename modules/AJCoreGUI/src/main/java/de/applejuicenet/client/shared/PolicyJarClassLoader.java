/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.shared;

import java.io.File;
import java.io.InputStream;

import java.lang.reflect.Constructor;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.gui.powerdownload.AutomaticPowerdownloadPolicy;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/PolicyJarClassLoader.java,v 1.7 2009/01/12 09:02:56 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */
public class PolicyJarClassLoader extends URLClassLoader
{
   private Logger logger;

   public PolicyJarClassLoader(URL url)
   {
      super(new URL[] {url});
      logger = Logger.getLogger(getClass());
   }

   @SuppressWarnings("unchecked")
   public AutomaticPowerdownloadPolicy getPolicy(String jar)
                                          throws Exception
   {
      try
      {
         File         aJar    = new File(jar);
         List<String> classes = loadClassBytesFromJar(aJar);

         if(classes == null)
         {
            return null;
         }

         String                       className;
         Class                        cl;
         Constructor                  con;
         AutomaticPowerdownloadPolicy policy;

         for(String curClassName : classes)
         {
            cl = loadClass(curClassName);
            if(AutomaticPowerdownloadPolicy.class.isAssignableFrom(cl))
            {
               con    = cl.getConstructor(new Class[] {ApplejuiceFassade.class});
               policy = (AutomaticPowerdownloadPolicy) con.newInstance(new Object[] {AppleJuiceClient.getAjFassade()});

               return policy;
            }
         }

         return null;
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.INFO))
         {
            logger.info("Plugin " + jar + " entspricht nicht dem Standard und wurde nicht geladen.", e);
         }

         return null;
      }
   }

   @SuppressWarnings("unchecked")
   private List<String> loadClassBytesFromJar(File jar)
                                       throws Exception
   {
      if(!jar.isFile())
      {
         return null;
      }

      JarFile      jf        = new JarFile(jar);
      String       entryName;
      List<String> classes = new ArrayList<String>();
      ZipEntry     entry;
      InputStream  is;
      int          l;
      byte[]       buf;
      int          read;
      int          incr;
      String       name;

      for(Enumeration e = jf.entries(); e.hasMoreElements();)
      {
         entry = (ZipEntry) e.nextElement();

         entryName = entry.getName();
         if(entryName.indexOf(".class") == -1)
         {
            continue;
         }

         is   = jf.getInputStream(entry);
         l    = (int) entry.getSize();
         buf  = new byte[l];
         read = 0;

         while(read < l)
         {
            incr = is.read(buf, read, l - read);

            read += incr;
         }

         name = entryName.replace('/', '.');

         name = name.replaceAll(".class", "");
         defineClass(name, buf, 0, buf.length);
         classes.add(name);
      }

      return classes;
   }
}
