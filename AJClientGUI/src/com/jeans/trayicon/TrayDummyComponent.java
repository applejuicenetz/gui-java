package com.jeans.trayicon;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Window;

public class TrayDummyComponent
    extends Window {

    private static final long serialVersionUID = -368072341691032372L;

	public TrayDummyComponent(Frame parentFrame) {
        super(parentFrame);
    }

    public Point getLocationOnScreen() {
        return new Point(0, 0);
    }
}