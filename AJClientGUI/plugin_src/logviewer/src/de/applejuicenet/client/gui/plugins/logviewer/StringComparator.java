package de.applejuicenet.client.gui.plugins.logviewer;

import java.util.Comparator;


public class StringComparator implements Comparator{

    public int compare(Object o1, Object o2) {
        if (o1.getClass() != String.class ||
                o2.getClass() != String.class){
            throw new RuntimeException("StringComparator needs Strings");
        }
        int result = ((String)o1).compareToIgnoreCase((String)o2);
        return (result == 0) ? -1 : result * -1;
    }

}
