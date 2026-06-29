package com.example.smarthome;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Der SmartHomeManager ist das Herzstück der Business-Logik.
 * Er verwaltet alle Geräte (Speichern, Löschen, Suchen, Filtern).
 * Durch die `@Service`-Annotation wird er von Spring Boot automatisch
 * im Hintergrund bereitgestellt und kann in den UIs verwendet werden.
 */
@Service
public class SmartHomeManager {

    // Die interne Liste aller gespeicherten Smart-Home-Geräte.
    private final List<SmartDevice> devices = new ArrayList<>();
    
    // Ein einfaches Log, um alle Aktionen festzuhalten (z.B. "Gerät hinzugefügt").
    private final List<String> logs = new ArrayList<>();

    /**
     * Fügt ein neues Gerät zur Verwaltung hinzu (Create).
     * 
     * @param device Das Gerät, das hinzugefügt werden soll.
     * @throws SmartHomeException wenn das Gerät null ist oder die ID schon vergeben wurde.
     */
    public void addDevice(SmartDevice device) {
        if (device == null) {
            throw new SmartHomeException("Device darf nicht null sein");
        }
        // Verhindert, dass zwei Geräte dieselbe ID bekommen
        for (SmartDevice d : devices) {
            if (d.getId() == device.getId()) {
                throw new SmartHomeException("Gerät mit dieser ID existiert bereits.");
            }
        }
        devices.add(device);
        addLog("Gerät " + device.getName() + " wurde hinzugefügt.");
    }

    /**
     * Entfernt ein Gerät basierend auf seinem Objekt (Delete).
     * 
     * @param device Das zu löschende Gerät.
     */
    public void removeDevice(SmartDevice device) {
        if (device != null) {
            devices.remove(device);
            addLog("Gerät " + device.getName() + " wurde entfernt.");
        }
    }

    /**
     * Entfernt ein Gerät basierend auf seiner ID (Delete).
     * Nutzt die 'findDeviceById'-Methode zum Auffinden.
     * 
     * @param id Die ID des Geräts, das gelöscht werden soll.
     */
    public void removeDevice(int id) {
        SmartDevice device = findDeviceById(id);
        devices.remove(device);
        addLog("Gerät " + device.getName() + " wurde entfernt.");
    }

