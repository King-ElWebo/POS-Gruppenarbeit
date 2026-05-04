package com.example.smarthome;

public abstract class SmartDevice {
    private int id;
    private String name;
    private String room;
    private boolean isOn;
    private double powerUsage;
    private boolean favorite;

    public SmartDevice(int id, String name, String room, double powerUsage) {
        if (id < 0) {
            throw new SmartHomeException("ID darf nicht negativ sein.");
        }
        if (powerUsage < 0) {
            throw new SmartHomeException("Stromverbrauch darf nicht negativ sein.");
        }
        
        this.id = id;
        this.powerUsage = powerUsage;
        this.isOn = false;
        this.favorite = false;
        
        validateText(name, "Name");
        this.name = name;
        
        validateText(room, "Raum");
        this.room = room;
    }

    public void turnOn() {
        this.isOn = true;
    }

    public void turnOff() {
        this.isOn = false;
    }

    public double calculateDailyConsumption(int hours) {
        if (hours < 0) {
            throw new SmartHomeException("Stunden dürfen nicht negativ sein.");
        }
        return this.powerUsage * hours;
    }

    public String getStatusText() {
        return isOn ? "Ein" : "Aus";
    }

    protected void validateText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new SmartHomeException(fieldName + " darf nicht leer sein.");
        }
    }

    public abstract String getDeviceType();

    public abstract String performAction();

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoom() {
        return room;
    }

    public boolean isOn() {
        return isOn;
    }

    public double getPowerUsage() {
        return powerUsage;
    }

    public boolean isFavorite() {
        return favorite;
    }
}