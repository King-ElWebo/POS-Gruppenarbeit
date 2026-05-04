package com.example.smarthome;

public class SmartThermostat extends SmartDevice {
    private double currentTemperature;
    private double targetTemperature;

    public SmartThermostat(int id, String name, String room, double powerUsage, double currentTemperature, double targetTemperature) {
        super(id, name, room, powerUsage);
        if (currentTemperature < -20 || currentTemperature > 50 || targetTemperature < 5 || targetTemperature > 40) {
            throw new SmartHomeException("Temperaturen sind in einem ungültigen Bereich.");
        }
        this.currentTemperature = currentTemperature;
        this.targetTemperature = targetTemperature;
    }

    public void setCurrentTemperature(double currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public void setTargetTemperature(double targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public void increaseTargetTemperature(double value) {
        this.targetTemperature += value;
    }

    public void decreaseTargetTemperature(double value) {
        this.targetTemperature -= value;
    }

    public boolean isHeatingNeeded() {
        return currentTemperature < targetTemperature;
    }

    @Override
    public String getDeviceType() {
        return "Thermostat";
    }

    @Override
    public String performAction() {
        return isHeatingNeeded() ? "Heizung wird aktiviert, Zieltemperatur: " + targetTemperature + "°C." : "Keine Heizung notwendig.";
    }

    public double getCurrentTemperature() { return currentTemperature; }
    public double getTargetTemperature() { return targetTemperature; }
}