package com.example.smarthome.ui;

import com.example.smarthome.SmartHomeException;
import com.example.smarthome.SmartHomeFileHandler;
import com.example.smarthome.SmartHomeManager;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

// Startseite "Dashboard": zeigt Kennzahlen und bietet Speichern/Laden der Geraete als CSV-Datei.
@Route("")
@PageTitle("Dashboard")
@Menu(order = 1, icon = "icons/clipboard-check.svg", title = "Dashboard")
public class DashboardView extends VerticalLayout {
    private final SmartHomeManager manager;
    private final SmartHomeFileHandler fileHandler;
    private final VerticalLayout statsLayout = new VerticalLayout();

    public DashboardView(SmartHomeManager manager, SmartHomeFileHandler fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;

        add(new H1("Smart Home Dashboard"));
        add(statsLayout);
        add(buildFileButtons());

        updateStatistics();
    }

    // Zeigt die wichtigsten Kennzahlen ueber den Manager an.
    private void updateStatistics() {
        statsLayout.removeAll();
        statsLayout.add(new H3("Gesamtverbrauch: " + String.format("%.2f W", manager.getTotalPowerUsage())));
        statsLayout.add(new H3("Durchschnittsverbrauch: " + String.format("%.2f W", manager.getAveragePowerUsage())));
        statsLayout.add(new H3("Aktive Geraete: " + manager.getActiveDeviceCount()));
        statsLayout.add(new H3("Favoriten: " + manager.filterFavorites().size()));
    }

    // Buttons zum Speichern in bzw. Laden aus der CSV-Datei ueber den FileHandler.
    private HorizontalLayout buildFileButtons() {
        Button saveButton = new Button("Geraete speichern (CSV)", e -> saveToFile());
        Button loadButton = new Button("Geraete laden (CSV)", e -> loadFromFile());
        return new HorizontalLayout(saveButton, loadButton);
    }

    private void saveToFile() {
        try {
            fileHandler.saveDevices(manager.getAllDevices());
            Notification.show("Geraete wurden gespeichert.");
        } catch (SmartHomeException e) {
            Notification.show("Fehler beim Speichern: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        try {
            fileHandler.loadDevices().forEach(device -> {
                // Bereits vorhandene IDs ueberspringen, damit addDevice nicht abbricht.
                if (manager.getAllDevices().stream().noneMatch(d -> d.getId() == device.getId())) {
                    manager.addDevice(device);
                }
            });
            updateStatistics();
            Notification.show("Geraete wurden geladen.");
        } catch (SmartHomeException e) {
            Notification.show("Fehler beim Laden: " + e.getMessage());
        }
    }
}
