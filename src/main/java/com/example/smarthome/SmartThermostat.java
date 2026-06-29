package com.example.smarthome;

/**
 * Repräsentiert ein smartes Thermostat im System.
 * Erbt von SmartDevice und ergänzt Logik zur Temperaturkontrolle.
 */
public class SmartThermostat extends SmartDevice {
    
    // Die aktuell gemessene Temperatur im Raum.
    private double currentTemperature;
    
    // Die Temperatur, auf die der Raum geheizt werden soll.
    private double targetTemperature;

    /**
     * Konstruktor für ein smartes Thermostat.
     * 
     * @param id Die ID des Geräts.
     * @param name Der Name (z.B. "Wohnzimmerheizung").
     * @param room Der Raum (z.B. "Wohnzimmer").
     * @param powerUsage Stromverbrauch in Watt.
     * @param currentTemperature Aktuelle Temperatur in Grad Celsius.
     * @param targetTemperature Zieltemperatur in Grad Celsius.
     */
    public SmartThermostat(int id, String name, String room, double powerUsage, double currentTemperature, double targetTemperature) {
        // Ruft den Konstruktor von SmartDevice auf
        super(id, name, room, powerUsage);
        
        // Prüft, ob die Temperaturen nicht utopisch (z.B. 1000 Grad) sind
        validateTemperature(currentTemperature);
        validateTemperature(targetTemperature);
        
        this.currentTemperature = currentTemperature;
        this.targetTemperature = targetTemperature;
    }

    /**
     * Gibt die aktuell gemessene Temperatur zurück.
     */
    public double getCurrentTemperature() { return currentTemperature; }
    
    /**
     * Ändert die manuell gesetzte, aktuelle Temperatur.
     * @param currentTemperature Neuer Messwert.
     */
    public void setCurrentTemperature(double currentTemperature) {
        validateTemperature(currentTemperature);
        this.currentTemperature = currentTemperature;
    }

    /**
     * Gibt die gewünschte Zieltemperatur zurück.
     */
    public double getTargetTemperature() { return targetTemperature; }
    
    /**
     * Legt die neue Wunschtemperatur fest, auf die geheizt werden soll.
     * @param targetTemperature Neuer Zielwert.
     */
    public void setTargetTemperature(double targetTemperature) {
        validateTemperature(targetTemperature);
        this.targetTemperature = targetTemperature;
    }

    /**
     * Erhöht die Zieltemperatur um einen bestimmten Wert.
     * 
     * @param value Der Wert, der addiert werden soll (z.B. 1.5 Grad).
     */
    public void increaseTargetTemperature(double value) {
        setTargetTemperature(this.targetTemperature + value);
    }

    /**
     * Verringert die Zieltemperatur um einen bestimmten Wert.
     * 
     * @param value Der Wert, der abgezogen werden soll.
     */
    public void decreaseTargetTemperature(double value) {
        setTargetTemperature(this.targetTemperature - value);
    }

    /**
     * Überprüft, ob die Heizung eingeschaltet werden muss.
     * Dies ist der Fall, wenn es im Raum kälter ist als gewünscht.
     * 
     * @return true, wenn geheizt werden muss, andernfalls false.
     */
    public boolean isHeatingNeeded() {
        return this.currentTemperature < this.targetTemperature;
    }

    /**
     * Gibt den Typ des Geräts zurück. (Implementierung der abstrakten Methode)
     * @return Immer "Thermostat" für diese Klasse.
     */
    @Override
    public String getDeviceType() {
        return "Thermostat";
    }

    /**
     * Führt die Hauptaktion des Thermostats aus. Gibt eine passende Statusmeldung
     * zurück, abhängig davon, ob geheizt werden muss oder nicht.
     * 
     * @return Ein Text, der den Heizstatus beschreibt.
     */
    @Override
    public String performAction() {
        if (isHeatingNeeded()) {
            return "Heizung wird aktiviert, Zieltemperatur: " + targetTemperature + "°C.";
        } else {
            return "Keine Heizung notwendig.";
        }
    }

    /**
     * Hilfsmethode, um zu überprüfen, ob eingegebene Temperaturen
     * in einem logischen Rahmen liegen (-20 bis 50 Grad).
     * 
     * @param temp Die zu prüfende Temperatur.
     */
    private void validateTemperature(double temp) {
        if (temp < -20.0 || temp > 50.0) {
            throw new SmartHomeException("Temperatur muss zwischen -20 und 50 Grad liegen.");
        }
    }
}