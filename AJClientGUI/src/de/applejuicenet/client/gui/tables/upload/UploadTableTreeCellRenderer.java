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
import javax.swing.Icon;
import de.applejuicenet.client.gui.tables.download.IconGetter;

public class UploadTableTreeCellRenderer
    extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column) {
        Object obj = ( (TreeTableModelAdapter) table.getModel()).nodeForRow(row);
        Color background = table.getBackground();
        Color foreground = table.getForeground();
        if (obj.getClass() == MainNode.class) {
            JPanel returnPanel = new JPanel(new BorderLayout());
            JLabel image = new JLabel();
            JLabel text = new JLabel();
            if (isSelected) {
                returnPanel.setBackground(table.getSelectionBackground());
                returnPanel.setForeground(table.getSelectionForeground());
                image.setBackground(table.getSelectionBackground());
                text.setBackground(table.getSelectionBackground());
                image.setForeground(table.getSelectionForeground());
                text.setBackground(table.getSelectionForeground());
            }
            else {
                returnPanel.setBackground(background);
                returnPanel.setForeground(foreground);
                image.setBackground(table.getBackground());
                text.setBackground(table.getBackground());
                image.setForeground(table.getForeground());
                text.setBackground(table.getForeground());
            }
            image.setIcon( ( (MainNode) obj).getConvenientIcon());
            text.setText(" " + (String) value);
            text.setFont(table.getFont());
            returnPanel.add(image, BorderLayout.WEST);
            returnPanel.add(text, BorderLayout.CENTER);
            return returnPanel;
        }
        else {
            UploadDO uploadDO = (UploadDO) obj;
            JLabel text = new JLabel();
            text.setOpaque(true);
            if (isSelected) {
                text.setBackground(table.getSelectionBackground());
            }
            else {
                text.setBackground(table.getBackground());
            }
            text.setText("   " + (String) value);
            text.setFont(table.getFont());
            Icon icon = IconGetter.getConvenientIcon(uploadDO);
            if (icon != null) {
                text.setIcon(icon);
            }
            return text;
        }
    }
}
