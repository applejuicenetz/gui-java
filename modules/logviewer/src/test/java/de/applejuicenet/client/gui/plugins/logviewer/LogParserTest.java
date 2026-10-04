package de.applejuicenet.client.gui.plugins.logviewer;

import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class LogParserTest {
    private static final String HEADER = "<html><body><p>Log session start time Sun Oct 04 22:20:50 CEST 2026</p>\n<table>\n"
            + "<tr class=\"header\">\n<td class=\"RelativeTime\">RelativeTime</td>\n<td class=\"Level\">Level</td>\n</tr>\n";

    private static String row(int ms, String level, String message) {
        return "<tr class=\"" + level.toLowerCase() + " even\">\n<td class=\"RelativeTime\">" + ms + "</td>\n"
                + "<td class=\"Thread\">main</td>\n<td class=\"Level\">" + level + "</td>\n"
                + "<td class=\"Logger\">de.applejuicenet.client.Foo</td>\n<td class=\"LineOfCaller\">12</td>\n"
                + "<td class=\"Message\">" + message + "</td>\n</tr>\n";
    }

    @Test public void parsesEntriesIgnoringHeaderAndDecodesEntities() {
        String html = HEADER + row(5, "INFO", "a &lt;b&gt; &amp; &quot;c&quot; &#39;d&#39;") + row(9, "WARN", "zwei");
        LogParser.Result result = LogParser.parse(html, 100);
        assertEquals(2, result.entries().size());
        assertEquals("a <b> & \"c\" 'd'", result.entries().get(0).message());
        assertEquals(LogParser.Level.WARN, result.entries().get(1).level());
        assertEquals("main", result.entries().get(0).thread());
        assertEquals(12, result.entries().get(0).line());
        assertEquals(5, result.entries().get(0).relativeMillis());
        assertEquals("Foo", result.entries().get(0).shortLogger());
        assertEquals("Sun Oct 04 22:20:50 CEST 2026", result.sessionStart());
    }

    @Test public void attachesExceptionToPrecedingEntryAndKeepsLineBreaks() {
        String html = HEADER + row(5, "ERROR", "kaputt")
                + "<tr><td class=\"Exception\" colspan=\"6\">java.io.IOException: x\n<br />&nbsp;&nbsp;&nbsp;&nbsp;at a.B.&lt;clinit&gt;(B.java:1)\n</td></tr>\n"
                + row(6, "INFO", "weiter");
        LogParser.Result result = LogParser.parse(html, 100);
        assertEquals(2, result.entries().size());
        String exception = result.entries().get(0).exception();
        assertTrue(exception, exception.startsWith("java.io.IOException: x\n    at a.B.<clinit>(B.java:1)"));
        assertEquals("", result.entries().get(1).exception());
    }

    @Test public void toleratesUnfinishedFilesUnknownLevelsAndGarbage() {
        String html = HEADER + row(1, "TRACE", "t") + row(2, "FATAL", "f")
                + "<tr class=\"info odd\">\n<td class=\"RelativeTime\">3</td>\n<td class=\"Message\">abgeschnitten";
        LogParser.Result result = LogParser.parse(html, 100);
        assertEquals(2, result.entries().size());
        assertEquals(LogParser.Level.TRACE, result.entries().get(0).level());
        assertEquals(LogParser.Level.ERROR, result.entries().get(1).level());
        assertTrue(LogParser.parse("", 10).entries().isEmpty());
        assertTrue(LogParser.parse("<<<not a log>>>", 10).entries().isEmpty());
    }

    @Test public void keepsNewestEntriesWhenLimitIsExceeded() {
        StringBuilder html = new StringBuilder(HEADER);
        for (int i = 0; i < 50; i++) html.append(row(i, "INFO", "m" + i));
        LogParser.Result result = LogParser.parse(html.toString(), 10);
        List<LogParser.Entry> entries = result.entries();
        assertEquals(10, entries.size());
        assertEquals("m40", entries.get(0).message());
        assertEquals("m49", entries.get(9).message());
        assertEquals(40, result.skipped());
    }

    @Test public void countsLevelsOverAllEntries() {
        String html = HEADER + row(1, "INFO", "a") + row(2, "WARN", "b") + row(3, "WARN", "c") + row(4, "ERROR", "d");
        LogParser.Result result = LogParser.parse(html, 100);
        assertEquals(1, result.count(LogParser.Level.INFO));
        assertEquals(2, result.count(LogParser.Level.WARN));
        assertEquals(1, result.count(LogParser.Level.ERROR));
        assertEquals(0, result.count(LogParser.Level.DEBUG));
    }
}
