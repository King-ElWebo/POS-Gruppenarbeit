package com.example.smarthome;

public abstract class SmartDevice {
    private int id;
    private String name;
    private String room;
    private boolean turnedOn;
    private double powerUsage;
    private boolean favorite;

    // Konstruktor mit Name und Raum (laut Anforderung)
    public SmartDevice(String name, String room) {
        validateText(name, "Name");
        validateText(room, "Raum");
        this.name = name;
        this.room = room;
    }

    // Erweiterter Konstruktor (passend zur Application.java und UI)
    public SmartDevice(int id, String name, String room, double powerUsage) {
        this(name, room);
        this.id = id;
        this.powerUsage = powerUsage;
        this.turnedOn = false;
    }

    public void turnOn() {
        this.turnedOn = true;
    }

    public void turnOff() {
        this.turnedOn = false;
    }

    public boolean isTurnedOn() {
        return turnedOn;
    }

    // Alias, da SmartHomeFileHandler isOn() erwartet
    public boolean isOn() {
        return isTurnedOn();
    }

    // Für DeviceListView benötigt
    public String getStatusText() {
        return turnedOn ? "Ein" : "Aus";
    }

    protected void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new SmartHomeException(fieldName + " darf nicht leer sein.");
        }
    }

    public abstract String getDeviceType();
    public abstract String performAction();

    public int getId() { return id; }
    public String getName() { return name; }
    public String getRoom() { return room; }
    public double getPowerUsage() { return powerUsage; }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}