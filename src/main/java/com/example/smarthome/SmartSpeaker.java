package com.example.smarthome;

public class SmartSpeaker extends SmartDevice {
    private int volume;
    private String currentSong;

    public SmartSpeaker(int id, String name, String room, double powerUsage, int volume, String currentSong) {
        super(id, name, room, powerUsage);
        this.volume = volume;
        this.currentSong = currentSong;
    }

    public int getVolume() { return volume; }
    public String getCurrentSong() { return currentSong; }

    @Override
    public String getDeviceType() {
        return "Speaker";
    }

    @Override
    public String performAction() {
        return "Lautsprecher spielt '" + currentSong + "' (Lautstärke: " + volume + ").";
    }
}