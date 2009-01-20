/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */
package de.applejuicenet.client.gui.upload;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Level;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.fassade.ApplejuiceFassade;
import de.applejuicenet.client.fassade.entity.Share;
import de.applejuicenet.client.fassade.entity.Upload;
import de.applejuicenet.client.gui.components.GuiController;
import de.applejuicenet.client.gui.components.GuiControllerActionListener;
import de.applejuicenet.client.gui.components.util.Value;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.gui.controller.PositionManager;
import de.applejuicenet.client.gui.controller.PositionManagerImpl;

public class UploadController extends GuiController
{
   private static UploadController instance            = null;
   private static final int        HEADER_DRAGGED      = 0;
   private static final int        HEADER_POPUP        = 1;
   private static final int        TABLE_MOUSE_CLICKED = 2;
   private static final int        TABLE_POPUP         = 3;
   private static final int        COPY_TO_CLIPBOARD   = 4;
   private final UploadPanel       uploadPanel;
   private boolean                 componentSelected   = false;
   private boolean                 initialized         = false;
   private int                     anzahlClients       = 0;
   private String                  clientText;
   private String                  warteschlangeVoll   = "";

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
      uploadPanel.getUploadTable().getTableHeader().addMouseListener(new HeaderPopupListener(this, HEADER_POPUP));
      uploadPanel.getUploadTable().getTableHeader().addMouseMotionListener(new UploadMouseMotionListener(this, HEADER_DRAGGED));
      uploadPanel.getUploadTable().addMouseListener(new UploadTableMouseListener(this, TABLE_MOUSE_CLICKED));
      uploadPanel.getUploadTable().addMouseListener(new UploadTablePopupListener(this, TABLE_POPUP));
      uploadPanel.getMnuCopyToClipboard().addActionListener(new GuiControllerActionListener(this, COPY_TO_CLIPBOARD));
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

         default:
            logger.error("Unregistrierte EventId " + actionId);
      }
   }

   private void headerDragged()
   {
      PositionManager  pm          = PositionManagerImpl.getInstance();
      TableColumnModel columnModel = uploadPanel.getUploadTable().getColumnModel();
      TableColumn[]    columns     = uploadPanel.getTableColumns();

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

   private void tablePopup(MouseEvent e)
   {
      Point p           = e.getPoint();
      int   selectedRow = uploadPanel.getUploadTable().rowAtPoint(p);

      if(selectedRow != -1)
      {
         uploadPanel.getUploadTable().setRowSelectionInterval(selectedRow, selectedRow);

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
      int selected = uploadPanel.getUploadTable().getSelectedRow();

      if(selected == -1)
      {
         return;
      }

      Upload             upload      = uploadPanel.getUploadTableModel().getRow(selected);

      String             shareFileId = upload.getShareFileIDAsString();
      Map<String, Share> share       = AppleJuiceClient.getAjFassade().getShare(false);

      if(share.containsKey(shareFileId))
      {
         Share shareObj = share.get(shareFileId);

         if(share != null)
         {
            Clipboard    cb     = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringBuffer toCopy = new StringBuffer();

            toCopy.append("ajfsp://file|");
            toCopy.append(shareObj.getShortfilename() + "|" + shareObj.getCheckSum() + "|" + shareObj.getSize() + "/");
            StringSelection contents = new StringSelection(toCopy.toString());

            cb.setContents(contents, null);
         }
      }
   }

   private void headerPopup(MouseEvent e)
   {
      TableColumn[]       columns          = uploadPanel.getTableColumns();
      JCheckBoxMenuItem[] columnPopupItems = uploadPanel.getColumnPopupItems();
      TableColumnModel    tableColumnModel = uploadPanel.getUploadTable().getColumnModel();

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

      uploadPanel.getColumnPopup().show(uploadPanel.getUploadTable().getTableHeader(), e.getX(), e.getY());
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
            TableColumnModel headerModel = uploadPanel.getUploadTable().getTableHeader().getColumnModel();
            TableColumn[]    columns     = uploadPanel.getTableColumns();
            int              columnCount = headerModel.getColumnCount();
            PositionManager  pm          = PositionManagerImpl.getInstance();

            if(pm.isLegal())
            {
               int[]                  widths         = pm.getUploadWidths();
               boolean[]              visibilies     = pm.getUploadColumnVisibilities();
               int[]                  indizes        = pm.getUploadColumnIndizes();
               ArrayList<TableColumn> visibleColumns = new ArrayList<TableColumn>();

               for(int i = 0; i < columns.length; i++)
               {
                  columns[i].setPreferredWidth(widths[i]);
                  uploadPanel.getUploadTable().removeColumn(columns[i]);
                  if(visibilies[i])
                  {
                     visibleColumns.add(columns[i]);
                  }
               }

               int pos = -1;

               for(int i = 0; i < visibleColumns.size(); i++)
               {
                  for(int x = 0; x < columns.length; x++)
                  {
                     if(visibleColumns.contains(columns[x]) && indizes[x] == pos + 1)
                     {
                        uploadPanel.getUploadTable().addColumn(columns[x]);
                        pos++;
                        break;
                     }
                  }
               }
            }
            else
            {
               for(int i = 0; i < columnCount; i++)
               {
                  headerModel.getColumn(i).setPreferredWidth(uploadPanel.getUploadTable().getWidth() / columnCount);
               }
            }

            uploadPanel.getUploadTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
         }

         uploadPanel.getUploadTable().updateUI();
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
      //      columnsText[1] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col3caption");
      columnsText[1] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col1caption");
      columnsText[2] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col2caption");
      columnsText[3] = languageSelector.getFirstAttrbuteByTagName("mainform.queue.col6caption");
      columnsText[4] = languageSelector.getFirstAttrbuteByTagName("javagui.uploadform.columnwasserstand");
      columnsText[5] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col4caption");
      columnsText[6] = languageSelector.getFirstAttrbuteByTagName("mainform.uploads.col5caption");
      TableColumn[] columns = uploadPanel.getTableColumns();

      for(int i = 0; i < columns.length; i++)
      {
         columns[i].setHeaderValue(columnsText[i]);
      }

      columns[0].setPreferredWidth(100);
      warteschlangeVoll = languageSelector.getFirstAttrbuteByTagName("javagui.downloadform.warteschlangevoll");
      uploadPanel.getMnuCopyToClipboard().setText(languageSelector.getFirstAttrbuteByTagName("mainform.getlink1.caption"));
   }

   protected void contentChanged(DATALISTENER_TYPE type, final Object content)
   {
      if(!componentSelected)
      {
         return;
      }

      if(type == DATALISTENER_TYPE.UPLOAD_CHANGED)
      {
         SwingUtilities.invokeLater(new Runnable()
            {
               @SuppressWarnings("unchecked")
               public void run()
               {
                  try
                  {
                     uploadPanel.getUploadTableModel().setUploads((Map<String, Upload>) content);
                     if(componentSelected)
                     {
                        uploadPanel.getUploadTable().updateUI();
                     }

                     anzahlClients = uploadPanel.getUploadTableModel().getRowCount();
                     String tmp          = clientText.replaceAll("%d", Integer.toString(anzahlClients));
                     long   maxUploadPos = AppleJuiceClient.getAjFassade().getInformation().getMaxUploadPositions();

                     if(anzahlClients >= maxUploadPos)
                     {
                        tmp += " (" + warteschlangeVoll + ")";
                     }

                     uploadPanel.getUploadListeLabel().setText(tmp);
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
