/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.download.table;

import java.util.Set;

import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.fassade.entity.DownloadSource;
import de.applejuicenet.client.gui.components.tree.WaitNode;
import de.applejuicenet.client.gui.components.treetable.AbstractTreeTableModel;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.gui.components.treetable.TreeTableModel;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.download.table.DownloadMainNode.MainNodeType;
import de.applejuicenet.client.gui.listener.LanguageListener;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/table/Attic/DownloadModel.java,v 1.12 2009/01/12 09:19:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */
public class DownloadModel extends AbstractTreeTableModel implements LanguageListener
{
   static protected String[] cNames = {"", "", "", "", "", "", "", "", "", ""};

   //Download-Stati
   public static String suchen                  = "";
   public static String laden                   = "";
   public static String keinPlatz               = "";
   public static String fertigstellen           = "";
   public static String fehlerBeimFertigstellen = "";
   public static String fertig                  = "";
   public static String abbrechen               = "";
   public static String abgebrochen             = "";
   public static String dataWirdErstellt        = "";

   //Source-Stati
   public static String                                   ungefragt                    = "";
   public static String                                   versucheZuVerbinden          = "";
   public static String                                   ggstZuAlteVersion            = "";
   public static String                                   kannDateiNichtOeffnen        = "";
   public static String                                   warteschlange                = "";
   public static String                                   keineBrauchbarenParts        = "";
   public static String                                   uebertragung                 = "";
   public static String                                   nichtGenugPlatz              = "";
   public static String                                   fertiggestellt               = "";
   public static String                                   keineVerbindungMoeglich      = "";
   public static String                                   pausiert                     = "";
   public static String                                   position                     = "";
   public static String                                   versucheIndirekt             = "";
   public static String                                   warteschlangeVoll            = "";
   public static String                                   eigenesLimitErreicht         = "";
   public static String                                   indirekteVerbindungAbgelehnt = "";
   @SuppressWarnings("unchecked")
   static protected Class[]                               cTypes                       = 
                                                                                         {
                                                                                            TreeTableModel.class, String.class,
                                                                                            String.class, String.class, String.class,
                                                                                            String.class, String.class, String.class,
                                                                                            String.class, String.class
                                                                                         };
   private boolean sort = false;

   public DownloadModel()
   {
      super(new DownloadNode());
      LanguageSelector.getInstance().addLanguageListener(this);
   }

   protected Object[] getChildren(Object node)
   {
      if(!(node instanceof Download) && !(node instanceof DownloadMainNode) && !(node instanceof DownloadSource) &&
            node.getClass() != WaitNode.class)
      {
         Set<Node> children = ((DownloadNode) node).getChildren();

         return (Node[]) children.toArray(new Node[children.size()]);
      }
      else if(node instanceof DownloadMainNode)
      {
         return ((DownloadMainNode) node).getChildren();
      }
      else if(node instanceof Download)
      {
         return ((Download) node).getSources();
      }

      return null;
   }

   public void sortNextRefresh(boolean sortNextRefresh)
   {
      sort = sortNextRefresh;
   }

   public int getChildCount(Object node)
   {
      if(!(node instanceof Download) && !(node instanceof DownloadMainNode) && !(node instanceof DownloadSource) &&
            node.getClass() != WaitNode.class)
      {
         return ((DownloadNode) node).getChildCount();
      }
      else if(node instanceof DownloadMainNode)
      {
         return ((DownloadMainNode) node).getChildCount();
      }
      else if(node instanceof Download)
      {
         return ((Download) node).getSources().length;
      }

      return 0;
   }

   public Object getChild(Object node, int i)
   {
      Object[] obj = getChildren(node);

      if(obj == null || i > obj.length - 1)
      {
         return null;
      }

      return obj[i];
   }

   public int getColumnCount()
   {
      return cNames.length;
   }

   public String getColumnName(int column)
   {
      return cNames[column];
   }

