/*
 * Created on 03.01.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.applejuicenet.client.gui.plugins;

import java.util.ArrayList;


/**
 * @author Zab
 * <p>
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class UpDownData {
    public class ArrayData {
        private long i_up;
        private long i_down;
        private long i_time;

        public ArrayData(long up, long down, long time) {
            i_up = up;
            i_down = down;
            i_time = time;
        }

        public long getUp() {
            return i_up;
        }

        public long getDown() {
            return i_down;
        }

        public long getTime() {
            return i_time;
        }
    }

    private ArrayList Data = new ArrayList();
    private long maxDownVal;
    private long maxUpVal;

    public UpDownData() {
    }

    public UpDownData(int size) {
        Data = new ArrayList(size);
    }

    public void add(int upRate, int downRate, long time) {
        add(upRate, downRate, time);
    }

    public void add(long upRate, long downRate, long time) {
        Data.add(new ArrayData(upRate, downRate, time));
        if (maxDownVal < downRate) maxDownVal = downRate;
        if (maxUpVal < upRate) maxUpVal = upRate;
    }

    public void add(Long upRate, Long downRate, long time) {
        add(upRate.longValue(), downRate.longValue(), time);
    }

    public void clear() {
        Data.clear();
    }

    public ArrayData get(int index) {
        return (ArrayData) Data.get(index);
    }

    public long getMaxUp() {
        return maxUpVal;
    }

    public long getMaxDown() {
        return maxDownVal;
    }

    public void remove(int index) {
        Data.remove(index);
    }

    public Object[] toArray() {
        return Data.toArray();
    }

    public int size() {
        return Data.size();
    }
}
