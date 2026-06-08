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

// Seite "Geraeteliste": zeigt alle Geraete an und bietet Filter + Aktionen (loeschen, favorisieren, ein/aus).
@Route("devices")
@PageTitle("Geraete verwalten")
@Menu(order = 2, icon = "icons/clipboard-check.svg", title = "Geraeteliste")
public class DeviceListView extends VerticalLayout {
    private final SmartHomeManager manager;
    private final Grid<SmartDevice> grid = new Grid<>(SmartDevice.class, false);

    // Filter-Felder
    private final TextField roomFilter = new TextField("Raum");
    private final ComboBox<String> typeFilter = new ComboBox<>("Typ");
    private final ComboBox<String> statusFilter = new ComboBox<>("Status");
    private final Checkbox favoriteFilter = new Checkbox("Nur Favoriten");

    public DeviceListView(SmartHomeManager manager) {
        this.manager = manager;

        setSizeFull();
        add(buildFilterBar());
        configureGrid();
        add(grid);
        refreshGrid();
    }

    // Baut die Filterleiste oberhalb der Tabelle.
    private HorizontalLayout buildFilterBar() {
        roomFilter.setPlaceholder("z. B. Kueche");
        roomFilter.setValueChangeMode(com.vaadin.flow.data.value.ValueChangeMode.EAGER);
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

    private void resetFilters() {
        roomFilter.clear();
        typeFilter.setValue("Alle");
        statusFilter.setValue("Alle");
        favoriteFilter.setValue(false);
        refreshGrid();
    }

    // Baut die Spalten der Tabelle inklusive Aktions-Buttons je Zeile.
    private void configureGrid() {
        grid.addColumn(SmartDevice::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(SmartDevice::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(SmartDevice::getRoom).setHeader("Raum").setAutoWidth(true);
        grid.addColumn(SmartDevice::getDeviceType).setHeader("Typ").setAutoWidth(true);
        grid.addColumn(SmartDevice::getStatusText).setHeader("Status").setAutoWidth(true);
        grid.addColumn(SmartDevice::getPowerUsage).setHeader("Watt").setAutoWidth(true);
        grid.addColumn(device -> device.isFavorite() ? "Ja" : "Nein").setHeader("Favorit").setAutoWidth(true);

        grid.addComponentColumn(device -> new Button(
                device.isTurnedOn() ? "Ausschalten" : "Einschalten",
                event -> toggleOnOff(device)));
        grid.addComponentColumn(device -> new Button(
                device.isFavorite() ? "Favorit entfernen" : "Favorit setzen",
                event -> toggleFavorite(device)));
        grid.addComponentColumn(device -> new Button("Loeschen", event -> deleteDevice(device.getId())));

        grid.setSizeFull();
    }

    // Schaltet das Geraet ein bzw. aus.
    private void toggleOnOff(SmartDevice device) {
        if (device.isTurnedOn()) {
            device.turnOff();
        } else {
            device.turnOn();
        }
        refreshGrid();
    }

    private void toggleFavorite(SmartDevice device) {
        if (device.isFavorite()) {
            manager.unmarkAsFavorite(device.getId());
        } else {
            manager.markAsFavorite(device.getId());
        }
        refreshGrid();
    }

    private void deleteDevice(int id) {
        manager.removeDevice(id);
        refreshGrid();
        Notification.show("Geraet geloescht.");
    }

    // Holt alle Geraete vom Manager und wendet danach die aktuell gesetzten Filter an.
    private void refreshGrid() {
        List<SmartDevice> devices = manager.getAllDevices();

        String room = roomFilter.getValue();
        if (room != null && !room.trim().isEmpty()) {
            String query = room.trim().toLowerCase();
            devices = devices.stream()
                    .filter(d -> d.getRoom() != null && d.getRoom().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        String type = typeFilter.getValue();
        if (type != null && !type.equals("Alle")) {
            devices = devices.stream()
                    .filter(d -> d.getDeviceType().equals(type))
                    .collect(Collectors.toList());
        }

        String status = statusFilter.getValue();
        if (status != null && !status.equals("Alle")) {
            boolean wantOn = status.equals("Ein");
            devices = devices.stream()
                    .filter(d -> d.isTurnedOn() == wantOn)
                    .collect(Collectors.toList());
        }

        if (favoriteFilter.getValue()) {
            devices = devices.stream()
                    .filter(SmartDevice::isFavorite)
                    .collect(Collectors.toList());
        }

        grid.setItems(devices);
    }
}
