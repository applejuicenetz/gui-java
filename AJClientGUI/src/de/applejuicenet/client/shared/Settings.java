package de.applejuicenet.client.shared;

import java.awt.Color;

import de.applejuicenet.client.gui.controller.OptionsManagerImpl;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Settings.java,v 1.9 2004/03/09 16:50:27 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 */

public class Settings {
    private boolean dirty;
    private Color downloadFertigHintergrundColor = Color.GREEN;
    private Color quelleHintergrundColor = new Color(255, 255, 150);
    private boolean farbenAktiv = true;
    private boolean downloadUebersicht = true;
    private boolean loadPlugins = true;

    public Settings(){
    }

    public Settings(Boolean farbenAktiv, Color downloadFertigHintergrundColor,
                    Color quelleHintergrundColor,
                    Boolean downloadUebersicht, Boolean loadPlugins) {
        if (farbenAktiv != null) {
            this.farbenAktiv = farbenAktiv.booleanValue();
        }
        if (downloadFertigHintergrundColor != null) {
            this.downloadFertigHintergrundColor =
                downloadFertigHintergrundColor;
        }
        if (quelleHintergrundColor != null) {
            this.quelleHintergrundColor = quelleHintergrundColor;
        }
        if (downloadUebersicht != null) {
            this.downloadUebersicht = downloadUebersicht.booleanValue();
        }
        if (loadPlugins != null) {
            this.loadPlugins = downloadUebersicht.booleanValue();
        }
    }

    public static Settings getSettings() {
        return OptionsManagerImpl.getInstance().getSettings();
    }

    public void save() {
        if (dirty) {
            OptionsManagerImpl.getInstance().saveSettings(this);
            dirty = false;
        }
    }

    public Color getDownloadFertigHintergrundColor() {
        return downloadFertigHintergrundColor;
    }

    public void setDownloadFertigHintergrundColor(Color
        downloadFertigHintergrundColor) {
        if (this.downloadFertigHintergrundColor.getRGB() !=
            downloadFertigHintergrundColor.getRGB()) {
            dirty = true;
            this.downloadFertigHintergrundColor =
                downloadFertigHintergrundColor;
        }
    }

    public Color getQuelleHintergrundColor() {
        return quelleHintergrundColor;
    }

    public void setQuelleHintergrundColor(Color quelleHintergrundColor) {
        if (this.quelleHintergrundColor.getRGB() !=
            quelleHintergrundColor.getRGB()) {
            dirty = true;
            this.quelleHintergrundColor = quelleHintergrundColor;
        }
    }

    public boolean isFarbenAktiv() {
        return farbenAktiv;
    }

    public void setFarbenAktiv(boolean farbenAktiv) {
        if (this.farbenAktiv != farbenAktiv) {
            dirty = true;
            this.farbenAktiv = farbenAktiv;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isDownloadUebersicht() {
        return downloadUebersicht;
    }

    public void setDownloadUebersicht(boolean downloadUebersicht) {
        if (this.downloadUebersicht != downloadUebersicht) {
            dirty = true;
            this.downloadUebersicht = downloadUebersicht;
        }
    }

    public boolean shouldLoadPluginsOnStartup() {
        return loadPlugins;
    }

    public void loadPluginsOnStartup(boolean loadPluginsOnStartup) {
        if (loadPlugins != loadPluginsOnStartup) {
            dirty = true;
            loadPlugins = loadPluginsOnStartup;
        }
    }
}
