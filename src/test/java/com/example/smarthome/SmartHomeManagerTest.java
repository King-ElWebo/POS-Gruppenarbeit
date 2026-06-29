package com.example.smarthome;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit-Testklasse für unseren SmartHomeManager.
 * Hier wird das gesamte "Gehirn" der App (Hinzufügen, Entfernen, Filtern mit Streams)
 * detailliert getestet, ohne dass man die Webseite im Browser starten muss.
 */
public class SmartHomeManagerTest {

    private SmartHomeManager manager;
    private SmartLight light;
    private SmartThermostat thermostat;
    private SmartSpeaker speaker;

    /**
     * Bereitet die Objekte vor, bevor jeder Test gestartet wird.
     * So hat jeder Test immer eine saubere, leere Umgebung.
     */
    @BeforeEach
    public void setup() {
        manager = new SmartHomeManager();
        // Leere Geräte zum Testen, die wir nach Bedarf in die Liste des Managers schieben können
        light = new SmartLight(1, "Wohnzimmerlampe", "Wohnzimmer", 15.0, 80, "Warmweiß");
        thermostat = new SmartThermostat(2, "Heizung", "Badezimmer", 45.0, 21.5, 23.0);
        speaker = new SmartSpeaker(3, "Küchenradio", "Küche", 5.0, 40, "Morning Show");
    }

