package de.applejuicenet.client.gui.plugins.logviewer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads Logback HTMLLayout output into entries without rendering the raw HTML. */
final class LogParser {
    enum Level { TRACE, DEBUG, INFO, WARN, ERROR;
        static Level of(String text) {
            String name = text.trim().toUpperCase(java.util.Locale.ROOT);
            if (name.equals("FATAL")) return ERROR;
            try { return valueOf(name); } catch (IllegalArgumentException e) { return INFO; }
        }
    }

    record Entry(long relativeMillis, String thread, Level level, String logger, int line, String message, String exception) {
        String shortLogger() {
            int dot = logger.lastIndexOf('.');
            return dot < 0 ? logger : logger.substring(dot + 1);
        }
        Entry withException(String text) { return new Entry(relativeMillis, thread, level, logger, line, message, text); }
    }

    record Result(String sessionStart, List<Entry> entries, int skipped, Map<Level, Integer> counts) {
        int count(Level level) { return counts.getOrDefault(level, 0); }
    }

    private static final Pattern ROW = Pattern.compile("<tr class=\"(?!header)[^\"]*\">(.*?)</tr>", Pattern.DOTALL);
    private static final Pattern EXCEPTION = Pattern.compile("<tr><td class=\"Exception\"[^>]*>(.*?)</td></tr>", Pattern.DOTALL);
    private static final Pattern START = Pattern.compile("<p>Log session start time ([^<]*)</p>");
    private static final Pattern PART = Pattern.compile("<td class=\"([A-Za-z]+)\">(.*?)</td>", Pattern.DOTALL);

    private LogParser() { }

    static Result parse(String html, int limit) {
        Matcher start = START.matcher(html);
        String sessionStart = start.find() ? start.group(1).trim() : "";
        ArrayDeque<Entry> kept = new ArrayDeque<>();
        Map<Level, Integer> counts = new EnumMap<>(Level.class);
        int total = 0;
        Matcher token = Pattern.compile(ROW.pattern() + "|" + EXCEPTION.pattern(), Pattern.DOTALL).matcher(html);
        Entry pending = null;
        while (token.find()) {
            if (token.group(1) != null) {
                if (pending != null) total = keep(kept, counts, pending, limit, total);
                pending = entry(token.group(1));
            } else if (pending != null) {
                pending = pending.withException(text(token.group(2)));
            }
        }
        if (pending != null) total = keep(kept, counts, pending, limit, total);
        return new Result(sessionStart, new ArrayList<>(kept), total - kept.size(), counts);
    }

    private static int keep(ArrayDeque<Entry> kept, Map<Level, Integer> counts, Entry entry, int limit, int total) {
        counts.merge(entry.level(), 1, Integer::sum);
        kept.addLast(entry);
        if (kept.size() > limit) kept.removeFirst();
        return total + 1;
    }

    private static Entry entry(String row) {
        Map<String, String> values = new java.util.HashMap<>();
        Matcher part = PART.matcher(row);
        while (part.find()) values.put(part.group(1), part.group(2));
        return new Entry(number(values.get("RelativeTime")), text(values.getOrDefault("Thread", "")),
                Level.of(values.getOrDefault("Level", "INFO")), text(values.getOrDefault("Logger", "")),
                (int) number(values.get("LineOfCaller")), text(values.getOrDefault("Message", "")), "");
    }

    private static long number(String value) {
        try { return value == null ? 0 : Long.parseLong(value.trim()); } catch (NumberFormatException e) { return 0; }
    }

    private static String text(String html) {
        return html.replace("\n<br />", "\n").replace("<br />", "\n").replace("&nbsp;", " ")
                .replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&#39;", "'")
                .replace("&amp;", "&").stripTrailing();
    }
}
