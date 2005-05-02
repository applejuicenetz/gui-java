package de.applejuicenet.client.gui.plugins.ircplugin;

import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.AbstractListModel;

/**
 * $Header: /home/xubuntu/berlios_backup/github/tmp-cvs/applejuicejava/Repository/AJClientGUI/plugin_src/ircplugin/src/de/applejuicenet/client/gui/plugins/ircplugin/SortedListModel.java,v 1.14 2005/05/02 14:59:28 maj0r Exp $
 *
 * <p>Titel: AppleJuice Client-GUI</p>
 * <p>Beschreibung: Erstes GUI fuer den von muhviehstarr entwickelten appleJuice-Core</p>
 * <p>Copyright: General Public License</p>
 *
 * @author: Maj0r [aj@tkl-soft.de]
 *
 */

public class SortedListModel extends AbstractListModel {

	private ArrayList<User> model = new ArrayList<User>();
	private StringComparator comparator = new StringComparator();

    public SortedListModel() {
    }

    public int getSize() {
        return model.size();
    }

    public User[] getValues(){
        return (User[])model.toArray(new User[model.size()]);
    }

    public synchronized Object getElementAt(int index) {
        if (index < model.size()){
            return model.toArray()[index];
        }
        else{
            return null;
        }
    }

    public synchronized void add(User user) {
        if (model.add(user))
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

    public synchronized boolean remove(User user) {
        boolean removed = model.remove(user);
        if (removed)
        {
            fireIntervalRemoved(this, 0, getSize());
        }
        return removed;
    }
    
    public void fireIntervalAdded(Object source, int index0, int index1){
        super.fireIntervalAdded(source, index0, index1);
        reorder();
    }
    
    public void fireIntervalRemoved(Object source, int index0, int index1){
        super.fireIntervalRemoved(source, index0, index1);
        reorder();
    }
    
    public int getOpCount(){
    	int ops = 0;
    	for (int i=0; i<model.size(); i++){
    		if ((model.get(i)).isAdmin()
    				|| (model.get(i)).isOp()
					|| (model.get(i)).isHalfop()){
    			ops++;
    		}
    	}
    	return ops;
    }
    
    public void reorder(){
        int n = model.size();
        for (int i = 0; i < n - 1; i++) {
            int k = i;
            for (int j = i + 1; j < n; j++) {                
                if (comparator.compare(model.get(j), model.get(k)) < 0) {
                    k = j;
                }
            }
            User tmp = model.get(i);
            model.set(i, model.get(k));
            model.set(k, tmp);
        }
        this.fireContentsChanged(this, 0, getSize());
    }
    

    private class StringComparator implements Comparator{
        public int compare(Object o1, Object o2) {
            if (o1.getClass()==User.class && o2.getClass()==User.class){
                int mods = 0;
                try{
                    mods = compareMods((User)o1, (User)o2);
                }
                catch(StringIndexOutOfBoundsException saoobE){
                    mods = 0;
                }
                if (mods != 0){
                    return mods;
                }
                else{
                    return ( ((User)o1).getName()).compareToIgnoreCase( ((User)o2).getName());
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

        private int compareMods(User mod1, User mod2){
            if (mod1.getRechteAsInt() == mod2.getRechteAsInt()){
                return 0;
            }
            else if (mod1.getRechteAsInt() > mod2.getRechteAsInt()){
                return -1;
            }
            else{
                return 1;
            }
        }
    }
}
