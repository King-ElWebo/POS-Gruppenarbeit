# IoT Dashboard Projekt - Klassendokumentation

## Klassenübersicht und Zusammenhänge

```
                          SmartDevice (abstrakt)
                                  ▲
                         _________|_________
                        |        |         |
                   SmartLight  SmartThermostat  SmartSpeaker
                   
                   SmartHomeManager (Verwaltung)
                           ▲
                           |
                    Kontrolliert
                           |
            ┌──────────────┼──────────────┐
            |              |              |
      SmartHomeFileHandler  SmartHomeException  SmartHomeDeviceTest
```

---

## 🎯 Kern-Klassen

### 1. **SmartDevice** (Abstrakte Basisklasse)

**Zweck:** Gemeinsame Schnittstelle für alle IoT-Geräte

**Attribute:**
- `id` - Eindeutige Gerät-ID
- `name` - Gerätename (z.B. "Wohnzimmerlampe")
- `room` - Raum, in dem das Gerät steht
- `turnedOn` - Zustand (an/aus)
- `powerUsage` - Stromverbrauch in Watt
- `favorite` - Als Favorit markiert?

**Wichtige Methoden:**
| Methode | Was macht es |
|---------|-------------|
| `turnOn()` | Schaltet das Gerät ein |
| `turnOff()` | Schaltet das Gerät aus |
| `getStatusText()` | Gibt "Ein" oder "Aus" zurück |
| `validateText(String, String)` | Prüft, ob Texte nicht leer sind |
| `abstract getDeviceType()` | Muss von Unterklassen implementiert werden |
| `abstract performAction()` | Muss von Unterklassen implementiert werden |

**Besonderheit:** Diese Klasse ist abstrakt - man kann sie nicht direkt benutzen, sondern nur von ihr erben.

---

### 2. **SmartLight** (Erbt von SmartDevice)

**Zweck:** Intelligente Lampe mit Farb- und Helligkeitssteuerung

**Zusätzliche Attribute:**
- `brightness` - Helligkeit in % (0-100)
- `color` - Farbe der Lampe (z.B. "Rot", "Weiß")

**Besondere Fähigkeiten:**
- Über `getBrightness()` kann die aktuelle Helligkeit abgefragt werden
- Über `getColor()` kann die Farbe abgefragt werden
- `performAction()` gibt aus: "Die Lampe leuchtet in [Farbe] mit [Helligkeit]% Helligkeit."

**Beispiel:**
```java
SmartLight lampe = new SmartLight(1, "Schreibtischlampe", "Arbeitszimmer", 15.0, 80, "Blau");
System.out.println(lampe.performAction()); 
// Ausgabe: Die Lampe leuchtet in Blau mit 80% Helligkeit.
```

---

### 3. **SmartThermostat** (Erbt von SmartDevice)

**Zweck:** Intelligentes Thermostat zur Temperaturregelung

**Zusätzliche Attribute:**
- `currentTemperature` - Aktuelle Raumtemperatur
- `targetTemperature` - Zieltemperatur

**Besondere Fähigkeiten:**
- Über `getCurrentTemperature()` kann die aktuelle Temperatur abgefragt werden
- Über `getTargetTemperature()` kann die Zieltemperatur abgefragt werden
- `performAction()` gibt aus: "Heizung auf Zieltemperatur [Temp]°C eingestellt."

**Beispiel:**
```java
SmartThermostat heizung = new SmartThermostat(2, "Wohnzimmer Heizung", "Wohnzimmer", 2500.0, 19.5, 22.0);
System.out.println(heizung.performAction()); 
// Ausgabe: Heizung auf Zieltemperatur 22.0°C eingestellt.
```

---

### 4. **SmartSpeaker** (Erbt von SmartDevice)

**Zweck:** Intelligenter Lautsprecher mit Musikwiedergabe

**Zusätzliche Attribute:**
- `volume` - Lautstärke (0-100)
- `currentSong` - Aktuell abgespielte Musik

**Besondere Fähigkeiten:**
- Über `getVolume()` kann die Lautstärke abgefragt werden
- Über `getCurrentSong()` kann abgefragt werden, welche Musik spielt
- `performAction()` gibt aus: "Lautsprecher spielt '[Lied]' (Lautstärke: [Volume])."

