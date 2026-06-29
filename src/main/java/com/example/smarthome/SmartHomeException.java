package com.example.smarthome;

/**
 * Eine selbstgeschriebene Exception (Fehlerklasse) für das Smart-Home-Projekt.
 * 
 * Warum machen wir das?
 * Statt im System einfach generische Java-Fehler (wie IllegalArgumentException) zu werfen,
 * werfen wir unsere eigene `SmartHomeException`. Das hat den riesigen Vorteil,
 * dass wir in der Benutzeroberfläche genau wissen: "Aha, das ist ein Benutzerfehler bei den
 * Smart-Home-Geräten!" und können die Meldung (z.B. "Temperatur zu hoch") dann schön 
 * als Rote Box in Vaadin anzeigen, anstatt dass das Programm komplett abstürzt.
 * 
 * Erbt von RuntimeException: Das bedeutet, wir müssen sie nicht mühsam in 
 * jeder Methodensignatur (mit 'throws') deklarieren.
 */
public class SmartHomeException extends RuntimeException {

    /**
     * Konstruktor der Fehlerklasse.
     * @param message Die genaue Beschreibung des Fehlers (z.B. "Name darf nicht leer sein").
     */
    public SmartHomeException(String message) {
        // Leitet die Nachricht an die interne Java-Fehlerklasse weiter.
        super(message);
    }
}
