package com.example.smarthome.ui;

import com.example.smarthome.SmartHomeManager;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Dashboard")
@Menu(order = 1, icon = "icons/clipboard-check.svg", title = "Dashboard")
public class DashboardView extends VerticalLayout {
    private final SmartHomeManager manager;
    private final VerticalLayout statsLayout = new VerticalLayout();

    public DashboardView(SmartHomeManager manager) {
        this.manager = manager;

        add(new H1("Smart Home Dashboard"));
        add(statsLayout);

        updateStatistics();
    }

    private void updateStatistics() {
        statsLayout.removeAll();
        statsLayout.add(new H3("Gesamtverbrauch: " + String.format("%.2f W", manager.getTotalPowerUsage())));
        statsLayout.add(new H3("Durchschnittsverbrauch: " + String.format("%.2f W", manager.getAveragePowerUsage())));
        statsLayout.add(new H3("Aktive Geraete: " + manager.getActiveDeviceCount()));
        statsLayout.add(new H3("Favoriten: " + manager.filterFavorites().size()));
    }
}
