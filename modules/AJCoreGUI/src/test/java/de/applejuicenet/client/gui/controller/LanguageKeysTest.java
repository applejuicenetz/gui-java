package de.applejuicenet.client.gui.controller;

import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

/** Every language key used in the GUI code must exist in the English and German language files. */
public class LanguageKeysTest {
    private static final Path LANGUAGE = Paths.get("../../resources/language").toAbsolutePath().normalize();
    private static final Path SOURCES = Paths.get("src/main/java").toAbsolutePath().normalize();
    private static final Pattern CALL = Pattern.compile(
            "(?:GuiText\\.(?:text|textOr|label|button|checkBox|menuItem)|getFirstAttributeByTagName)\\(\\s*\"([A-Za-z0-9_.]+)\"");

    private static Properties load(String name) throws IOException {
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(LANGUAGE.resolve(name + ".properties"), StandardCharsets.UTF_8)) {
            properties.load(reader);
        }
        return properties;
    }

    private static Set<String> usedKeys() throws IOException {
        Set<String> keys = new TreeSet<>();
        try (Stream<Path> files = Files.walk(SOURCES)) {
            for (Path file : (Iterable<Path>) files.filter(p -> p.toString().endsWith(".java"))::iterator) {
                Matcher matcher = CALL.matcher(Files.readString(file, StandardCharsets.UTF_8));
                while (matcher.find()) {
                    keys.add(matcher.group(1));
                }
            }
        }
        return keys;
    }

    private void assertAllPresent(String language) throws IOException {
        Properties properties = load(language);
        Set<String> missing = new TreeSet<>();
        for (String key : usedKeys()) {
            String value = properties.getProperty(key);
            if (value == null || value.isBlank()) {
                missing.add(key);
            }
        }
        assertTrue(language + " is missing keys: " + missing, missing.isEmpty());
    }

    @Test
    public void englishHasAllUsedKeys() throws IOException {
        assertAllPresent("english");
    }

    @Test
    public void germanHasAllKeysTheCodeUses() throws IOException {
        Properties german = load("deutsch");
        Properties english = load("english");
        Set<String> missing = new TreeSet<>();
        for (String key : usedKeys()) {
            if (english.getProperty(key) != null && german.getProperty(key) == null) {
                missing.add(key);
            }
        }
        assertTrue("deutsch is missing keys: " + missing, missing.isEmpty());
    }

    @Test
    public void searchKeysAreEnglishNamed() throws IOException {
        Properties english = load("english");
        for (String key : new String[] {
                "javagui.searchform.searchedclients", "javagui.searchform.foundfiles", "javagui.searchform.opensearches",
                "javagui.shareform.searchrequests", "mainform.searchsheet.caption"}) {
            assertTrue(key + " missing", english.getProperty(key) != null);
        }
        for (String old : new String[] {
                "javagui.searchform.durchsuchteclients", "javagui.searchform.gefundenedateien",
                "javagui.searchform.offenesuchen", "javagui.shareform.suchanfragen", "mainform.seachsheet.caption"}) {
            assertTrue(old + " must be gone", english.getProperty(old) == null);
        }
    }

    @Test
    public void placeholdersMatchBetweenLanguages() throws IOException {
        Properties german = load("deutsch");
        Properties english = load("english");
        Pattern placeholder = Pattern.compile("%[ds]|%i|\\{\\d+}");
        Set<String> mismatched = new TreeSet<>();
        for (String key : german.stringPropertyNames()) {
            String en = english.getProperty(key);
            if (en == null) {
                continue;
            }
            if (count(placeholder, german.getProperty(key)) != count(placeholder, en)) {
                mismatched.add(key);
            }
        }
        assertTrue("placeholder count differs for: " + mismatched, mismatched.isEmpty());
    }

    private static int count(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        int n = 0;
        while (matcher.find()) {
            n++;
        }
        return n;
    }
}