**Beispiel:**
```java
SmartSpeaker speaker = new SmartSpeaker(3, "Alexa", "Küche", 10.0, 70, "Bohemian Rhapsody");
System.out.println(speaker.performAction()); 
// Ausgabe: Lautsprecher spielt 'Bohemian Rhapsody' (Lautstärke: 70).
```

---

## 📊 Service-Klassen

### 5. **SmartHomeManager** (Verwaltungs-Service)

**Zweck:** Zentrale Verwaltung aller Geräte

**Funktionen:**
| Methode | Was macht es |
|---------|-------------|
| `addDevice(SmartDevice)` | Fügt ein Gerät hinzu |
| `removeDevice(SmartDevice)` | Entfernt ein Gerät (per Objekt) |
| `removeDevice(int id)` | Entfernt ein Gerät (per ID) |
| `getAllDevices()` | Gibt alle Geräte als Liste zurück |
| `getDeviceCount()` | Zählt die Anzahl der Geräte |
| `getTotalPowerUsage()` | Berechnet den gesamten Stromverbrauch |
| `getAveragePowerUsage()` | Berechnet den durchschnittlichen Verbrauch |
| `getActiveDeviceCount()` | Zählt die eingeschalteten Geräte |
| `filterFavorites()` | Gibt nur Favoriten zurück |

**Zusammenhang:** Dieser Manager wird von allen View-Klassen benutzt, um Geräte zu verwalten.

---

### 6. **SmartHomeException** (Fehlerbehandlung)

**Zweck:** Eigene Exception für Smart-Home-Fehler

**Verwendung:** Wird geworfen, wenn etwas schiefgeht, z.B.:
- Leere Texteingaben
- Ungültige Geräteparameter
- Fehler beim Speichern/Laden

**Beispiel:**
```java
throw new SmartHomeException("Name darf nicht leer sein.");
```

---

### 7. **SmartHomeFileHandler** (Persistierung)

**Zweck:** Speichert und lädt Geräte in/aus CSV-Datei

**Funktionen:**
| Methode | Was macht es |
|---------|-------------|
| `saveDevices(List<SmartDevice>)` | Speichert alle Geräte in `devices.csv` |
| `loadDevices()` | Lädt alle Geräte aus `devices.csv` |
| `deviceToCsv(SmartDevice)` | Konvertiert Gerät zu CSV-Format |
| `csvToDevice(String)` | Konvertiert CSV-Zeile zu Gerät-Objekt |

**CSV-Format Beispiel:**
```csv
Light;1;Schreibtischlampe;Arbeitszimmer;true;15.0;false;80;Blau
Thermostat;2;Heizung;Wohnzimmer;true;2500.0;true;19.5;22.0
Speaker;3;Alexa;Küche;false;10.0;false;70;Bohemian Rhapsody
```

---

## 🧪 Test-Klassen

### 11. **SmartHomeDeviceTest** (JUnit-Tests)

**Zweck:** Automatisierte Tests für alle Geräte-Klassen

**Test-Methoden:**
| Test-Methode | Was wird getestet |
|-------------|-------------------|
| `testSmartLightCreation()` | Erstellung von SmartLight-Objekten |
| `testSmartThermostatCreation()` | Erstellung von SmartThermostat-Objekten |
| `testSmartSpeakerCreation()` | Erstellung von SmartSpeaker-Objekten |
| `testDeviceTurnOnOff()` | Ein-/Ausschalten-Funktionalität |
| `testDeviceValidation()` | Validierung von Eingabeparametern |
| `testPerformAction()` | `performAction()`-Methoden aller Geräte |

**Beispiel-Test:**
```java
@Test
public void testSmartLightCreation() {
    SmartLight light = new SmartLight(1, "Lampe", "Zimmer", 10.0, 80, "Blau");
    assertEquals("Light", light.getDeviceType());
    assertEquals(80, light.getBrightness());
    assertEquals("Blau", light.getColor());
}
```

**Zusammenhang:** Testet alle Unterklassen von `SmartDevice` auf korrekte Funktionalität.

---

## 🔗 Zusammenspiel aller Klassen

