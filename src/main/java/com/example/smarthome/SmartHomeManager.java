package com.example.smarthome;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmartHomeManager {

    private final List<SmartDevice> devices = new ArrayList<>();
    private final List<String> logs = new ArrayList<>();

    public void addDevice(SmartDevice device) {
        if (device == null) {
            throw new SmartHomeException("Device darf nicht null sein");
        }
        for (SmartDevice d : devices) {
            if (d.getId() == device.getId()) {
                throw new SmartHomeException("Gerät mit dieser ID existiert bereits.");
            }
        }
        devices.add(device);
        addLog("Gerät " + device.getName() + " wurde hinzugefügt.");
    }

    public void removeDevice(SmartDevice device) {
        if (device != null) {
            devices.remove(device);
            addLog("Gerät " + device.getName() + " wurde entfernt.");
        }
    }

    // Überladung für DeviceListView (Löschen per ID)
    public void removeDevice(int id) {
        SmartDevice device = findDeviceById(id);
        devices.remove(device);
        addLog("Gerät " + device.getName() + " wurde entfernt.");
    }

    public void updateDevice(SmartDevice device) {
        if (device == null) {
            throw new SmartHomeException("Device darf nicht null sein");
        }
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getId() == device.getId()) {
                devices.set(i, device);
                addLog("Gerät " + device.getName() + " wurde aktualisiert.");
                return;
            }
        }
        throw new SmartHomeException("Gerät mit ID " + device.getId() + " nicht gefunden.");
    }

    public SmartDevice findDeviceById(int id) {
        return devices.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElseThrow(() -> new SmartHomeException("Gerät mit ID " + id + " nicht gefunden."));
    }

    public List<SmartDevice> getDevices() {
        return Collections.unmodifiableList(devices);
    }

    public int getDeviceCount() {
        return devices.size();
    }

    public List<SmartDevice> getAllDevices() {
        return new ArrayList<>(devices);
    }

    // Such- und Filtermethoden
    public List<SmartDevice> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllDevices();
        }
        String query = name.toLowerCase().trim();
        return devices.stream()
                .filter(d -> d.getName() != null && d.getName().toLowerCase().contains(query))
                .collect(Collectors.toList());
    }

    public List<SmartDevice> filterByRoom(String room) {
        if (room == null || room.trim().isEmpty()) {
            return getAllDevices();
        }
        String query = room.toLowerCase().trim();
        return devices.stream()
                .filter(d -> d.getRoom() != null && d.getRoom().toLowerCase().equals(query))
                .collect(Collectors.toList());
    }

    public List<SmartDevice> filterByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return getAllDevices();
        }
        String query = type.toLowerCase().trim();
        return devices.stream()
                .filter(d -> d.getDeviceType() != null && d.getDeviceType().toLowerCase().equals(query))
                .collect(Collectors.toList());
    }

    public List<SmartDevice> filterByStatus(boolean isOn) {
        return devices.stream()
                .filter(d -> d.isTurnedOn() == isOn)
                .collect(Collectors.toList());
    }

    public List<SmartDevice> filterFavorites() {
        return devices.stream()
                .filter(SmartDevice::isFavorite)
                .collect(Collectors.toList());
    }

    // Sortiermethoden (in-place)
    public void sortByName() {
        devices.sort(Comparator.comparing(SmartDevice::getName, String.CASE_INSENSITIVE_ORDER));
    }

    public void sortByPowerUsage() {
        devices.sort(Comparator.comparingDouble(SmartDevice::getPowerUsage));
    }

    // Gruppierungsmethoden
    public Map<String, List<SmartDevice>> groupByRoom() {
        return devices.stream().collect(Collectors.groupingBy(SmartDevice::getRoom));
    }

    public Map<String, List<SmartDevice>> groupByType() {
        return devices.stream().collect(Collectors.groupingBy(SmartDevice::getDeviceType));
    }

    // Methoden für das DashboardView & Auswertungen
    public double getTotalPowerUsage() {
        return devices.stream().mapToDouble(SmartDevice::getPowerUsage).sum();
    }

    public double getAveragePowerUsage() {
        return devices.isEmpty() ? 0.0 : getTotalPowerUsage() / devices.size();
    }

    public SmartDevice getHighestPowerUsageDevice() {
        return devices.stream()
                .max(Comparator.comparingDouble(SmartDevice::getPowerUsage))
                .orElse(null);
    }

    public int getActiveDeviceCount() {
        return (int) devices.stream().filter(SmartDevice::isTurnedOn).count();
    }

    // Kombinierte Methode
    public List<SmartDevice> filterByRoomAndSortByPowerUsage(String room) {
        return filterByRoom(room).stream()
                .sorted(Comparator.comparingDouble(SmartDevice::getPowerUsage))
                .collect(Collectors.toList());
    }

    // Favoriten-System
    public void markAsFavorite(int id) {
        SmartDevice device = findDeviceById(id);
        device.setFavorite(true);
        addLog("Gerät " + device.getName() + " wurde als Favorit markiert.");
    }

    public void unmarkAsFavorite(int id) {
        SmartDevice device = findDeviceById(id);
        device.setFavorite(false);
        addLog("Favoritenmarkierung für Gerät " + device.getName() + " wurde entfernt.");
    }

    // Logfunktion
    public void addLog(String message) {
        if (message != null && !message.trim().isEmpty()) {
            logs.add(message);
        }
    }

    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }
}