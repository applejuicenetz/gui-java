package de.applejuicenet.client.gui.wizard;

import javax.swing.JPanel;
import de.applejuicenet.client.gui.controller.LanguageSelector;

import java.awt.*;

/**
 * Copyright: General Public License
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
