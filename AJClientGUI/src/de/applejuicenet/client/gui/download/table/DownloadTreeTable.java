package de.applejuicenet.client.gui.download.table;

import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JToolTip;

import de.applejuicenet.client.fassade.controller.dac.DownloadSourceDO;
import de.applejuicenet.client.fassade.entity.Download;
import de.applejuicenet.client.gui.components.treetable.DefaultTreeTableCellRenderer;
import de.applejuicenet.client.gui.components.treetable.JTreeTable;
import de.applejuicenet.client.gui.components.treetable.TreeTableModel;
import de.applejuicenet.client.gui.components.treetable.TreeTableModelAdapter;
import de.applejuicenet.client.shared.MultiLineToolTip;
import de.applejuicenet.client.shared.Settings;

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
                return getToolTipForDownloadDO(( (DownloadMainNode) value).getDownload());
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    private String getToolTipForDownloadDO(Download download){
        String[] columns = DownloadMainNode.getColumnTitles();
        StringBuffer toolTip = new StringBuffer();
        toolTip.append(DownloadColumnValue.getColumn0(download));
        toolTip.append("| |" + columns[1] + ": ");
        toolTip.append(DownloadColumnValue.getColumn1(download));
        if (download.getStatus()==Download.SUCHEN_LADEN ||
            download.getStatus()==Download.PAUSIERT){
            toolTip.append("|" + columns[2] + ": ");
            toolTip.append(DownloadColumnValue.getColumn2(download));
            toolTip.append("|" + columns[3] + ": ");
            toolTip.append(DownloadColumnValue.getColumn3(download));
            if (download.getStatus()==Download.SUCHEN_LADEN ){
                toolTip.append("|" + columns[4] + ": ");
                toolTip.append(DownloadColumnValue.getColumn4(download));
                toolTip.append("|" + columns[5] + ": ");
                toolTip.append(DownloadColumnValue.getColumn5(download));
            }
            toolTip.append("|" + columns[6] + ": ");
            toolTip.append(download.getProzentGeladenAsString());
            toolTip.append("|" + columns[7] + ": ");
            toolTip.append(DownloadColumnValue.getColumn7(download));
            toolTip.append("|" + columns[8] + ": ");
            toolTip.append(DownloadColumnValue.getColumn8(download));
        }
        return toolTip.toString();
    }

    private String getToolTipForDownloadSourceDO(DownloadSourceDO downloadSourceDO){
        String[] columns = DownloadMainNode.getColumnTitles();
        StringBuffer toolTip = new StringBuffer();
        toolTip.append(DownloadColumnValue.getColumn0(downloadSourceDO));
        toolTip.append("| |" + columns[1] + ": ");
        toolTip.append(DownloadColumnValue.getColumn1(downloadSourceDO));
        toolTip.append("|" + columns[2] + ": ");
        toolTip.append(DownloadColumnValue.getColumn2(downloadSourceDO));
        toolTip.append("|" + columns[3] + ": ");
        toolTip.append(DownloadColumnValue.getColumn3(downloadSourceDO));
        toolTip.append("|" + columns[4] + ": ");
        toolTip.append(DownloadColumnValue.getColumn4(downloadSourceDO));
        toolTip.append("|" + columns[5] + ": ");
        toolTip.append(DownloadColumnValue.getColumn5(downloadSourceDO));
        toolTip.append("|" + columns[6] + ": ");
        toolTip.append(downloadSourceDO.getDownloadPercentAsString());
        toolTip.append("|" + columns[7] + ": ");
        toolTip.append(DownloadColumnValue.getColumn7(downloadSourceDO));
        toolTip.append("|" + columns[8] + ": ");
        toolTip.append(DownloadColumnValue.getColumn8(downloadSourceDO));
        toolTip.append("|" + columns[9] + ": ");
        toolTip.append(DownloadColumnValue.getColumn9(downloadSourceDO));
        return toolTip.toString();
    }
    
}
