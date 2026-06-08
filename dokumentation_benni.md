# Technische Dokumentation - Smart Home Geräteklassen (Bennis Teil)

Diese Dokumentation beschreibt die Implementierung der smarten Geräteklassen, der Validierungslogik und der dazugehörigen Unit-Tests im IoT-Dashboard-Projekt.

---

## 🏗️ Klassenstruktur und OOP-Konzepte

Die Klassen wurden streng nach den Vorgaben der UML-Dokumentation entworfen und setzen grundlegende Konzepte der objektorientierten Programmierung (OOP) um:

```
                          SmartDevice (abstrakt)
                                    ▲
         ┌──────────────────────────┼──────────────────────────┐
         │                          │                          │
    SmartLight               SmartThermostat              SmartSpeaker
(Dimmen & Farbsteuerung)   (Temperaturregelung)       (Musikwiedergabe)
```

1. **Abstraktion:** `SmartDevice` dient als abstrakte Basisklasse. Sie definiert gemeinsame Attribute und zwingt Unterklassen über abstrakte Methoden (`getDeviceType()`, `performAction()`), gerätespezifisches Verhalten zu implementieren.
2. **Vererbung:** `SmartLight`, `SmartThermostat` und `SmartSpeaker` erben alle Eigenschaften und grundlegenden Validierungsmethoden von `SmartDevice`.
3. **Kapselung (Encapsulation):** Alle Attribute sind als `private` deklariert. Der Zugriff erfolgt ausschließlich über validierte Getter und Setter.
4. **Polymorphismus:** Jede Unterklasse implementiert die Methode `performAction()` unterschiedlich, um die spezifischen Aktionen des jeweiligen Gerätetyps darzustellen.

---

## 🛡️ Validierungsregeln & Exception-Handling

Alle Eingabeparameter werden sowohl im **Konstruktor** als auch in den **Settern** auf ihre Gültigkeit überprüft. Schlägt eine Validierung fehl, wird eine `SmartHomeException` (eine `RuntimeException`) geworfen.

| Klasse | Attribut / Parameter | Validierungsregel | Exception-Nachricht bei Fehler |
| :--- | :--- | :--- | :--- |
| **SmartDevice** | `id` | darf nicht negativ sein (`< 0`) | `"ID darf nicht negativ sein."` |
| | `name` | darf nicht leer, null oder blank sein | `"Name darf nicht leer sein."` |
| | `room` | darf nicht leer, null oder blank sein | `"Raum darf nicht leer sein."` |
| | `powerUsage` | darf nicht negativ sein (`< 0`) | `"Stromverbrauch darf nicht negativ sein."` |
| | `calculateDailyConsumption(hours)` | Stunden dürfen nicht negativ sein (`< 0`) | `"Stunden dürfen nicht negativ sein."` |
| **SmartLight** | `brightness` / `dim()` | muss zwischen `0` und `100` liegen | `"Helligkeit muss zwischen 0 und 100 liegen."` |
| | `color` / `changeColor()` | darf nicht leer, null oder blank sein | `"Farbe darf nicht leer sein."` |
| **SmartThermostat** | `currentTemperature` | muss zwischen `-20.0` und `50.0` liegen | `"Temperatur muss zwischen -20 und 50 Grad liegen."` |
| | `targetTemperature` | muss zwischen `-20.0` und `50.0` liegen | `"Temperatur muss zwischen -20 und 50 Grad liegen."` |
| **SmartSpeaker** | `volume` | muss zwischen `0` und `100` liegen | `"Lautstärke muss zwischen 0 und 100 liegen."` |

---

## 💻 Spezifische Methoden & Logik

### 1. SmartDevice (Basisklasse)
* **`calculateDailyConsumption(int hours)`**:
  Berechnet den Stromverbrauch des Geräts in Wattstunden für eine bestimmte Anzahl an Stunden.
  $$DailyConsumption = powerUsage \times hours$$
