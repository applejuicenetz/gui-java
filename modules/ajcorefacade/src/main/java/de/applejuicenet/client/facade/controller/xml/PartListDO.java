/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.facade.controller.xml;


/**
 * Copyright: General Public License
 */

import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.facade.entity.Part;
import de.applejuicenet.client.facade.entity.PartList;

import java.util.ArrayList;
import java.util.List;

class PartListDO extends PartList
{
   private Object       valueHolder;
   private long         size;
   private List<PartDO> parts = new ArrayList<PartDO>();
   private int          type;

   public PartListDO(Download download)
   {
      valueHolder = download;
      type        = MAIN_PARTLIST;
   }

   public PartListDO(DownloadSource downloadSource)
   {
      valueHolder = downloadSource;
      type        = SOURCE_PARTLIST;
   }

   public int getPartListType()
   {
      return type;
   }

   public long getSize()
   {
      return size;
   }

   public void addPart(PartDO aPart)
   {
      parts.add(aPart);
   }

   public void removeAllParts()
   {
      parts.clear();
   }

   public PartDO[] getParts()
   {
      return parts.toArray(new PartDO[parts.size()]);
   }

   public void setSize(long size)
   {
      this.size = size;
   }

   public Object getValueObject()
   {
      return valueHolder;
   }

   class PartDO extends Part
   {
      private long fromPosition;
      private int  type;

      public PartDO(long fromPosition, int type)
      {
         this.fromPosition = fromPosition;
         this.type         = type;
      }

      public long getFromPosition()
      {
         return fromPosition;
      }

      public int getType()
      {
         return type;
      }
   }
}
