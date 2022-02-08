/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.fassade.entity;

import java.awt.*;

public abstract class PartList
{
   public static final Color COLOR_TYPE_UEBERPRUEFT = Color.GREEN;
   public static final Color COLOR_TYPE_OK   = Color.BLACK;
   public static final Color COLOR_TYPE_0    = Color.RED;
   public static final Color COLOR_TYPE_9    = new Color(25, 25, 250);
   public static final Color COLOR_TYPE_8    = new Color(50, 50, 250);
   public static final Color COLOR_TYPE_7    = new Color(75, 75, 250);
   public static final Color COLOR_TYPE_6    = new Color(100, 100, 250);
   public static final Color COLOR_TYPE_5    = new Color(125, 125, 250);
   public static final Color COLOR_TYPE_4    = new Color(150, 150, 250);
   public static final Color COLOR_TYPE_3    = new Color(175, 175, 250);
   public static final Color COLOR_TYPE_2    = new Color(200, 200, 250);
   public static final Color COLOR_TYPE_1    = new Color(225, 225, 250);
   public static final Color COLOR_TYPE_10   = Color.BLUE;
   public static final Color COLOR_READY_10  = new Color(255, 255, 132);
   public static final Color COLOR_READY_30  = new Color(255, 255, 98);
   public static final Color COLOR_READY_50  = new Color(255, 255, 0);
   public static final Color COLOR_READY_70  = new Color(210, 210, 0);
   public static final Color COLOR_READY_90  = new Color(176, 176, 0);
   public static final Color COLOR_READY_100 = new Color(157, 157, 0);
   public static final int   MAIN_PARTLIST   = 0;
   public static final int   SOURCE_PARTLIST = 1;

   public abstract int getPartListType();

   public abstract long getGroesse();

   public abstract Part[] getParts();

   public abstract Object getValueObject();

   public final double getProzentVerfuegbar()
   {
      Part[] allParts = getParts();

      if(allParts == null || allParts.length == 0)
      {
         return -1;
      }

      long available = 0;

      for(int i = 0; i < allParts.length - 1; i++)
      {
         if(allParts[i].getType() != 0)
         {
            available += allParts[i + 1].getFromPosition() - allParts[i].getFromPosition();
         }
      }

      if(allParts[allParts.length - 1].getType() != 0)
      {
         available += getGroesse() - allParts[allParts.length - 1].getFromPosition();
      }

      return ((available * 100.0) / getGroesse());
   }
}
