package com.example.smarthome;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SmartHomeManager {

    private final List<SmartDevice> devices = new ArrayList<>();

    public void addDevice(SmartDevice device) {
        if (device == null) {
            throw new IllegalArgumentException("Device darf nicht null sein");
        }
        devices.add(device);
    }

    public void removeDevice(SmartDevice device) {
        devices.remove(device);
    }

    // Überladung für DeviceListView (Löschen per ID)
    public void removeDevice(int id) {
        devices.removeIf(d -> d.getId() == id);
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

    // Methoden für das DashboardView
    public double getTotalPowerUsage() {
        return devices.stream().mapToDouble(SmartDevice::getPowerUsage).sum();
    }

    public double getAveragePowerUsage() {
        return devices.isEmpty() ? 0.0 : getTotalPowerUsage() / devices.size();
    }

    public int getActiveDeviceCount() {
        return (int) devices.stream().filter(SmartDevice::isTurnedOn).count();
    }

    public List<SmartDevice> filterFavorites() {
        return devices.stream().filter(SmartDevice::isFavorite).collect(Collectors.toList());
    }
}