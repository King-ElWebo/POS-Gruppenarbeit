package com.example.smarthome;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class SmartHomeFileHandler {
    private final String filePath = "devices.csv";

    public void saveDevices(List<SmartDevice> devices) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (SmartDevice device : devices) {
                writer.write(deviceToCsv(device));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SmartHomeException("Fehler beim Speichern der Geräte: " + e.getMessage());
        }
    }

    public List<SmartDevice> loadDevices() {
        List<SmartDevice> devices = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return devices;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    devices.add(csvToDevice(line));
                }
            }
        } catch (IOException e) {
            throw new SmartHomeException("Fehler beim Laden der Geräte: " + e.getMessage());
        }
        return devices;
    }

    private String deviceToCsv(SmartDevice device) {
        String base = String.format("%s;%d;%s;%s;%b;%s;%b",
                device.getDeviceType(), device.getId(), device.getName(),
                device.getRoom(), device.isOn(), String.valueOf(device.getPowerUsage()), device.isFavorite());

        if (device instanceof SmartLight light) {
            return base + String.format(";%d;%s", light.getBrightness(), light.getColor());
        } else if (device instanceof SmartThermostat thermostat) {
            return base + String.format(";%s;%s", String.valueOf(thermostat.getCurrentTemperature()), String.valueOf(thermostat.getTargetTemperature()));
        } else if (device instanceof SmartSpeaker speaker) {
            return base + String.format(";%d;%s", speaker.getVolume(), speaker.getCurrentSong());
        }
        return base;
    }

    private SmartDevice csvToDevice(String line) {
        String[] parts = line.split(";");
        String type = parts[0];
        
        SmartDevice device = switch (type) {
            case "Light" -> new SmartLight(Integer.parseInt(parts[1]), parts[2], parts[3],
                    Double.parseDouble(parts[5]), Integer.parseInt(parts[7]), parts[8]);
            case "Thermostat" -> new SmartThermostat(Integer.parseInt(parts[1]), parts[2], parts[3],
                    Double.parseDouble(parts[5]), Double.parseDouble(parts[7]), Double.parseDouble(parts[8]));
            case "Speaker" -> new SmartSpeaker(Integer.parseInt(parts[1]), parts[2], parts[3],
                    Double.parseDouble(parts[5]), Integer.parseInt(parts[7]), parts.length > 8 ? parts[8] : "");
            default -> throw new SmartHomeException("Unbekannter Gerätetyp in CSV: " + type);
        };

        if (Boolean.parseBoolean(parts[4])) device.turnOn();
        device.setFavorite(Boolean.parseBoolean(parts[6]));
        
        return device;
    }
}