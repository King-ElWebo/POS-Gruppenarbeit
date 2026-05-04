package com.example.smarthome.ui;

import com.example.smarthome.SmartDevice;
import com.example.smarthome.SmartHomeManager;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("devices")
@PageTitle("Geräte verwalten")
@Menu(order = 2, icon = "line-awesome/svg/list-solid.svg", title = "Geräteliste")
public class DeviceListView extends VerticalLayout {
    private final SmartHomeManager manager;
    private final Grid<SmartDevice> grid = new Grid<>(SmartDevice.class, false);

    public DeviceListView(SmartHomeManager manager) {
        this.manager = manager;
        setSizeFull();

        grid.addColumn(SmartDevice::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(SmartDevice::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(SmartDevice::getRoom).setHeader("Raum").setAutoWidth(true);
        grid.addColumn(SmartDevice::getDeviceType).setHeader("Typ").setAutoWidth(true);
        grid.addColumn(SmartDevice::getStatusText).setHeader("Status").setAutoWidth(true);
        
        grid.addComponentColumn(device -> {
            Button deleteBtn = new Button("Löschen", e -> deleteSelectedDevice(device.getId()));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return deleteBtn;
        });

        add(grid);
        refreshGrid();
    }

    public void refreshGrid() {
        grid.setItems(manager.getAllDevices());
    }

    private void deleteSelectedDevice(int id) {
        manager.removeDevice(id);
        refreshGrid();
        Notification.show("Gerät gelöscht.");
    }
}