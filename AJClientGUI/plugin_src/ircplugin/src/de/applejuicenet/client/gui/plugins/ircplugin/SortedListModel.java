package de.applejuicenet.client.gui.plugins.ircplugin;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractListModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/SortedListModel.java,v 1.9 2004/10/14 08:57:55 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [Maj0r@applejuicenet.de]
 *
 */

public class SortedListModel extends AbstractListModel {

    private static final long serialVersionUID = 6788175271347551432L;
	private SortedSet model;

    public SortedListModel() {
        model = new TreeSet(new StringComparator());
    }

    public int getSize() {
        return model.size();
    }

    public Set getValues(){
        return model;
    }

    public synchronized Object getElementAt(int index) {
        if (index < model.size()){
            return model.toArray()[index];
        }
        else{
            return null;
        }
    }

    public synchronized void add(Object element) {
        if (model.add(element))
        {
            fireIntervalAdded(this, 0, getSize());
        }
    }

    public synchronized void clear() {
        model.clear();
        fireIntervalRemoved(this, 0, getSize());
    }

    public boolean contains(Object element) {
        return model.contains(element);
    }

    public Object firstElement() {
        return model.first();
    }

    public Object lastElement() {
        return model.last();
    }

    public synchronized boolean remove(Object element) {
        boolean removed = model.remove(element);
        if (removed)
        {
            fireIntervalRemoved(this, 0, getSize());
        }
        return removed;
    }

    private class StringComparator implements Comparator{
        public int compare(Object o1, Object o2) {
            if (o1.getClass()==String.class && o2.getClass()==String.class){
                int mods = 0;
                try{
                    mods = compareMods(o1.toString().charAt(0),
                                           o2.toString().charAt(0));
                }
                catch(StringIndexOutOfBoundsException saoobE){
                    mods = 0;
                }
                if (mods != 0){
                    return mods;
                }
                else{
                    return ( (String) o1).compareToIgnoreCase( (String) o2);
                }
            }
            else{
                if (o1.hashCode()==o2.hashCode()){
                    return 0;
                }
                else if (o1.hashCode()<=o2.hashCode()){
                    return -1;
                }
                else{
                    return 1;
                }
            }
        }
    }

    private int compareMods(char mod1, char mod2){
        switch (mod1){
            case '!':{
                if (mod2=='!'){
                    return 0;
                }
                else{
                    return 1;
                }
            }
            case '@':{
                if (mod2=='!'){
                    return 1;
                }
                else if (mod2=='@'){
                    return 0;
                }
                else {
                    return -1;
                }
            }
            case '%':{
                if (mod2=='!' || mod2=='@'){
                    return 1;
                }
                else if (mod2=='%'){
                    return 0;
                }
                else {
                    return -1;
                }
            }
            case '+':{
                if (mod2=='!' || mod2=='@' || mod2=='%'){
                    return 1;
                }
                else if (mod2=='+'){
                    return 0;
                }
                else {
                    return -1;
                }
            }
            default:{
                return 0;
            }
        }
    }
}
