package de.applejuicenet.client.gui.plugins.ircplugin;

public class User {
    public static final int NOTHING = 0;
    public static final int VOICE = 1;
    public static final int HALFOP = 2;
    public static final int OPERATOR = 4;
    public static final int ADMINISTRATOR = 8;
    
    private String name;
    
    private int status = 0;
    private boolean admin = false;
    private boolean op = false;
    private boolean halfop = false;
    private boolean voice = false;

    public User(String name) {
        this.name = name;
    }
    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean enable) {
        admin = enable;
    }

    public boolean isHalfop() {
        return halfop;
    }

    public void setHalfop(boolean enable) {
        halfop = enable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOp() {
        return op;
    }

    public void setOp(boolean enable) {
        op = enable;
    }

    public boolean isVoice() {
        return voice;
    }

    public void setVoice(boolean enable) {
        voice = enable;
    }
    
    public int getRechteAsInt(){
        int status = 0;
        if (admin){
            status += ADMINISTRATOR;
        }
        if (op){
            status += OPERATOR;
        }
        if (halfop){
            status += HALFOP;
        }
        if (voice){
            status += VOICE;
        }
        return status;
    }
    
    public String toString(){
        return name;
    }
}
