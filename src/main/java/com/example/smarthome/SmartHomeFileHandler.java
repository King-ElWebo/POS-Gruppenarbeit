package com.example.smarthome;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class SmartHomeFileHandler {
    private final Path filePath;

    public SmartHomeFileHandler() {
        this(Path.of("devices.csv"));
    }

    public SmartHomeFileHandler(Path filePath) {
        this.filePath = filePath;
    }

    public void saveDevices(List<SmartDevice> devices) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (SmartDevice device : devices) {
                writer.write(deviceToCsv(device));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SmartHomeException("Fehler beim Speichern der Geraete: " + e.getMessage());
        }
    }

    public List<SmartDevice> loadDevices() {
        List<SmartDevice> devices = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return devices;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    devices.add(csvToDevice(line));
                }
            }
        } catch (IOException e) {
            throw new SmartHomeException("Fehler beim Laden der Geraete: " + e.getMessage());
        }

        return devices;
    }

    private String deviceToCsv(SmartDevice device) {
        String base = String.format("%s;%d;%s;%s;%b;%s;%b",
                device.getDeviceType(),
                device.getId(),
                device.getName(),
                device.getRoom(),
                device.isOn(),
                device.getPowerUsage(),
                device.isFavorite());

        if (device instanceof SmartLight light) {
            return base + String.format(";%d;%s", light.getBrightness(), light.getColor());
        }
        if (device instanceof SmartThermostat thermostat) {
            return base + String.format(";%s;%s",
                    thermostat.getCurrentTemperature(),
                    thermostat.getTargetTemperature());
        }
        if (device instanceof SmartSpeaker speaker) {
            return base + String.format(";%d;%s", speaker.getVolume(), speaker.getCurrentSong());
        }

        return base;
    }

    private SmartDevice csvToDevice(String line) {
        String[] parts = line.split(";", -1);
        if (parts.length < 7) {
            throw new SmartHomeException("Ungueltige CSV-Zeile: " + line);
        }

        SmartDevice device = switch (parts[0]) {
            case "Light" -> new SmartLight(
                    Integer.parseInt(parts[1]),
                    parts[2],
                    parts[3],
                    Double.parseDouble(parts[5]),
                    Integer.parseInt(parts[7]),
                    parts[8]);
            case "Thermostat" -> new SmartThermostat(
                    Integer.parseInt(parts[1]),
                    parts[2],
                    parts[3],
                    Double.parseDouble(parts[5]),
                    Double.parseDouble(parts[7]),
                    Double.parseDouble(parts[8]));
            case "Speaker" -> new SmartSpeaker(
                    Integer.parseInt(parts[1]),
                    parts[2],
                    parts[3],
                    Double.parseDouble(parts[5]),
                    Integer.parseInt(parts[7]),
                    parts.length > 8 ? parts[8] : "");
            default -> throw new SmartHomeException("Unbekannter Geraetetyp in CSV: " + parts[0]);
        };

        if (Boolean.parseBoolean(parts[4])) {
            device.turnOn();
        }
        device.setFavorite(Boolean.parseBoolean(parts[6]));

        return device;
    }
}
