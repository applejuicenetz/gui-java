package de.applejuicenet.client.gui.wizard;

import java.awt.Color;

import de.applejuicenet.client.gui.controller.LanguageSelector;
import de.tklsoft.gui.controls.TKLPanel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/WizardPanel.java,v 1.7 2005/02/22 09:21:07 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class WizardPanel
    extends TKLPanel {
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
