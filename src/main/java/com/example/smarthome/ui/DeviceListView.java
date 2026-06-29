package com.example.smarthome.ui;

import com.example.smarthome.SmartDevice;
import com.example.smarthome.SmartHomeManager;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Seite "Geraeteliste": Stellt eine dynamische Tabelle (Grid) dar, die alle Geräte anzeigt.
 * Bietet umfangreiche Filter (Raum, Typ, Status) und Aktions-Buttons in jeder Zeile.
 */
@Route("devices")
@PageTitle("Geraete verwalten")
@Menu(order = 2, icon = "icons/clipboard-check.svg", title = "Geraeteliste")
public class DeviceListView extends VerticalLayout {
    
    private final SmartHomeManager manager;
    
    // Ein Vaadin Grid ist eine smarte Tabelle. Wir sagen ihr, dass sie SmartDevice-Objekte anzeigen soll.
    private final Grid<SmartDevice> grid = new Grid<>(SmartDevice.class, false);

    // Filter-Komponenten für die Suchleiste
    private final TextField roomFilter = new TextField("Raum");
    private final ComboBox<String> typeFilter = new ComboBox<>("Typ");
    private final ComboBox<String> statusFilter = new ComboBox<>("Status");
    private final Checkbox favoriteFilter = new Checkbox("Nur Favoriten");

    public DeviceListView(SmartHomeManager manager) {
        this.manager = manager;

        setSizeFull(); // Nutzt den gesamten Platz auf dem Bildschirm aus
        
        // Fügt die obere Suchleiste hinzu
        add(buildFilterBar());
        
        // Baut die Spalten der Tabelle zusammen
        configureGrid();
        add(grid);
        
        // Lädt beim Öffnen der Seite die Geräte in die Tabelle
        refreshGrid();
    }

    /**
     * Baut die obere Leiste mit den Eingabefeldern zum Filtern.
     */
    private HorizontalLayout buildFilterBar() {
        roomFilter.setPlaceholder("z. B. Kueche");
        // EAGER bedeutet: Suchen wird sofort ausgeführt, wenn der User auch nur einen Buchstaben tippt
        roomFilter.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.EAGER);
        // Bei jeder Eingabe rufen wir refreshGrid() auf, um die Tabelle zu filtern
        roomFilter.addValueChangeListener(e -> refreshGrid());

        typeFilter.setItems("Alle", "Light", "Thermostat", "Speaker");
        typeFilter.setValue("Alle");
        typeFilter.addValueChangeListener(e -> refreshGrid());

        statusFilter.setItems("Alle", "Ein", "Aus");
        statusFilter.setValue("Alle");
        statusFilter.addValueChangeListener(e -> refreshGrid());

        favoriteFilter.addValueChangeListener(e -> refreshGrid());

        Button resetButton = new Button("Filter zuruecksetzen", e -> resetFilters());

        return new HorizontalLayout(roomFilter, typeFilter, statusFilter, favoriteFilter, resetButton);
    }

    /**
     * Setzt alle Suchfelder auf ihre Standardwerte zurück.
     */
    private void resetFilters() {
        roomFilter.clear();
        typeFilter.setValue("Alle");
        statusFilter.setValue("Alle");
        favoriteFilter.setValue(false);
        refreshGrid();
    }

    /**
     * Definiert die Spalten der Tabelle (Grid).
     */
    private void configureGrid() {
        // SmartDevice::getId ist eine Methodenreferenz. Vaadin ruft intern getId() für jedes Gerät auf,
        // um den Text für die Tabellenzelle zu erhalten.
        grid.addColumn(SmartDevice::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(SmartDevice::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(SmartDevice::getRoom).setHeader("Raum").setAutoWidth(true);
        grid.addColumn(SmartDevice::getDeviceType).setHeader("Typ").setAutoWidth(true);
        grid.addColumn(SmartDevice::getStatusText).setHeader("Status").setAutoWidth(true);
        grid.addColumn(SmartDevice::getPowerUsage).setHeader("Watt").setAutoWidth(true);
        grid.addColumn(device -> device.isFavorite() ? "Ja" : "Nein").setHeader("Favorit").setAutoWidth(true);

        // addComponentColumn: Fügt keine Texte, sondern interaktive Vaadin-Komponenten (Buttons) in die Spalten ein.
        
        // Ein/Aus-Button (Text ändert sich dynamisch)
        grid.addComponentColumn(device -> new Button(
                device.isTurnedOn() ? "Ausschalten" : "Einschalten",
                event -> toggleOnOff(device)));
                
        // Favoriten-Button
        grid.addComponentColumn(device -> new Button(
                device.isFavorite() ? "Favorit entfernen" : "Favorit setzen",
                event -> toggleFavorite(device)));
                
        // Löschen-Button
        grid.addComponentColumn(device -> new Button("Loeschen", event -> deleteDevice(device.getId())));

        grid.setSizeFull();
    }

    /**
     * Wechselt den Status des Geräts (an/aus) und aktualisiert sofort die Tabelle.
     */
    private void toggleOnOff(SmartDevice device) {
        if (device.isTurnedOn()) {
            device.turnOff();
        } else {
            device.turnOn();
        }
        refreshGrid();
    }

    /**
     * Markiert oder ent-markiert ein Gerät als Favorit über den Manager (damit Logs geschrieben werden).
     */
    private void toggleFavorite(SmartDevice device) {
        if (device.isFavorite()) {
            manager.unmarkAsFavorite(device.getId());
        } else {
            manager.markAsFavorite(device.getId());
        }
        refreshGrid();
    }

    /**
     * Löscht ein Gerät aus dem Manager und zeigt eine Info-Benachrichtigung.
     */
    private void deleteDevice(int id) {
        manager.removeDevice(id);
        refreshGrid();
        Notification.show("Geraet geloescht.");
    }

    /**
     * Das Herzstück der Seite: Diese Methode holt alle Geräte vom Manager 
     * und wendet dann schrittweise (mit Java Streams) alle Filter an, die 
     * der User oben in der Suchleiste eingestellt hat.
     */
    private void refreshGrid() {
        // 1. Hole Original-Liste
        List<SmartDevice> devices = manager.getAllDevices();

        // 2. Filtern nach Raum, wenn ein Text eingegeben wurde
        String room = roomFilter.getValue();
        if (room != null && !room.trim().isEmpty()) {
            String query = room.trim().toLowerCase();
            devices = devices.stream()
                    .filter(d -> d.getRoom() != null && d.getRoom().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        // 3. Filtern nach Typ (wenn nicht "Alle" ausgewählt ist)
        String type = typeFilter.getValue();
        if (type != null && !type.equals("Alle")) {
            devices = devices.stream()
                    .filter(d -> d.getDeviceType().equals(type))
                    .collect(Collectors.toList());
        }

        // 4. Filtern nach Status (Ein oder Aus)
        String status = statusFilter.getValue();
        if (status != null && !status.equals("Alle")) {
            boolean wantOn = status.equals("Ein");
            devices = devices.stream()
                    .filter(d -> d.isTurnedOn() == wantOn)
                    .collect(Collectors.toList());
        }

        // 5. Filtern nach Favoriten, wenn die Checkbox aktiv ist
        if (favoriteFilter.getValue()) {
            devices = devices.stream()
                    .filter(SmartDevice::isFavorite)
                    .collect(Collectors.toList());
        }

        // Letzter Schritt: Übrige, gefilterte Geräte an die Tabelle übergeben
        grid.setItems(devices);
    }
}
