package de.applejuicenet.client.gui.share.tree;

import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.facade.ApplejuiceFacade;
import de.applejuicenet.client.facade.entity.Directory;
import de.applejuicenet.client.facade.entity.ShareEntry;
import de.applejuicenet.client.facade.entity.ShareEntry.SHAREMODE;
import de.applejuicenet.client.facade.shared.AJSettings;
import de.applejuicenet.client.facade.exception.IllegalArgumentException;
import de.applejuicenet.client.gui.components.tree.ApplejuiceNode;
import de.applejuicenet.client.gui.components.treetable.Node;
import de.applejuicenet.client.shared.IconManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * Copyright: General Public License
 */

public class DirectoryNode
        extends DefaultMutableTreeNode
        implements Node, ApplejuiceNode {
    public static final int NOT_SHARED = 0;
    public static final int SHARED_WITH_SUB = 1;
    public static final int SHARED_WITHOUT_SUB = 2;
    public static final int SHARED_SOMETHING = 3;
    public static final int SHARED_SUB = 4;

    private static final Logger logger = LoggerFactory.getLogger(DirectoryNode.class);

    private static Set<ShareEntry> shareDirs = new HashSet<ShareEntry>();
    private static Set<String> warningDirs = Set.of();
    private static Map<String, Icon> icons = new HashMap<String, Icon>();
    private static boolean initialized = false;

    private Directory directory;
    private List<DirectoryNode> children = null;
    private DirectoryNode parent;

    public DirectoryNode(DirectoryNode parent, Directory directory) {
        this.parent = parent;
        this.directory = directory;
    }

    public DirectoryNode() {
        this.parent = null;
        this.directory = null;
        try {
            children = new ArrayList<DirectoryNode>();
            List<Directory> directories = AppleJuiceClient.getAjFacade().getDirectories(null);
            for (Directory curDirectory : directories) {
                children.add(new DirectoryNode(this, curDirectory));
            }
        } catch (IllegalArgumentException e) {
            logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
        }
    }

    public static void setShareDirs(Set<ShareEntry> newShareDirs) {
        shareDirs = newShareDirs;
        warningDirs = collectWarningDirs(newShareDirs);
    }

    private static Set<String> collectWarningDirs(Set<ShareEntry> entries) {
        Set<String> result = new HashSet<String>();

        for (ShareWarningDetector.Warning warning : ShareWarningDetector.findWarnings(entries)) {
            result.add(warning.directory());
        }
        return result;
    }

    /** True when any share exposes a whole drive, the filesystem root or the home directory. */
    public static boolean hasWarningShares() {
        return !warningDirs.isEmpty();
    }

    /** True when this node is one of the shares that triggered the startup warning. */
    public boolean isWarningShare() {
        if (directory == null) {
            return false;
        }
        String nodePath = ShareWarningDetector.trimSeparators(directory.getPath());

        for (String warningDir : warningDirs) {
            if (nodePath.equalsIgnoreCase(ShareWarningDetector.trimSeparators(warningDir))) {
                return true;
            }
        }
        return false;
    }

    /** True when this node is the incoming folder configured in the core. */
    public boolean isIncomingDirectory() {
        return isConfiguredDirectory(true);
    }

    /** True when this node is the temp folder configured in the core. */
    public boolean isTempDirectory() {
        return isConfiguredDirectory(false);
    }

    /** Filesystem root, home directory or system drive; other drives such as a network drive stay shareable. */
    public boolean isCriticalDirectory() {
        return directory != null && ShareWarningDetector.isCritical(directory.getPath());
    }

    private boolean isConfiguredDirectory(boolean incoming) {
        if (directory == null) {
            return false;
        }
        try {
            AJSettings settings = AppleJuiceClient.getAjFacade().getAJSettings();

            if (settings == null) {
                return false;
            }
            return sameDirectory(directory.getPath(), incoming ? settings.getIncomingDir() : settings.getTempDir());
        } catch (RuntimeException e) {
            logger.debug("Configured core directory unavailable", e);
            return false;
        }
    }

    /** Compares two folder paths ignoring case and trailing separators. */
    static boolean sameDirectory(String nodePath, String configuredPath) {
        if (nodePath == null || configuredPath == null) {
            return false;
        }
        String configured = trimSeparators(configuredPath);

        return !configured.isEmpty() && trimSeparators(nodePath).equalsIgnoreCase(configured);
    }

    private static String trimSeparators(String path) {
        int end = path.length();

        while (end > 0 && (path.charAt(end - 1) == '/' || path.charAt(end - 1) == '\\')) {
            end--;
        }
        return path.substring(0, end);
    }

    public int getChildCount() {
        return getChildren().length;
    }

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return false;
    }

    public int getShareMode() {
        if (directory == null) {
            return NOT_SHARED;
        }
        String nodePath = normalizeSharePath(directory.getPath());
        for (ShareEntry curShareEntry : shareDirs) {
            String sharePath = normalizeSharePath(curShareEntry.getDir());

            if (!sharePath.startsWith(nodePath)) {
                continue;
            }
            if (nodePath.length() < sharePath.length()
                    && lastSeparatorIndex(nodePath) == lastSeparatorIndex(sharePath)) {
                continue;
            }
            if (nodePath.equals(sharePath)) {
                return curShareEntry.getShareMode() == SHAREMODE.SUBDIRECTORY
                        ? SHARED_WITH_SUB : SHARED_WITHOUT_SUB;
            }
            return SHARED_SOMETHING;
        }
        int parentShareMode = parent.getShareMode();
        if (parentShareMode == SHARED_WITH_SUB || parentShareMode == SHARED_SUB) {
            return SHARED_SUB;
        }
        return NOT_SHARED;
    }

    /** Lower case and without trailing separators, so {@code C:\} matches the share entry {@code C:}. */
    private static String normalizeSharePath(String path) {
        return ShareWarningDetector.trimSeparators(path).toLowerCase(Locale.ROOT);
    }

    private static int lastSeparatorIndex(String path) {
        int slash = path.lastIndexOf('/');
        int backslash = path.lastIndexOf('\\');

        return Math.max(slash, backslash);
    }

    public Icon getShareModeIcon() {
        if (!initialized) {
            initializeImages();
        }
        switch (getShareMode()) {
            case SHARED_WITH_SUB:
                return icons.get("sharedwsub");
            case SHARED_SUB:
                return icons.get("sharedwsub");
            case SHARED_WITHOUT_SUB:
                return icons.get("sharedwosub");
            case SHARED_SOMETHING:
                return icons.get("somethingshared");
            default:
                return icons.get("notshared");
        }
    }

    public Icon getConvenientIcon() {
        if (directory == null) {
            return null; //rootNode
        }
        if (!initialized) {
            initializeImages();
        }
        switch (directory.getType()) {
            case Directory.TYPE_DESKTOP:
                return icons.get("server");
            case Directory.TYPE_DISKETTE:
                return icons.get("diskette");
            case Directory.TYPE_DRIVE:
                return icons.get("laufwerk");
            case Directory.TYPE_FOLDER:
                return icons.get("tree");
            case Directory.TYPE_COMPUTER:
                return icons.get("server");
            default:
                return null;
        }
    }

    public Directory getDirectory() {
        return directory;
    }

    public String toString() {
        if (directory == null) {
            return "rootNode";
        }
        return directory.getName();
    }

    public ApplejuiceNode addChild(Directory childDirectory) {
        if (children == null) {
            children = new ArrayList<DirectoryNode>();
        }
        DirectoryNode childNode = new DirectoryNode(this, childDirectory);
        children.add(childNode);
        return childNode;
    }

    private void sortChildren() {
        if (children == null || children.size() < 2) {
            return;
        }
        int n = children.size();
        int k;
        for (int i = 0; i < n - 1; i++) {
            k = i;
            for (int j = i + 1; j < n; j++) {
                if (!children.get(j).getDirectory().isFileSystem()) {
                    continue;
                }
                if (children.get(j).toString().compareToIgnoreCase(children.get(
                        k).toString()) < 0) {
                    k = j;
                }
            }
            DirectoryNode tmp = children.get(i);
            children.set(i, children.get(k));
            children.set(k, tmp);
        }
    }

    protected Object[] getChildren() {
        if (children == null) {
            children = new ArrayList<DirectoryNode>();
            try {
                List<Directory> directories = AppleJuiceClient.getAjFacade().
                        getDirectories(directory.getPath());
                for (Directory curDirectory : directories) {
                    children.add(new DirectoryNode(this, curDirectory));
                }
            } catch (IllegalArgumentException e) {
                logger.error(ApplejuiceFacade.ERROR_MESSAGE, e);
            }
            sortChildren();
        }
        return children.toArray(new DirectoryNode[children.size()]);
    }

    private static void initializeImages() {
        initialized = true;
        IconManager im = IconManager.getInstance();
        icons.put("server", im.getIcon("server"));
        icons.put("diskette", im.getIcon("diskette"));
        icons.put("laufwerk", im.getIcon("laufwerk"));
        icons.put("tree", im.getIcon("tree"));
        icons.put("sharedwsub", im.getIcon("sharedwsub"));
        icons.put("sharedwsub", im.getIcon("sharedwsub"));
        icons.put("sharedwosub", im.getIcon("sharedwosub"));
        icons.put("somethingshared", im.getIcon("somethingshared"));
        icons.put("notshared", im.getIcon("notshared"));
        icons.put("warten", im.getIcon("warten"));
    }

    public static int getMaxHeight() {
        if (!initialized) {
            initializeImages();
        }
        int maxHeight = 0;
        for (Icon curIcon : icons.values()) {
            if (curIcon.getIconHeight() > maxHeight) {
                maxHeight = curIcon.getIconHeight();
            }
        }
        return maxHeight;
    }
}