```
Benutzer nutzt UI
        ↓
[DashboardView] [DeviceListView] [DeviceFormView]
        ↓              ↓                 ↓
        └──────────────┴─────────────────┘
                       ↓
              SmartHomeManager
              (verwaltet alle Geräte)
                       ↓
         ┌─────────────┼─────────────┐
         ↓             ↓             ↓
    SmartLight  SmartThermostat  SmartSpeaker
         ↓             ↓             ↓
         └─────────────┴─────────────┘
                       ↓
              SmartDevice (abstrakt)
                       ↓
              SmartHomeFileHandler
              (speichert/lädt in CSV)
                       ↓
              SmartHomeException
              (bei Fehlern)
```

---

## 🧪 Test-Zusammenhang

```
SmartHomeDeviceTest
        ↓
    Testet alle Geräte-Klassen
        ↓
SmartDevice ← SmartLight, SmartThermostat, SmartSpeaker
```

---

## 🧪 Test-Szenario: "Geräte-Funktionalität testen"

### Szenario: "JUnit-Tests für alle Geräte ausführen"

1. **Entwickler** führt `SmartHomeDeviceTest` aus
2. **testSmartLightCreation()** erstellt ein `SmartLight`-Objekt
3. **testDeviceTurnOnOff()** testet Ein-/Ausschalten
4. **testDeviceValidation()** prüft, ob Exceptions bei ungültigen Eingaben geworfen werden
5. **testPerformAction()** verifiziert die spezifischen Aktionen jedes Gerätetyps
6. **JUnit-Runner** zeigt grüne Tests bei Erfolg oder rote bei Fehlern

**Beispiel-Test-Code:**
```java
@Test
public void testDeviceTurnOnOff() {
    SmartLight light = new SmartLight(1, "Lampe", "Zimmer", 10.0, 80, "Blau");
    
    // Test: Gerät ist anfangs aus
    assertFalse(light.isTurnedOn());
    
    // Test: Einschalten
    light.turnOn();
    assertTrue(light.isTurnedOn());
    assertEquals("Ein", light.getStatusText());
    
    // Test: Ausschalten
    light.turnOff();
    assertFalse(light.isTurnedOn());
    assertEquals("Aus", light.getStatusText());
}
```

---

## 🎓 Wichtige OOP-Konzepte

| Konzept | Wie verwendet |
|---------|--------------|
| **Vererbung** | `SmartLight`, `SmartThermostat`, `SmartSpeaker` erben von `SmartDevice` |
| **Abstraktion** | `SmartDevice` ist abstrakt, `getDeviceType()` und `performAction()` müssen implementiert werden |
| **Polymorphismus** | Jedes Gerät kann `performAction()` unterschiedlich implementieren |
| **Encapsulation** | Private Attribute mit Getter/Setter-Methoden |
| **Exception Handling** | `SmartHomeException` für Fehlerbehandlung |
| **Dependency Injection** | `SmartHomeManager` wird in Views injiziert |  

---

## Abstrakte Klasse SmartDevice

`SmartDevice` ist die abstrakte Oberklasse für alle smarten Geräte.

Von dieser Klasse werden keine direkten Objekte erstellt. Sie dient als gemeinsame Vorlage für alle Gerätetypen.

### Attribute
```java
private int id;
private String name;
private String room;
private boolean isOn;
private double powerUsage;
private boolean favorite;
```

### Bedeutung der Attribute
| Attribut | Bedeutung |
| :--- | :--- |
| `id` | eindeutige Nummer des Geräts |
| `name` | Name des Geräts, z. B. „Wohnzimmerlampe“ |
| `room` | Raum, in dem sich das Gerät befindet |
| `isOn` | gibt an, ob das Gerät eingeschaltet ist |
| `powerUsage` | Stromverbrauch des Geräts |
| `favorite` | gibt an, ob das Gerät als Favorit markiert ist |

### Konstruktor
```java
public SmartDevice(int id, String name, String room, double powerUsage)
```

Der Konstruktor erstellt ein neues Gerät mit den wichtigsten Grunddaten.

Er soll prüfen:
* `id` darf nicht negativ sein
* `name` darf nicht leer sein
* `room` darf nicht leer sein
* `powerUsage` darf nicht negativ sein

Bei falschen Werten soll eine `SmartHomeException` geworfen werden.

### Normale Methoden mit Logik

#### `turnOn()`
```java
public void turnOn()
```

