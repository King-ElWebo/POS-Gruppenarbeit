package com.example.smarthome;

public class SmartLight extends SmartDevice {
    private int brightness;
    private String color;

    public SmartLight(int id, String name, String room, double powerUsage, int brightness, String color) {
        super(id, name, room, powerUsage);
        if (brightness < 0 || brightness > 100) {
            throw new SmartHomeException("Helligkeit muss zwischen 0 und 100 liegen.");
        }
        validateText(color, "Farbe");
        this.brightness = brightness;
        this.color = color;
    }

    public int getBrightness() { return brightness; }
    
    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new SmartHomeException("Helligkeit muss zwischen 0 und 100 liegen.");
        }
        this.brightness = brightness;
    }

    public String getColor() { return color; }

    public void setColor(String color) {
        validateText(color, "Farbe");
        this.color = color;
    }

    public void dim(int percent) {
        setBrightness(percent);
    }

    public void changeColor(String color) {
        setColor(color);
    }

    @Override
    public String getDeviceType() {
        return "Light";
    }

    @Override
    public String performAction() {
        return "Die Lampe leuchtet in " + color + " mit " + brightness + "% Helligkeit.";
    }
}