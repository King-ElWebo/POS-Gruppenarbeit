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
        
        add(new H1("Smart Home Übersicht"));
        add(statsLayout);
        
        createStatisticCards();
    }

    private void createStatisticCards() {
        updateStatistics();
    }

    public void updateStatistics() {
        statsLayout.removeAll();
        statsLayout.add(new H3("Gesamtverbrauch: " + String.format("%.2f", manager.getTotalPowerUsage()) + " W"));
        statsLayout.add(new H3("Durchschnittsverbrauch: " + Stringformat("%.2f", manager.getAveragePowerUsage()) + " W"));
        statsLayout.add(new H3("Aktive Geräte: " + manager.getActiveDeviceCount()));
        statsLayout.add(new H3("Favoriten: " + manager.filterFavorites().size()));
    }
}