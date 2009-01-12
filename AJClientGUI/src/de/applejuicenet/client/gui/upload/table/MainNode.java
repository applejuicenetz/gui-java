/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload.table;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.Icon;

import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;
import de.applejuicenet.client.shared.IconManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/upload/table/Attic/MainNode.java,v 1.7 2009/01/12 09:19:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class MainNode implements Node, LanguageListener
{
   public static final int            ROOT_NODE       = -1;
   public static final int            LOADING_UPLOADS = 0;
   public static final int            WAITING_UPLOADS = 1;
   public static final int            REST_UPLOADS    = 2;
   private static Map<String, Upload> uploads         = null;
   private String                     text;
   private int                        type;
   private MainNode[]                 children;

   public MainNode()
   {
      type        = ROOT_NODE;
      children    = new MainNode[2];
      children[0] = new MainNode(LOADING_UPLOADS);
      children[1] = new MainNode(WAITING_UPLOADS);
   }

   public MainNode(int type)
   {
      super();
      this.type = type;
      if(type == LOADING_UPLOADS)
      {
         LanguageSelector.getInstance().addLanguageListener(this);
      }
      else if(type == WAITING_UPLOADS)
      {
         LanguageSelector.getInstance().addLanguageListener(this);
      }
   }

   public static void setUploads(Map<String, Upload> uploadMap)
   {
      uploads = uploadMap;
   }

   public Icon getConvenientIcon()
   {
      if(type == LOADING_UPLOADS)
      {
         return IconManager.getInstance().getIcon("upload");
      }
      else if(type == WAITING_UPLOADS)
      {
         return IconManager.getInstance().getIcon("cool");
      }
      else
      {
         return null;
      }
   }

   public String toString()
   {
      return text;
   }

   public int getType()
   {
      return type;
   }

   public int getChildCount()
   {
      Object[] object = getChildren();

      if(object == null)
      {
         return 0;
      }
      else
      {
         return object.length;
      }
   }

   public Upload[] getChildrenByStatus(int statusToCheck)
   {
      if(uploads == null)
      {
         return null;
      }
      else
      {
         ArrayList<Upload> children         = new ArrayList<Upload>();
         Upload[]          uploadsForThread = (Upload[]) uploads.values().toArray(new Upload[uploads.size()]);

         for(int i = 0; i < uploadsForThread.length; i++)
         {
            if(uploadsForThread[i].getStatus() == statusToCheck)
            {
               children.add(uploadsForThread[i]);
            }
         }

         return (Upload[]) children.toArray(new Upload[children.size()]);
      }
   }

   public Object[] getChildren()
   {
      if(type == ROOT_NODE)
      {
         return children;
      }
      else
      {
         if(getType() == MainNode.LOADING_UPLOADS)
         {
            return getChildrenByStatus(Upload.AKTIVE_UEBERTRAGUNG);
         }
         else if(getType() == MainNode.WAITING_UPLOADS)
         {
            return getChildrenByStatus(Upload.WARTESCHLANGE);
         }
         else
         {
            return null;
         }
      }
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      if(type == LOADING_UPLOADS)
      {
         text = languageSelector.getFirstAttrbuteByTagName("javagui.uploadform.ladendeuploads");
      }
      else if(type == WAITING_UPLOADS)
      {
         text = languageSelector.getFirstAttrbuteByTagName("javagui.uploadform.wartendeuploads");
      }
   }
}
