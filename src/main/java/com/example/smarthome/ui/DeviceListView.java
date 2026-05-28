package com.example.smarthome.ui;

import com.example.smarthome.SmartDevice;
import com.example.smarthome.SmartHomeManager;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("devices")
@PageTitle("Geraete verwalten")
@Menu(order = 2, icon = "icons/clipboard-check.svg", title = "Geraeteliste")
public class DeviceListView extends VerticalLayout {
    private final SmartHomeManager manager;
    private final Grid<SmartDevice> grid = new Grid<>(SmartDevice.class, false);

    public DeviceListView(SmartHomeManager manager) {
        this.manager = manager;

        setSizeFull();
        configureGrid();
        add(grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.addColumn(SmartDevice::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(SmartDevice::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(SmartDevice::getRoom).setHeader("Raum").setAutoWidth(true);
        grid.addColumn(SmartDevice::getDeviceType).setHeader("Typ").setAutoWidth(true);
        grid.addColumn(SmartDevice::getStatusText).setHeader("Status").setAutoWidth(true);
        grid.addColumn(SmartDevice::getPowerUsage).setHeader("Watt").setAutoWidth(true);
        grid.addColumn(device -> device.isFavorite() ? "Ja" : "Nein").setHeader("Favorit").setAutoWidth(true);

        grid.addComponentColumn(device -> new Button(
                device.isFavorite() ? "Favorit entfernen" : "Favorit setzen",
                event -> toggleFavorite(device)));
        grid.addComponentColumn(device -> new Button("Loeschen", event -> deleteDevice(device.getId())));

        grid.setSizeFull();
    }

    private void toggleFavorite(SmartDevice device) {
        device.setFavorite(!device.isFavorite());
        refreshGrid();
    }

    private void deleteDevice(int id) {
        manager.removeDevice(id);
        refreshGrid();
        Notification.show("Geraet geloescht.");
    }

    private void refreshGrid() {
        grid.setItems(manager.getAllDevices());
    }
}
