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

// Einfache Tests fuer Speichern und Laden ueber den SmartHomeFileHandler.
// Es wird eine temporaere Test-Datei verwendet, damit echte Daten nicht ueberschrieben werden.
class SmartHomeFileHandlerTest {

    private Path testFile;
    private SmartHomeFileHandler fileHandler;

    @BeforeEach
    void setUp() throws IOException {
        testFile = Files.createTempFile("devices-test", ".csv");
        fileHandler = new SmartHomeFileHandler(testFile);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(testFile);
    }

    @Test
    void loadDevicesGibtLeereListeZurueckWennDateiFehlt() throws IOException {
        Files.deleteIfExists(testFile);

        List<SmartDevice> devices = fileHandler.loadDevices();

        assertTrue(devices.isEmpty());
    }

    @Test
    void speichernUndLadenGebenGleicheGeraeteZurueck() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Warmweiss");
        SmartThermostat thermostat = new SmartThermostat(2, "Heizung", "Bad", 5.0, 19.5, 21.0);
        SmartSpeaker speaker = new SmartSpeaker(3, "Box", "Kueche", 7.5, 40, "Lieblingslied");
        light.turnOn();
        speaker.setFavorite(true);

        fileHandler.saveDevices(List.of(light, thermostat, speaker));
        List<SmartDevice> geladen = fileHandler.loadDevices();

        assertEquals(3, geladen.size());

        SmartDevice geladeneLampe = geladen.get(0);
        assertEquals("Light", geladeneLampe.getDeviceType());
        assertEquals("Lampe", geladeneLampe.getName());
        assertTrue(geladeneLampe.isTurnedOn());
        assertEquals(80, ((SmartLight) geladeneLampe).getBrightness());
        assertEquals("Warmweiss", ((SmartLight) geladeneLampe).getColor());

        SmartDevice geladenesThermostat = geladen.get(1);
        assertEquals("Thermostat", geladenesThermostat.getDeviceType());
        assertEquals(19.5, ((SmartThermostat) geladenesThermostat).getCurrentTemperature());
        assertEquals(21.0, ((SmartThermostat) geladenesThermostat).getTargetTemperature());

        SmartDevice geladenerSpeaker = geladen.get(2);
        assertEquals("Speaker", geladenerSpeaker.getDeviceType());
        assertTrue(geladenerSpeaker.isFavorite());
        assertEquals(40, ((SmartSpeaker) geladenerSpeaker).getVolume());
        assertEquals("Lieblingslied", ((SmartSpeaker) geladenerSpeaker).getCurrentSong());
    }
}
