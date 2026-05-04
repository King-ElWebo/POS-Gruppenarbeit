package com.example.smarthome;

public class SmartThermostat extends SmartDevice {
    private double currentTemperature;
    private double targetTemperature;

    public SmartThermostat(int id, String name, String room, double powerUsage, double currentTemperature, double targetTemperature) {
        super(id, name, room, powerUsage);
        this.currentTemperature = currentTemperature;
        this.targetTemperature = targetTemperature;
    }

    public double getCurrentTemperature() { return currentTemperature; }
    public double getTargetTemperature() { return targetTemperature; }

    @Override
    public String getDeviceType() {
        return "Thermostat";
    }

    @Override
    public String performAction() {
        return "Heizung auf Zieltemperatur " + targetTemperature + "°C eingestellt.";
    }
}