Schaltet das Gerät ein.
**Beispiel:**
```java
isOn = true;
```

#### `turnOff()`
```java
public void turnOff()
```

Schaltet das Gerät aus.
**Beispiel:**
```java
isOn = false;
```

#### `calculateDailyConsumption(int hours)`
```java
public double calculateDailyConsumption(int hours)
```

Berechnet den Stromverbrauch für eine bestimmte Anzahl an Stunden.
**Beispiel:**
```java
powerUsage * hours;
```
Wenn `hours` kleiner als 0 ist, soll eine Exception geworfen werden.

#### `getStatusText()`
```java
public String getStatusText()
```

Gibt den Gerätestatus als Text zurück.
**Beispiel:** `"Ein"` oder `"Aus"`

#### `validateText(String value, String fieldName)`
```java
protected void validateText(String value, String fieldName)
```

Prüft, ob ein Textfeld leer ist.
Diese Methode kann von Konstruktoren oder Settern verwendet werden.

### Abstrakte Methoden in SmartDevice

Diese Methoden haben in der Oberklasse keinen fertigen Inhalt. Jede Subklasse muss sie selbst implementieren.

#### 1. `getDeviceType()`
```java
public abstract String getDeviceType();
```

Diese Methode gibt den Gerätetyp zurück.
**Beispiele:** `"Light"`, `"Thermostat"`, `"Speaker"`  
**Warum abstrakt?**
Weil jedes Gerät einen anderen Typ hat.

#### 2. `performAction()`
```java
public abstract String performAction();
```

Diese Methode führt eine typische Aktion des jeweiligen Geräts aus.
**Beispiele:**
* **Lampe:** Licht einschalten oder Farbe anzeigen
* **Thermostat:** Heizen prüfen
* **Lautsprecher:** Song abspielen  

**Warum abstrakt?**
Weil jedes Gerät eine andere Hauptfunktion hat.

---

## Klasse SmartLight

`SmartLight` ist eine Subklasse von `SmartDevice`.
Sie steht für eine smarte Lampe.

```java
public class SmartLight extends SmartDevice
```

### Zusätzliche Attribute
```java
private int brightness;
private String color;
```

| Attribut | Bedeutung |
| :--- | :--- |
| `brightness` | Helligkeit von 0 bis 100 |
| `color` | Lichtfarbe, z. B. „Weiß“, „Blau“, „Rot“ |

### Konstruktor
```java
public SmartLight(int id, String name, String room, double powerUsage, int brightness, String color)
```

Er erstellt eine Lampe und prüft zusätzlich:
* `brightness` muss zwischen 0 und 100 liegen
* `color` darf nicht leer sein

### Methoden

#### `setBrightness(int brightness)`

Setzt die Helligkeit.
Erlaubt sind nur Werte von 0 bis 100.

#### `dim(int percent)`
```java
public void dim(int percent)
```

Dimmt die Lampe auf einen bestimmten Prozentwert.
**Beispiel:** `dim(50);` setzt die Helligkeit auf 50 %.

#### `changeColor(String color)`
```java
public void changeColor(String color)
```

Ändert die Farbe der Lampe.
**Beispiel:** `changeColor("Blau");`

#### `getDeviceType()`
```java
@Override 
public String getDeviceType()
```
Gibt zurück: `"Light"`

#### `performAction()`
```java
@Override 
public String performAction()
```

Soll eine typische Lampenaktion zurückgeben.
**Beispiel:** `"Die Lampe leuchtet mit 80% Helligkeit in der Farbe Weiß."`

---

## Klasse SmartThermostat

`SmartThermostat` ist eine Subklasse von `SmartDevice`.
Sie steht für ein smartes Thermostat.

```java
public class SmartThermostat extends SmartDevice
```

### Zusätzliche Attribute
```java
private double currentTemperature;
private double targetTemperature;
```

| Attribut | Bedeutung |
| :--- | :--- |
| `currentTemperature` | aktuelle Raumtemperatur |
| `targetTemperature` | gewünschte Zieltemperatur |

### Konstruktor
```java
public SmartThermostat(int id, String name, String room, double powerUsage, double currentTemperature, double targetTemperature)
```

