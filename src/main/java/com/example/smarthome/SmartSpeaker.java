package com.example.smarthome;

/**
 * Repräsentiert einen smarten Lautsprecher.
 * Erbt von SmartDevice und ergänzt Funktionen für Lautstärke und Musikwiedergabe.
 */
public class SmartSpeaker extends SmartDevice {
    
    // Die aktuelle Lautstärke des Geräts in Prozent (0 bis 100).
    private int volume;
    
    // Der aktuell wiedergegebene Song.
    private String currentSong;

    /**
     * Konstruktor für den smarten Lautsprecher.
     * 
     * @param id Die ID des Geräts.
     * @param name Der Name (z.B. "Alexa").
     * @param room Der Raum (z.B. "Küche").
     * @param powerUsage Stromverbrauch in Watt.
     * @param volume Startlautstärke (0 bis 100).
     * @param currentSong Der Song, der zu Beginn läuft (darf auch leer sein).
     */
    public SmartSpeaker(int id, String name, String room, double powerUsage, int volume, String currentSong) {
        // Ruft den Basis-Konstruktor von SmartDevice auf
        super(id, name, room, powerUsage);
        
        // Die Lautstärke darf nicht über 100 oder unter 0 liegen
        if (volume < 0 || volume > 100) {
            throw new SmartHomeException("Lautstärke muss zwischen 0 und 100 liegen.");
        }
        
        this.volume = volume;
        this.currentSong = currentSong;
    }

    /**
     * Gibt die aktuelle Lautstärke zurück.
     */
    public int getVolume() { return volume; }

    /**
     * Ändert die Lautstärke des Lautsprechers.
     * @param volume Neue Lautstärke (0 bis 100).
     */
    public void setVolume(int volume) {
        if (volume < 0 || volume > 100) {
            throw new SmartHomeException("Lautstärke muss zwischen 0 und 100 liegen.");
        }
        this.volume = volume;
    }

    /**
     * Gibt den Namen des gerade abspielenden Songs zurück.
     */
    public String getCurrentSong() { return currentSong; }

    /**
     * Ändert den aktuellen Song.
     * @param currentSong Der neue Song-Titel.
     */
    public void setCurrentSong(String currentSong) {
        this.currentSong = currentSong;
    }

    /**
     * Erhöht die Lautstärke um 10.
     * Nutzt Math.min(), um sicherzustellen, dass die Lautstärke niemals über 100 steigt.
     */
    public void increaseVolume() {
        setVolume(Math.min(100, this.volume + 10));
    }

    /**
     * Reduziert die Lautstärke um 10.
     * Nutzt Math.max(), um sicherzustellen, dass die Lautstärke niemals unter 0 fällt.
     */
    public void decreaseVolume() {
        setVolume(Math.max(0, this.volume - 10));
    }

    /**
     * Startet die Wiedergabe eines bestimmten Songs.
     * @param song Der Titel des Songs.
     */
    public void playSong(String song) {
        setCurrentSong(song);
    }

    /**
     * Gibt den Typ des Geräts zurück. (Implementierung der abstrakten Methode)
     * @return Immer "Speaker" für diese Klasse.
     */
    @Override
    public String getDeviceType() {
        return "Speaker";
    }

    /**
     * Führt die Hauptaktion des Lautsprechers aus.
     * Gibt eine Statusmeldung über den aktuellen Song und die Lautstärke zurück.
     * 
     * @return Eine Beschreibung der aktuellen Wiedergabe.
     */
    @Override
    public String performAction() {
        // Falls currentSong null ist, wird ein leerer String gedruckt, um Fehler zu vermeiden
        return "Lautsprecher spielt '" + (currentSong == null ? "" : currentSong) + "' (Lautstärke: " + volume + ").";
    }
}