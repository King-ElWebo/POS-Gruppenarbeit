package com.example.smarthome.ui;

import com.example.smarthome.SmartDevice;
import com.example.smarthome.SmartHomeManager;
import com.example.smarthome.SmartLight;
import com.example.smarthome.SmartSpeaker;
import com.example.smarthome.SmartThermostat;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Seite "Neues Geraet": Stellt ein Formular zur Verfügung, um neue Geräte anzulegen.
 * Besonderheit: Je nach ausgewähltem Typ (Lampe, Thermostat, etc.) blenden wir
 * bestimmte Eingabefelder ein oder aus, um das Formular übersichtlich zu halten.
 */
@Route("add-device")
@PageTitle("Neues Geraet")
@Menu(order = 3, icon = "icons/clipboard-check.svg", title = "Geraet hinzufuegen")
public class DeviceFormView extends VerticalLayout {
    
    private final SmartHomeManager manager;

    // --- UI-Komponenten (Eingabefelder) ---
    // ComboBox ist ein Dropdown-Menü
    private final ComboBox<String> typeField = new ComboBox<>("Typ");
    
    // NumberField erlaubt nur Zahlen, TextField erlaubt Text
    private final NumberField idField = new NumberField("ID");
    private final TextField nameField = new TextField("Name");
    private final TextField roomField = new TextField("Raum");
    private final NumberField powerField = new NumberField("Stromverbrauch (W)");

    // Felder, die NUR für die Lampe relevant sind
    private final NumberField brightnessField = new NumberField("Helligkeit (%)");
    private final TextField colorField = new TextField("Farbe");

    // Felder, die NUR für das Thermostat relevant sind
    private final NumberField currentTemperatureField = new NumberField("Aktuelle Temperatur");
    private final NumberField targetTemperatureField = new NumberField("Zieltemperatur");

    // Felder, die NUR für den Lautsprecher relevant sind
    private final NumberField volumeField = new NumberField("Lautstaerke (%)");
    private final TextField songField = new TextField("Song");

    public DeviceFormView(SmartHomeManager manager) {
        this.manager = manager;
        createForm();
    }

    /**
     * Baut das Formular zusammen und fügt alle Felder zur Seite hinzu.
     */
    private void createForm() {
        // Dropdown-Menü füllen
        typeField.setItems("Light", "Thermostat", "Speaker");
        typeField.setValue("Light"); // Standardauswahl
        
        // Listener: Wenn sich der Wert im Dropdown ändert, rufen wir showFieldsForType() auf!
        typeField.addValueChangeListener(e -> showFieldsForType());

        // Wir setzen Platzhalter-Startwerte, damit man beim Testen nicht so viel tippen muss
        brightnessField.setValue(100.0);
        colorField.setValue("Weiss");
        currentTemperatureField.setValue(20.0);
        targetTemperatureField.setValue(22.0);
        volumeField.setValue(50.0);
        songField.setValue("");

        // Erstellt den Speichern-Button und bindet den Klick an saveDevice()
        Button saveButton = new Button("Speichern", event -> saveDevice());
        saveButton.addThemeVariants(ButtonVariant.PRIMARY); // Macht den Button optisch auffälliger (meist blau)

        // add() platziert alle Elemente vertikal auf der Seite
        add(typeField, idField, nameField, roomField, powerField,
                brightnessField, colorField,
                currentTemperatureField, targetTemperatureField,
                volumeField, songField,
                saveButton);

        // Blendet direkt am Anfang Felder aus, die nicht zum Typ "Light" passen
        showFieldsForType();
    }

    /**
     * Blendet die typ-spezifischen Felder je nach Auswahl im Dropdown ein oder aus.
     * Verwendet die Methode setVisible(true/false) der Vaadin-Komponenten.
     */
    private void showFieldsForType() {
        String type = typeField.getValue();
        
        // Logische Ausdrücke prüfen, welcher Typ aktuell im Dropdown gewählt ist
        boolean isLight = "Light".equals(type);
        boolean isThermostat = "Thermostat".equals(type);
        boolean isSpeaker = "Speaker".equals(type);

        // Setzt Sichtbarkeit basierend auf den Variablen oben
        brightnessField.setVisible(isLight);
        colorField.setVisible(isLight);

        currentTemperatureField.setVisible(isThermostat);
        targetTemperatureField.setVisible(isThermostat);

        volumeField.setVisible(isSpeaker);
        songField.setVisible(isSpeaker);
    }

    /**
     * Wird beim Klick auf "Speichern" ausgeführt.
     * Erstellt das Objekt und übergibt es an den Manager.
     */
    private void saveDevice() {
        try {
            SmartDevice device = createDeviceFromFields();
            manager.addDevice(device);
            Notification.show("Geraet gespeichert.");
            clearForm();
        } catch (Exception e) {
            // Hier greift unsere Fehlerbehandlung! 
            // Falls in createDeviceFromFields eine Exception (z.B. SmartHomeException) fliegt,
            // fangen wir sie hier und zeigen sie dem User als rotes Popup.
            Notification.show("Fehler: " + e.getMessage());
        }
    }

    /**
     * Liest die Werte aus den Formularfeldern aus und erstellt mit 'new' das passende Gerät.
     * 
     * @return Das erzeugte SmartDevice (als Light, Thermostat oder Speaker).
     */
    private SmartDevice createDeviceFromFields() {
        // Hilfsmethode requiredNumber checkt, ob das Feld nicht leer gelassen wurde.
        int id = requiredNumber(idField, "ID").intValue();
        String name = nameField.getValue();
        String room = roomField.getValue();
        double powerUsage = requiredNumber(powerField, "Stromverbrauch");

        // Switch-Statement wählt die passende Unterklasse für unser Gerät aus (Polymorphismus)
        return switch (typeField.getValue()) {
            case "Thermostat" -> new SmartThermostat(
                    id, name, room, powerUsage,
                    requiredNumber(currentTemperatureField, "Aktuelle Temperatur"),
                    requiredNumber(targetTemperatureField, "Zieltemperatur"));
            case "Speaker" -> new SmartSpeaker(
                    id, name, room, powerUsage,
                    requiredNumber(volumeField, "Lautstaerke").intValue(),
                    songField.getValue());
            default -> new SmartLight(
                    id, name, room, powerUsage,
                    requiredNumber(brightnessField, "Helligkeit").intValue(),
                    colorField.getValue());
        };
    }

    /**
     * Hilfsmethode, um sicherzustellen, dass in Zahlenfeldern auch wirklich etwas steht.
     * Verhindert NullPointerExceptions, wenn ein Feld komplett leer gelassen wird.
     */
    private Double requiredNumber(NumberField field, String fieldName) {
        Double value = field.getValue();
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " muss ausgefuellt sein.");
        }
        return value;
    }

    /**
     * Setzt die allgemeinen Formularfelder wieder auf leer,
     * damit man direkt das nächste Gerät eintippen kann.
     */
    private void clearForm() {
        idField.clear();
        nameField.clear();
        roomField.clear();
        powerField.clear();
    }
}
