package com.example.smarthome;

public class SmartSpeaker extends SmartDevice {
    private int volume;
    private String currentSong;

    public SmartSpeaker(int id, String name, String room, double powerUsage, int volume, String currentSong) {
        super(id, name, room, powerUsage);
        setVolume(volume);
        this.currentSong = (currentSong == null) ? "" : currentSong;
    }

    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new SmartHomeException("Lautstärke muss zwischen 0 und 100 liegen.");
        }
        this.volume = volume;
    }

    public void increaseVolume() {
        if (this.volume + 10 <= 100) {
            this.volume += 10;
        } else {
            this.volume = 100;
        }
    }

    public void decreaseVolume() {
        if (this.volume - 10 >= 0) {
            this.volume -= 10;
        } else {
            this.volume = 0;
        }
    }

    public void playSong(String song) {
        this.currentSong = song;
    }

    @Override
    public String getDeviceType() {
        return "Speaker";
    }

    @Override
    public String performAction() {
        return "Der Lautsprecher spielt " + (currentSong.isEmpty() ? "momentan keinen Song" : currentSong) + " mit Lautstärke " + volume + ".";
    }

    public int getVolume() { return volume; }
    public String getCurrentSong() { return currentSong; }
}