   @SuppressWarnings("unchecked")
   public Class getColumnClass(int column)
   {
      return cTypes[column];
   }

   public Object getValueAt(Object node, int column)
   {
      Object columnValue = null;

      if(node.getClass() == DownloadMainNode.class && ((DownloadMainNode) node).getType() == MainNodeType.ROOT_NODE)
      {
         columnValue = ((DownloadMainNode) node).getDownload();
      }
      else if(node instanceof DownloadSource)
      {
         columnValue = node;
      }

      if(columnValue != null)
      {
         switch(column)
         {

            case 0:
               return DownloadColumnValue.getColumn0(columnValue);

            case 1:
               return DownloadColumnValue.getColumn1(columnValue);

            case 2:
               return DownloadColumnValue.getColumn2(columnValue);

            case 3:
               return DownloadColumnValue.getColumn3(columnValue);

            case 4:
               return DownloadColumnValue.getColumn4(columnValue);

            case 5:
               return DownloadColumnValue.getColumn5(columnValue);

            case 6:
               return DownloadColumnValue.getColumn6(columnValue);

            case 7:
               return DownloadColumnValue.getColumn7(columnValue);

            case 8:
               return DownloadColumnValue.getColumn8(columnValue);

            case 9:
               return DownloadColumnValue.getColumn9(columnValue);

            default:
               return "";
         }
      }

      return "";
   }

   public static String powerdownload(int pwdl)
   {
      if(pwdl == 0)
      {
         return "1:1,0";
      }

      double power = pwdl;

      power = power / 10 + 1;
      String temp = Double.toString(power);

      temp = temp.replace('.', ',');
      return "1:" + temp;
   }

   public static String parseGroesse(long groesse)
   {
      double share  = Double.parseDouble(Long.toString(groesse));
      int    faktor;

      if(share == 0)
      {
         return "";
      }

      if(share < 1024)
      {
         return groesse + " Bytes";
      }
      else if(share / 1024 < 1024)
      {
         faktor = 1024;
      }
      else if(share / 1048576 < 1024)
      {
         faktor = 1048576;
      }
      else if(share / 1073741824 < 1024)
      {
         faktor = 1073741824;
      }
      else
      {
         faktor = 1;
      }

      share = share / faktor;
      String result = Double.toString(share);

      if(result.indexOf('.') != -1 && (result.indexOf('.') + 3 < result.length()))
      {
         result = result.substring(0, result.indexOf('.') + 3);
      }

      result = result.replace('.', ',');
      if(faktor == 1024)
      {
         result += " KB";
      }
      else if(faktor == 1048576)
      {
         result += " MB";
      }
      else if(faktor == 1073741824)
      {
         result += " GB";
      }
      else
      {
         result += " ??";
      }

      return result;
   }

   public static String getSpeedAsString(long speed)
   {
      if(speed == 0)
      {
         return "0 Bytes/s";
      }

      double size   = speed;
      int    faktor = 1;

      if(size < 1024)
      {
         faktor = 1;
      }
      else
      {
         faktor = 1024;

      }

      size = size / faktor;
      String s = Double.toString(size);

      if(s.indexOf(".") + 3 < s.length())
      {
         s = s.substring(0, s.indexOf(".") + 3);
      }

      if(faktor == 1)
      {
         s += " Bytes/s";
      }
      else
      {
         s += " kb/s";
      }

      return s;
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
      suchen                       = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestatlook");
      laden                        = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestattransfer");
      keinPlatz                    = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat1");
      fertigstellen                = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat12");
      fertig                       = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat14");
      abbrechen                    = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat15");
      abgebrochen                  = languageSelector.getFirstAttrbuteByTagName("mainform.queue.queuestat17");
      warteschlangeVoll            = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.warteschlangevoll");
      fehlerBeimFertigstellen      = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.fehlerbeimfertigstellen");
      dataWirdErstellt             = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.datawirderstellt");
   }
}
