package de.applejuicenet.client.gui.wizard;

import de.applejuicenet.client.gui.listener.LanguageListener;

import javax.swing.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/WizardPanel.java,v 1.1 2003/09/08 14:55:09 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: open-source</p>
 *
 * @author: Maj0r <AJCoreGUI@maj0r.de>
 *
 * $Log: WizardPanel.java,v $
 * Revision 1.1  2003/09/08 14:55:09  maj0r
 * Wizarddialog weitergefuehrt.
 *
 *
 */

public abstract class WizardPanel extends JPanel implements LanguageListener{
    private WizardPanel naechstesPanel;
    private WizardPanel vorherigesPanel;

    public WizardPanel(){
        super();
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
}
