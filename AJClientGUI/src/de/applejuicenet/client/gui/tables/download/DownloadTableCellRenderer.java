package de.applejuicenet.client.gui.tables.download;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import de.applejuicenet.client.shared.*;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.dac.*;
import de.applejuicenet.client.gui.listener.DataUpdateListener;
import de.applejuicenet.client.gui.controller.OptionsManager;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/tables/download/Attic/DownloadTableCellRenderer.java,v 1.12 2003/09/02 16:06:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: DownloadTableCellRenderer.java,v $
 * Revision 1.12  2003/09/02 16:06:26  maj0r
 * Downloadbaum komplett umgebaut.
 *
 * Revision 1.11  2003/08/16 17:50:42  maj0r
 * Diverse Farben können nun manuell eingestellt bzw. deaktiviert werden.
 * DownloaduebersichtTabelle kann deaktiviert werden.
 *
 * Revision 1.10  2003/08/11 19:42:51  maj0r
 * Fertig-Status-Farbe korrigiert.
 *
 * Revision 1.9  2003/08/11 14:42:13  maj0r
 * Versions-Icon-Beschaffung in die Klasse Version verschoben.
 *
 * Revision 1.8  2003/08/10 21:08:18  maj0r
 * Diverse Änderungen.
 *
 * Revision 1.7  2003/08/09 10:56:38  maj0r
 * DownloadTabelle weitergeführt.
 *
 * Revision 1.6  2003/07/06 20:00:19  maj0r
 * DownloadTable bearbeitet.
 *
 * Revision 1.5  2003/07/04 06:43:51  maj0r
 * Diverse Änderungen am DownloadTableModel.
 *
 * Revision 1.4  2003/07/03 19:11:16  maj0r
 * DownloadTable überarbeitet.
 *
 * Revision 1.3  2003/07/02 13:54:34  maj0r
 * JTreeTable komplett überarbeitet.
 *
 * Revision 1.2  2003/07/01 18:41:39  maj0r
 * Struktur verändert.
 *
 * Revision 1.1  2003/07/01 18:34:28  maj0r
 * Struktur verändert.
 *
 * Revision 1.4  2003/06/10 12:31:03  maj0r
 * Historie eingefügt.
 *
 *
 */

