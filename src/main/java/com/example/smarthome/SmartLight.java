package com.example.smarthome;

public class SmartLight extends SmartDevice {
    private int brightness;
    private String color;

    public SmartLight(int id, String name, String room, double powerUsage, int brightness, String color) {
        super(id, name, room, powerUsage);
        this.brightness = brightness;
        this.color = color;
    }

    public int getBrightness() { return brightness; }
    public String getColor() { return color; }

    @Override
    public String getDeviceType() {
        return "Light";
    }

    @Override
    public String performAction() {
        return "Die Lampe leuchtet in " + color + " mit " + brightness + "% Helligkeit.";
    }
}