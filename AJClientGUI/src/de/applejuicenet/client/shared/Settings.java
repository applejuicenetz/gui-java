package de.applejuicenet.client.shared;

import de.applejuicenet.client.gui.controller.OptionsManager;

import java.awt.*;

public class Settings {
    private boolean dirty;
    private Color downloadFertigHintergrundColor = Color.GREEN;
    private Color quelleHintergrundColor = new Color(255, 255, 150);
    private boolean farbenAktiv = true;
    private boolean downloadUebersicht = true;

    public Settings(Boolean farbenAktiv, Color downloadFertigHintergrundColor, Color quelleHintergrundColor,
                    Boolean downloadUebersicht){
        if (farbenAktiv!=null)
            this.farbenAktiv = farbenAktiv.booleanValue();
        if (downloadFertigHintergrundColor!=null)
            this.downloadFertigHintergrundColor = downloadFertigHintergrundColor;
        if (quelleHintergrundColor!=null)
            this.quelleHintergrundColor = quelleHintergrundColor;
        if (downloadUebersicht!=null)
            this.downloadUebersicht = downloadUebersicht.booleanValue();
    }

    public static Settings getSettings(){
        return OptionsManager.getInstance().getSettings();
    }

    public void save(){
        if (dirty){
            OptionsManager.getInstance().saveSettings(this);
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
}
