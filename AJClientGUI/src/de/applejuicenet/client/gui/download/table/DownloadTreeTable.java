package de.applejuicenet.client.gui.download.table;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JToolTip;

import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.components.treetable.TreeTableModel;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.Settings;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

public class DownloadTreeTable extends JTreeTable {

	public DownloadTreeTable(TreeTableModel treeTableModel, DefaultTreeTableCellRenderer treeTableCellRenderer) {
        super(treeTableModel, treeTableCellRenderer);
    }

    public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip(new DownloadToolTipUI());
        tip.setComponent(this);
        return tip;
    }

    public String getToolTipText(MouseEvent me){
        if (!Settings.getSettings().isToolTipEnabled()) {
            return null;
        }
        Point p = me.getPoint();
        int column = columnAtPoint(p);
        if (column == 0){
            int row = rowAtPoint(p);
            Object value = ( (TreeTableModelAdapter) getModel()).
                nodeForRow(row);
            if (value == null){
            	return null;
            }
            else if (value.getClass() == DownloadSourceDO.class) {
                return getToolTipForDownloadSourceDO( (DownloadSourceDO) value);
            }
            else if (value.getClass() == DownloadMainNode.class
                &&
                ( (DownloadMainNode) value).getType() ==
                DownloadMainNode.ROOT_NODE) {
                return getToolTipForDownloadDO(( (DownloadMainNode) value).getDownloadDO());
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    private String getToolTipForDownloadDO(DownloadDO downloadDO){
        String[] columns = DownloadMainNode.getColumnTitles();
        StringBuffer toolTip = new StringBuffer();
        toolTip.append(downloadDO.getColumn0());
        toolTip.append("| |" + columns[1] + ": ");
        toolTip.append(downloadDO.getColumn1());
        if (downloadDO.getStatus()==DownloadDO.SUCHEN_LADEN ||
            downloadDO.getStatus()==DownloadDO.PAUSIERT){
            toolTip.append("|" + columns[2] + ": ");
            toolTip.append(downloadDO.getColumn2());
            toolTip.append("|" + columns[3] + ": ");
            toolTip.append(downloadDO.getColumn3());
            if (downloadDO.getStatus()==DownloadDO.SUCHEN_LADEN ){
                toolTip.append("|" + columns[4] + ": ");
                toolTip.append(downloadDO.getColumn4());
                toolTip.append("|" + columns[5] + ": ");
                toolTip.append(downloadDO.getColumn5());
            }
            toolTip.append("|" + columns[6] + ": ");
            toolTip.append(downloadDO.getProzentGeladenAsString());
            toolTip.append("|" + columns[7] + ": ");
            toolTip.append(downloadDO.getColumn7());
            toolTip.append("|" + columns[8] + ": ");
            toolTip.append(downloadDO.getColumn8());
        }
        return toolTip.toString();
    }

    private String getToolTipForDownloadSourceDO(DownloadSourceDO downloadSourceDO){
        String[] columns = DownloadMainNode.getColumnTitles();
        StringBuffer toolTip = new StringBuffer();
        toolTip.append(downloadSourceDO.getColumn0());
        toolTip.append("| |" + columns[1] + ": ");
        toolTip.append(downloadSourceDO.getColumn1());
        toolTip.append("|" + columns[2] + ": ");
        toolTip.append(downloadSourceDO.getColumn2());
        toolTip.append("|" + columns[3] + ": ");
        toolTip.append(downloadSourceDO.getColumn3());
        toolTip.append("|" + columns[4] + ": ");
        toolTip.append(downloadSourceDO.getColumn4());
        toolTip.append("|" + columns[5] + ": ");
        toolTip.append(downloadSourceDO.getColumn5());
        toolTip.append("|" + columns[6] + ": ");
        toolTip.append(downloadSourceDO.getDownloadPercentAsString());
        toolTip.append("|" + columns[7] + ": ");
        toolTip.append(downloadSourceDO.getColumn7());
        toolTip.append("|" + columns[8] + ": ");
        toolTip.append(downloadSourceDO.getColumn8());
        toolTip.append("|" + columns[9] + ": ");
        toolTip.append(downloadSourceDO.getColumn9());
        return toolTip.toString();
    }
    
}
