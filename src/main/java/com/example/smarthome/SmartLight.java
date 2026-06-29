package com.example.smarthome;

/**
 * Repräsentiert eine smarte Lampe im System.
 * Erbt alle grundlegenden Eigenschaften von SmartDevice und
 * erweitert diese um spezifische Licht-Eigenschaften wie Helligkeit und Farbe.
 */
public class SmartLight extends SmartDevice {
    
    // Die aktuelle Helligkeit der Lampe in Prozent (0 bis 100).
    private int brightness;
    
    // Die eingestellte Lichtfarbe (z. B. "Weiß", "Blau").
    private String color;

    /**
     * Konstruktor für eine smarte Lampe.
     * Nimmt Basisdaten und spezifische Lampendaten entgegen und validiert sie.
     * 
     * @param id Die ID des Geräts.
     * @param name Der Name (z.B. "Schreibtischlampe").
     * @param room Der Raum (z.B. "Arbeitszimmer").
     * @param powerUsage Stromverbrauch in Watt.
     * @param brightness Helligkeit in Prozent (muss zwischen 0 und 100 liegen).
     * @param color Die Lichtfarbe (darf nicht leer sein).
     */
    public SmartLight(int id, String name, String room, double powerUsage, int brightness, String color) {
        // Ruft den Konstruktor der übergeordneten Klasse (SmartDevice) auf
        super(id, name, room, powerUsage);
        
        // Prüft, ob die Helligkeit sinnvolle Werte hat
        if (brightness < 0 || brightness > 100) {
            throw new SmartHomeException("Helligkeit muss zwischen 0 und 100 liegen.");
        }
        
        // Nutzt die Methode aus der Oberklasse, um zu prüfen, ob die Farbe leer ist
        validateText(color, "Farbe");
        
        this.brightness = brightness;
        this.color = color;
    }

    /**
     * Gibt die aktuelle Helligkeit zurück.
     * @return Helligkeit in Prozent.
     */
    public int getBrightness() { return brightness; }
    
    /**
     * Ändert die Helligkeit der Lampe.
     * @param brightness Neuer Helligkeitswert (0 bis 100).
     */
    public void setBrightness(int brightness) {
        if (brightness < 0 || brightness > 100) {
            throw new SmartHomeException("Helligkeit muss zwischen 0 und 100 liegen.");
        }
        this.brightness = brightness;
    }

    /**
     * Gibt die aktuelle Lichtfarbe zurück.
     * @return Die Farbe als String.
     */
    public String getColor() { return color; }

    /**
     * Ändert die Lichtfarbe der Lampe.
     * @param color Die neue Farbe.
     */
    public void setColor(String color) {
        validateText(color, "Farbe");
        this.color = color;
    }

    /**
     * Dimmt die Lampe auf einen bestimmten Prozentwert.
     * Ist im Prinzip eine alternative Methode zu setBrightness.
     * 
     * @param percent Der neue Helligkeitswert.
     */
    public void dim(int percent) {
        setBrightness(percent);
    }

    /**
     * Ändert die Farbe der Lampe. 
     * Alternative Bezeichnung für setColor.
     * 
     * @param color Die neue Farbe.
     */
    public void changeColor(String color) {
        setColor(color);
    }

    /**
     * Gibt den Typ des Geräts zurück. (Implementierung der abstrakten Methode)
     * @return Immer "Light" für diese Klasse.
     */
    @Override
    public String getDeviceType() {
        return "Light";
    }

    /**
     * Führt die Hauptaktion der Lampe aus und liefert einen Beschreibungstext.
     * (Implementierung der abstrakten Methode)
     * 
     * @return Eine Beschreibung der aktuellen Lichtausgabe.
     */
    @Override
    public String performAction() {
        return "Die Lampe leuchtet in " + color + " mit " + brightness + "% Helligkeit.";
    }
}