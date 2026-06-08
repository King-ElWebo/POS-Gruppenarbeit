package com.example.smarthome;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SmartHomeDeviceTest {

    @Test
    public void testSmartLightCreation() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        assertEquals(1, light.getId());
        assertEquals("Lampe", light.getName());
        assertEquals("Wohnzimmer", light.getRoom());
        assertEquals(10.0, light.getPowerUsage());
        assertEquals(80, light.getBrightness());
        assertEquals("Blau", light.getColor());
        assertEquals("Light", light.getDeviceType());
        assertFalse(light.isTurnedOn());
        assertFalse(light.isFavorite());
    }

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

    @Test
    public void testDeviceTurnOnOff() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        
        assertFalse(light.isTurnedOn());
        assertEquals("Aus", light.getStatusText());
        assertFalse(light.isOn());

        light.turnOn();
        assertTrue(light.isTurnedOn());
        assertEquals("Ein", light.getStatusText());
        assertTrue(light.isOn());

        light.turnOff();
        assertFalse(light.isTurnedOn());
        assertEquals("Aus", light.getStatusText());
        assertFalse(light.isOn());
    }

    @Test
    public void testDeviceValidation() {
        // ID negativ
        assertThrows(SmartHomeException.class, () -> new SmartLight(-1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau"));
        
        // Name leer
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "", "Wohnzimmer", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "   ", "Wohnzimmer", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, null, "Wohnzimmer", 10.0, 80, "Blau"));

        // Raum leer
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", "", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", "   ", 10.0, 80, "Blau"));
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", null, 10.0, 80, "Blau"));

        // Stromverbrauch negativ
        assertThrows(SmartHomeException.class, () -> new SmartLight(1, "Lampe", "Wohnzimmer", -0.1, 80, "Blau"));

        // Setters Validierung
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        assertThrows(SmartHomeException.class, () -> light.setName(""));
        assertThrows(SmartHomeException.class, () -> light.setRoom(""));
        assertThrows(SmartHomeException.class, () -> light.setPowerUsage(-5.0));
    }

    @Test
    public void testPerformAction() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        assertEquals("Die Lampe leuchtet in Blau mit 80% Helligkeit.", light.performAction());

        SmartThermostat thermostat = new SmartThermostat(2, "Thermostat", "Küche", 15.5, 21.0, 23.0);
        assertEquals("Heizung wird aktiviert, Zieltemperatur: 23.0°C.", thermostat.performAction());
        
        thermostat.setTargetTemperature(20.0);
        assertEquals("Keine Heizung notwendig.", thermostat.performAction());

        SmartSpeaker speaker = new SmartSpeaker(3, "Lautsprecher", "Schlafzimmer", 5.0, 50, "Musik");
        assertEquals("Lautsprecher spielt 'Musik' (Lautstärke: 50).", speaker.performAction());
    }

    @Test
    public void testDailyConsumption() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        assertEquals(240.0, light.calculateDailyConsumption(24));
        assertEquals(0.0, light.calculateDailyConsumption(0));
        assertThrows(SmartHomeException.class, () -> light.calculateDailyConsumption(-1));
    }

    @Test
    public void testSmartLightSpecifics() {
        SmartLight light = new SmartLight(1, "Lampe", "Wohnzimmer", 10.0, 80, "Blau");
        
        // dim()
        light.dim(50);
        assertEquals(50, light.getBrightness());
        assertThrows(SmartHomeException.class, () -> light.dim(-1));
        assertThrows(SmartHomeException.class, () -> light.dim(101));

        // changeColor()
        light.changeColor("Rot");
        assertEquals("Rot", light.getColor());
        assertThrows(SmartHomeException.class, () -> light.changeColor(""));
        assertThrows(SmartHomeException.class, () -> light.changeColor(null));
    }

    @Test
    public void testSmartThermostatSpecifics() {
        SmartThermostat thermostat = new SmartThermostat(2, "Thermostat", "Küche", 15.5, 21.0, 23.0);
        
        // Temperatures validation
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "Thermostat", "Küche", 15.5, -20.1, 23.0));
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "Thermostat", "Küche", 15.5, 50.1, 23.0));
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "Thermostat", "Küche", 15.5, 20.0, -21.0));
        assertThrows(SmartHomeException.class, () -> new SmartThermostat(2, "Thermostat", "Küche", 15.5, 20.0, 51.0));

        // target temperature increase/decrease
        thermostat.increaseTargetTemperature(1.5);
        assertEquals(24.5, thermostat.getTargetTemperature());
        
        thermostat.decreaseTargetTemperature(2.0);
        assertEquals(22.5, thermostat.getTargetTemperature());

        assertThrows(SmartHomeException.class, () -> thermostat.increaseTargetTemperature(30.0));
        assertThrows(SmartHomeException.class, () -> thermostat.decreaseTargetTemperature(50.0));

        // heating needed
        thermostat.setCurrentTemperature(20.0);
        thermostat.setTargetTemperature(22.0);
        assertTrue(thermostat.isHeatingNeeded());

        thermostat.setCurrentTemperature(22.0);
        assertFalse(thermostat.isHeatingNeeded());

        thermostat.setCurrentTemperature(23.0);
        assertFalse(thermostat.isHeatingNeeded());
    }

    @Test
    public void testSmartSpeakerSpecifics() {
        SmartSpeaker speaker = new SmartSpeaker(3, "Lautsprecher", "Schlafzimmer", 5.0, 50, "Musik");
        
        // Volume validation
        assertThrows(SmartHomeException.class, () -> new SmartSpeaker(3, "Lautsprecher", "Schlafzimmer", 5.0, -1, "Musik"));
        assertThrows(SmartHomeException.class, () -> new SmartSpeaker(3, "Lautsprecher", "Schlafzimmer", 5.0, 101, "Musik"));

        // volume increase/decrease
        speaker.increaseVolume();
        assertEquals(60, speaker.getVolume());
        
        speaker.setVolume(95);
        speaker.increaseVolume();
        assertEquals(100, speaker.getVolume()); // Capped

        speaker.decreaseVolume();
        assertEquals(90, speaker.getVolume());

        speaker.setVolume(5);
        speaker.decreaseVolume();
        assertEquals(0, speaker.getVolume()); // Bottomed

        // playSong()
        speaker.playSong("Another One Bites The Dust");
        assertEquals("Another One Bites The Dust", speaker.getCurrentSong());
        
        speaker.playSong(null);
        assertNull(speaker.getCurrentSong());
    }
}
