package com.example.smarthome;

/**
 * Abstrakte Basisklasse für alle Smart-Home-Geräte.
 * Diese Klasse definiert die grundlegenden Eigenschaften (wie ID, Name, Raum)
 * und Methoden (wie Ein-/Ausschalten), die jedes smarte Gerät haben muss.
 * Da sie "abstract" ist, kann man kein direktes Objekt davon erstellen,
 * sondern nur von den spezifischen Unterklassen (wie SmartLight).
 */
public abstract class SmartDevice {
    
    // Die eindeutige Kennung des Geräts.
    private int id;
    
    // Der Name des Geräts, z. B. "Deckenlampe".
    private String name;
    
    // Der Raum, in dem das Gerät steht, z. B. "Wohnzimmer".
    private String room;
    
    // Gibt an, ob das Gerät aktuell eingeschaltet ist (true) oder nicht (false).
    private boolean turnedOn;
    
    // Der Stromverbrauch des Geräts in Watt.
    private double powerUsage;
    
    // Gibt an, ob der Benutzer dieses Gerät als Favorit markiert hat.
    private boolean favorite;

    /**
     * Basis-Konstruktor: Erstellt ein neues Gerät nur mit Name und Raum.
     * Wird oft intern oder als Vorstufe für den Haupt-Konstruktor verwendet.
     * 
     * @param name Der Name des Geräts.
     * @param room Der Raum des Geräts.
     */
    public SmartDevice(String name, String room) {
        validateText(name, "Name");
        validateText(room, "Raum");
        this.name = name;
        this.room = room;
    }

    /**
     * Erweiterter Konstruktor: Dies ist der Haupt-Konstruktor, der alle wichtigen
     * Daten aufnimmt und validiert.
     * 
     * @param id Die ID des Geräts (darf nicht negativ sein).
     * @param name Der Name des Geräts.
     * @param room Der Raum des Geräts.
     * @param powerUsage Der Stromverbrauch in Watt (darf nicht negativ sein).
     */
    public SmartDevice(int id, String name, String room, double powerUsage) {
        // Ruft den anderen Konstruktor (oben) auf, um Name und Raum zu setzen
        this(name, room);
        
        // Validierung: ID darf nicht kleiner als 0 sein
        if (id < 0) {
            throw new SmartHomeException("ID darf nicht negativ sein.");
        }
        // Validierung: Stromverbrauch darf nicht kleiner als 0 sein
        if (powerUsage < 0) {
            throw new SmartHomeException("Stromverbrauch darf nicht negativ sein.");
        }
        
        this.id = id;
        this.powerUsage = powerUsage;
        this.turnedOn = false; // Standardmäßig ist jedes neue Gerät ausgeschaltet
    }

    /**
     * Schaltet das Gerät ein. Setzt den Status 'turnedOn' auf true.
     */
    public void turnOn() {
        this.turnedOn = true;
    }

    /**
     * Schaltet das Gerät aus. Setzt den Status 'turnedOn' auf false.
     */
    public void turnOff() {
        this.turnedOn = false;
    }

    /**
     * Überprüft, ob das Gerät eingeschaltet ist.
     * 
     * @return true wenn eingeschaltet, sonst false.
     */
    public boolean isTurnedOn() {
        return turnedOn;
    }

    /**
     * Alias-Methode für isTurnedOn().
     * Wird vom SmartHomeFileHandler verwendet, der exakt nach dieser Methode sucht.
     * 
     * @return true wenn eingeschaltet, sonst false.
     */
    public boolean isOn() {
        return isTurnedOn();
    }

    /**
     * Gibt den Status als lesbaren Text für die Benutzeroberfläche (UI) zurück.
     * 
     * @return "Ein", wenn das Gerät an ist, "Aus", wenn es aus ist.
     */
    public String getStatusText() {
        return turnedOn ? "Ein" : "Aus";
    }

    /**
     * Hilfsmethode zur Validierung von Texteingaben.
     * Verhindert, dass man leere Namen oder Räume speichert.
     * 
     * @param value Der zu prüfende Text.
     * @param fieldName Der Name des Feldes für die Fehlermeldung (z.B. "Name").
     */
    protected void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new SmartHomeException(fieldName + " darf nicht leer sein.");
        }
    }

    /**
     * Berechnet den Stromverbrauch des Geräts über einen bestimmten Zeitraum.
     * 
     * @param hours Die Anzahl der Stunden.
     * @return Der Gesamtverbrauch in Wattstunden (Stromverbrauch * Stunden).
     */
    public double calculateDailyConsumption(int hours) {
        if (hours < 0) {
            throw new SmartHomeException("Stunden dürfen nicht negativ sein.");
        }
        return this.powerUsage * hours;
    }

    /**
     * Abstrakte Methode, die von den Unterklassen (z. B. SmartLight)
     * implementiert werden muss. Gibt den Typ des Geräts zurück.
     * 
     * @return Der Gerätetyp als String (z.B. "Light").
     */
    public abstract String getDeviceType();
    
    /**
     * Abstrakte Methode, die die Hauptaktion des jeweiligen Geräts ausführt.
     * Jedes Gerät (Lampe, Speaker, etc.) macht hier etwas anderes (Polymorphismus).
     * 
     * @return Ein Text, der beschreibt, was das Gerät gerade macht.
     */
    public abstract String performAction();

    // --------------------------------------------------------
    // Getter und Setter (Methoden zum Lesen und Schreiben der Eigenschaften)
    // --------------------------------------------------------

    /**
     * Gibt die ID des Geräts zurück.
     */
    public int getId() { return id; }
    
    /**
     * Gibt den Namen des Geräts zurück.
     */
    public String getName() { return name; }
    
    /**
     * Ändert den Namen des Geräts (mit Validierung).
     */
    public void setName(String name) {
        validateText(name, "Name");
        this.name = name;
    }
    
    /**
     * Gibt den Raum zurück.
     */
    public String getRoom() { return room; }
    
    /**
     * Ändert den Raum des Geräts (mit Validierung).
     */
    public void setRoom(String room) {
        validateText(room, "Raum");
        this.room = room;
    }
    
    /**
     * Gibt den Stromverbrauch in Watt zurück.
     */
    public double getPowerUsage() { return powerUsage; }
    
    /**
     * Ändert den Stromverbrauch (mit Validierung auf negative Werte).
     */
    public void setPowerUsage(double powerUsage) {
        if (powerUsage < 0) {
            throw new SmartHomeException("Stromverbrauch darf nicht negativ sein.");
        }
        this.powerUsage = powerUsage;
    }
    
    /**
     * Prüft, ob das Gerät ein Favorit ist.
     */
    public boolean isFavorite() { return favorite; }
    
    /**
     * Setzt das Gerät als Favorit oder entfernt es als Favorit.
     */
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}