package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.controller.PropertiesManager;

import java.awt.*;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/src/de/applejuicenet/client/shared/Settings.java,v 1.5 2004/01/05 19:17:19 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Offizielles GUI für den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r <aj@tkl-soft.de>
 *
 * $Log: Settings.java,v $
 * Revision 1.5  2004/01/05 19:17:19  maj0r
 * Bug #56 gefixt (Danke an MeineR)
 * Das Laden der Plugins beim Start kann über das Optionenmenue deaktiviert werden.
 *
 * Revision 1.4  2003/12/29 16:04:17  maj0r
 * Header korrigiert.
 *
 * Revision 1.3  2003/10/14 15:41:06  maj0r
 * CVS-Header eingebaut.
 *
 *
 */

public class Settings {
    private boolean dirty;
    private Color downloadFertigHintergrundColor = Color.GREEN;
    private Color quelleHintergrundColor = new Color(255, 255, 150);
    private boolean farbenAktiv = true;
    private boolean downloadUebersicht = true;
    private boolean loadPlugins = true;

    public Settings(Boolean farbenAktiv, Color downloadFertigHintergrundColor, Color quelleHintergrundColor,
                    Boolean downloadUebersicht, Boolean loadPlugins){
        if (farbenAktiv!=null)
            this.farbenAktiv = farbenAktiv.booleanValue();
        if (downloadFertigHintergrundColor!=null)
            this.downloadFertigHintergrundColor = downloadFertigHintergrundColor;
        if (quelleHintergrundColor!=null)
            this.quelleHintergrundColor = quelleHintergrundColor;
        if (downloadUebersicht!=null)
            this.downloadUebersicht = downloadUebersicht.booleanValue();
        if (loadPlugins!=null)
            this.loadPlugins = downloadUebersicht.booleanValue();
    }

    public static Settings getSettings(){
        return PropertiesManager.getOptionsManager().getSettings();
    }

    public void save(){
        if (dirty){
            PropertiesManager.getOptionsManager().saveSettings(this);
            dirty = false;
        }
    }

    public Color getDownloadFertigHintergrundColor() {
        return downloadFertigHintergrundColor;
    }

    public void setDownloadFertigHintergrundColor(Color downloadFertigHintergrundColor) {
        if (this.downloadFertigHintergrundColor.getRGB() != downloadFertigHintergrundColor.getRGB()){
            dirty = true;
            this.downloadFertigHintergrundColor = downloadFertigHintergrundColor;
        }
    }

    public Color getQuelleHintergrundColor() {
        return quelleHintergrundColor;
    }

    public void setQuelleHintergrundColor(Color quelleHintergrundColor) {
        if (this.quelleHintergrundColor.getRGB() != quelleHintergrundColor.getRGB()){
            dirty = true;
            this.quelleHintergrundColor = quelleHintergrundColor;
        }
    }

    public boolean isFarbenAktiv() {
        return farbenAktiv;
    }

    public void setFarbenAktiv(boolean farbenAktiv) {
        if (this.farbenAktiv!=farbenAktiv){
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
        if (this.downloadUebersicht!=downloadUebersicht){
            dirty = true;
            this.downloadUebersicht = downloadUebersicht;
        }
    }

    public boolean shouldLoadPluginsOnStartup(){
        return loadPlugins;
    }

    public void loadPluginsOnStartup(boolean loadPluginsOnStartup){
        if (loadPlugins != loadPluginsOnStartup){
            dirty = true;
            loadPlugins = loadPluginsOnStartup;
        }
    }
}
