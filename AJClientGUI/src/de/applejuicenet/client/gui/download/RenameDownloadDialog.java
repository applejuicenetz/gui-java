package de.applejuicenet.client.gui.download;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Iterator;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.applejuicenet.client.gui.controller.ApplejuiceFassade;
import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;
import de.applejuicenet.client.shared.dac.DownloadDO;
import de.applejuicenet.client.shared.dac.DownloadSourceDO;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/download/RenameDownloadDialog.java,v 1.2 2004/11/22 16:25:26 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class RenameDownloadDialog
    extends JDialog {
	
	private DownloadDO downloadDO;
    private JButton schliessen = new JButton();
    private JComboBox possibleNames = new JComboBox();
    private boolean somethingSelected = false;

    public RenameDownloadDialog(JFrame parentDialog,
                                      DownloadDO selectedDownloadDO) {
        super(parentDialog, true);
        downloadDO = selectedDownloadDO;
        init();
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.renamefile.caption")));
        schliessen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.einstform.Button1.caption")));
        schliessen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                somethingSelected = true;
                RenameDownloadDialog.this.dispose();
            }
        });
        JLabel label1 = new JLabel();
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.downloadform.neuerdateiname")));
        DownloadSourceDO[] sources = downloadDO.getSources();
        HashSet set = new HashSet();
        set.add(downloadDO.getFilename());
        for (int i = 0; i < sources.length; i++) {
            set.add(sources[i].getFilename());
        }
        Iterator it = set.iterator();
        String name;
        while (it.hasNext()){
            name = (String) it.next();
            possibleNames.addItem(name);
            if (name.compareTo(downloadDO.getFilename()) == 0) {
                possibleNames.setSelectedItem(name);
            }
        }
        possibleNames.setEditable(true);
        possibleNames.getEditor().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ke) {
                schliessen.doClick();
            }
        });
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(label1);
        panel1.add(possibleNames);
        getContentPane().setLayout(new BorderLayout());
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        southPanel.add(schliessen);
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(southPanel, BorderLayout.SOUTH);
        pack();
        Dimension appDimension = getSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().
            getScreenSize();
        setLocation( (screenSize.width -
                      appDimension.width) / 2,
                    (screenSize.height -
                     appDimension.height) / 2);
    }

    public String getNewName() {
        if (somethingSelected) {
            String neuerName = ((String)possibleNames.getSelectedItem()).trim();
            if (neuerName.indexOf(File.separator) == 0 ||
                neuerName.indexOf(ApplejuiceFassade.separator) == 0) {
                neuerName = neuerName.substring(1);
            }
            if (neuerName.length()==0){
                neuerName = null;
            }
            StringBuffer tempLink = new StringBuffer(neuerName);
            for (int i = 0; i < tempLink.length(); i++) {
                if (tempLink.charAt(i) == ' ') {
                    tempLink.setCharAt(i, '.');
                }
            }
            try {
                neuerName = URLEncoder.encode(tempLink.toString(),
                                              "ISO-8859-1");
            }
            catch (UnsupportedEncodingException ex) {
                ;
                //gibbet nicht, also nix zu behandeln...
            }
            return neuerName;
        }
        else {
            return null;
        }
    }
}
