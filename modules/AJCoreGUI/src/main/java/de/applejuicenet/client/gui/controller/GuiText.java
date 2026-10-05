package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.gui.listener.LanguageListener;
import javax.swing.*;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Shared language bindings for GUI and plugin controls; never retains disposed views. */
public final class GuiText {
    private record Binding(WeakReference<JComponent> component, String[] keys, Object[] arguments, boolean tooltip) { }
    private static final List<Binding> bindings = new ArrayList<>();
    private static final List<WeakReference<JComponent>> callbacks = new ArrayList<>();

    public static void onLanguageChange(JComponent owner, Runnable callback) {
        LanguageSelector.getInstance().addLanguageListener(LISTENER);
        owner.putClientProperty("GuiText.languageCallback", callback);
        callbacks.add(new WeakReference<>(owner));
    }
    private static final LanguageListener LISTENER = () -> {
        if (SwingUtilities.isEventDispatchThread()) refresh();
        else SwingUtilities.invokeLater(GuiText::refresh);
    };

    private GuiText() { }

    public static Locale locale() {
        return "deutsch".equalsIgnoreCase(LanguageSelector.getInstance()
                .getFirstAttrbuteByTagName("Languageinfo.name")) ? Locale.GERMANY : Locale.ENGLISH;
    }

    public static String text(String key, Object... arguments) {
        String pattern = LanguageSelector.getInstance().getFirstAttrbuteByTagName(key);
        return arguments.length == 0 ? pattern : new MessageFormat(pattern, locale()).format(arguments);
    }

    public static JLabel label(String key) {
        JLabel label = new JLabel();
        setText(label, key);
        return label;
    }

    public static JButton button(String key) {
        JButton button = new JButton();
        setText(button, key);
        return button;
    }

    public static JCheckBox checkBox(String key) {
        JCheckBox box = new JCheckBox();
        setText(box, key);
        return box;
    }

    public static JMenuItem menuItem(String key) {
        JMenuItem item = new JMenuItem();
        setText(item, key);
        return item;
    }

    public static JComboBox<String> combo(String... keys) {
        JComboBox<String> combo = new JComboBox<>();
        bind(combo, keys, new Object[0], false);
        return combo;
    }

    public static void setText(JComponent component, String key, Object... arguments) {
        bind(component, new String[]{key}, arguments, false);
    }

    public static void clearTextBinding(JComponent component) {
        bindings.removeIf(binding -> binding.component().get() == component && !binding.tooltip());
    }

    public static void tooltip(JComponent component, String key, Object... arguments) {
        bind(component, new String[]{key}, arguments, true);
    }

    private static void bind(JComponent component, String[] keys, Object[] arguments, boolean tooltip) {
        LanguageSelector.getInstance().addLanguageListener(LISTENER);
        bindings.removeIf(b -> b.component().get() == null
                || (b.component().get() == component && b.tooltip() == tooltip));
        Binding binding = new Binding(new WeakReference<>(component), keys.clone(), arguments.clone(), tooltip);
        bindings.add(binding);
        apply(binding);
    }

    public static void refreshLanguage() {
        if (SwingUtilities.isEventDispatchThread()) refresh();
        else SwingUtilities.invokeLater(GuiText::refresh);
    }

    private static void refresh() {
        bindings.removeIf(b -> b.component().get() == null);
        for (Binding binding : List.copyOf(bindings)) apply(binding);
        callbacks.removeIf(reference -> reference.get() == null);
        for (var reference : List.copyOf(callbacks)) {
            JComponent owner = reference.get();
            if (owner != null && owner.getClientProperty("GuiText.languageCallback") instanceof Runnable callback) {
                callback.run();
            }
        }
    }

    private static void apply(Binding binding) {
        JComponent component = binding.component().get();
        if (component == null) return;
        String value = text(binding.keys()[0], binding.arguments());
        if (binding.tooltip()) component.setToolTipText(value);
        else if (component instanceof JLabel label) label.setText(value);
        else if (component instanceof AbstractButton button) button.setText(value);
        else if (component instanceof JComboBox<?> combo) {
            int selected = combo.getSelectedIndex();
            String[] values = java.util.Arrays.stream(binding.keys()).map(GuiText::text).toArray(String[]::new);
            if (combo.getItemCount() == values.length) {
                boolean same = true;
                for (int i = 0; i < values.length; i++) same &= values[i].equals(combo.getItemAt(i));
                if (same) return;
            }
            java.awt.event.ActionListener[] listeners = combo.getActionListeners();
            for (var listener : listeners) combo.removeActionListener(listener);
            @SuppressWarnings("unchecked") JComboBox<String> strings = (JComboBox<String>) combo;
            strings.setModel(new DefaultComboBoxModel<>(values));
            if (selected >= 0) strings.setSelectedIndex(Math.min(selected, values.length - 1));
            for (var listener : listeners) combo.addActionListener(listener);
        }
    }
}
