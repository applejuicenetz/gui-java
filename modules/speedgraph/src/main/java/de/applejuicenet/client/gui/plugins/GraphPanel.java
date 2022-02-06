package de.applejuicenet.client.gui.plugins;

import javax.swing.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.HashMap;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/ajzab0815/Repository/AJStatsPlugin/de/applejuicenet/client/gui/plugins/ajstats/GraphPanel.java,v 1.1 2004/05/21 18:52:21 zab0815 Exp $
 *
 * <p>Titel: AppleJuice Core-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 * <p>
 * $Log: GraphPanel.java,v $
 * Revision 1.1  2004/05/21 18:52:21  zab0815
 * First commit since upgrading to new Plugin interface. Only few changes with settings are needed.
 * <p>
 * Revision 1.1  2004/01/06 20:49:07  andy
 * Initial checkin
 * <p>
 * Revision 1.4  2003/12/29 11:00:02  zab0815
 * Automatisches scaling der Anzeige und scrolling bei vollem Display, komplett überarbeitet.
 * <p>
 * * $Log: GraphPanel.java,v $
 * * Revision 1.1  2004/05/21 18:52:21  zab0815
 * * First commit since upgrading to new Plugin interface. Only few changes with settings are needed.
 * *
 * * Revision 1.1  2004/01/06 20:49:07  andy
 * * Initial checkin
 * *
 * Revision 1.3  2003/12/22 16:25:02  maj0r
 * Bug behoben, der auftratt, wenn das Plugin aktualisiert wurde, obwohl es noch nicht angezeigt wird (Danke an Luke).
 * <p>
 * Revision 1.2  2003/09/15 07:28:45  maj0r
 * Plugin zeigt nun ein Raster und die Zeit auf der x-Achse.
 * <p>
 * Revision 1.1  2003/09/13 11:33:17  maj0r
 * Neues Plugin SpeedGraph.
 */

public class GraphPanel extends JPanel {

    public GraphPanel() {
        //private UpDownChart ud = new UpDownChart();
        Logger logger = Logger.getLogger(getClass());
        try {
            setLayout(new BorderLayout());
            this.setBackground(Color.BLACK);
            //add(new JScrollPane(ud), BorderLayout.NORTH);
            setBackground(Color.BLACK);
        } catch (Exception e) {
            if (logger.isEnabledFor(Level.ERROR))
                logger.error("Unbehandelte Exception", e);
        }

    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        //System.out.println("GpahPanel ("+getHeight()+","+getWidth()+") paintComponent");
        //ud.paintComponent(g);
        //super.paintComponent(g);
    }

    public void update(HashMap speeds) {
        //ud.update (speeds);
    }

    ;

}
