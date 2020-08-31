/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public abstract class ZLibUtils
{
   public static byte[] compress(String s)
   {
      Deflater defl = new Deflater(Deflater.BEST_COMPRESSION);

      defl.setInput(s.getBytes());
      defl.finish();
      ByteArrayOutputStream bos             = new ByteArrayOutputStream();
      byte[]                buf             = new byte[256];
      int                   countCompressed;

      while(true)
      {
         countCompressed = defl.deflate(buf);
         bos.write(buf, 0, countCompressed);
         if(countCompressed < buf.length)
         {
            break;
         }
      }

      try
      {
         bos.flush();
         bos.close();
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }

      defl.end();
      return bos.toByteArray();
   }

   public static StringBuffer uncompress(byte[] b)
   {
      StringBuffer retval = new StringBuffer();
      Inflater     infl = new Inflater();

      infl.setInput(b);
      int    countUncompressed;
      byte[] buf = new byte[256];

      while(true)
      {
         try
         {
            countUncompressed = infl.inflate(buf);

            for(int i = 0; i < countUncompressed; i++)
            {
               retval.append((char) buf[i]);
            }

            if(countUncompressed < buf.length)
            {
               break;
            }
         }
         catch(DataFormatException dfe)
         {
            break;
         }
      }

      infl.end();
      return retval;
   }
}
