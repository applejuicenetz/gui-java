/*
 * Decompiled with CFR 0.150.
 */
package de.tklsoft.gui.controls.border;

import de.tklsoft.gui.controls.StatusHolder;
import de.tklsoft.icon.IconManager;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ModifyableComponentBorder
        extends AbstractBorder
        implements PropertyChangeListener {
    public static final String BORDER_STATUS_CHANGED = "border";
    private static Icon invalidIcon = IconManager.getInstance().getIcon("border_invalid");
    private final JComponent parentComponent;
    private final Insets insets;
    private int[] colors = new int[]{255, 0, 0};
    private Color currentColor = new Color(this.colors[0], this.colors[1], this.colors[2]);
    private Border notDirtyBorder;
    private int increment = 20;
    private Timer timer;
    private StatusHolder.STATUSFLAG status = StatusHolder.STATUSFLAG.NORMAL;
    private Color modifiedColor = Color.BLUE;
    private final int borderWidth;
    private boolean hasToEnableBorder = false;

    public ModifyableComponentBorder(JComponent parentComponent, int borderWidth) {
        this.parentComponent = parentComponent;
        this.borderWidth = borderWidth;
        this.notDirtyBorder = parentComponent.getBorder();
        if (this.notDirtyBorder != null) {
            this.insets = this.notDirtyBorder.getBorderInsets(parentComponent);
            this.hasToEnableBorder = true;
        } else {
            this.notDirtyBorder = BorderFactory.createLineBorder(parentComponent.getBackground(), 2);
            this.insets = new Insets(1, 1, 1, 1);
        }
        this.init();
    }

    public ModifyableComponentBorder(JComponent parentComponent) {
        this(parentComponent, -1);
    }

    private void init() {
        this.timer = new Timer(100, new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                if (ModifyableComponentBorder.this.parentComponent.isShowing()) {
                    ModifyableComponentBorder.this.currentColor = new Color(ModifyableComponentBorder.this.colors[0], ModifyableComponentBorder.this.colors[1], ModifyableComponentBorder.this.colors[2]);
                    int[] arrn = ModifyableComponentBorder.this.colors;
                    arrn[1] = arrn[1] + ModifyableComponentBorder.this.increment;
                    int[] arrn2 = ModifyableComponentBorder.this.colors;
                    arrn2[2] = arrn2[2] + ModifyableComponentBorder.this.increment;
                    if (ModifyableComponentBorder.this.colors[1] >= 240 || ModifyableComponentBorder.this.colors[1] == 0) {
                        ModifyableComponentBorder.this.increment = -ModifyableComponentBorder.this.increment;
                    }
                    ModifyableComponentBorder.this.parentComponent.repaint();
                }
            }
        });
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        if (this.status == StatusHolder.STATUSFLAG.NORMAL) {
            this.notDirtyBorder.paintBorder(c, g, x, y, width, height);
        } else if (this.status == StatusHolder.STATUSFLAG.MODIFIED) {
            this.drawBorder(this.modifiedColor, g, x, y, this.borderWidth == -1 ? width : this.borderWidth, height);
        } else {
            this.drawBorder(this.currentColor, g, x, y, this.borderWidth == -1 ? width : this.borderWidth, height);
            invalidIcon.paintIcon(c, g, width - invalidIcon.getIconWidth() - this.insets.right - 2, this.insets.top + 2);
        }
    }

    private void drawBorder(Color color, Graphics g, int x, int y, int width, int height) {
        Color tmpcolor = g.getColor();
        g.setColor(color);
        g.drawRect(x, y, width - 1, height - 1);
        g.drawRect(x + 1, y + 1, width - 3, height - 3);
        g.setColor(tmpcolor);
    }

    public Insets getBorderInsets(Component c) {
        if (this.status == StatusHolder.STATUSFLAG.NORMAL) {
            return this.notDirtyBorder.getBorderInsets(c);
        }
        if (this.status == StatusHolder.STATUSFLAG.MODIFIED) {
            return this.insets;
        }
        return this.insets;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == this.parentComponent && evt.getPropertyName().equals(BORDER_STATUS_CHANGED)) {
            this.status = (StatusHolder.STATUSFLAG)((Object)evt.getNewValue());
            if (this.status == StatusHolder.STATUSFLAG.INVALID && !this.timer.isRunning()) {
                this.timer.start();
            } else if (this.status != StatusHolder.STATUSFLAG.INVALID && this.timer.isRunning()) {
                this.timer.stop();
            }
            this.parentComponent.repaint();
        }
    }
}

