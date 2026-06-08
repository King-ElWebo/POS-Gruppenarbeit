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
        if (id < 0) {
            throw new SmartHomeException("ID darf nicht negativ sein.");
        }
        if (powerUsage < 0) {
            throw new SmartHomeException("Stromverbrauch darf nicht negativ sein.");
        }
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

    public double calculateDailyConsumption(int hours) {
        if (hours < 0) {
            throw new SmartHomeException("Stunden dürfen nicht negativ sein.");
        }
        return this.powerUsage * hours;
    }

    public abstract String getDeviceType();
    public abstract String performAction();

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) {
        validateText(name, "Name");
        this.name = name;
    }
    public String getRoom() { return room; }
    public void setRoom(String room) {
        validateText(room, "Raum");
        this.room = room;
    }
    public double getPowerUsage() { return powerUsage; }
    public void setPowerUsage(double powerUsage) {
        if (powerUsage < 0) {
            throw new SmartHomeException("Stromverbrauch darf nicht negativ sein.");
        }
        this.powerUsage = powerUsage;
    }
    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
}