package de.applejuicenet.client.gui.plugins.ircplugin;

public class IrcUser{
    public static final int MOD_USER = -1;
    public static final int MOD_VOICE = 0;
    public static final int MOD_HALFOP = 1;
    public static final int MOD_OP = 2;
    public static final int MOD_ADMIN = 3;

    private String nick;
    private int mod;

    public IrcUser(String nick, int mod){
        this.nick = nick;
        this.mod = mod;
    }

    public String toString(){
        switch (mod){
            case MOD_VOICE: return "+" + nick;
            case MOD_HALFOP: return "%" + nick;
            case MOD_OP: return "@" + nick;
            case MOD_ADMIN: return "!" + nick;
            default: return nick;
        }
    }

    public String getString(){
        return nick;
    }

    public int getMod(){
        return mod;
    }

    public int compareTo(Object object){
        if (object.getClass() != IrcUser.class) {
            return -1;
        }
        int mod2 = ((IrcUser)object).getMod();
        switch (mod){
            case MOD_ADMIN: {
                if (mod2 == MOD_ADMIN) {
                    return 0;
                }
                else {
                    return 1;
                }
            }
            case MOD_OP: {
                if (mod2 == MOD_ADMIN) {
                    return 1;
                }
                else if (mod2 == MOD_OP) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
            case MOD_HALFOP: {
                if (mod2 == MOD_ADMIN || mod2 == MOD_OP) {
                    return 1;
                }
                else if (mod2 == MOD_HALFOP) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
            case MOD_VOICE: {
                if (mod2 == MOD_ADMIN || mod2 == MOD_OP
                    || mod2 == MOD_HALFOP) {
                    return 1;
                }
                else if (mod2 == MOD_VOICE) {
                    return 0;
                }
                else {
                    return -1;
                }
            }
            default: {
                return 0;
            }
        }
    }
}
