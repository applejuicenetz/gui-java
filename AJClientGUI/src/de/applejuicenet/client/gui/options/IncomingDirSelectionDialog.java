package de.applejuicenet.client.gui.options;

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

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.applejuicenet.client.shared.ZeichenErsetzer;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/options/IncomingDirSelectionDialog.java,v 1.1 2004/10/28 14:57:57 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class IncomingDirSelectionDialog
    extends JDialog {
    private static final long serialVersionUID = -5334471942691884492L;

    private String[] selectionValues;
    private JButton schliessen = new JButton();
    private JComboBox incomingDirs = new JComboBox();
    private boolean somethingSelected = false;

    public IncomingDirSelectionDialog(JFrame parentDialog,
                                      String[] selectionValues) {
        super(parentDialog, true);
        this.selectionValues = selectionValues;
        init();
    }

    private void init() {
        LanguageSelector languageSelector = LanguageSelector.getInstance();
        setTitle(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.mainform.changetarget.caption")));
        schliessen.setText(ZeichenErsetzer.korrigiereUmlaute(
            languageSelector.getFirstAttrbuteByTagName(".root.einstform.Button1.caption")));
        schliessen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                somethingSelected = true;
                IncomingDirSelectionDialog.this.dispose();
            }
        });
        JLabel label1 = new JLabel();
        label1.setText(ZeichenErsetzer.korrigiereUmlaute(languageSelector.
            getFirstAttrbuteByTagName(".root.javagui.downloadform.neuesverzeichnis")));
        for (int i = 0; i < selectionValues.length; i++) {
            incomingDirs.addItem(selectionValues[i]);
            if (selectionValues[i].compareTo("") == 0) {
                incomingDirs.setSelectedItem(selectionValues[i]);
            }
        }
        incomingDirs.setEditable(true);
        incomingDirs.getEditor().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ke) {
                schliessen.doClick();
            }
        });
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(label1);
        panel1.add(incomingDirs);
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

    public String getSelectedIncomingDir() {
        if (somethingSelected) {
            return (String) incomingDirs.getSelectedItem();
        }
        else {
            return null;
        }
    }
}