public class DownloadTableCellRenderer
    implements TableCellRenderer, DataUpdateListener {

    private Settings settings;

    public DownloadTableCellRenderer(){
        super();
        settings = Settings.getSettings();
        OptionsManager.getInstance().addSettingsListener(this);
    }

  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column) {
    Object node = ( (TreeTableModelAdapter) table.getModel()).nodeForRow(row);
    if (node.getClass()==DownloadSourceDO.class)
        return getComponentForSource((DownloadSourceDO)node, table, value, isSelected, hasFocus, row, column);
    else if (node.getClass()==DownloadMainNode.class)
        return getComponentForDownload((DownloadMainNode)node, table, value, isSelected, hasFocus, row, column);
    else if (node.getClass()==DownloadDirectoryNode.class)
        return getComponentForDirectory((DownloadDirectoryNode)node, table, value, isSelected, hasFocus, row, column);
    else
        return new JLabel("");
  }

  public Component getComponentForDownload(DownloadMainNode downloadMainNode, JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus,
                                                 int row,
                                                 int column){
    DownloadDO downloadDO = downloadMainNode.getDownloadDO();
    if (column == 6 && downloadMainNode.getType()==DownloadMainNode.ROOT_NODE) {
        String prozent = downloadDO.getProzentGeladenAsString();
        String wert = null;
        int i;
        if ( (i = prozent.indexOf(".")) != -1) {
          wert = prozent.substring(0, i);
        }
        else{
          wert = prozent;
        }
        JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                                                 100);
        progress.setValue(Integer.parseInt(wert));
        progress.setString(prozent + " %");
        progress.setStringPainted(true);
        return progress;
      }
    else if(column!=6 && downloadMainNode.getType()!=DownloadMainNode.ROOT_NODE){
        JLabel label1 = new JLabel();
        label1.setOpaque(true);
        if (isSelected) {
          label1.setBackground(table.getSelectionBackground());
          label1.setForeground(table.getSelectionForeground());
        }
        else {
            if (downloadDO.getStatus()==DownloadDO.FERTIG && settings.isFarbenAktiv())
              label1.setBackground(settings.getDownloadFertigHintergrundColor());
            else
              label1.setBackground(table.getBackground());
            label1.setForeground(table.getForeground());
        }
        return label1;
    }
    else {
      JLabel label1 = new JLabel();
      label1.setOpaque(true);
      label1.setFont(table.getFont());
      label1.setText((String)value);
      if (isSelected) {
        label1.setBackground(table.getSelectionBackground());
        label1.setForeground(table.getSelectionForeground());
      }
      else {
          if (downloadDO.getStatus()==DownloadDO.FERTIG && settings.isFarbenAktiv())
            label1.setBackground(settings.getDownloadFertigHintergrundColor());
          else
            label1.setBackground(table.getBackground());
          label1.setForeground(table.getForeground());
      }
      return label1;
    }
  }

    public Component getComponentForSource(DownloadSourceDO downloadSourceDO, JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column){
        Color foreground = table.getForeground();
        if (column == 6) {
            String prozent = downloadSourceDO.getDownloadPercentAsString();
            JProgressBar progress = new JProgressBar(JProgressBar.HORIZONTAL, 0,
                                                     100);
            int pos = prozent.indexOf('.');
            String balken = prozent;
            if (pos!=-1)
                balken = balken.substring(0, pos);
            progress.setValue(Integer.parseInt(balken));
            progress.setString(prozent + " %");
            progress.setStringPainted(true);
            return progress;
          }
        else if (column == 9) {
          JPanel returnPanel = new JPanel(new BorderLayout());
          JLabel image = new JLabel();

          JLabel versionText = new JLabel();

          if (isSelected) {
            returnPanel.setBackground(table.getSelectionBackground());
            returnPanel.setForeground(table.getSelectionForeground());
            image.setBackground(table.getSelectionBackground());
            versionText.setBackground(table.getSelectionBackground());
            image.setForeground(table.getSelectionForeground());
            versionText.setBackground(table.getSelectionForeground());
          }
          else {
            if (settings.isFarbenAktiv()){
                returnPanel.setBackground(settings.getQuelleHintergrundColor());
                returnPanel.setForeground(foreground);
            }
            else{
                returnPanel.setBackground(table.getBackground());
                returnPanel.setForeground(table.getForeground());
            }
            image.setBackground(table.getBackground());
            versionText.setBackground(table.getBackground());
            image.setForeground(table.getForeground());
            versionText.setBackground(table.getForeground());
          }

          if (downloadSourceDO.getVersion() == null) {
            return returnPanel;
          }
          else {
            image.setIcon(downloadSourceDO.getVersion().getVersionIcon());
          }
          versionText.setText("  " + downloadSourceDO.getVersion().getVersion());
          versionText.setFont(table.getFont());
          returnPanel.add(image, BorderLayout.WEST);
          returnPanel.add(versionText, BorderLayout.CENTER);
          return returnPanel;
        }
      else {
        JLabel label1 = new JLabel();
        label1.setText((String)value);
        label1.setFont(table.getFont());
        label1.setOpaque(true);
        if (isSelected) {
          label1.setBackground(table.getSelectionBackground());
          label1.setForeground(table.getSelectionForeground());
        }
        else {
          if (settings.isFarbenAktiv())
              label1.setBackground(settings.getQuelleHintergrundColor());
          else
              label1.setBackground(table.getBackground());
          label1.setForeground(foreground);
        }
        return label1;
      }
    }

    public Component getComponentForDirectory(DownloadDirectoryNode node, JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column){
        JLabel label1 = new JLabel();
        label1.setText((String)value);
        label1.setFont(table.getFont());
        label1.setOpaque(true);
        if (isSelected) {
          label1.setBackground(table.getSelectionBackground());
          label1.setForeground(table.getSelectionForeground());
        }
        else {
          label1.setBackground(table.getBackground());
          label1.setForeground(table.getForeground());
        }
        return label1;
    }

    public void fireContentChanged(int type, Object content) {
        if (type == DataUpdateListener.SETTINGS_CHANGED){
            settings = (Settings) content;
        }
    }
}