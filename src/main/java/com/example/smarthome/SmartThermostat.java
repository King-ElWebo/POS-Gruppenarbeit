package com.example.smarthome;

public class SmartThermostat extends SmartDevice {
    private double currentTemperature;
    private double targetTemperature;

    public SmartThermostat(int id, String name, String room, double powerUsage, double currentTemperature, double targetTemperature) {
        super(id, name, room, powerUsage);
        validateTemperature(currentTemperature);
        validateTemperature(targetTemperature);
        this.currentTemperature = currentTemperature;
        this.targetTemperature = targetTemperature;
    }

    public double getCurrentTemperature() { return currentTemperature; }
    
    public void setCurrentTemperature(double currentTemperature) {
        validateTemperature(currentTemperature);
        this.currentTemperature = currentTemperature;
    }

    public double getTargetTemperature() { return targetTemperature; }
    
    public void setTargetTemperature(double targetTemperature) {
        validateTemperature(targetTemperature);
        this.targetTemperature = targetTemperature;
    }

    public void increaseTargetTemperature(double value) {
        setTargetTemperature(this.targetTemperature + value);
    }

    public void decreaseTargetTemperature(double value) {
        setTargetTemperature(this.targetTemperature - value);
    }

    public boolean isHeatingNeeded() {
        return this.currentTemperature < this.targetTemperature;
    }

    @Override
    public String getDeviceType() {
        return "Thermostat";
    }

    @Override
    public String performAction() {
        if (isHeatingNeeded()) {
            return "Heizung wird aktiviert, Zieltemperatur: " + targetTemperature + "°C.";
        } else {
            return "Keine Heizung notwendig.";
        }
    }

    private void validateTemperature(double temp) {
        if (temp < -20.0 || temp > 50.0) {
            throw new SmartHomeException("Temperatur muss zwischen -20 und 50 Grad liegen.");
        }
    }
}