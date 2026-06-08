# Dokumentation – Sibis Teil (UI und Datei-Speicherung)

Diese Datei beschreibt, was im Bereich UI (Vaadin-Oberflaeche) und Datei-Speicherung (CSV) gemacht wurde.

---

## 1. Ueberblick

Es gibt drei Seiten (Views) und eine Klasse zum Speichern/Laden:

| Datei | Zweck |
| :--- | :--- |
| `ui/DashboardView.java` | Startseite mit Kennzahlen + Speichern/Laden-Buttons |
| `ui/DeviceListView.java` | Geraeteliste mit Filtern und Aktionen |
| `ui/DeviceFormView.java` | Formular zum Anlegen neuer Geraete |
| `SmartHomeFileHandler.java` | Speichert/laedt Geraete als CSV-Datei |

Wichtig: Die UI ruft nur Methoden vom `SmartHomeManager` auf. Fachlogik (Validierung, Berechnung, etc.) bleibt in den Geraete-Klassen und im Manager.

---

## 2. DashboardView (`/`)

Zeigt vier Kennzahlen ueber den Manager an:

- Gesamtverbrauch (`getTotalPowerUsage`)
- Durchschnittsverbrauch (`getAveragePowerUsage`)
- Aktive Geraete (`getActiveDeviceCount`)
- Anzahl Favoriten (`filterFavorites().size()`)

Neu hinzugefuegt: zwei Buttons unten auf der Seite.

- **„Geraete speichern (CSV)"**: ruft `fileHandler.saveDevices(...)` auf und speichert alle aktuellen Geraete in `devices.csv`.
- **„Geraete laden (CSV)"**: ruft `fileHandler.loadDevices()` auf. Geraete, deren ID schon existiert, werden uebersprungen (sonst wuerde `addDevice` einen Fehler werfen). Danach werden die Kennzahlen neu berechnet.

Fehler beim Speichern/Laden (z. B. `SmartHomeException`) werden als Notification angezeigt, die Anwendung stuerzt nicht ab.

---

## 3. DeviceListView (`/devices`)

Zeigt alle Geraete in einer Tabelle (Grid) mit den Spalten ID, Name, Raum, Typ, Status, Watt, Favorit.

### Filterleiste (neu)

Oberhalb der Tabelle gibt es vier Filter, die kombiniert werden koennen:

- **Raum**: Textfeld, filtert per Teiltext (z. B. „kueche" findet „Kueche").
- **Typ**: Auswahl zwischen „Alle", „Light", „Thermostat", „Speaker".
- **Status**: Auswahl zwischen „Alle", „Ein", „Aus".
- **Nur Favoriten**: Checkbox.

Ein „Filter zuruecksetzen"-Button setzt alle Filter wieder zurueck. Die Filterung passiert direkt in `refreshGrid()`: zuerst werden alle Geraete vom Manager geholt (`getAllDevices()`), danach wird die Liste schrittweise nach den gesetzten Filtern eingeschraenkt.

### Aktionen pro Zeile (neu: Ein/Aus)

- **Einschalten / Ausschalten**: ruft `device.turnOn()` bzw. `device.turnOff()` auf.
- **Favorit setzen / entfernen**: ruft `manager.markAsFavorite(id)` bzw. `manager.unmarkAsFavorite(id)` auf (nutzt den Manager statt direkt das Geraet zu aendern, damit auch das Log mitgeschrieben wird).
- **Loeschen**: ruft `manager.removeDevice(id)` auf und zeigt eine Bestaetigung als Notification.

Nach jeder Aktion wird `refreshGrid()` aufgerufen, damit die Tabelle aktuell bleibt.

---

## 4. DeviceFormView (`/add-device`)

Formular zum Anlegen eines neuen Geraets. Es gibt eine Typ-Auswahl (Light, Thermostat, Speaker) und gemeinsame Felder (ID, Name, Raum, Stromverbrauch).

### Dynamische Felder (neu)

Je nach gewaehltem Typ werden nur die passenden Zusatzfelder angezeigt (`showFieldsForType()`):

- **Light**: Helligkeit, Farbe
- **Thermostat**: Aktuelle Temperatur, Zieltemperatur
- **Speaker**: Lautstaerke, Song

Das macht das Formular uebersichtlicher, weil man nicht alle zehn Felder gleichzeitig sieht.

### Speichern und Validierung