    /**
     * Testet, ob das Hinzufügen (Create) und das Erstellen eines Log-Eintrags funktioniert.
     */
    @Test
    public void testAddDevice() {
        assertEquals(0, manager.getDeviceCount()); // Startet bei 0
        manager.addDevice(light);
        assertEquals(1, manager.getDeviceCount()); // Jetzt ist 1 drin
        assertEquals(light, manager.findDeviceById(1)); // Gerät ist auch wirklich da

        // Test logs
        List<String> logs = manager.getLogs();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).contains("Wohnzimmerlampe wurde hinzugefügt."));
    }

    @Test
    public void testAddNullDeviceThrowsException() {
        assertThrows(SmartHomeException.class, () -> manager.addDevice(null));
    }

    /**
     * Sichert ab, dass IDs im System einzigartig bleiben.
     */
    @Test
    public void testAddDuplicateIdThrowsException() {
        manager.addDevice(light); // ID 1
        SmartLight duplicate = new SmartLight(1, "Andere Lampe", "Wohnzimmer", 10.0, 50, "Weiß"); // Wieder ID 1
        assertThrows(SmartHomeException.class, () -> manager.addDevice(duplicate));
    }

    /**
     * Testet das Löschen (Delete) über eine ID.
     */
    @Test
    public void testRemoveDeviceById() {
        manager.addDevice(light);
        manager.addDevice(thermostat);

        assertEquals(2, manager.getDeviceCount());
        manager.removeDevice(1); // Lampe löschen
        assertEquals(1, manager.getDeviceCount());
        
        // Prüfen, ob die ID 1 wirklich nicht mehr gefunden wird
        assertThrows(SmartHomeException.class, () -> manager.findDeviceById(1));

        List<String> logs = manager.getLogs();
        assertTrue(logs.get(logs.size() - 1).contains("Wohnzimmerlampe wurde entfernt."));
    }

    @Test
    public void testRemoveNonExistingDeviceByIdThrowsException() {
        assertThrows(SmartHomeException.class, () -> manager.removeDevice(99)); // ID gibt's nicht
    }

    @Test
    public void testRemoveDeviceByObject() {
        manager.addDevice(light);
        manager.addDevice(thermostat);

        manager.removeDevice(light);
        assertEquals(1, manager.getDeviceCount());
        assertThrows(SmartHomeException.class, () -> manager.findDeviceById(1));
    }

    /**
     * Testet das Aktualisieren (Update). Eine alte Lampe in der Liste soll 
     * mit einer neuen (veränderten) Lampe überschrieben werden.
     */
    @Test
    public void testUpdateDevice() {
        manager.addDevice(light);

        // Neues Objekt, aber SELBE ID (1)
        SmartLight updatedLight = new SmartLight(1, "Neue Lampe", "Küche", 20.0, 90, "Blau");
        manager.updateDevice(updatedLight);

        // Wir lesen die ID 1 wieder aus der Liste und schauen, ob die neuen Werte da sind
        SmartDevice found = manager.findDeviceById(1);
        assertEquals("Neue Lampe", found.getName());
        assertEquals("Küche", found.getRoom());
        assertEquals(20.0, found.getPowerUsage());

        List<String> logs = manager.getLogs();
        assertTrue(logs.get(logs.size() - 1).contains("Neue Lampe wurde aktualisiert."));
    }

    @Test
    public void testUpdateNonExistingDeviceThrowsException() {
        assertThrows(SmartHomeException.class, () -> manager.updateDevice(light));
    }

    @Test
    public void testUpdateNullDeviceThrowsException() {
        assertThrows(SmartHomeException.class, () -> manager.updateDevice(null));
    }

    @Test
    public void testFindDeviceById() {
        manager.addDevice(light);
        SmartDevice found = manager.findDeviceById(1);
        assertNotNull(found);
        assertEquals(light, found);
    }

    @Test
    public void testFindDeviceByIdNonExistingThrowsException() {
        assertThrows(SmartHomeException.class, () -> manager.findDeviceById(99));
    }

    /**
     * Testet die Suchleiste (case-insensitive = Groß-/Kleinschreibung egal).
     */
    @Test
    public void testSearchByName() {
        manager.addDevice(light);      // "Wohnzimmerlampe"
        manager.addDevice(thermostat); // "Heizung"
        manager.addDevice(speaker);    // "Küchenradio"

        List<SmartDevice> results = manager.searchByName("lampe"); // kleingeschrieben!
        assertEquals(1, results.size());
        assertEquals(light, results.get(0));

        results = manager.searchByName("KÜCHE"); // GROSSGESCHRIEBEN!
        assertEquals(1, results.size());
        assertEquals(speaker, results.get(0));

        results = manager.searchByName(""); // leere suche
        assertEquals(3, results.size());

        results = manager.searchByName(null); // gar keine suche
        assertEquals(3, results.size());
    }

    /**
     * Testet den Raum-Filter.
     */
    @Test
    public void testFilterByRoom() {
        manager.addDevice(light);      // "Wohnzimmer"
        manager.addDevice(thermostat); // "Badezimmer"
        manager.addDevice(speaker);    // "Küche"

        List<SmartDevice> results = manager.filterByRoom("Wohnzimmer");
        assertEquals(1, results.size());
        assertEquals(light, results.get(0));

        results = manager.filterByRoom("wohnzimmer "); // Mit Leerzeichen (Trim!)
        assertEquals(1, results.size());

        results = manager.filterByRoom("");
        assertEquals(3, results.size());
    }

    @Test
    public void testFilterByType() {
        manager.addDevice(light);      // "Light"
        manager.addDevice(thermostat); // "Thermostat"
        manager.addDevice(speaker);    // "Speaker"

        List<SmartDevice> results = manager.filterByType("Light");
        assertEquals(1, results.size());
        assertEquals(light, results.get(0));

        results = manager.filterByType("speaker");
        assertEquals(1, results.size());
        assertEquals(speaker, results.get(0));

        results = manager.filterByType("");
        assertEquals(3, results.size());
    }

    @Test
    public void testFilterByStatus() {
        manager.addDevice(light);
        manager.addDevice(thermostat);
        manager.addDevice(speaker);

        // Wir schalten 2 von 3 Geräten ein
        light.turnOn();
        speaker.turnOn();

        // Suche alle eingeschalteten
        List<SmartDevice> onDevices = manager.filterByStatus(true);
        assertEquals(2, onDevices.size());
        assertTrue(onDevices.contains(light));
        assertTrue(onDevices.contains(speaker));

        // Suche alle ausgeschalteten
        List<SmartDevice> offDevices = manager.filterByStatus(false);
        assertEquals(1, offDevices.size());
        assertTrue(offDevices.contains(thermostat));
    }

    @Test
    public void testFilterFavorites() {
        manager.addDevice(light);
        manager.addDevice(thermostat);
        manager.addDevice(speaker);

        light.setFavorite(true);

        List<SmartDevice> favorites = manager.filterFavorites();
        assertEquals(1, favorites.size());
        assertEquals(light, favorites.get(0));
    }

    /**
     * Testet unsere in-place Sortierfunktion (Alphabetisch).
     */
    @Test
    public void testSortByName() {
        SmartLight a = new SmartLight(10, "B Lampe", "Zimmer", 10.0, 50, "Weiß");
        SmartLight b = new SmartLight(11, "A Lampe", "Zimmer", 10.0, 50, "Weiß");
        SmartLight c = new SmartLight(12, "C Lampe", "Zimmer", 10.0, 50, "Weiß");

        manager.addDevice(a);
        manager.addDevice(b);
        manager.addDevice(c);

        manager.sortByName();

        List<SmartDevice> sorted = manager.getAllDevices();
        // Sollte jetzt alphabetisch A, B, C sein (nicht die Reihenfolge, in der wir sie hinzugefügt haben)
        assertEquals("A Lampe", sorted.get(0).getName());
        assertEquals("B Lampe", sorted.get(1).getName());
        assertEquals("C Lampe", sorted.get(2).getName());
    }

    /**
     * Testet unsere Stromverbrauchs-Sortierung.
     */
    @Test
    public void testSortByPowerUsage() {
        manager.addDevice(light);      // 15.0 W
        manager.addDevice(thermostat); // 45.0 W
        manager.addDevice(speaker);    // 5.0 W

        manager.sortByPowerUsage();

        List<SmartDevice> sorted = manager.getAllDevices();
        assertEquals(speaker, sorted.get(0)); // Wenigster Strom zuerst
        assertEquals(light, sorted.get(1));
        assertEquals(thermostat, sorted.get(2)); // Meister Strom zuletzt
    }

    /**
     * Testet unsere Gruppierungsfunktion (Erzeugt eine Map / Wörterbuch).
     */
    @Test
    public void testGroupByRoom() {
        manager.addDevice(light);      // Wohnzimmer
        manager.addDevice(thermostat); // Badezimmer
        SmartLight secondLight = new SmartLight(4, "Lampe2", "Wohnzimmer", 10, 50, "Weiß");
        manager.addDevice(secondLight);

        Map<String, List<SmartDevice>> grouped = manager.groupByRoom();
        assertEquals(2, grouped.size()); // 2 Räume
        assertEquals(2, grouped.get("Wohnzimmer").size()); // 2 Geräte im Wohnzimmer
        assertEquals(1, grouped.get("Badezimmer").size()); // 1 Gerät im Bad
    }

    @Test
    public void testGroupByType() {
        manager.addDevice(light);
        manager.addDevice(thermostat);
        manager.addDevice(speaker);

        Map<String, List<SmartDevice>> grouped = manager.groupByType();
        assertEquals(3, grouped.size());
        assertEquals(1, grouped.get("Light").size());
        assertEquals(1, grouped.get("Thermostat").size());
        assertEquals(1, grouped.get("Speaker").size());
    }

    /**
     * Testet das Berechnen der Zahlen fürs Dashboard (Summen und Durchschnitte).
     */
    @Test
    public void testPowerUsageStats() {
        // Grenzfall: Liste noch leer
        assertEquals(0.0, manager.getTotalPowerUsage());
        assertEquals(0.0, manager.getAveragePowerUsage());
        assertNull(manager.getHighestPowerUsageDevice());
        assertEquals(0, manager.getActiveDeviceCount());

        manager.addDevice(light);      // 15.0 W
        manager.addDevice(thermostat); // 45.0 W
        manager.addDevice(speaker);    // 5.0 W

        light.turnOn();

        assertEquals(65.0, manager.getTotalPowerUsage()); // 15 + 45 + 5
        assertEquals(65.0 / 3, manager.getAveragePowerUsage(), 0.001); // ca 21.6 Watt
        assertEquals(thermostat, manager.getHighestPowerUsageDevice());
        assertEquals(1, manager.getActiveDeviceCount()); // Nur 1 eingeschaltet
    }

    /**
     * Testet unsere "Königsmethode" (Kombinieren von zwei Stream-Aktionen in einer Kette).
     */
    @Test
    public void testFilterByRoomAndSortByPowerUsage() {
        SmartLight l1 = new SmartLight(10, "L1", "Wohnzimmer", 20.0, 50, "Weiß");
        SmartLight l2 = new SmartLight(11, "L2", "Wohnzimmer", 10.0, 50, "Weiß");
        SmartLight l3 = new SmartLight(12, "L3", "Badezimmer", 5.0, 50, "Weiß");

        manager.addDevice(l1);
        manager.addDevice(l2);
        manager.addDevice(l3);

        List<SmartDevice> result = manager.filterByRoomAndSortByPowerUsage("Wohnzimmer");
        assertEquals(2, result.size()); // Nur die zwei aus dem Wohnzimmer
        assertEquals(l2, result.get(0)); // Das mit 10 W muss VOR dem mit 20 W kommen!
        assertEquals(l1, result.get(1)); 
    }

    @Test
    public void testFavoritesManagement() {
        manager.addDevice(light);
        assertFalse(light.isFavorite());

        manager.markAsFavorite(1);
        assertTrue(light.isFavorite());

        manager.unmarkAsFavorite(1);
        assertFalse(light.isFavorite());
    }
}
