package de.applejuicenet.client.gui.controller;

import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class LanguageSelectorTest {
    private LanguageSelector language(String name) {
        return new LanguageSelector(Paths.get("../../resources/language/" + name + ".properties")
                .toAbsolutePath().normalize().toString());
    }

    @Test
    public void missingGermanKeysUseEnglish() {
        LanguageSelector german = language("deutsch");
        assertEquals("01", german.getFirstAttrbuteByTagName("mainform.N11.caption"));
        assertEquals("Deutsch", german.getFirstAttrbuteByTagName("Languageinfo.name"));
    }

    @Test
    public void removedLanguagesUseEnglish() {
        assertEquals("English", language("tuerkce").getFirstAttrbuteByTagName("Languageinfo.name"));
        assertEquals("English", language("italiano").getFirstAttrbuteByTagName("Languageinfo.name"));
    }

    @Test
    public void missingLanguageFileUsesEnglish() {
        LanguageSelector missing = language("missing-language");
        assertEquals("English", missing.getFirstAttrbuteByTagName("Languageinfo.name"));
        assertEquals("Paste", missing.getFirstAttrbuteByTagName("javagui.downloadform.einfuegen"));
    }

    @Test
    public void existingTranslationsKeepPriority() {
        assertEquals("Einfügen", language("deutsch").getFirstAttrbuteByTagName("javagui.downloadform.einfuegen"));
    }

    @Test
    public void readsLanguageNameWithoutSwitchingLanguage() throws Exception {
        File german = Paths.get("../../resources/language/deutsch.properties").toAbsolutePath().normalize().toFile();
        assertEquals("Deutsch", LanguageSelector.readLanguageName(german));
    }
}
