package com.example.smarthome;

public class SmartLight extends SmartDevice {
    private int brightness;
    private String color;

    public SmartLight(int id, String name, String room, double powerUsage, int brightness, String color) {
        super(id, name, room, powerUsage);
        setBrightness(brightness);
        changeColor(color);
    }

    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new SmartHomeException("Helligkeit muss zwischen 0 und 100 liegen.");
        }
        this.brightness = brightness;
    }

    public void dim(int percent) {
        setBrightness(percent);
    }

    public void changeColor(String color) {
        validateText(color, "Farbe");
        this.color = color;
    }

    @Override
    public String getDeviceType() {
        return "Light";
    }

    @Override
    public String performAction() {
        return "Die Lampe leuchtet mit " + brightness + "% Helligkeit in der Farbe " + color + ".";
    }

    public int getBrightness() { return brightness; }
    public String getColor() { return color; }
}