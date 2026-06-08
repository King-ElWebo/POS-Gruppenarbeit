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

// Seite "Neues Geraet": Formular zum Anlegen von Light, Thermostat oder Speaker.
// Je nach gewaehltem Typ werden nur die passenden Felder angezeigt.
@Route("add-device")
@PageTitle("Neues Geraet")
@Menu(order = 3, icon = "icons/clipboard-check.svg", title = "Geraet hinzufuegen")
public class DeviceFormView extends VerticalLayout {
    private final SmartHomeManager manager;

    private final ComboBox<String> typeField = new ComboBox<>("Typ");
    private final NumberField idField = new NumberField("ID");
    private final TextField nameField = new TextField("Name");
    private final TextField roomField = new TextField("Raum");
    private final NumberField powerField = new NumberField("Stromverbrauch (W)");

    // Felder nur fuer Light
    private final NumberField brightnessField = new NumberField("Helligkeit (%)");
    private final TextField colorField = new TextField("Farbe");

    // Felder nur fuer Thermostat
    private final NumberField currentTemperatureField = new NumberField("Aktuelle Temperatur");
    private final NumberField targetTemperatureField = new NumberField("Zieltemperatur");

    // Felder nur fuer Speaker
    private final NumberField volumeField = new NumberField("Lautstaerke (%)");
    private final TextField songField = new TextField("Song");

    public DeviceFormView(SmartHomeManager manager) {
        this.manager = manager;
        createForm();
    }

    private void createForm() {
        typeField.setItems("Light", "Thermostat", "Speaker");
        typeField.setValue("Light");
        // Beim Wechsel des Typs werden nur die passenden Felder angezeigt.
        typeField.addValueChangeListener(e -> showFieldsForType());

        brightnessField.setValue(100.0);
        colorField.setValue("Weiss");
        currentTemperatureField.setValue(20.0);
        targetTemperatureField.setValue(22.0);
        volumeField.setValue(50.0);
        songField.setValue("");

        Button saveButton = new Button("Speichern", event -> saveDevice());
        saveButton.addThemeVariants(ButtonVariant.PRIMARY);

        add(typeField, idField, nameField, roomField, powerField,
                brightnessField, colorField,
                currentTemperatureField, targetTemperatureField,
                volumeField, songField,
                saveButton);

        showFieldsForType();
    }

    // Blendet die typ-spezifischen Felder je nach Auswahl im typeField ein/aus.
    private void showFieldsForType() {
        String type = typeField.getValue();
        boolean isLight = "Light".equals(type);
        boolean isThermostat = "Thermostat".equals(type);
        boolean isSpeaker = "Speaker".equals(type);

        brightnessField.setVisible(isLight);
        colorField.setVisible(isLight);

        currentTemperatureField.setVisible(isThermostat);
        targetTemperatureField.setVisible(isThermostat);

        volumeField.setVisible(isSpeaker);
        songField.setVisible(isSpeaker);
    }

    private void saveDevice() {
        try {
            SmartDevice device = createDeviceFromFields();
            manager.addDevice(device);
            Notification.show("Geraet gespeichert.");
            clearForm();
        } catch (Exception e) {
            // Fehler (z. B. ungueltige Eingaben) als Notification anzeigen, Formular bleibt erhalten.
            Notification.show("Fehler: " + e.getMessage());
        }
    }

    private SmartDevice createDeviceFromFields() {
        int id = requiredNumber(idField, "ID").intValue();
        String name = nameField.getValue();
        String room = roomField.getValue();
        double powerUsage = requiredNumber(powerField, "Stromverbrauch");

        return switch (typeField.getValue()) {
            case "Thermostat" -> new SmartThermostat(
                    id,
                    name,
                    room,
                    powerUsage,
                    requiredNumber(currentTemperatureField, "Aktuelle Temperatur"),
                    requiredNumber(targetTemperatureField, "Zieltemperatur"));
            case "Speaker" -> new SmartSpeaker(
                    id,
                    name,
                    room,
                    powerUsage,
                    requiredNumber(volumeField, "Lautstaerke").intValue(),
                    songField.getValue());
            default -> new SmartLight(
                    id,
                    name,
                    room,
                    powerUsage,
                    requiredNumber(brightnessField, "Helligkeit").intValue(),
                    colorField.getValue());
        };
    }

    private Double requiredNumber(NumberField field, String fieldName) {
        Double value = field.getValue();
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " muss ausgefuellt sein.");
        }
        return value;
    }

    private void clearForm() {
        idField.clear();
        nameField.clear();
        roomField.clear();
        powerField.clear();
    }
}