Erstellt ein Thermostat und prüft:
* Temperaturen sollten in einem sinnvollen Bereich liegen, z. B. -20 bis 50 Grad
* Zieltemperatur darf nicht unrealistisch sein

### Methoden

#### `setCurrentTemperature(double currentTemperature)`

Setzt die aktuelle Temperatur.

#### `setTargetTemperature(double targetTemperature)`

Setzt die gewünschte Zieltemperatur.

#### `increaseTargetTemperature(double value)`
```java
public void increaseTargetTemperature(double value)
```

Erhöht die Zieltemperatur.
**Beispiel:** `increaseTargetTemperature(1.5);`

#### `decreaseTargetTemperature(double value)`
```java
public void decreaseTargetTemperature(double value)
```

Senkt die Zieltemperatur.

#### `isHeatingNeeded()`
```java
public boolean isHeatingNeeded()
```

Prüft, ob geheizt werden muss.
**Beispiel:**
```java
return currentTemperature < targetTemperature;
```
Wenn die aktuelle Temperatur niedriger ist als die Zieltemperatur, gibt die Methode `true` zurück.

#### `getDeviceType()`
Gibt zurück: `"Thermostat"`

#### `performAction()`
Gibt eine typische Thermostat-Aktion zurück.  
**Beispiele:** 
* `"Heizung wird aktiviert, Zieltemperatur: 22°C."`
* `"Keine Heizung notwendig."`

---

## Klasse SmartSpeaker

`SmartSpeaker` ist eine Subklasse von `SmartDevice`.
Sie steht für einen smarten Lautsprecher.

```java
public class SmartSpeaker extends SmartDevice
```

### Zusätzliche Attribute
```java
private int volume;
private String currentSong;
```

| Attribut | Bedeutung |
| :--- | :--- |
| `volume` | Lautstärke von 0 bis 100 |
| `currentSong` | aktuell abgespielter Song |

### Konstruktor
```java
public SmartSpeaker(int id, String name, String room, double powerUsage, int volume, String currentSong)
```

Er prüft:
* `volume` muss zwischen 0 und 100 liegen
* `currentSong` darf leer sein, wenn gerade nichts läuft

### Methoden

#### `setVolume(int volume)`

Setzt die Lautstärke.
Nur Werte von 0 bis 100 sind erlaubt.

#### `increaseVolume()`
```java
public void increaseVolume()
```
Erhöht die Lautstärke, z. B. um 10. Maximal bis 100.

#### `decreaseVolume()`
```java
public void decreaseVolume()
```
Senkt die Lautstärke, z. B. um 10. Minimal bis 0.

#### `playSong(String song)`
```java
public void playSong(String song)
```

Setzt den aktuellen Song.
**Beispiel:** `playSong("Imagine");`

#### `getDeviceType()`
Gibt zurück: `"Speaker"`

#### `performAction()`

Gibt eine typische Lautsprecheraktion zurück.
**Beispiel:** `"Der Lautsprecher spielt Imagine mit Lautstärke 60."`

---

## Klasse SmartHomeManager

Der `SmartHomeManager` ist die wichtigste Klasse für die Fachlogik.
Er verwaltet alle Geräte in einer Liste.

```java
private List<SmartDevice> devices;
private List<String> logs;
```

### CRUD-Methoden

#### `addDevice(SmartDevice device)`
```java
public void addDevice(SmartDevice device)
```

Fügt ein Gerät zur Liste hinzu.
Soll prüfen:
* Gerät darf nicht `null` sein
* ID darf noch nicht existieren

#### `removeDevice(int id)`
```java
public void removeDevice(int id)
```

Entfernt ein Gerät anhand der ID.
Wenn kein Gerät gefunden wird, wird eine Exception geworfen.

#### `updateDevice(SmartDevice device)`
```java
public void updateDevice(SmartDevice device)
```

Ersetzt ein bestehendes Gerät durch eine neue Version.
Wird für das Bearbeiten in der GUI verwendet.

#### `findDeviceById(int id)`
```java
public SmartDevice findDeviceById(int id)
```

Sucht ein Gerät anhand der ID.
Wenn nichts gefunden wird, Exception oder `null`, besser: Exception.

#### `getAllDevices()`
```java
public List<SmartDevice> getAllDevices()
```

