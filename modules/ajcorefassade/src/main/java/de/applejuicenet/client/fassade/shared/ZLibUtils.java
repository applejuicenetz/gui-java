/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
         if(defl.finished())
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

   /**
    * Entpackt vollstaendig, bis der Zlib-Strom beendet ist. Wirft bei
    * beschaedigten, unvollstaendigen oder zu grossen Daten.
    */
   public static byte[] inflate(byte[] b, int maxBytes) throws DataFormatException
   {
      Inflater infl = new Inflater();

      try
      {
         infl.setInput(b);
         ByteArrayOutputStream bos = new ByteArrayOutputStream(Math.min(Math.max(b.length * 4, 256), maxBytes));
         byte[]                buf = new byte[4096];

         while(!infl.finished())
         {
            int count = infl.inflate(buf);

            if(count == 0 && (infl.needsInput() || infl.needsDictionary()))
            {
               throw new DataFormatException("Unvollstaendige Zlib-Daten");
            }

            if(bos.size() + count > maxBytes)
            {
               throw new DataFormatException("Entpackte Daten ueberschreiten " + maxBytes + " Bytes");
            }

            bos.write(buf, 0, count);
         }

         return bos.toByteArray();
      }
      finally
      {
         infl.end();
      }
   }

   /**
    * Liest und entpackt, bis der Zlib-Strom beendet ist. Endet der Eingabestrom
    * vorher, wird eine DataFormatException geworfen.
    */
   public static byte[] inflate(InputStream in, int maxBytes) throws IOException, DataFormatException
   {
      Inflater infl = new Inflater();

      try
      {
         ByteArrayOutputStream bos   = new ByteArrayOutputStream();
         byte[]                input = new byte[4096];
         byte[]                buf   = new byte[4096];

         while(!infl.finished())
         {
            int count = infl.inflate(buf);

            if(count > 0)
            {
               if(bos.size() + count > maxBytes)
               {
                  throw new DataFormatException("Entpackte Daten ueberschreiten " + maxBytes + " Bytes");
               }

               bos.write(buf, 0, count);
            }
            else if(infl.needsDictionary())
            {
               throw new DataFormatException("Zlib-Dictionary nicht unterstuetzt");
            }
            else if(infl.needsInput())
            {
               int read = in.read(input);

               if(read < 0)
               {
                  throw new DataFormatException("Unvollstaendige Zlib-Daten");
               }

               infl.setInput(input, 0, read);
            }
         }

         return bos.toByteArray();
      }
      finally
      {
         infl.end();
      }
   }

   public static StringBuffer uncompress(byte[] b)
   {
      try
      {
         byte[]       data   = inflate(b, Integer.MAX_VALUE - 8);
         StringBuffer retval = new StringBuffer(data.length);

         for(byte value : data)
         {
            retval.append((char) (value & 0xFF));
         }

         return retval;
      }
      catch(DataFormatException dfe)
      {
         throw new IllegalArgumentException(dfe);
      }
   }
}
