package de.applejuicenet.client.shared;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Splash.java,v 1.11 2004/12/08 21:14:33 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r [aj@tkl-soft.de]
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Properties;

public class Splash
    extends JDialog {
	private Image image;
    private JProgressBar progress;
    private JLayeredPane panel;

    public Splash(Frame parent, Image image, int progressMin, int progressMax) {
        super(parent);
        this.image = image;
        progress = new JProgressBar(progressMin, progressMax);
        progress.setStringPainted(true);
        init();
    }

    public void setProgress(int position, String text){
        if (position >= progress.getMinimum()
            && position <= progress.getMaximum() ){
            progress.setValue(position);
            progress.setString(text);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void init(){
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        int w = image.getWidth(this);
        int h = image.getHeight(this);
        Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Dimension d = new Dimension(r.width, r.height);
        setBounds((d.width - w) / 2, (d.height - h) / 3, w, h);
        Image back = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = back.getGraphics();
        g.drawImage(image, 0, 0, this);
        g.dispose();
        JLabel label = new JLabel(new ImageIcon(back));
        label.setOpaque(false);
        label.setBounds(0, 0, w, h);
        try{
            Properties props = IconManager.getInstance().getIconProperties("splashscreen");
        	int x = Integer.parseInt(props.getProperty("x"));
            int y = Integer.parseInt(props.getProperty("y"));
            int width = Integer.parseInt(props.getProperty("width"));
            int height = Integer.parseInt(props.getProperty("height"));
            if (x <= 0  || y <= 0 || width <=1 || height <= 1
                    || x+width >= w || y+height >= h){
                throw new Exception();
            }
            progress.setBounds(x, y, width, height);
            progress.setOpaque(false);
        }
        catch(Exception e){
            progress.setBounds(160, 61, 180, 15);
            progress.setOpaque(false);
        }
        panel = new JLayeredPane();
        panel.setBounds(0, 0, w, h);
        panel.setOpaque(false);
        panel.add(label, JLayeredPane.DEFAULT_LAYER);
        panel.add(progress, JLayeredPane.PALETTE_LAYER);
        setContentPane(panel);
        getRootPane().setOpaque(false);
    }

    public void dispose() {
        super.dispose();
        image = null;
    }
}