Gibt alle Geräte zurück.
Am besten als Kopie:
```java
return new ArrayList<>(devices);
```
Damit die GUI die interne Liste nicht kaputt macht.

### Such- und Filtermethoden

#### `searchByName(String name)`

Sucht Geräte, deren Name den Suchtext enthält.
**Beispiel:** `"lampe"` findet `"Wohnzimmerlampe"`

#### `filterByRoom(String room)`

Gibt alle Geräte aus einem bestimmten Raum zurück.
**Beispiel:** `filterByRoom("Wohnzimmer")`

#### `filterByType(String type)`

Filtert nach Gerätetyp.
**Beispiele:** `"Light"`, `"Thermostat"`, `"Speaker"`  
Verwendet intern: `device.getDeviceType()`

#### `filterByStatus(boolean isOn)`

Gibt alle Geräte zurück, die ein- oder ausgeschaltet sind.

#### `filterFavorites()`

Gibt alle Geräte zurück, die als Favorit markiert sind.

### Sortiermethoden

#### `sortByName()`

Sortiert alle Geräte alphabetisch nach Name.

#### `sortByPowerUsage()`

Sortiert alle Geräte nach Stromverbrauch.
Zum Beispiel von niedrig nach hoch.

### Gruppierungsmethoden

#### `groupByRoom()`
```java
public Map<String, List<SmartDevice>> groupByRoom()
```

Gruppiert Geräte nach Raum.
**Beispiel:**
* Wohnzimmer → Lampe, Lautsprecher
* Küche → Thermostat

#### `groupByType()`
```java
public Map<String, List<SmartDevice>> groupByType()
```

Gruppiert Geräte nach Gerätetyp.
**Beispiel:**
* Light → alle Lampen
* Speaker → alle Lautsprecher
* Thermostat → alle Thermostate

### Auswertungsmethoden

#### `getTotalPowerUsage()`
```java
public double getTotalPowerUsage()
```

Berechnet den gesamten Stromverbrauch aller Geräte.

#### `getAveragePowerUsage()`
```java
public double getAveragePowerUsage()
```

Berechnet den durchschnittlichen Stromverbrauch.
Wenn keine Geräte vorhanden sind, sollte 0 zurückgegeben werden.

#### `getHighestPowerUsageDevice()`
```java
public SmartDevice getHighestPowerUsageDevice()
```

Gibt das Gerät mit dem höchsten Stromverbrauch zurück.

#### `getActiveDeviceCount()`
```java
public int getActiveDeviceCount()
```

Zählt alle Geräte, die eingeschaltet sind.

### Kombinierte Methode

#### `filterByRoomAndSortByPowerUsage(String room)`
```java
public List<SmartDevice> filterByRoomAndSortByPowerUsage(String room)
```

Diese Methode erfüllt eine wichtige Anforderung, weil sie mehrere Verarbeitungsschritte kombiniert.
Sie macht:
1. Geräte nach Raum filtern
2. Ergebnis nach Stromverbrauch sortieren
3. sortierte Liste zurückgeben

**Beispiel:** `filterByRoomAndSortByPowerUsage("Wohnzimmer")`

### Zusatzfunktionen

#### Favoriten-System
* `markAsFavorite(int id)` - Markiert ein Gerät als Favorit.
* `unmarkAsFavorite(int id)` - Entfernt die Favoriten-Markierung.

#### Logfunktion
* `addLog(String message)` - Speichert eine Aktion im Log.  
  **Beispiel:** `"Gerät Wohnzimmerlampe wurde hinzugefügt."`
* `getLogs()` - Gibt alle Logeinträge zurück.

---

## Klasse SmartHomeFileHandler

Diese Klasse ist für Dateioperationen zuständig.
Sie speichert und lädt Geräte aus einer CSV-Datei.

### Attribut
```java
private String filePath;
```

Speichert den Pfad zur Datei.

### Methoden

#### `saveDevices(List<SmartDevice> devices)`
```java
public void saveDevices(List<SmartDevice> devices)
```

Speichert alle Geräte in einer Datei.
Jedes Gerät wird als CSV-Zeile gespeichert.

#### `loadDevices()`
```java
public List<SmartDevice> loadDevices()
```

Lädt Geräte aus der Datei und gibt sie als Liste zurück.

