package com.example.smarthome;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Testklasse für alle Geräte-Klassen (Light, Thermostat, Speaker).
 * JUnit-Tests sind extrem wichtig, um sicherzustellen, dass die Kernlogik 
 * der Objekte (Getter, Setter, Validierung) fehlerfrei funktioniert, bevor 
 * man sie in ein UI einbaut.
 */
public class SmartHomeDeviceTest {

    /**
     * @Test markiert eine Methode als JUnit-Test.
     * Dieser Test prüft, ob eine smarte Lampe mit den korrekten Werten
     * erstellt wird und ob die Getter das Richtige zurückgeben.
     */
    @Test
    public void testSmartLightCreation() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        
        // assertEquals(Erwarteter Wert, Tatsächlicher Wert) vergleicht Werte
        assertEquals(1, light.getId());
        assertEquals("Lampe", light.getName());
        assertEquals("Wohnzimmer", light.getRoom());
        assertEquals(10.0, light.getPowerUsage());
        assertEquals(80, light.getBrightness());
        assertEquals("Blau", light.getColor());
        
        // Prüft, ob unser Polymorphismus / getDeviceType() korrekt funktioniert
        assertEquals("Light", light.getDeviceType());
        
        // Neue Geräte müssen immer standardmäßig ausgeschaltet sein
        assertFalse(light.isTurnedOn());
        assertFalse(light.isFavorite());
    }

    /**
     * Prüft die erfolgreiche Erstellung eines Thermostats.
     */
    @Test
    public void testSmartThermostatCreation() {
        SmartThermostat thermostat = new SmartThermostat(2, "Thermostat", "Küche", 15.5, 21.0, 23.0);
        assertEquals(2, thermostat.getId());
        assertEquals("Thermostat", thermostat.getName());
        assertEquals("Küche", thermostat.getRoom());
        assertEquals(15.5, thermostat.getPowerUsage());
        assertEquals(21.0, thermostat.getCurrentTemperature());
        assertEquals(23.0, thermostat.getTargetTemperature());
        assertEquals("Thermostat", thermostat.getDeviceType());
    }

    /**
     * Prüft die erfolgreiche Erstellung eines Lautsprechers.
     */
    @Test
    public void testSmartSpeakerCreation() {
        SmartSpeaker speaker = new SmartSpeaker(3, "Lautsprecher", "Schlafzimmer", 5.0, 50, "Musik");
        assertEquals(3, speaker.getId());
        assertEquals("Lautsprecher", speaker.getName());
        assertEquals("Schlafzimmer", speaker.getRoom());
        assertEquals(5.0, speaker.getPowerUsage());
        assertEquals(50, speaker.getVolume());
        assertEquals("Musik", speaker.getCurrentSong());
        assertEquals("Speaker", speaker.getDeviceType());
    }

    /**
     * Testet, ob das Ein- und Ausschalten eines Geräts korrekt funktioniert.
     */
    @Test
    public void testDeviceTurnOnOff() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        
        assertFalse(light.isTurnedOn());
        assertEquals("Aus", light.getStatusText());
        assertFalse(light.isOn());

        // Einschalten!
        light.turnOn();
        assertTrue(light.isTurnedOn()); // Jetzt muss es true sein!
        assertEquals("Ein", light.getStatusText());
        assertTrue(light.isOn());

        // Und wieder ausschalten
        light.turnOff();
        assertFalse(light.isTurnedOn());
        assertEquals("Aus", light.getStatusText());
        assertFalse(light.isOn());
    }

    /**
     * WICHTIG: Testet unsere "Exception Handling" bzw. "Kapselung".
     * Stellt sicher, dass das System ungültige Eingaben blockiert.
     */
    @Test
    public void testDeviceValidation() {
        // assertThrows prüft, ob wirklich ein Fehler geworfen wird, wenn wir z.B. eine negative ID vergeben.
        // Falls kein Fehler geworfen werden würde, würde der Test ROT anzeigen.
        assertThrows(SmartHomeException.class, () -> new SmartLight(-1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau"));
        
        // Name darf nicht leer sein
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "", "Wohnzimmer", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "   ", "Wohnzimmer", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, null, "Wohnzimmer", 10.0, 80, "Blau"));

        // Raum darf nicht leer sein
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", "", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", "   ", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", null, 10.0, 80, "Blau"));

        // Stromverbrauch negativ (verboten)
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", "Wohnzimmer", -0.1, 80, "Blau"));

        // Test, ob auch die Setter absichern, dass nachträglich nichts Falsches eingestellt wird
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        assertThrows(SmartHomeException.class, () -> light.setName(""));
        assertThrows(SmartHomeException.class, () -> light.setRoom(""));
        assertThrows(SmartHomeException.class, () -> light.setPowerUsage(-5.0));
    }

    /**
     * Testet unsere polymorphe performAction() Methode über alle Geräte hinweg.
     */
    @Test
    public void testPerformAction() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        assertEquals("Die Lampe leuchtet in Blau mit 80% Helligkeit.", light.performAction());

        SmartThermostat thermostat = new SmartThermostat(2, "Thermostat", "Küche", 15.5, 21.0, 23.0);
        assertEquals("Heizung wird aktiviert, Zieltemperatur: 23.0°C.", thermostat.performAction());
        
        thermostat.setTargetTemperature(20.0); // Raum ist 21 Grad warm, Ziel ist 20. Wir müssen nicht heizen!
        assertEquals("Keine Heizung notwendig.", thermostat.performAction());

        SmartSpeaker speaker = new SmartSpeaker(3, "Lautsprecher", "Schlafzimmer", 5.0, 50, "Musik");
        assertEquals("Lautsprecher spielt 'Musik' (Lautstärke: 50).", speaker.performAction());
    }

    /**
     * Prüft die mathematische Stromverbrauch-Berechnung.
     */
    @Test
    public void testDailyConsumption() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        // 10 Watt * 24 Stunden = 240 Wattstunden
        assertEquals(240.0, light.calculateDailyConsumption(24));
        assertEquals(0.0, light.calculateDailyConsumption(0));
        
        // Negative Stunden? Fehler werfen!
        assertThrows(SmartHomeException.class, () -> light.calculateDailyConsumption(-1));
    }

    /**
     * Testet die Methoden, die es NUR bei SmartLight gibt.
     */
    @Test
    public void testSmartLightSpecifics() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        
        // Test der Dimmer-Funktion
        light.dim(50);
        assertEquals(50, light.getBrightness());
        assertThrows(SmartHomeException.class, () -> light.dim(-1));  // Darf nicht unter 0 sein
        assertThrows(SmartHomeException.class, () -> light.dim(101)); // Darf nicht über 100 sein

        // Test der Farb-Funktion
        light.changeColor("Rot");
        assertEquals("Rot", light.getColor());
        assertThrows(SmartHomeException.class, () -> light.changeColor(""));
        assertThrows(SmartHomeException.class, () -> light.changeColor(null));
    }

    /**
     * Testet die Methoden, die es NUR bei SmartThermostat gibt.
     */
    @Test
    public void testSmartThermostatSpecifics() {
        SmartThermostat thermostat = new SmartThermostat(2, "Thermostat", "Küche", 15.5, 21.0, 23.0);
        
        // Temperatureingaben über und unter Limit testen (z.B. -20 und 50)
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "T", "Küche", 15.5, -20.1, 23.0));
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "T", "Küche", 15.5, 50.1, 23.0));
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "T", "Küche", 15.5, 20.0, -21.0));
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "T", "Küche", 15.5, 20.0, 51.0));

        // Zieltemperatur per Tasten (+) oder (-) ändern
        thermostat.increaseTargetTemperature(1.5);
        assertEquals(24.5, thermostat.getTargetTemperature());
        
        thermostat.decreaseTargetTemperature(2.0);
        assertEquals(22.5, thermostat.getTargetTemperature());

        assertThrows(SmartHomeException.class, () -> thermostat.increaseTargetTemperature(30.0));
        assertThrows(SmartHomeException.class, () -> thermostat.decreaseTargetTemperature(50.0));

        // Test: Wird Heizung gebraucht?
        thermostat.setCurrentTemperature(20.0);
        thermostat.setTargetTemperature(22.0);
        assertTrue(thermostat.isHeatingNeeded());

        thermostat.setCurrentTemperature(22.0);
        assertFalse(thermostat.isHeatingNeeded());

        thermostat.setCurrentTemperature(23.0);
        assertFalse(thermostat.isHeatingNeeded());
    }

    /**
     * Testet die Methoden, die es NUR bei SmartSpeaker gibt.
     */
    @Test
    public void testSmartSpeakerSpecifics() {
        SmartSpeaker speaker = new SmartSpeaker(3, "Lautsprecher", "Schlafzimmer", 5.0, 50, "Musik");
        
        // Lautstärkevalidierung
        assertThrows(SmartHomeException.class, () -> new SmartSpeaker(3, "L", "Schlafzimmer", 5.0, -1, "Musik"));
        assertThrows(SmartHomeException.class, () -> new SmartSpeaker(3, "L", "Schlafzimmer", 5.0, 101, "Musik"));

        // increaseVolume() / decreaseVolume() Tests
        speaker.increaseVolume();
        assertEquals(60, speaker.getVolume());
        
        // Lautstärke kann nicht über 100 steigen!
        speaker.setVolume(95);
        speaker.increaseVolume();
        assertEquals(100, speaker.getVolume()); // Sollte abgefangen (Capped) sein bei 100

        speaker.decreaseVolume();
        assertEquals(90, speaker.getVolume());

        // Lautstärke kann nicht unter 0 fallen!
        speaker.setVolume(5);
        speaker.decreaseVolume();
        assertEquals(0, speaker.getVolume()); // Sollte bei 0 enden (Bottomed)

        // Song wechseln
        speaker.playSong("Another One Bites The Dust");
        assertEquals("Another One Bites The Dust", speaker.getCurrentSong());
        
        speaker.playSong(null);
        assertNull(speaker.getCurrentSong());
    }
}
