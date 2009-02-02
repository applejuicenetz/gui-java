/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.fassade.entity.Version;
import de.applejuicenet.client.gui.components.table.SortableTableModel;
import de.applejuicenet.client.gui.components.table.TableSorter;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.listener.LanguageListener;

public class DownloadSourcesTableModel extends AbstractTableModel implements LanguageListener, SortableTableModel<DownloadSource>
{

   @SuppressWarnings("unchecked")
   public static final Class[]                               CLASS_TYPES = 
                                                                           {
                                                                              String.class, String.class, String.class,
                                                                              Integer.class, Integer.class, Integer.class,
                                                                              String.class, Double.class, Integer.class,
                                                                              Integer.class, Version.class
                                                                           };

   //Source-Stati
   public static String                ungefragt                    = "";
   public static String                versucheZuVerbinden          = "";
   public static String                ggstZuAlteVersion            = "";
   public static String                kannDateiNichtOeffnen        = "";
   public static String                warteschlange                = "";
   public static String                keineBrauchbarenParts        = "";
   public static String                uebertragung                 = "";
   public static String                nichtGenugPlatz              = "";
   public static String                fertiggestellt               = "";
   public static String                keineVerbindungMoeglich      = "";
   public static String                pausiert                     = "";
   public static String                position                     = "";
   public static String                versucheIndirekt             = "";
   public static String                warteschlangeVoll            = "";
   public static String                eigenesLimitErreicht         = "";
   public static String                indirekteVerbindungAbgelehnt = "";
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
            return source.getBereitsGeladen();

         case 5:
            return source.getSpeed();

         case 6:
            return source.getRestZeitAsString();

         case 7:
            return source.getReadyPercent();

         case 8:
            return source.getNochZuLaden();

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

      Map<Integer, DownloadSource> sourcesMap = theDownload.getSourcesMap();

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

         case DownloadSource.UNGEFRAGT:
            return ungefragt;

         case DownloadSource.VERSUCHE_ZU_VERBINDEN:
            return versucheZuVerbinden;

         case DownloadSource.GEGENSTELLE_HAT_ZU_ALTE_VERSION:
            return ggstZuAlteVersion;

         case DownloadSource.GEGENSTELLE_KANN_DATEI_NICHT_OEFFNEN:
            return kannDateiNichtOeffnen;

         case DownloadSource.IN_WARTESCHLANGE:
            return position.replaceFirst("%d", Integer.toString(downloadSource.getQueuePosition()));

         case DownloadSource.KEINE_BRAUCHBAREN_PARTS:
            return keineBrauchbarenParts;

         case DownloadSource.UEBERTRAGUNG:
            return uebertragung;

         case DownloadSource.NICHT_GENUEGEND_PLATZ_AUF_DER_PLATTE:
            return nichtGenugPlatz;

         case DownloadSource.FERTIGGESTELLT:
            return fertiggestellt;

         case DownloadSource.KEINE_VERBINDUNG_MOEGLICH:
            return keineVerbindungMoeglich;

         case DownloadSource.PAUSIERT:
            return pausiert;

         case DownloadSource.VERSUCHE_INDIREKT:
            return versucheIndirekt;

         case DownloadSource.WARTESCHLANGE_VOLL:
            return warteschlangeVoll;

         case DownloadSource.EIGENES_LIMIT_ERREICHT:
            return eigenesLimitErreicht;

         case DownloadSource.INDIREKTE_VERBINDUNG_ABGELEHNT:
            return indirekteVerbindungAbgelehnt;

         default:
            return "";
      }
   }

   public void fireLanguageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      ungefragt                    = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat1");
      versucheZuVerbinden          = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat2");
      ggstZuAlteVersion            = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat3");
      kannDateiNichtOeffnen        = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat4");
      warteschlange                = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat5");
      keineBrauchbarenParts        = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat6");
      uebertragung                 = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat7");
      nichtGenugPlatz              = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat8");
      fertiggestellt               = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat14");
      keineVerbindungMoeglich      = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.verbindungunmoeglich");
      pausiert                     = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat13");
      position                     = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat51");
      versucheIndirekt             = languageSelector.getFirstAttrbuteByTagName("mainform.queue.userstat10");
      eigenesLimitErreicht         = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.eigeneslimiterreicht");
      indirekteVerbindungAbgelehnt = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.indverbindungabgelehnt");
      warteschlangeVoll            = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.warteschlangevoll");
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
