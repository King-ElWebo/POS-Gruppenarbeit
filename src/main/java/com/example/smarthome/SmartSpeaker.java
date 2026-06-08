package com.example.smarthome;

public class SmartSpeaker extends SmartDevice {
    private int volume;
    private String currentSong;

    public SmartSpeaker(int id, String name, String room, double powerUsage, int volume, String currentSong) {
        super(id, name, room, powerUsage);
        if (volume < 0 || volume > 100) {
            throw new SmartHomeException("Lautstärke muss zwischen 0 und 100 liegen.");
        }
        this.volume = volume;
        this.currentSong = currentSong;
    }

    public int getVolume() { return volume; }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new SmartHomeException("Lautstärke muss zwischen 0 und 100 liegen.");
        }
        this.volume = volume;
    }

    public String getCurrentSong() { return currentSong; }

    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }

    public void increaseVolume() {
        setVolume(Math.min(100, this.volume + 10));
    }

    public void decreaseVolume() {
        setVolume(Math.max(0, this.volume - 10));
    }

    public void playSong(String song) {
        setCurrentSong(song);
    }

    @Override
    public String getDeviceType() {
        return "Speaker";
    }

    @Override
    public String performAction() {
        return "Lautsprecher spielt '" + (currentSong == null ? "" : currentSong) + "' (Lautstärke: " + volume + ").";
    }
}