package de.applejuicenet.client.gui.tables.upload;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import de.applejuicenet.client.shared.dac.UploadDO;
import javax.swing.JProgressBar;

public class UploadTablePercentCellRenderer
    extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        Object obj = ( (TreeTableModelAdapter) table.getModel()).nodeForRow(row);

        if (obj.getClass()==MainNode.class){
            return super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        }
        else{
            UploadDO uploadDO = (UploadDO) obj;
            Color background = table.getBackground();
            Color foreground = table.getForeground();
            if (uploadDO.getStatus()==UploadDO.AKTIVE_UEBERTRAGUNG){
                String prozent = uploadDO.getDownloadPercentAsString();
                JProgressBar progress = new JProgressBar(JProgressBar.
                    HORIZONTAL, 0,
                    100);
                int pos = prozent.indexOf('.');
                String balken = prozent;
                if (pos != -1) {
                    balken = balken.substring(0, pos);
                }
                progress.setValue(Integer.parseInt(balken));
                progress.setString(prozent + " %");
                progress.setStringPainted(true);
                return progress;
            }
            else{
                JLabel label1 = new JLabel();
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
        }
    }
}