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

/**
 * Startseite "Dashboard": Zeigt die wichtigsten Kennzahlen des Systems
 * und bietet Buttons zum Speichern/Laden der Geräte als CSV-Datei.
 * 
 * @Route(""): Gibt an, dass diese Ansicht unter der Haupt-URL (localhost:8080/) erreichbar ist.
 * @PageTitle: Setzt den Titel des Browser-Tabs.
 * @Menu: Sorgt dafür, dass Vaadin automatisch einen Link im Seitenmenü anlegt.
 */
@Route("")
@PageTitle("Dashboard")
@Menu(order = 1, icon = "icons/clipboard-check.svg", title = "Dashboard")
public class DashboardView extends VerticalLayout {
    
    // Die Verbindung zu unserer Business-Logik (Dependency Injection durch Spring)
    private final SmartHomeManager manager;
    private final SmartHomeFileHandler fileHandler;
    
    // Ein Container (Layout), der Elemente vertikal untereinander anordnet
    private final VerticalLayout statsLayout = new VerticalLayout();

    /**
     * Konstruktor: Vaadin und Spring Boot kümmern sich darum, dass 'manager'
     * und 'fileHandler' hier automatisch übergeben werden.
     */
    public DashboardView(SmartHomeManager manager, SmartHomeFileHandler fileHandler) {
        this.manager = manager;
        this.fileHandler = fileHandler;

        // Fügt eine große Überschrift (H1) zur Seite hinzu
        add(new H1("Smart Home Dashboard"));
        
        // Fügt unseren Container für die Statistiken hinzu
        add(statsLayout);
        
        // Fügt die Buttons zum Speichern und Laden hinzu
        add(buildFileButtons());

        // Lädt die Statistiken zum ersten Mal
        updateStatistics();
    }

    /**
     * Holt sich die aktuellsten Zahlen vom Manager und zeigt sie an.
     * Wird immer aufgerufen, wenn sich Daten ändern (z.B. nach dem Laden aus CSV).
     */
    private void updateStatistics() {
        // Löscht alle alten Texte aus dem Layout
        statsLayout.removeAll();
        
        // H3 ist eine Überschrift. String.format("%.2f") rundet die Zahl auf 2 Kommastellen.
        statsLayout.add(new H3("Gesamtverbrauch: " + String.format("%.2f W", manager.getTotalPowerUsage())));
        statsLayout.add(new H3("Durchschnittsverbrauch: " + String.format("%.2f W", manager.getAveragePowerUsage())));
        statsLayout.add(new H3("Aktive Geraete: " + manager.getActiveDeviceCount()));
        statsLayout.add(new H3("Favoriten: " + manager.filterFavorites().size()));
    }

    /**
     * Erstellt die Buttons für die CSV-Speicherung.
     * @return Ein HorizontalLayout (ordnet die Buttons nebeneinander an).
     */
    private HorizontalLayout buildFileButtons() {
        // e -> saveToFile() ist ein sogenannter Lambda-Ausdruck.
        // Er bedeutet: "Wenn jemand auf den Button klickt, führe die Methode saveToFile() aus".
        Button saveButton = new Button("Geraete speichern (CSV)", e -> saveToFile());
        Button loadButton = new Button("Geraete laden (CSV)", e -> loadFromFile());
        
        return new HorizontalLayout(saveButton, loadButton);
    }

    /**
     * Speichert die Daten. Fängt Fehler mit unserer eigenen SmartHomeException ab,
     * damit die Seite nicht abstürzt.
     */
    private void saveToFile() {
        try {
            fileHandler.saveDevices(manager.getAllDevices());
            // Notification.show zeigt ein kurzes Pop-Up (Toast) auf dem Bildschirm an
            Notification.show("Geraete wurden gespeichert.");
        } catch (SmartHomeException e) {
            Notification.show("Fehler beim Speichern: " + e.getMessage());
        }
    }

    /**
     * Lädt die Daten aus der CSV-Datei in den Manager.
     */
    private void loadFromFile() {
        try {
            // Geht jedes Gerät aus der CSV einzeln durch
            fileHandler.loadDevices().forEach(device -> {
                // Prüft, ob es schon ein Gerät mit dieser ID gibt. Wenn nicht (noneMatch), fügen wir es hinzu.
                // Das verhindert, dass der Manager beim Einlesen abstürzt wegen "doppelter ID".
                if (manager.getAllDevices().stream().noneMatch(d -> d.getId() == device.getId())) {
                    manager.addDevice(device);
                }
            });
            // Aktualisiert die Anzeige mit den neu geladenen Daten
            updateStatistics();
            Notification.show("Geraete wurden geladen.");
        } catch (SmartHomeException e) {
            Notification.show("Fehler beim Laden: " + e.getMessage());
        }
    }
}
