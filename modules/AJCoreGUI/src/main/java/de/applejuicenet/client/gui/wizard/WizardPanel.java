package de.applejuicenet.client.gui.wizard;

import javax.swing.JPanel;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/gui/wizard/WizardPanel.java,v 1.7 2005/02/22 09:21:07 maj0r Exp $
 *
 * <p>Title: AppleJuice Client-GUI</p>
 * <p>Description: Official GUI for the appleJuice core developed by muhviehstarr</p>
 * <p>Copyright: General Public License</p>
 *
 * @author Maj0r <aj@tkl-soft.de>
 *
 */

public abstract class WizardPanel
    extends JPanel {
    private WizardPanel nextPanel;
    private WizardPanel previousPanel;
    protected LanguageSelector languageSelector;

    public WizardPanel() {
        super();
        languageSelector = LanguageSelector.getInstance();
    }

    public WizardPanel getNextPanel() {
        return nextPanel;
    }

    public void setNextPanel(WizardPanel nextPanel) {
        this.nextPanel = nextPanel;
    }

    public WizardPanel getPreviousPanel() {
        return previousPanel;
    }

    public void setPreviousPanel(WizardPanel previousPanel) {
        this.previousPanel = previousPanel;
    }

    public abstract void fireLanguageChanged();
}
