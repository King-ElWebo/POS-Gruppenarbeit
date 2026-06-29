package com.example.smarthome;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Diese Klasse ist für die dauerhafte Speicherung der Geräte zuständig (Persistenz).
 * Sie speichert die aktuelle Liste der Geräte in eine Textdatei im CSV-Format 
 * (Comma-Separated Values, hier durch Semikolons getrennt) und liest diese 
 * beim Neustart des Programms wieder ein.
 * 
 * Durch die `@Component`-Annotation wird auch diese Klasse von Spring Boot verwaltet.
 */
@Component
public class SmartHomeFileHandler {
    
    // Pfad zur Speicher-Datei
    private final Path filePath;

    /**
     * Standard-Konstruktor. Setzt den Pfad standardmäßig auf "devices.csv"
     * im Hauptverzeichnis des Projekts.
     */
    public SmartHomeFileHandler() {
        this(Path.of("devices.csv"));
    }

    /**
     * Überladener Konstruktor für Tests oder abweichende Dateinamen.
     * @param filePath Der Pfad, unter dem gespeichert wird.
     */
    public SmartHomeFileHandler(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Schreibt eine Liste aller Geräte in die CSV-Datei.
     * Jedes Gerät wird dabei durch 'deviceToCsv' in einen Text-String umgewandelt.
     * 
     * @param devices Die Liste der zu speichernden Geräte.
     */
    public void saveDevices(List<SmartDevice> devices) {
        // try-with-resources: Öffnet die Datei und schließt sie danach automatisch ab
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (SmartDevice device : devices) {
                // Konvertiert das Objekt in eine Zeile und schreibt es in die Datei
                writer.write(deviceToCsv(device));
                writer.newLine(); // Zeilenumbruch für das nächste Gerät
            }
        } catch (IOException e) {
            // Fängt Systemfehler ab und wandelt sie in eine eigene Exception um
            throw new SmartHomeException("Fehler beim Speichern der Geraete: " + e.getMessage());
        }
    }

    /**
     * Liest die CSV-Datei aus und erstellt aus den ausgelesenen Textzeilen 
     * wieder echte Java-Objekte (SmartLight, SmartThermostat, etc.).
     * 
     * @return Eine Liste aller erfolgreich geladenen Geräte.
     */
    public List<SmartDevice> loadDevices() {
        List<SmartDevice> devices = new ArrayList<>();
        
        // Wenn noch keine Datei existiert (z.B. beim ersten Start), gib einfach eine leere Liste zurück.
        if (!Files.exists(filePath)) {
            return devices;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            // Lies Zeile für Zeile, solange es noch Zeilen gibt
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    // Verwandelt die ausgelesene Textzeile wieder in ein SmartDevice
                    devices.add(csvToDevice(line));
                }
            }
        } catch (IOException e) {
            throw new SmartHomeException("Fehler beim Laden der Geraete: " + e.getMessage());
        }

        return devices;
    }

    /**
     * Hilfsmethode: Wandelt ein Java-Objekt in einen kommagetrennten (Semikolon) String um.
     * Die ersten Felder (Typ, ID, Name, Raum, Status, Verbrauch, Favorit) haben alle gemeinsam.
     * Danach werden je nach Gerätetyp noch die Spezialfelder drangehängt.
     * 
     * @param device Das Gerät, das konvertiert werden soll.
     * @return Ein String im Format "Typ;ID;Name;Raum;..."
     */
    private String deviceToCsv(SmartDevice device) {
        // Basisdaten, die in der Klasse SmartDevice definiert sind
        String base = String.format("%s;%d;%s;%s;%b;%s;%b",
                device.getDeviceType(),
                device.getId(),
                device.getName(),
                device.getRoom(),
                device.isOn(),
                device.getPowerUsage(),
                device.isFavorite());

        // Pattern Matching für instanceof (Java Feature): 
        // Prüft den Typ und wandelt ihn automatisch in die Variable (z.B. 'light') um.
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

        return base; // Fallback, falls der Typ unbekannt ist
    }

    /**
     * Hilfsmethode: Zerlegt eine Textzeile aus der CSV-Datei in ihre Bestandteile
     * und "baut" daraus mit 'new' das richtige Java-Objekt zusammen.
     * 
     * @param line Die eingelesene Textzeile.
     * @return Das fertig zusammengebaute Gerät.
     */
    private SmartDevice csvToDevice(String line) {
        // Zerschneidet die Textzeile bei jedem Semikolon in ein Array
        String[] parts = line.split(";", -1);
        
        // Eine korrekte Zeile muss immer mindestens 7 Basis-Informationen haben
        if (parts.length < 7) {
            throw new SmartHomeException("Ungueltige CSV-Zeile: " + line);
        }

        // Switch-Statement: Erkennt anhand des ersten Wortes in der CSV (parts[0]), welches Gerät erstellt werden muss.
        SmartDevice device = switch (parts[0]) {
            case "Light" -> new SmartLight(
                    Integer.parseInt(parts[1]), // ID
                    parts[2],                   // Name
                    parts[3],                   // Raum
                    Double.parseDouble(parts[5]), // Verbrauch
                    Integer.parseInt(parts[7]), // Helligkeit (ab hier typspezifisch)
                    parts[8]);                  // Farbe
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
                    parts.length > 8 ? parts[8] : ""); // Sichert ab, falls der Songtext leer war
            default -> throw new SmartHomeException("Unbekannter Geraetetyp in CSV: " + parts[0]);
        };

        // Rekonstruiert den Einschalt-Status
        if (Boolean.parseBoolean(parts[4])) {
            device.turnOn();
        }
        
        // Rekonstruiert den Favoriten-Status
        device.setFavorite(Boolean.parseBoolean(parts[6]));

        return device;
    }
}
