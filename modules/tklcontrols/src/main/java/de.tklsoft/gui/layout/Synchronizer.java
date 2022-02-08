/*
 * Decompiled with CFR 0.150.
 */
package de.tklsoft.gui.layout;

import java.awt.*;
import java.util.HashSet;

public class Synchronizer {
    private HashSet<Synchronizable> items = new HashSet();
    private final METHOD method;

    public Synchronizer(METHOD method) {
        this.method = method;
    }

    public void add(Synchronizable synchronizable) {
        this.items.add(synchronizable);
        synchronizable.setSynchronizer(this);
    }

    public Dimension getSize(Synchronizable synchronizable) {
        if (this.method == METHOD.WIDTH) {
            return this.getSizeByWidth(synchronizable);
        }
        if (this.method == METHOD.HEIGHT) {
            return this.getSizeByHeight(synchronizable);
        }
        if (this.method == METHOD.WIGHT_AND_HEIGHT) {
            return this.getSizeByWidthAndHeight();
        }
        return null;
    }

    private Dimension getSizeByWidthAndHeight() {
        int height = 0;
        int width = 0;
        for (Synchronizable curSynchronizable : this.items) {
            Dimension dim = curSynchronizable.getNormalSize();
            if (dim.height > height) {
                height = dim.height;
            }
            if (dim.width <= width) continue;
            width = dim.width;
        }
        return new Dimension(width, height);
    }

    private Dimension getSizeByHeight(Synchronizable synchronizable) {
        int height = 0;
        for (Synchronizable curSynchronizable : this.items) {
            Dimension dim = curSynchronizable.getNormalSize();
            if (dim.height <= height) continue;
            height = dim.height;
        }
        return new Dimension(synchronizable.getNormalSize().width, height);
    }

    private Dimension getSizeByWidth(Synchronizable synchronizable) {
        int width = 0;
        for (Synchronizable curSynchronizable : this.items) {
            Dimension dim = curSynchronizable.getNormalSize();
            if (dim.width <= width) continue;
            width = dim.width;
        }
        return new Dimension(width, synchronizable.getNormalSize().height);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum METHOD {
        WIDTH,
        HEIGHT,
        WIGHT_AND_HEIGHT;

    }
}