* **`turnOn()` / `turnOff()`**:
  Schaltet das Gerät ein (`turnedOn = true`) oder aus (`turnedOn = false`).

### 2. SmartLight
* **`dim(int percent)`**:
  Ändert die Helligkeit der Lampe auf einen Wert zwischen 0% und 100% (nutzt intern den validierten Setter).
* **`changeColor(String color)`**:
  Ändert die Leuchtfarbe der Lampe.
* **`performAction()`**:
  Gibt folgendes Format zurück:  
  `"Die Lampe leuchtet in [Farbe] mit [Helligkeit]% Helligkeit."`

### 3. SmartThermostat
* **`increaseTargetTemperature(double value)`** / **`decreaseTargetTemperature(double value)`**:
  Erhöht oder verringert die Zieltemperatur um den angegebenen Wert (prüft über `setTargetTemperature` die Grenzwerte).
* **`isHeatingNeeded()`**:
  Gibt `true` zurück, wenn die aktuelle Raumtemperatur kleiner als die Zieltemperatur ist.
* **`performAction()`**:
  Gibt je nach Heizbedarf einen der folgenden Texte zurück:
  * Falls geheizt werden muss: `"Heizung wird aktiviert, Zieltemperatur: [Zieltemperatur]°C."`
  * Falls warm genug: `"Keine Heizung notwendig."`

### 4. SmartSpeaker
* **`increaseVolume()`** / **`decreaseVolume()`**:
  Erhöht oder verringert die Lautstärke in **10er-Schritten**. Der Wert wird automatisch bei maximal `100` gedeckelt bzw. bei minimal `0` abgefangen.
* **`playSong(String song)`**:
  Startet die Wiedergabe des Songs (kann leer oder null sein, um die Musik zu stoppen).
* **`performAction()`**:
  Gibt folgendes Format zurück:  
  `"Lautsprecher spielt '[Song]' (Lautstärke: [Lautstärke])."`

---

## 🧪 Qualitätssicherung (JUnit-Tests)

In der Testklasse `SmartHomeDeviceTest` wurden **10 eigenständige Testfälle** implementiert, um die Zuverlässigkeit des Codes abzusichern:

1. **`testSmartLightCreation()`**: Überprüft das korrekte Anlegen einer Lampe und das Auslesen der Standardwerte.
2. **`testSmartThermostatCreation()`**: Prüft das Anlegen eines Thermostats mit Stromverbrauch und Raumtemperaturen.
3. **`testSmartSpeakerCreation()`**: Prüft die Erstellung eines Lautsprechers mit Lautstärke und Start-Song.
4. **`testDeviceTurnOnOff()`**: Testet die Schaltlogik (an/aus) und die Text-Rückgabe (`getStatusText()`).
5. **`testDeviceValidation()`**: Testet, ob bei unzulässigen Werten im Konstruktor oder in den Settern korrekterweise eine `SmartHomeException` geworfen wird (z.B. negative IDs, leere Namen/Räume).
6. **`testPerformAction()`**: Verifiziert, dass alle polymorphen `performAction()`-Methoden exakt die erwarteten Statusmeldungen zurückgeben.
7. **`testDailyConsumption()`**: Prüft die korrekte mathematische Berechnung des Verbrauchs und das Abfangen von negativen Zeiteinheiten.
8. **`testSmartLightSpecifics()`**: Testet das Dimmen (inklusive der Bereichsüberschreitungen) und das Ändern der Farbe.
9. **`testSmartThermostatSpecifics()`**: Überprüft die Temperaturgrenzen, das schrittweise Erhöhen/Senken der Zieltemperatur und die Erkennung, ob Heizbedarf besteht.
10. **`testSmartSpeakerSpecifics()`**: Testet die Lautstärkenänderung in Zehnerschritten (inkl. automatischem Capping bei 0 und 100) sowie das Setzen und Zurücksetzen von Liedern.
