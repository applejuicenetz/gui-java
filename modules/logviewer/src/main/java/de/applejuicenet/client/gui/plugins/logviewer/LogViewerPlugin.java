package de.applejuicenet.client.gui.plugins.logviewer;

import de.applejuicenet.client.gui.controller.GuiText;
import de.applejuicenet.client.AppleJuiceClient;
import de.applejuicenet.client.gui.DialogLocation;
import de.applejuicenet.client.gui.plugins.PluginConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

/** Shows Logback HTML logs as a filterable list; keeps the 20 newest log files. */
public class LogViewerPlugin extends PluginConnector {
    static final int MAX_LOG_FILES = 20;
    static final int MAX_ENTRIES = 5000;
    private static final Logger LOGGER = LoggerFactory.getLogger(LogViewerPlugin.class);

    private final Path directory;
    private final Predicate<File> active;
    private final DefaultListModel<File> files = new DefaultListModel<>();
    private final JList<File> list = new JList<>(files);
    private final LogEntryTable entries = new LogEntryTable();
    private final JLabel summary = GuiText.label("plugins.logviewer.empty");
    private final JLabel notice = new JLabel(" ");
    private final JComboBox<String> levels = GuiText.combo("plugins.logviewer.all", "plugins.logviewer.info", "plugins.logviewer.warnings", "plugins.logviewer.errorsonly");
    private final JTextField search = new JTextField(22);
    private SwingWorker<LogParser.Result, Void> loader;

