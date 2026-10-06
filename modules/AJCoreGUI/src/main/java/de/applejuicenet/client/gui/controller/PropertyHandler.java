/*
 * Copyright 2006 TKLSoft.de   All rights reserved.
 */

package de.applejuicenet.client.gui.controller;

import de.applejuicenet.client.fassade.ApplejuiceFassade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Properties;

public class PropertyHandler {
    private final Logger logger;
    private HashSet<PropertyChangeListener> listeners = null;
    private String path;
    private Properties props;
    private String beschreibung;
    private boolean inform = true;

    public PropertyHandler(String propertiesLocation, String beschreibung, boolean load)
            throws IllegalArgumentException {
        logger = LoggerFactory.getLogger(getClass());
        try {
            path = propertiesLocation;
            if (beschreibung == null) {
                this.beschreibung = "";
            } else {
                this.beschreibung = beschreibung;
            }

            if (load) {
                reload();
            } else {
                props = new Properties();
            }
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    private void informListener(String identifier, String oldValue, String newValue) {
        if (listeners != null && inform) {
            PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, identifier, oldValue, newValue);

            for (PropertyChangeListener curListener : listeners) {
                curListener.propertyChange(propertyChangeEvent);
            }
        }
    }

    public void allowInform(boolean shouldInform) {
        inform = shouldInform;
    }

    public void put(String identifier, String value) {
        try {
            String oldValue = get(identifier, null);

            props.put(identifier, value);
            informListener(identifier, oldValue, value);
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    public void put(String identifier, int value) {
        put(identifier, Integer.toString(value));
    }

    public void put(String identifier, boolean value) {
        put(identifier, Boolean.toString(value));
    }

    public String get(String identifier, String defaultValue) {
        Object obj = props.get(identifier);

        if (obj == null) {
            return defaultValue;
        } else {
            return obj.toString();
        }
    }

    public Boolean getAsBoolean(String identifier, Boolean defaultValue) {
        String obj = (String) props.get(identifier);

        if (null == obj) {
            return defaultValue;
        }

        if ("true".equalsIgnoreCase(obj) || "false".equalsIgnoreCase(obj)) {
            return Boolean.valueOf(obj);
        } else {
            return defaultValue;
        }
    }

    public Integer getAsInt(String identifier, int defaultValue) {
        String obj = (String) props.get(identifier);

        try {
            return Integer.parseInt(obj);
        } catch (NumberFormatException nfE) {
            return defaultValue;
        }
    }

    public void reload() throws IllegalArgumentException {
        try {
            props = new Properties();
            Properties loaded = new Properties();

            try (InputStream inputStream = Files.newInputStream(Path.of(path))) {
                loaded.load(inputStream);
            } catch (NoSuchFileException e) {
                throw new IllegalArgumentException("PropertyDatei konnte nicht gefunden werden.", e);
            } catch (IOException | IllegalArgumentException e) {
                throw new IllegalArgumentException("Ungueltige PropertyDatei.", e);
            }

            props = loaded;
        } catch (Exception e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
        }
    }

    /**
     * Schreibt in eine Temp-Datei im Zielverzeichnis und ersetzt die Zieldatei erst danach.
     * Bei einem Fehler bleibt die letzte gueltige Datei erhalten.
     */
    public void save() throws IllegalArgumentException {
        Path target = Path.of(path).toAbsolutePath();
        Path directory = target.getParent();
        Path temp = null;

        try {
            Files.createDirectories(directory);
            temp = Files.createTempFile(directory, target.getFileName().toString(), ".tmp");
            try (OutputStream outputStream = Files.newOutputStream(temp)) {
                props.store(outputStream, beschreibung);
            }
            copyPermissions(target, temp);
            try {
                Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            temp = null;
        } catch (IOException e) {
            logger.error(ApplejuiceFassade.ERROR_MESSAGE, e);
            throw new IllegalArgumentException("PropertyDatei konnte nicht gespeichert werden.", e);
        } finally {
            if (temp != null) {
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException e) {
                    logger.warn("Temporaere Datei {} nicht geloescht", temp, e);
                }
            }
        }
    }

    private static void copyPermissions(Path from, Path to) {
        try {
            if (Files.exists(from)) {
                Files.setPosixFilePermissions(to, Files.getPosixFilePermissions(from));
            }
        } catch (UnsupportedOperationException | IOException e) {
            // Dateisystem ohne POSIX-Rechte: Standardrechte der Temp-Datei bleiben
        }
    }

    public boolean addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (listeners == null) {
            listeners = new HashSet<PropertyChangeListener>();
        }

        return listeners.add(propertyChangeListener);
    }

    public boolean removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        if (listeners == null) {
            return false;
        }

        return listeners.remove(propertyChangeListener);
    }
}
