package de.applejuicenet.client.gui.tables.upload;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Component;
import javax.swing.JTable;
import de.applejuicenet.client.gui.tables.TreeTableModelAdapter;
import java.awt.Color;
import de.applejuicenet.client.shared.dac.UploadDO;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;

public class UploadTableVersionCellRenderer extends DefaultTableCellRenderer {
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
            JPanel returnPanel = new JPanel(new BorderLayout());
            JLabel image = new JLabel();

            JLabel serverName = new JLabel();

            if (isSelected) {
                returnPanel.setBackground(table.getSelectionBackground());
                returnPanel.setForeground(table.getSelectionForeground());
                image.setBackground(table.getSelectionBackground());
                serverName.setBackground(table.getSelectionBackground());
                image.setForeground(table.getSelectionForeground());
                serverName.setBackground(table.getSelectionForeground());
            }
            else {
                returnPanel.setBackground(background);
                returnPanel.setForeground(foreground);
                image.setBackground(table.getBackground());
                serverName.setBackground(table.getBackground());
                image.setForeground(table.getForeground());
                serverName.setBackground(table.getForeground());
            }

            if (uploadDO.getVersion() == null) {
                return returnPanel;
            }
            else {
                image.setIcon(uploadDO.getVersion().getVersionIcon());
            }
            serverName.setText("  " + uploadDO.getVersion().getVersion());
            serverName.setFont(table.getFont());
            returnPanel.add(image, BorderLayout.WEST);
            returnPanel.add(serverName, BorderLayout.CENTER);
            return returnPanel;
        }
    }
}
