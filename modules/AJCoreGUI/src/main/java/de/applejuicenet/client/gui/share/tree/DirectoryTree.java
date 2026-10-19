package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.gui.controller.GuiText;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;


/**
 * Copyright: General Public License
 */

public class DirectoryTree
    extends JTree {
    private static final int WARNING_BLINK_MS = 700;

    private final Timer warningBlinkTimer;
    private boolean warningBlinking = false;
    private boolean warningVisible = true;

    public DirectoryTree() {
        // Tooltips only reach a component after it is registered with the ToolTipManager;
        // JComponent does that in setToolTipText, which this tree does not use.
        ToolTipManager.sharedInstance().registerComponent(this);
        warningBlinkTimer = new Timer(WARNING_BLINK_MS, event -> {
            warningVisible = !warningVisible;
            repaint();
        });
    }

    /** Starts or stops the red blinking of shares that expose a whole drive, root or home directory. */
    public void setWarningBlinking(boolean blinking) {
        warningBlinking = blinking;
        warningVisible = true;
        if (blinking) {
            warningBlinkTimer.start();
        } else {
            warningBlinkTimer.stop();
        }
        repaint();
    }

    /** Current blink phase; the cell renderer draws the red marker only while it is true. */
    public boolean isWarningVisible() {
        return warningVisible;
    }

    public void addNotify() {
        super.addNotify();
        if (warningBlinking) {
            warningBlinkTimer.start();
        }
    }

    public void removeNotify() {
        warningBlinkTimer.stop();
        super.removeNotify();
    }

	public Dimension getPreferredScrollableViewportSize() {
        Dimension result = super.getPreferredScrollableViewportSize();
        result = new Dimension(200, result.height);
        return result;
    }

    /** True when the node at {@code path} is a drive or filesystem root, e.g. {@code C:} or {@code /}. */
    public static boolean isRootDrive(TreePath path) {
        return path != null && path.getLastPathComponent() instanceof DirectoryNode node && node.isRootDrive();
    }

    /** Explains core-owned folders that cannot be shared or unshared from the tree. */
    public String getToolTipText(MouseEvent event) {
        TreePath path = getPathForLocation(event.getX(), event.getY());

        if (path != null && path.getLastPathComponent() instanceof DirectoryNode node) {
            String tip = tooltipFor(node);

            if (tip != null) {
                return tip;
            }
        }
        return super.getToolTipText(event);
    }

    /** Tooltip for one node, or {@code null} when the node needs no explanation. */
    public String tooltipFor(DirectoryNode node) {
        String key = tooltipKey(node);

        return key == null ? null : GuiText.textOr(key, null);
    }

    /** Language key explaining a core-owned folder, or {@code null} for normal folders. */
    static String tooltipKey(DirectoryNode node) {
        if (node == null) {
            return null;
        }
        if (node.isIncomingDirectory()) {
            return "mainform.sharetree.incoming.hint";
        }
        if (node.isTempDirectory()) {
            return "mainform.sharetree.temp.hint";
        }
        if (node.isWarningShare()) {
            return "mainform.sharetree.warning.hint";
        }
        return null;
    }

    public void updateUI(){
        super.updateUI();
        setRowHeight(0);
    }
}
