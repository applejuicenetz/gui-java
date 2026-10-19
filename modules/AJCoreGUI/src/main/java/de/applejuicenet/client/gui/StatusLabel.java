package de.applejuicenet.client.gui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;

/**
 * Status bar field: only grows, never shrinks and has a minimum width in digit widths so changing
 * values do not shift the neighbouring fields.
 */
class StatusLabel extends JLabel {
    private final int minChars;
    private int maxWidth;

    StatusLabel(int minChars) {
        super(" ");
        this.minChars = minChars;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        // The separator colour comes from the look and feel, so rebuild it on every theme change.
        java.awt.Color separator = UIManager.getColor("Separator.foreground");
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, separator != null ? separator : java.awt.Color.GRAY),
                BorderFactory.createEmptyBorder(1, 5, 1, 5)));
    }

    @Override
    public Dimension getPreferredSize() {
        if (isPreferredSizeSet()) {
            return super.getPreferredSize();
        }
        Dimension d = super.getPreferredSize();
        int width = d.width;
        if (minChars > 0) {
            FontMetrics fm = getFontMetrics(getFont());
            Insets in = getInsets();
            int iconWidth = getIcon() != null ? getIcon().getIconWidth() + getIconTextGap() : 0;
            width = Math.max(width, minChars * fm.charWidth('0') + iconWidth + in.left + in.right);
        }
        maxWidth = Math.max(maxWidth, width);
        return new Dimension(maxWidth, d.height);
    }

    @Override
    public Dimension getMinimumSize() {
        return isMinimumSizeSet() ? super.getMinimumSize() : getPreferredSize();
    }
}