    public LogViewerPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                           Map<String, ImageIcon> availableIcons) {
        this(pluginsProperties, languageFiles, icon, availableIcons,
                Path.of(AppleJuiceClient.getPath(), "logs"), LogFileProtection::isActive);
    }

    LogViewerPlugin(Properties pluginsProperties, Map<String, Properties> languageFiles, ImageIcon icon,
                    Map<String, ImageIcon> availableIcons, Path directory, Predicate<File> active) {
        super(pluginsProperties, languageFiles, icon, availableIcons);
        this.directory = directory;
        this.active = active;
        buildUi();
        reload();
    }

    private void buildUi() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        list.setCellRenderer(new LogFileRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) display(list.getSelectedValue()); });

        JButton refresh = GuiText.button("plugins.logviewer.refresh");
        refresh.addActionListener(e -> reload());
        JButton delete = GuiText.button("plugins.logviewer.delete");
        delete.addActionListener(e -> deleteSelected());
        JPanel listButtons = new JPanel(new GridLayout(1, 2, 6, 0));
        listButtons.add(refresh);
        listButtons.add(delete);
        JPanel left = new JPanel(new BorderLayout(0, 8));
        JLabel title = GuiText.label("plugins.logviewer.title");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        left.add(title, BorderLayout.NORTH);
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        left.add(listButtons, BorderLayout.SOUTH);
        left.setPreferredSize(new Dimension(270, 100));

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JLabel levelLabel = GuiText.label("plugins.logviewer.level");
        levelLabel.setLabelFor(levels);
        JLabel searchLabel = GuiText.label("plugins.logviewer.search");
        searchLabel.setLabelFor(search);
        tools.add(levelLabel);
        tools.add(levels);
        tools.add(searchLabel);
        tools.add(search);
        levels.addActionListener(e -> entries.setMinimumLevel(selectedLevel()));
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { entries.setFilterText(search.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { entries.setFilterText(search.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { entries.setFilterText(search.getText()); }
        });

        summary.setFont(summary.getFont().deriveFont(Font.BOLD, 15f));
        JPanel header = new JPanel(new GridLayout(0, 1, 0, 6));
        header.add(summary);
        header.add(tools);
        JPanel right = new JPanel(new BorderLayout(0, 8));
        right.add(header, BorderLayout.NORTH);
        right.add(entries, BorderLayout.CENTER);
        notice.setForeground(UIManager.getColor("Label.disabledForeground") == null ? Color.GRAY : UIManager.getColor("Label.disabledForeground"));
        notice.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                UIManager.getColor("Separator.foreground") == null ? Color.LIGHT_GRAY : UIManager.getColor("Separator.foreground")));
        right.add(notice, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
    }

    private LogParser.Level selectedLevel() {
        return switch (levels.getSelectedIndex()) {
            case 1 -> LogParser.Level.INFO;
            case 2 -> LogParser.Level.WARN;
            case 3 -> LogParser.Level.ERROR;
            default -> LogParser.Level.TRACE;
        };
    }

    /** Applies the retention rule, then lists what remains. */
    void reload() {
        File selected = list.getSelectedValue();
        int deleted = 0;
        int failed = 0;
        List<File> remaining = List.of();
        try {
            LogRetention.Result result = LogRetention.clean(directory, MAX_LOG_FILES, active);
            remaining = result.files();
            deleted = result.deleted();
            failed = result.failed();
        } catch (IOException e) {
            LOGGER.warn("LogViewer: Logordner nicht lesbar: {}", directory, e);
        }
        files.clear();
        remaining.stream().sorted((a, b) -> Long.compare(b.lastModified(), a.lastModified())).forEach(files::addElement);
        if (deleted > 0) LOGGER.info("LogViewer: {} ältere Logdateien gelöscht, {} behalten", deleted, files.size());
        if (deleted > 0) GuiText.setText(notice, "plugins.logviewer.retentiondeleted", deleted, MAX_LOG_FILES);
        else if (failed > 0) GuiText.setText(notice, "plugins.logviewer.retentionfailed", failed);
        else GuiText.setText(notice, "plugins.logviewer.retention", MAX_LOG_FILES);
        if (selected != null && files.contains(selected)) list.setSelectedValue(selected, true);
        else if (!files.isEmpty()) list.setSelectedIndex(0);
        else { entries.setEntries(List.of()); GuiText.setText(summary, "plugins.logviewer.nofiles"); }
    }

    private void display(File file) {
        if (loader != null) loader.cancel(true);
        if (file == null) { entries.setEntries(List.of()); GuiText.setText(summary, "plugins.logviewer.empty"); return; }
        GuiText.setText(summary, "plugins.logviewer.loading", file.getName());
        loader = new SwingWorker<>() {
            @Override protected LogParser.Result doInBackground() throws IOException {
                return LogParser.parse(Files.readString(file.toPath(), StandardCharsets.UTF_8), MAX_ENTRIES);
            }
            @Override protected void done() {
                if (isCancelled()) return;
                try {
                    LogParser.Result r = get();
                    entries.setEntries(r.entries());
                    GuiText.setText(summary, r.skipped() > 0 ? "plugins.logviewer.summarylimited" : "plugins.logviewer.summary",
                            file.getName(), r.count(LogParser.Level.ERROR), r.count(LogParser.Level.WARN),
                            r.entries().size() + r.skipped(), MAX_ENTRIES);
                } catch (Exception e) {
                    entries.setEntries(List.of());
                    GuiText.setText(summary, "plugins.logviewer.unreadable", file.getName());
                    LOGGER.warn("LogViewer: Lesen fehlgeschlagen: {}", file, e);
                }
            }
        };
        loader.execute();
    }

    private void deleteSelected() {
        File file = list.getSelectedValue();
        if (file == null) return;
        if (active.test(file)) {
            JOptionPane.showMessageDialog(DialogLocation.getReference(this), GuiText.text("plugins.logviewer.active"),
                    GuiText.text("plugins.logviewer.deletetitle"), JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int answer = JOptionPane.showConfirmDialog(DialogLocation.getReference(this),
                GuiText.text("plugins.logviewer.confirmdelete", file.getName()), GuiText.text("plugins.logviewer.deletetitle"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) return;
        try {
            Files.deleteIfExists(file.toPath());
            reload();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(DialogLocation.getReference(this), GuiText.text("plugins.logviewer.deletefailed", e.getMessage()),
                    GuiText.text("plugins.logviewer.deletetitle"), JOptionPane.ERROR_MESSAGE);
        }
    }

    JList<File> fileList() { return list; }
    LogEntryTable entryTable() { return entries; }

    private final class LogFileRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> l, Object value, int index, boolean selected, boolean focus) {
            File file = (File) value;
            String when = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, GuiText.locale()).format(new Date(file.lastModified()));
            String size = file.length() < 1024 ? file.length() + " B" : String.format(GuiText.locale(), "%.0f KiB", file.length() / 1024.0);
            super.getListCellRendererComponent(l, "<html><b>" + when + "</b><br><small>" + size
                    + (active.test(file) ? GuiText.text("plugins.logviewer.running") : "") + "</small></html>", index, selected, focus);
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            setToolTipText(file.getAbsolutePath());
            return this;
        }
    }

    @Override public void fireLanguageChanged() { GuiText.refreshLanguage(); }
    @Override public void registerSelected() { reload(); }
    @Override public void fireContentChanged(DATALISTENER_TYPE type, Object content) { }
}
