package de.applejuicenet.client.gui.wizard;

import java.awt.Color;
import javax.swing.JPanel;

import de.applejuicenet.client.gui.controller.LanguageSelector;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/WizardPanel.java,v 1.5 2004/02/05 23:11:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: WizardPanel.java,v $
 * Revision 1.5  2004/02/05 23:11:27  maj0r
 * Formatierung angepasst.
 *
 * Revision 1.4  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.3  2003/10/21 14:08:45  maj0r
 * Mittels PMD Code verschoenert, optimiert.
 *
 * Revision 1.2  2003/09/09 06:37:36  maj0r
 * Wizard erweitert, aber noch nicht fertiggestellt.
 *
 * Revision 1.1  2003/09/08 14:55:09  maj0r
 * Wizarddialog weitergefuehrt.
 *
 *
 */

public abstract class WizardPanel
    extends JPanel {
    private WizardPanel naechstesPanel;
    private WizardPanel vorherigesPanel;
    protected LanguageSelector languageSelector;

    public WizardPanel() {
        super();
        setBackground(Color.WHITE);
        languageSelector = LanguageSelector.getInstance();
    }

    public WizardPanel getNaechstesPanel() {
        return naechstesPanel;
    }

    public void setNaechstesPanel(WizardPanel naechstesPanel) {
        this.naechstesPanel = naechstesPanel;
    }

    public WizardPanel getVorherigesPanel() {
        return vorherigesPanel;
    }

    public void setVorherigesPanel(WizardPanel vorherigesPanel) {
        this.vorherigesPanel = vorherigesPanel;
    }

    public abstract void fireLanguageChanged();
}
