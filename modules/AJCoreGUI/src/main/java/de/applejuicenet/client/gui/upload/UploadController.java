/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.upload;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import de.applejuicenet.client.shared.ReleaseInfo;
import org.apache.log4j.Level;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.table.HeaderListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;

public class UploadController extends GuiController
{
   private static UploadController instance               = null;
   private static final int        HEADER_DRAGGED         = 0;
   private static final int        HEADER_POPUP           = 1;
   private static final int        TABLE_MOUSE_CLICKED    = 2;
   private static final int        TABLE_POPUP            = 3;
   private static final int        COPY_TO_CLIPBOARD      = 4;
   private static final int        HEADER_WAITING_DRAGGED = 8;
   private static final int        HEADER_WAITING_POPUP   = 9;
   private static final int        RELEASE_INFO           = 18;
   private final UploadPanel       uploadPanel;
   private boolean                 componentSelected      = false;
   private boolean                 initialized            = false;
   private int                     anzahlClients          = 0;
   private String                  clientText;
   private String                  warteschlangeVoll      = "";

   private UploadController()
   {
      super();
      uploadPanel = new UploadPanel(this);
      try
      {
         init();
         LanguageSelector.getInstance().addLanguageListener(this);
         AppleJuiceClient.getAjFassade().addDataUpdateListener(this, DATALISTENER_TYPE.UPLOAD_CHANGED);
      }
      catch(Exception e)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
         }
      }
   }

   public static synchronized UploadController getInstance()
   {
      if(null == instance)
      {
         instance = new UploadController();
      }

      return instance;
   }

   public Value[] getCustomizedValues()
   {
      return null;
   }

   private void init()
   {
      uploadPanel.getUploadActiveTable().getTableHeader().addMouseListener(new HeaderPopupListener(this, HEADER_POPUP));
      uploadPanel.getUploadActiveTable().getTableHeader().addMouseMotionListener(new UploadMouseMotionListener(this, HEADER_DRAGGED));
      uploadPanel.getUploadActiveTable().addMouseListener(new UploadTableMouseListener(this, TABLE_MOUSE_CLICKED));
      uploadPanel.getUploadActiveTable().addMouseListener(new UploadTablePopupListener(this, TABLE_POPUP));
      uploadPanel.getMnuCopyToClipboard().addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
      uploadPanel.getMnuReleaseInfo().addActionListener(new GuiControllerActionListener(this, RELEASE_INFO));

      uploadPanel.getUploadWaitingTable().getTableHeader().addMouseListener(new HeaderPopupListener(this, HEADER_WAITING_POPUP));
      uploadPanel.getUploadWaitingTable().getTableHeader()
      .addMouseMotionListener(new UploadMouseMotionListener(this, HEADER_WAITING_DRAGGED));
   }

   public void fireAction(int actionId, Object obj)
   {
      switch(actionId)
      {

         case HEADER_DRAGGED:
         {
            headerDragged();
            break;
         }

         case HEADER_POPUP:
         {
            headerPopup((MouseEvent) obj);
            break;
         }

         case HEADER_WAITING_DRAGGED:
         {
            headerWaitingDragged();
            break;
         }

         case HEADER_WAITING_POPUP:
         {
            headerWaitingPopup((MouseEvent) obj);
            break;
         }

         case TABLE_MOUSE_CLICKED:
         {
            tableMouseClicked((MouseEvent) obj);
            break;
         }

         case TABLE_POPUP:
         {
            tablePopup((MouseEvent) obj);
            break;
         }

         case COPY_TO_CLIPBOARD:
         {
            copyLinkToClipboard();
            break;
         }

         case RELEASE_INFO:
         {
            showReleaseInfo();
            break;
         }

         default:
            logger.error("Unregistrierte EventId " + actionId);
      }
   }

   private void showReleaseInfo()
   {
      int selected = uploadPanel.getUploadActiveTable().getSelectedRow();

      if(selected == -1)
      {
         return;
      }

      Share shareObj = getShareObject4SelectedRow(selected);

      if(shareObj != null)
      {
         ReleaseInfo.handle(shareObj.getShortfilename(), shareObj.getCheckSum(), shareObj.getSize());
      }
   }

   private Share getShareObject4SelectedRow(int selected) {
      Upload upload = uploadPanel.getUploadActiveTableModel().getRow(selected);

      Integer             shareFileId = upload.getShareFileID();
      Map<Integer, Share> share       = AppleJuiceClient.getAjFassade().getShare(false);

      if(share.containsKey(shareFileId))
      {
         return share.get(shareFileId);
      }

      return null;
   }

   private void headerDragged()
   {
      PositionManager  pm          = PositionManagerImpl.getInstance();
      TableColumnModel columnModel = uploadPanel.getUploadActiveTable().getColumnModel();
      TableColumn[]    columns     = uploadPanel.getTableActiveColumns();

      for(int i = 0; i < columns.length; i++)
      {
         try
         {
            pm.setUploadColumnIndex(i, columnModel.getColumnIndex(columns[i].getIdentifier()));
         }
         catch(IllegalArgumentException niaE)
         {
            ;

            //nix zu tun
         }
      }
   }

   private void headerWaitingDragged()
   {
      PositionManager  pm          = PositionManagerImpl.getInstance();
      TableColumnModel columnModel = uploadPanel.getUploadWaitingTable().getColumnModel();
      TableColumn[]    columns     = uploadPanel.getTableWaitingColumns();

      for(int i = 0; i < columns.length; i++)
      {
         try
         {
            pm.setUploadWaitingColumnIndex(i, columnModel.getColumnIndex(columns[i].getIdentifier()));
         }
         catch(IllegalArgumentException niaE)
         {
            ;

            //nix zu tun
         }
      }
   }

   private void tablePopup(MouseEvent e)
   {
      Point p           = e.getPoint();
      int   selectedRow = uploadPanel.getUploadActiveTable().rowAtPoint(p);

      if(selectedRow != -1)
      {
         uploadPanel.getUploadActiveTable().setRowSelectionInterval(selectedRow, selectedRow);

         //         Object selectedItem = ((TreeTableModelAdapter) uploadPanel.getTable().getModel()).nodeForRow(selectedRow);
         //
         //         if(selectedItem instanceof Upload)
         //         {
         //            uploadPanel.getPopup().show(uploadPanel.getTable(), e.getX(), e.getY());
         //         }
      }
   }

   private void tableMouseClicked(MouseEvent e)
   {
      Point p = e.getPoint();

      //      if(uploadPanel.getTable().columnAtPoint(p) != 0)
      //      {
      //         int selectedRow = uploadPanel.getTable().rowAtPoint(p);
      //
      //         if(e.getClickCount() == 2)
      //         {
      //            ((TreeTableModelAdapter) uploadPanel.getTable().getModel()).expandOrCollapseRow(selectedRow);
      //         }
      //      }
   }

   private void copyLinkToClipboard()
   {
      int selected = uploadPanel.getUploadActiveTable().getSelectedRow();

      if(selected == -1)
      {
         return;
      }

      Share shareObj = getShareObject4SelectedRow(selected);

      if(shareObj != null) {
         Clipboard    cb     = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringBuffer toCopy = new StringBuffer();

         toCopy.append(shareObj.getAjfspLink());
         StringSelection contents = new StringSelection(toCopy.toString());
         cb.setContents(contents, null);
      }
   }

   private void headerPopup(MouseEvent e)
   {
      TableColumn[]       columns          = uploadPanel.getTableActiveColumns();
      JCheckBoxMenuItem[] columnPopupItems = uploadPanel.getColumnActivePopupItems();
      TableColumnModel    tableColumnModel = uploadPanel.getUploadActiveTable().getColumnModel();

      for(int i = 1; i < columns.length; i++)
      {
         try
         {
            tableColumnModel.getColumnIndex(columns[i].getIdentifier());
            columnPopupItems[i].setSelected(true);
         }
         catch(IllegalArgumentException niaE)
         {
            columnPopupItems[i].setSelected(false);
         }
      }

      uploadPanel.getColumnActivePopup().show(uploadPanel.getUploadActiveTable().getTableHeader(), e.getX(), e.getY());
   }

   private void headerWaitingPopup(MouseEvent e)
   {
      TableColumn[]       columns          = uploadPanel.getTableWaitingColumns();
      JCheckBoxMenuItem[] columnPopupItems = uploadPanel.getColumnWaitingPopupItems();
      TableColumnModel    tableColumnModel = uploadPanel.getUploadWaitingTable().getColumnModel();

      for(int i = 1; i < columns.length; i++)
      {
         try
         {
            tableColumnModel.getColumnIndex(columns[i].getIdentifier());
            columnPopupItems[i].setSelected(true);
         }
         catch(IllegalArgumentException niaE)
         {
            columnPopupItems[i].setSelected(false);
         }
      }

      uploadPanel.getColumnWaitingPopup().show(uploadPanel.getUploadWaitingTable().getTableHeader(), e.getX(), e.getY());
   }

   public JComponent getComponent()
   {
      return uploadPanel;
   }

   public void componentSelected()
   {
      try
      {
         componentSelected = true;
         if(!initialized)
         {
            initialized = true;
            TableColumnModel headerModelActive  = uploadPanel.getUploadActiveTable().getTableHeader().getColumnModel();
            TableColumnModel headerModelWaiting = uploadPanel.getUploadWaitingTable().getTableHeader().getColumnModel();

            TableColumn[]    columnsActive      = uploadPanel.getTableActiveColumns();
            int              columnCountActive  = headerModelActive.getColumnCount();
            int[]            uploadActiveSort   = null;
            TableColumn[]    columnsWaiting     = uploadPanel.getTableWaitingColumns();
            int              columnCountWaiting = headerModelWaiting.getColumnCount();
            PositionManager  pm                 = PositionManagerImpl.getInstance();
            int[]            uploadWaitingSort  = null;

            if(pm.isLegal())
            {
               int[]     uploadActiveWidths     = pm.getUploadWidths();
               boolean[] uploadActiveVisibilies = pm.getUploadColumnVisibilities();
               int[]     indizesActive          = pm.getUploadColumnIndizes();

               uploadActiveSort = pm.getUploadSort();
               ArrayList<TableColumn> visibleColumnsActive = new ArrayList<TableColumn>();

               for(int i = 0; i < columnsActive.length; i++)
               {
                  columnsActive[i].setPreferredWidth(uploadActiveWidths[i]);
                  uploadPanel.getUploadActiveTable().removeColumn(columnsActive[i]);
                  if(uploadActiveVisibilies[i])
                  {
                     visibleColumnsActive.add(columnsActive[i]);
                  }
               }

               int pos = -1;

               for(int i = 0; i < visibleColumnsActive.size(); i++)
               {
                  for(int x = 0; x < columnsActive.length; x++)
                  {
                     if(visibleColumnsActive.contains(columnsActive[x]) && indizesActive[x] == pos + 1)
                     {
                        uploadPanel.getUploadActiveTable().addColumn(columnsActive[x]);
                        pos++;
                        break;
                     }
                  }
               }

               int[]     uploadWaitingWidths     = pm.getUploadWaitingWidths();
               boolean[] uploadWaitingVisibilies = pm.getUploadWaitingColumnVisibilities();
               int[]     indizesWaiting          = pm.getUploadWaitingColumnIndizes();

               uploadWaitingSort = pm.getUploadWaitingSort();
               ArrayList<TableColumn> visibleColumnsWaiting = new ArrayList<TableColumn>();

               for(int i = 0; i < columnsWaiting.length; i++)
               {
                  columnsWaiting[i].setPreferredWidth(uploadWaitingWidths[i]);
                  uploadPanel.getUploadWaitingTable().removeColumn(columnsWaiting[i]);
                  if(uploadWaitingVisibilies[i])
                  {
                     visibleColumnsWaiting.add(columnsWaiting[i]);
                  }
               }

               pos = -1;

               for(int i = 0; i < visibleColumnsWaiting.size(); i++)
               {
                  for(int x = 0; x < columnsWaiting.length; x++)
                  {
                     if(visibleColumnsWaiting.contains(columnsWaiting[x]) && indizesWaiting[x] == pos + 1)
                     {
                        uploadPanel.getUploadWaitingTable().addColumn(columnsWaiting[x]);
                        pos++;
                        break;
                     }
                  }
               }
            }
            else
            {
               for(int i = 0; i < columnCountActive; i++)
               {
                  headerModelActive.getColumn(i).setPreferredWidth(uploadPanel.getUploadActiveTable().getWidth() / columnCountActive);
               }

               for(int i = 0; i < columnCountWaiting; i++)
               {
                  headerModelWaiting.getColumn(i)
                  .setPreferredWidth(uploadPanel.getUploadWaitingTable().getWidth() / columnCountWaiting);
               }
            }

            uploadPanel.getUploadActiveTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            uploadPanel.getUploadWaitingTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            if(null != uploadActiveSort)
            {
               for(MouseListener curMl : uploadPanel.getUploadActiveTable().getTableHeader().getMouseListeners())
               {
                  if(curMl instanceof HeaderListener)
                  {
                     ((HeaderListener) curMl).sort(uploadActiveSort[0], uploadActiveSort[1] == 1);
                  }
               }
            }

            if(null != uploadWaitingSort)
            {
               for(MouseListener curMl : uploadPanel.getUploadWaitingTable().getTableHeader().getMouseListeners())
               {
                  if(curMl instanceof HeaderListener)
                  {
                     ((HeaderListener) curMl).sort(uploadWaitingSort[0], uploadWaitingSort[1] == 1);
                  }
               }
            }
         }

         uploadPanel.getUploadActiveTable().updateUI();
         uploadPanel.getUploadWaitingTable().updateUI();
      }
      catch(Exception ex)
      {
         if(logger.isEnabledFor(Level.ERROR))
         {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
         }
      }
   }

   public void componentLostSelection()
   {
      componentSelected = false;
   }

   protected void languageChanged()
   {
      LanguageSelector languageSelector = LanguageSelector.getInstance();

      clientText = languageSelector.getFirstAttrbuteByTagName("mainform.uplcounttext");
      uploadPanel.getUploadListeLabel().setText(clientText.replaceAll("%d", Integer.toString(anzahlClients)));
      String[] columnsText = new String[7];

      columnsText[0] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col0caption");
      columnsText[1] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col1caption");
      columnsText[2] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col2caption");
      columnsText[3] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col6caption");
      columnsText[4] = languageSelector.getFirstAttrbuteByTagName("javagui.uploadform.columnwasserstand");
      columnsText[5] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col4caption");
      columnsText[6] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col5caption");
      TableColumn[] columns = uploadPanel.getTableActiveColumns();

      for(int i = 0; i < columns.length; i++)
      {
         columns[i].setHeaderValue(columnsText[i]);
      }

      columns[0].setPreferredWidth(100);

      columnsText = new String[7];

      columnsText[0] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col0caption");
      columnsText[1] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col3caption");
      columnsText[2] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col1caption");
      columnsText[3] = languageSelector.getFirstAttrbuteByTagName("javagui.uploadform.columnwasserstand");
      columnsText[4] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col4caption");
      columnsText[5] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.colletzteverbindung");
      columnsText[6] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col5caption");
      columns        = uploadPanel.getTableWaitingColumns();

      for(int i = 0; i < columns.length; i++)
      {
         columns[i].setHeaderValue(columnsText[i]);
      }

      columns[0].setPreferredWidth(100);

      warteschlangeVoll = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.warteschlangevoll");
      uploadPanel.getMnuCopyToClipboard().setText(languageSelector.getFirstAttrbuteByTagName("mainform.getlink1.caption"));
      uploadPanel.getMnuReleaseInfo().setText(languageSelector.getFirstAttrbuteByTagName("releaseinfo.menu"));
   }

   protected void contentChanged(DATALISTENER_TYPE type, final Object content)
   {
      if(type == DATALISTENER_TYPE.UPLOAD_CHANGED)
      {
         SwingUtilities.invokeLater(new Runnable()
            {
               @SuppressWarnings("unchecked")
               public void run()
               {
                  try
                  {
                     boolean change = uploadPanel.getUploadActiveTableModel().setUploads((Map<Integer, Upload>) content);

                     if(uploadPanel.getUploadWaitingTableModel().setUploads((Map<Integer, Upload>) content))
                     {
                        change = true;
                     }

                     anzahlClients = uploadPanel.getUploadActiveTableModel().getRowCount() +
                                     uploadPanel.getUploadWaitingTableModel().getRowCount();
                     String tmp          = clientText.replaceAll("%d", Integer.toString(anzahlClients));
                     long   maxUploadPos = AppleJuiceClient.getAjFassade().getInformation().getMaxUploadPositions();

                     if(anzahlClients >= maxUploadPos)
                     {
                        tmp += " (" + warteschlangeVoll + ")";
                     }

                     uploadPanel.getUploadListeLabel().setText(tmp);
                     if(componentSelected && change)
                     {
                        uploadPanel.getUploadActiveTableModel().forceResort();
                        uploadPanel.getUploadActiveTable().updateUI();
                        uploadPanel.getUploadWaitingTableModel().forceResort();
                        uploadPanel.getUploadWaitingTable().updateUI();
                     }
                  }
                  catch(Exception ex)
                  {
                     if(logger.isEnabledFor(Level.ERROR))
                     {
                        logger.error(ApplejuiceFassade.ERROR_MESSAGE, ex);
                     }
                  }
               }
            });
      }
   }
}