#### `deviceToCsv(SmartDevice device)`
```java
private String deviceToCsv(SmartDevice device)
```

Wandelt ein Gerät in eine CSV-Zeile um.
**Beispiel:** `Light;1;Wohnzimmerlampe;Wohnzimmer;true;12.5;false;80;Weiß`

#### `csvToDevice(String line)`
```java
private SmartDevice csvToDevice(String line)
```

Wandelt eine CSV-Zeile zurück in ein Objekt.
Je nach erstem Wert wird entschieden:
* `Light` → `SmartLight`
* `Thermostat` → `SmartThermostat`
* `Speaker` → `SmartSpeaker`

---

## Klasse SmartHomeException

Eigene Exception-Klasse für Projektfehler.

```java
public class SmartHomeException extends Exception
```

Oder einfacher:
```java
public class SmartHomeException extends RuntimeException
```

### Methode
```java
public SmartHomeException(String message)
```

Erstellt eine Fehlermeldung.
**Beispiele:**
```java
throw new SmartHomeException("Name darf nicht leer sein.");
throw new SmartHomeException("Gerät mit dieser ID existiert bereits.");
throw new SmartHomeException("Helligkeit muss zwischen 0 und 100 liegen.");
```

---

## Vaadin-Klasse DashboardView

Diese View zeigt eine Übersicht.

### Attribute
```java
private SmartHomeManager manager;
```

### Methoden

#### `updateStatistics()`

Aktualisiert die angezeigten Werte.
Zum Beispiel:
* Gesamtverbrauch
* Durchschnittsverbrauch
* Anzahl aktiver Geräte
* Anzahl Favoriten

#### `createStatisticCards()`
Erstellt die optischen Statistik-Karten in der GUI.
**Wichtig:**
Diese Methode berechnet nichts selbst, sondern ruft nur Manager-Methoden auf.

---

## Vaadin-Klasse DeviceListView

Diese View zeigt alle Geräte in einer Tabelle.

### Attribute
```java
private SmartHomeManager manager;
private Grid<SmartDevice> grid;
```

### Methoden

#### `refreshGrid()`

Aktualisiert die Tabelle.
**Beispiel:**
```java
grid.setItems(manager.getAllDevices());
```

#### `applyFilters()`
Liest die Filterfelder der GUI aus und ruft passende Manager-Methoden auf.
**Beispiel:**
```java
manager.filterByRoom(selectedRoom);
```

#### `deleteSelectedDevice()`
Löscht das ausgewählte Gerät über den Manager.
**Beispiel:**
```java
manager.removeDevice(id);
```
Fehler werden mit `try-catch` abgefangen und als Notification angezeigt.

---

## Vaadin-Klasse DeviceFormView

Diese View ist für Hinzufügen und Bearbeiten zuständig.

### Attribut
```java
private SmartHomeManager manager;
```

### Methoden

#### `createForm()`
Erstellt Eingabefelder wie:
* Name
* Raum
* Gerätetyp
* Verbrauch
* Helligkeit
* Temperatur
* Lautstärke

#### `saveDevice()`
Liest die Eingaben aus dem Formular und erstellt je nach Auswahl das richtige Objekt: `SmartLight`, `SmartThermostat`, `SmartSpeaker`.
Danach:
```java
manager.addDevice(device);
```
Bei Fehlern:
```java
Notification.show(exception.getMessage());
```

#### `clearForm()`
Leert alle Eingabefelder nach dem Speichern.

---

## Testklasse SmartHomeManagerTest

Diese Klasse testet die Verwaltungsklasse.

### Attribut
```java
private SmartHomeManager manager;
```

### Methoden

#### `setup()`
Wird vor jedem Test ausgeführt.
Erstellt einen frischen Manager und Beispielgeräte.

#### `testAddDevice()`
Prüft, ob ein Gerät korrekt hinzugefügt wird.

#### `testRemoveDevice()`
Prüft, ob ein Gerät gelöscht wird.

#### `testFilterByRoom()`
Prüft, ob Geräte korrekt nach Raum gefiltert werden.

#### `testSortByPowerUsage()`
Prüft, ob Geräte korrekt nach Verbrauch sortiert werden.

#### `testTotalPowerUsage()`
Prüft, ob der Gesamtverbrauch richtig berechnet wird.
