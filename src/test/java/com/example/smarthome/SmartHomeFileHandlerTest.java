package com.example.smarthome;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit-Testklasse für unseren SmartHomeFileHandler.
 * Testet, ob Speichern und Laden reibungslos funktionieren.
 * 
 * Trick: Wir erstellen für diese Tests eine echte Datei auf dem Computer,
 * allerdings eine *temporäre*, um unsere echte "devices.csv" vom Projekt
 * nicht versehentlich mit Testdaten zu überschreiben.
 */
class SmartHomeFileHandlerTest {

    private Path testFile;
    private SmartHomeFileHandler fileHandler;

    /**
     * @BeforeEach wird VOR jedem einzelnen @Test-Block ausgeführt.
     * Sorgt für einen sauberen Startzustand.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Erstellt eine versteckte Temp-Datei im Betriebssystem-Ordner
        testFile = Files.createTempFile("devices-test", ".csv");
        // Der Handler arbeitet jetzt mit dieser Fake-Datei
        fileHandler = new SmartHomeFileHandler(testFile);
    }

    /**
     * @AfterEach wird NACH jedem einzelnen @Test-Block ausgeführt.
     * Räumt den Müll auf (löscht die Datei).
     */
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFile);
    }

    /**
     * Testfall: Was passiert, wenn noch nie etwas gespeichert wurde (Datei existiert nicht)?
     * Das Programm darf nicht abstürzen, sondern muss eine leere Liste liefern.
     */
    @Test
    void loadDevicesGibtLeereListeZurueckWennDateiFehlt() throws IOException {
        // Wir löschen die Datei, um den "Ersten Start"-Zustand zu simulieren
        Files.deleteIfExists(testFile);

        List<SmartDevice> devices = fileHandler.loadDevices();

        // Überprüft, ob die Liste tatsächlich leer ist
        assertTrue(devices.isEmpty());
    }

    /**
     * Testfall: Kann der Handler alle unsere Geräte-Typen mit allen ihren Werten
     * in Strings verwandeln, in die Datei schreiben und beim Laden wieder exakt so herstellen?
     */
    @Test
    void speichernUndLadenGebenGleicheGeraeteZurueck() {
        // 1. Geräte mit Testdaten zusammenbauen
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Warmweiss");
        SmartThermostat thermostat = new SmartThermostat(2, "Heizung", "Bad", 5.0, 19.5, 21.0);
        SmartSpeaker speaker = new SmartSpeaker(3, "Box", "Kueche", 7.5, 40, "Lieblingslied");
        
        light.turnOn(); // Testen, ob On/Off gespeichert wird
        speaker.setFavorite(true); // Testen, ob Favoriten gespeichert werden

        // 2. Speichern und sofort wieder Laden
        fileHandler.saveDevices(List.of(light, thermostat, speaker));
        List<SmartDevice> geladen = fileHandler.loadDevices();

        // 3. Überprüfen, ob alles geklappt hat
        assertEquals(3, geladen.size());

        // Lampe überprüfen (Wir wissen, Lampe ist an Stelle 0, weil wir sie zuerst gespeichert haben)
        SmartDevice geladeneLampe = geladen.get(0);
        assertEquals("Light", geladeneLampe.getDeviceType());
        assertEquals("Lampe", geladeneLampe.getName());
        assertTrue(geladeneLampe.isTurnedOn());
        assertEquals(80, ((SmartLight) geladeneLampe).getBrightness()); // Typumwandlung (Cast), um an spezifische Felder zu kommen
        assertEquals("Warmweiss", ((SmartLight) geladeneLampe).getColor());

        // Thermostat überprüfen
        SmartDevice geladenesThermostat = geladen.get(1);
        assertEquals("Thermostat", geladenesThermostat.getDeviceType());
        assertEquals(19.5, ((SmartThermostat) geladenesThermostat).getCurrentTemperature());
        assertEquals(21.0, ((SmartThermostat) geladenesThermostat).getTargetTemperature());

        // Lautsprecher überprüfen
        SmartDevice geladenerSpeaker = geladen.get(2);
        assertEquals("Speaker", geladenerSpeaker.getDeviceType());
        assertTrue(geladenerSpeaker.isFavorite());
        assertEquals(40, ((SmartSpeaker) geladenerSpeaker).getVolume());
        assertEquals("Lieblingslied", ((SmartSpeaker) geladenerSpeaker).getCurrentSong());
    }
}
