package de.applejuicenet.client.gui.plugins.panels;

public class VersionHolder {

    public String versionsNr;
    public int countWin = 0;
    public int countLinux = 0;
    public int countMac = 0;
    public int countSolaris = 0;
    public int countOS2 = 0;
    public int countFreeBSD = 0;
    public int countNetWare = 0;
    public int countSonstige = 0;

    public VersionHolder(String versionsNr) {
        this.versionsNr = versionsNr;
    }

    public void addUser(int os) {
        switch (os) {
            case 1: {
                countWin++;
                break;
            }
            case 2: {
                countLinux++;
                break;
            }
            case 3: {
                countMac++;
                break;
            }
            case 4: {
                countSolaris++;
                break;
            }
            case 5: {
                countOS2++;
                break;
            }
            case 6: {
                countFreeBSD++;
                break;
            }
            case 7: {
                countNetWare++;
                break;
            }
            default: {
                countSonstige++;
                break;
            }
        }
    }
}