Beim Klick auf „Speichern" wird je nach Typ das passende Geraete-Objekt gebaut (`createDeviceFromFields`) und an `manager.addDevice(...)` uebergeben. Die eigentliche Pruefung (z. B. „Helligkeit zwischen 0 und 100") passiert in den Geraete-Klassen selbst (Konstruktoren werfen `SmartHomeException`).

Falls etwas schiefgeht (leeres Pflichtfeld oder ungueltiger Wert), wird der Fehler als Notification angezeigt und das Formular bleibt erhalten – die Anwendung stuerzt nicht ab.

---

## 5. SmartHomeFileHandler (CSV-Speicherung)

Die Klasse war bereits vorhanden und funktionsfaehig. Ergaenzt wurden Kommentare zur Erklaerung und Tests.

### Format

Jede Zeile in `devices.csv` sieht so aus:

```
Typ;ID;Name;Raum;Status(true/false);Stromverbrauch;Favorit(true/false);<typ-spezifische Felder>
```

Beispiele fuer die typ-spezifischen Felder:

- **Light**: `Helligkeit;Farbe`
- **Thermostat**: `AktuelleTemperatur;Zieltemperatur`
- **Speaker**: `Lautstaerke;Song`

### Ablauf

- `saveDevices(List<SmartDevice>)`: schreibt jede Zeile mit `deviceToCsv(...)`.
- `loadDevices()`: liest Zeile fuer Zeile, baut mit `csvToDevice(...)` das passende Objekt (per `switch` auf den Typ-String), setzt danach Status (`turnOn()`) und Favorit (`setFavorite(...)`).
- Wenn die Datei nicht existiert, gibt `loadDevices()` einfach eine leere Liste zurueck (kein Fehler).
- Bei IO-Fehlern wird eine `SmartHomeException` mit verstaendlicher Meldung geworfen.

---

## 6. Tests

Neue Datei: `src/test/java/com/example/smarthome/SmartHomeFileHandlerTest.java`

Verwendet eine **temporaere Testdatei** (`Files.createTempFile`), damit echte `devices.csv`-Daten nicht ueberschrieben werden. Nach jedem Test wird die Datei wieder geloescht.

Getestete Faelle:

1. `loadDevicesGibtLeereListeZurueckWennDateiFehlt` – wenn die Datei nicht existiert, kommt eine leere Liste zurueck.
2. `speichernUndLadenGebenGleicheGeraeteZurueck` – legt je ein Light, Thermostat und Speaker an (inkl. Status „Ein" und Favorit), speichert sie und laedt sie wieder. Geprueft wird, dass Typ, Name, Status, Favorit und alle typ-spezifischen Werte (Helligkeit, Farbe, Temperaturen, Lautstaerke, Song) nach dem Laden wieder stimmen.

Alle Tests (inkl. der neuen) laufen erfolgreich durch (`.\mvnw.cmd test` → `Tests run: 42, Failures: 0, Errors: 0`).

---

## 7. Seite `/tasks` – was ist das?

Wer die Anwendung startet und auf `http://localhost:8080/tasks` geht, sieht eine Taskliste auf Englisch. Das ist **keine** eigene Funktion vom IoT-Dashboard, sondern die **Vaadin/Spring-Boot Beispiel-App**, die mit dem Projekt-Template mitgeliefert wurde (Paket `com.example.examplefeature`: `Task.java`, `TaskService.java`, `TaskListView.java`, ...).

- Deshalb auf Englisch: ist Standard-Demo-Code von Vaadin, nicht von uns geschrieben.
- Zweck: zeigt nur, wie eine einfache Vaadin-Seite mit Datenbank-Anbindung aussieht (Beispiel zum Lernen).
- Hat keinen Bezug zu Geraeten/Smart-Home.
- In der Aufgabenverteilung steht, dass Sibi diese Beispiel-App bei Bedarf entfernen oder ersetzen kann, sobald das echte Dashboard fertig ist (`eventuell src/main/java/com/example/examplefeature/...`).

**Empfehlung**: vorerst stehen lassen (stoert nichts), spaeter im Team entscheiden ob geloescht wird.

---

## 8. Was noch offen sein koennte

- Die UI zeigt aktuell keine Logs (`manager.getLogs()`) an – koennte als zusaetzliche Seite ergaenzt werden.
- Beim Laden aus CSV werden doppelte IDs stillschweigend uebersprungen – je nach Wunsch koennte man hier auch eine Warnung anzeigen, welche Geraete uebersprungen wurden.
- Die Beispiel-App unter `/tasks` (siehe Abschnitt 7) koennte spaeter entfernt werden.
