/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import de.applejuicenet.client.facade.entity.Download;
import de.applejuicenet.client.facade.entity.DownloadSource;
import de.applejuicenet.client.facade.entity.Version;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadSourcesTableModel extends AbstractTableModel implements LanguageListener, SortableTableModel<DownloadSource>
{

   @SuppressWarnings("unchecked")
   public static final Class[]                               CLASS_TYPES = 
                                                                           {
                                                                              String.class, String.class, String.class,
                                                                              Long.class, Long.class, Integer.class,
                                                                              String.class, Double.class, Long.class,
                                                                              Integer.class, Version.class
                                                                           };

   //source states
   public static String                unasked                    = "";
   public static String                tryingToConnect          = "";
   public static String                peerHasOldVersion            = "";
   public static String                cannotOpenFile        = "";
   public static String                queue                = "";
   public static String                noUsableParts        = "";
   public static String                transferText                 = "";
   public static String                notEnoughSpace              = "";
   public static String                completed               = "";
   public static String                noConnectionPossible      = "";
   public static String                paused                     = "";
   public static String                position                     = "";
   public static String                tryingIndirect             = "";
   public static String                queueFull            = "";
   public static String                ownLimitReached         = "";
   public static String                indirectConnectionRejected = "";
   private TableSorter<DownloadSource> sorter;
   private List<DownloadSource>        sources                      = new ArrayList<DownloadSource>();
   private Download                    curDownload                  = null;

   public DownloadSourcesTableModel()
   {
      super();
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   public int getColumnCount()
   {
      return CLASS_TYPES.length;
   }

   @Override
   public Class<? > getColumnClass(int columnIndex)
   {
      return CLASS_TYPES[columnIndex];
   }

   public int getRowCount()
   {
      return sources.size();
   }

   public DownloadSource getRow(int row)
   {
      return sources.get(row);
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      DownloadSource source = sources.get(rowIndex);

      switch(columnIndex)
      {

         case 0:
            return source.getFilename();

         case 1:
            return getStatusAsString(source);

         case 2:
            return source.getNickname();

         case 3:
            return source.getSize();

         case 4:
            return source.getAlreadyLoaded();

         case 5:
            return source.getSpeed();

         case 6:
            return source.getRemainingTimeAsString();

         case 7:
            return source.getReadyPercent();

         case 8:
            return source.getRemainingToLoad();

         case 9:
            return source.getPowerDownload();

         case 10:
            return source.getVersion();

         default:
            return "";
      }
   }

   public Download getDownload()
   {
      return curDownload;
   }

   public boolean setDownload(Download theDownload)
   {
      boolean change = false;

      if(null == curDownload && null == theDownload)
      {
         return false;
      }
      else if(null == curDownload)
      {
         curDownload = theDownload;
         sources.clear();
         change = true;
      }
      else if(null != curDownload && null == theDownload)
      {
         curDownload = null;
         sources.clear();
         return true;
      }
      else if(curDownload.getId() != theDownload.getId())
      {
         curDownload = theDownload;
         sources.clear();
         change = true;
      }

      curDownload = theDownload;
      Map<Integer, DownloadSource> sourcesMap = theDownload.getSourcesMap();
      sources.replaceAll(old -> sourcesMap.getOrDefault(old.getId(), old));

      for(DownloadSource curDownloadSource : sourcesMap.values())
      {
         change = true;
         if(!sources.contains(curDownloadSource))
         {
            sources.add(curDownloadSource);
         }
      }

      int            count           = sources.size();
      DownloadSource aDownloadSource;

      if(count > 0)
      {
         for(int x = count - 1; x >= 0; x--)
         {
            aDownloadSource = sources.get(x);
            if(!sourcesMap.containsKey(aDownloadSource.getId()))
            {
               sources.remove(x);
               change = true;
            }
         }
      }

      return change;
   }

   public String getStatusAsString(DownloadSource downloadSource)
   {
      switch(downloadSource.getStatus())
      {

         case DownloadSource.UNASKED:
            return unasked;

         case DownloadSource.TRYING_TO_CONNECT:
            return tryingToConnect;

         case DownloadSource.PEER_HAS_OLD_VERSION:
            return peerHasOldVersion;

         case DownloadSource.PEER_CANNOT_OPEN_FILE:
            return cannotOpenFile;

         case DownloadSource.IN_QUEUE:
            return position.replaceFirst("%d", Integer.toString(downloadSource.getQueuePosition()));

         case DownloadSource.NO_USABLE_PARTS:
            return noUsableParts;

         case DownloadSource.TRANSFER:
            return transferText;

         case DownloadSource.NOT_ENOUGH_SPACE_ON_DISK:
            return notEnoughSpace;

         case DownloadSource.COMPLETED:
            return completed;

         case DownloadSource.NO_CONNECTION_POSSIBLE:
            return noConnectionPossible;

         case DownloadSource.PAUSED:
            return paused;

         case DownloadSource.TRYING_INDIRECT:
            return tryingIndirect;

         case DownloadSource.QUEUE_FULL:
            return queueFull;

         case DownloadSource.OWN_LIMIT_REACHED:
            return ownLimitReached;

         case DownloadSource.INDIRECT_CONNECTION_REJECTED:
            return indirectConnectionRejected;

         default:
            return "";
      }
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      unasked                    = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat1");
      tryingToConnect          = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat2");
      peerHasOldVersion            = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat3");
      cannotOpenFile        = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat4");
      queue                = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat5");
      noUsableParts        = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat6");
      transferText                 = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat7");
      notEnoughSpace              = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat8");
      completed               = languageSelector.getFirstAttributeByTagName("mainform.queue.queuestat14");
      noConnectionPossible      = languageSelector.getFirstAttributeByTagName("javagui.downloadform.connectionimpossible");
      paused                     = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat13");
      position                     = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat51");
      tryingIndirect             = languageSelector.getFirstAttributeByTagName("mainform.queue.userstat10");
      ownLimitReached         = languageSelector.getFirstAttributeByTagName("javagui.downloadform.ownlimitreached");
      indirectConnectionRejected = languageSelector.getFirstAttributeByTagName("javagui.downloadform.indirectconnectionrejected");
      queueFull            = languageSelector.getFirstAttributeByTagName("javagui.downloadform.queuefull");
   }

   public void forceResort()
   {
      if(null != sorter)
      {
         sorter.forceResort();
      }
   }

   public List<DownloadSource> getContent()
   {
      return sources;
   }

   public Object getValueForSortAt(int row, int column)
   {
      return getValueAt(row, column);
   }

   public void sortByColumn(int column, boolean isAscent)
   {
      if(sorter == null)
      {
         sorter = new TableSorter<DownloadSource>(this);
      }

      sorter.sort(column, isAscent);
      fireTableDataChanged();
   }
}