    /**
     * Aktualisiert ein bestehendes Gerät (Update).
     * Ersetzt das alte Gerät mit derselben ID durch das neue Objekt.
     * 
     * @param device Das bearbeitete Gerät.
     */
    public void updateDevice(SmartDevice device) {
        if (device == null) {
            throw new SmartHomeException("Device darf nicht null sein");
        }
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getId() == device.getId()) {
                devices.set(i, device); // Ersetzt das Objekt an Position i
                addLog("Gerät " + device.getName() + " wurde aktualisiert.");
                return;
            }
        }
        throw new SmartHomeException("Gerät mit ID " + device.getId() + " nicht gefunden.");
    }

    /**
     * Sucht ein bestimmtes Gerät anhand seiner ID (Read).
     * 
     * @param id Die gesuchte ID.
     * @return Das gefundene SmartDevice-Objekt.
     * @throws SmartHomeException wenn keine ID passt.
     */
    public SmartDevice findDeviceById(int id) {
        // Sucht mit Java Streams das erste Gerät, dessen ID passt.
        return devices.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElseThrow(() -> new SmartHomeException("Gerät mit ID " + id + " nicht gefunden."));
    }

    /**
     * Gibt eine unveränderliche Ansicht der Geräteliste zurück.
     */
    public List<SmartDevice> getDevices() {
        return Collections.unmodifiableList(devices);
    }

    /**
     * Zählt, wie viele Geräte insgesamt registriert sind.
     */
    public int getDeviceCount() {
        return devices.size();
    }

    /**
     * Gibt eine *Kopie* aller Geräte zurück. 
     * Dies verhindert, dass externe Klassen wie die UI die Original-Liste manipulieren.
     */
    public List<SmartDevice> getAllDevices() {
        return new ArrayList<>(devices);
    }

    // ==========================================
    // Such- und Filtermethoden (Java Streams)
    // ==========================================

    /**
     * Sucht Geräte, deren Name den übergebenen Text enthält.
     * Groß-/Kleinschreibung wird ignoriert.
     */
    public List<SmartDevice> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllDevices();
        }
        String query = name.toLowerCase().trim();
        return devices.stream()
                .filter(d -> d.getName() != null && d.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    /**
     * Filtert alle Geräte, die sich in einem bestimmten Raum befinden.
     */
    public List<SmartDevice> filterByRoom(String room) {
        if (room == null || room.trim().isEmpty()) {
            return getAllDevices();
        }
        String query = room.toLowerCase().trim();
        return devices.stream()
                .filter(d -> d.getRoom() != null && d.getRoom().toLowerCase().equals(query))
                .collect(Collectors.toList());
    }

    /**
     * Filtert Geräte nach Typ (z.B. "Light" oder "Speaker").
     */
    public List<SmartDevice> filterByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return getAllDevices();
        }
        String query = type.toLowerCase().trim();
        return devices.stream()
                .filter(d -> d.getDeviceType() != null && d.getDeviceType().toLowerCase().equals(query))
                .collect(Collectors.toList());
    }

    /**
     * Filtert nach Status (Ein oder Aus).
     * @param isOn true für eingeschaltete Geräte, false für ausgeschaltete.
     */
    public List<SmartDevice> filterByStatus(boolean isOn) {
        return devices.stream()
                .filter(d -> d.isTurnedOn() == isOn)
                .collect(Collectors.toList());
    }

    /**
     * Gibt eine Liste aller Geräte zurück, die als Favorit markiert sind.
     */
    public List<SmartDevice> filterFavorites() {
        return devices.stream()
                .filter(SmartDevice::isFavorite)
                .collect(Collectors.toList());
    }

    // ==========================================
    // Sortier- und Gruppierungsmethoden
    // ==========================================

    /**
     * Sortiert die originäre Geräteliste alphabetisch nach Namen.
     */
    public void sortByName() {
        devices.sort(Comparator.comparing(SmartDevice::getName, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Sortiert die originäre Geräteliste aufsteigend nach Stromverbrauch.
     */
    public void sortByPowerUsage() {
        devices.sort(Comparator.comparingDouble(SmartDevice::getPowerUsage));
    }

    /**
     * Gruppiert alle Geräte nach ihren Räumen in eine Map.
     * Der Schlüssel ist der Raumname, der Wert ist eine Liste der Geräte im Raum.
     */
    public Map<String, List<SmartDevice>> groupByRoom() {
        return devices.stream().collect(Collectors.groupingBy(SmartDevice::getRoom));
    }

    /**
     * Gruppiert alle Geräte nach Gerätetyp in eine Map.
     */
    public Map<String, List<SmartDevice>> groupByType() {
        return devices.stream().collect(Collectors.groupingBy(SmartDevice::getDeviceType));
    }

    // ==========================================
    // Statistische Auswertungen (fürs Dashboard)
    // ==========================================

    /**
     * Berechnet die Summe des Stromverbrauchs aller registrierten Geräte.
     */
    public double getTotalPowerUsage() {
        return devices.stream().mapToDouble(SmartDevice::getPowerUsage).sum();
    }

    /**
     * Berechnet den durchschnittlichen Stromverbrauch pro Gerät.
     */
    public double getAveragePowerUsage() {
        return devices.isEmpty() ? 0.0 : getTotalPowerUsage() / devices.size();
    }

    /**
     * Sucht das Gerät, das den allerhöchsten Stromverbrauch hat.
     */
    public SmartDevice getHighestPowerUsageDevice() {
        return devices.stream()
                .max(Comparator.comparingDouble(SmartDevice::getPowerUsage))
                .orElse(null);
    }

    /**
     * Zählt, wie viele Geräte im Moment eingeschaltet sind.
     */
    public int getActiveDeviceCount() {
        return (int) devices.stream().filter(SmartDevice::isTurnedOn).count();
    }

    /**
     * Kombinierte Methode: Filtert zuerst nach einem Raum und
     * sortiert das verbleibende Ergebnis dann nach dem Stromverbrauch.
     */
    public List<SmartDevice> filterByRoomAndSortByPowerUsage(String room) {
        return filterByRoom(room).stream()
                .sorted(Comparator.comparingDouble(SmartDevice::getPowerUsage))
                .collect(Collectors.toList());
    }

    // ==========================================
    // Favoriten und Logging
    // ==========================================

    /**
     * Markiert ein Gerät anhand der ID als Favorit und schreibt einen Log-Eintrag.
     */
    public void markAsFavorite(int id) {
        SmartDevice device = findDeviceById(id);
        device.setFavorite(true);
        addLog("Gerät " + device.getName() + " wurde als Favorit markiert.");
    }

    /**
     * Entfernt die Favoritenmarkierung eines Geräts.
     */
    public void unmarkAsFavorite(int id) {
        SmartDevice device = findDeviceById(id);
        device.setFavorite(false);
        addLog("Favoritenmarkierung für Gerät " + device.getName() + " wurde entfernt.");
    }

    /**
     * Fügt dem internen System-Log eine neue Nachricht hinzu.
     */
    public void addLog(String message) {
        if (message != null && !message.trim().isEmpty()) {
            logs.add(message);
        }
    }

    /**
     * Gibt eine Kopie der Logs zurück (damit man von außen nicht in das Original-Log schreiben kann).
     */
    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }
}