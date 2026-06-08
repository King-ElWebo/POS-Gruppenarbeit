# Technische Dokumentation - SmartHomeManager (Robis Teil)

Diese Dokumentation beschreibt die Implementierung der zentralen Verwaltungsklasse `SmartHomeManager`, ihrer Log- und Filterfunktionen sowie der dazugehörigen Unit-Tests im IoT-Dashboard-Projekt.

---

## 🏗️ Funktionsweise und Architektur

Der `SmartHomeManager` fungiert als zentraler Service im Projekt. Er kapselt die Liste aller IoT-Geräte (`SmartDevice`) und stellt der Benutzeroberfläche (Vaadin-Views) sowie dem Datei-Handler alle notwendigen Methoden zur Datenmanipulation, Filterung, Sortierung und Auswertung zur Verfügung.

1. **Spring `@Service` Annotation:** Die Klasse ist als Spring-Service deklariert, um eine einfache Dependency Injection in die Vaadin-Views (`DashboardView`, `DeviceListView`, `DeviceFormView`) zu ermöglichen.
2. **Kapselung der Daten:** Die interne Liste `devices` ist privat. Über `getAllDevices()` wird eine Kopie der Liste zurückgegeben, um zu verhindern, dass die GUI die interne Datenstruktur unbeabsichtigt manipuliert.

---

## 🛡️ CRUD-Operationen & Validierung

Die Verwaltung der Geräte über den Manager validiert alle Aktionen, um Datenkonsistenz zu gewährleisten. Schlagen Validierungen fehl, wird eine `SmartHomeException` geworfen.

| Methode | Zweck / Validierungsregel | Exception-Nachricht bei Fehler |
| :--- | :--- | :--- |
| `addDevice(SmartDevice)` | Fügt ein Gerät hinzu. Das Gerät darf nicht `null` sein und die ID darf nicht doppelt vorkommen. | `"Device darf nicht null sein"`<br>`"Gerät mit dieser ID existiert bereits."` |
| `removeDevice(int id)` | Entfernt ein Gerät anhand der ID. Wenn kein Gerät mit der ID existiert, wird ein Fehler geworfen. | `"Gerät mit ID [id] nicht gefunden."` |
| `removeDevice(SmartDevice)` | Entfernt ein Gerät per Objekt-Referenz (falls nicht null). | - |
| `updateDevice(SmartDevice)` | Ersetzt ein bestehendes Gerät mit der gleichen ID durch ein aktualisiertes Objekt. | `"Device darf nicht null sein"`<br>`"Gerät mit ID [id] nicht gefunden."` |
| `findDeviceById(int id)` | Sucht ein Gerät per ID und gibt es zurück. | `"Gerät mit ID [id] nicht gefunden."` |

---

## 🔍 Such-, Filter- & Sortierlogik

Zur gezielten Abfrage der IoT-Geräte in der Benutzeroberfläche wurden performante Filter- und Sortiermethoden implementiert:

### 1. Such- und Filtermethoden
* **`searchByName(String name)`**:
  Sucht Geräte, deren Name den Suchbegriff enthält (case-insensitive). Wenn der Suchbegriff leer oder null ist, werden alle Geräte zurückgegeben.
* **`filterByRoom(String room)`**:
  Gibt alle Geräte eines bestimmten Raums zurück (exakter, case-insensitiver Abgleich).
* **`filterByType(String type)`**:
  Filtert die Geräte nach ihrem Typ (`Light`, `Thermostat`, `Speaker`, case-insensitiver Abgleich).
* **`filterByStatus(boolean isOn)`**:
  Gibt alle eingeschalteten bzw. ausgeschalteten Geräte zurück.
* **`filterFavorites()`**:
  Gibt eine Liste aller als Favoriten markierten Geräte zurück.

### 2. Sortiermethoden (in-place)
* **`sortByName()`**:
  Sortiert die interne Geräteliste alphabetisch nach Name (case-insensitive).
* **`sortByPowerUsage()`**:
  Sortiert die interne Geräteliste aufsteigend nach dem aktuellen Stromverbrauch (`powerUsage`).

### 3. Kombinierte Methoden
* **`filterByRoomAndSortByPowerUsage(String room)`**:
  Kombiniert zwei Schritte: filtert die Geräte nach dem angegebenen Raum und sortiert das Ergebnis anschließend aufsteigend nach dem Stromverbrauch.

---

## 📊 Gruppierung & Auswertungen

Der Manager berechnet statistische Werte für das Dashboard und gruppiert Geräte für strukturierte Ansichten:

* **`groupByRoom()`**: Gruppiert alle Geräte nach Raum und gibt eine `Map<String, List<SmartDevice>>` zurück.
* **`groupByType()`**: Gruppiert alle Geräte nach ihrem Gerätetyp und gibt eine `Map<String, List<SmartDevice>>` zurück.
* **`getTotalPowerUsage()`**: Summiert den aktuellen Stromverbrauch aller registrierten Geräte.
* **`getAveragePowerUsage()`**: Berechnet den durchschnittlichen Stromverbrauch aller Geräte. Gibt `0.0` zurück, falls keine Geräte vorhanden sind.
* **`getHighestPowerUsageDevice()`**: Identifiziert und gibt das Gerät mit dem höchsten Stromverbrauch zurück (oder `null`, falls die Liste leer ist).
* **`getActiveDeviceCount()`**: Zählt alle Geräte, die aktuell eingeschaltet sind (`isTurnedOn() == true`).

---

## 📝 Favoriten- & Logsystem

* **`markAsFavorite(int id)` / `unmarkAsFavorite(int id)`**:
  Sucht das Gerät per ID und setzt den Favoritenstatus (`favorite = true` bzw. `false`). Jede Änderung wird automatisch im Log aufgezeichnet.
* **`addLog(String message)` / `getLogs()`**:
  Speichert Aktionen (wie Hinzufügen, Löschen, Aktualisieren oder Favorisieren von Geräten) in einer internen `List<String>`. Die Methode `getLogs()` stellt eine Kopie dieser Einträge für die UI bereit.

---

## 🧪 Qualitätssicherung (JUnit-Tests)

In der Testklasse `SmartHomeManagerTest` wurden **23 eigenständige Testfälle** implementiert, die alle funktionalen Aspekte des Managers abdecken:

1. **`testAddDevice()`**: Verifiziert das erfolgreiche Hinzufügen eines Geräts und das automatische Schreiben des Logs.
2. **`testAddNullDeviceThrowsException()`**: Stellt sicher, dass das Hinzufügen von `null` mit einer `SmartHomeException` abgelehnt wird.
3. **`testAddDuplicateIdThrowsException()`**: Prüft den Schutz vor ID-Duplikaten.
4. **`testRemoveDeviceById()`**: Testet das korrekte Löschen eines Geräts über seine ID und die Protokollierung.
5. **`testRemoveNonExistingDeviceByIdThrowsException()`**: Sichert ab, dass das Löschen einer nicht vorhandenen ID eine Exception wirft.
6. **`testRemoveDeviceByObject()`**: Überprüft das Löschen über die Objekt-Referenz.
7. **`testUpdateDevice()`**: Testet das Ändern aller editierbaren Werte eines Geräts und das Protokollieren des Updates.
8. **`testUpdateNonExistingDeviceThrowsException()`**: Sichert ab, dass Updates für nicht vorhandene Geräte abgelehnt werden.
9. **`testUpdateNullDeviceThrowsException()`**: Verhindert das Updaten mit `null`.
10. **`testFindDeviceById()`**: Prüft das erfolgreiche Auffinden eines Geräts.
11. **`testFindDeviceByIdNonExistingThrowsException()`**: Prüft das Fehlerverhalten bei der Suche nach ungültigen IDs.
12. **`testSearchByName()`**: Testet die case-insensitive Namenssuche inklusive leerer Suchanfragen.
13. **`testFilterByRoom()`**: Verifiziert das Filtern nach Räumen (exakt und case-insensitiv).
14. **`testFilterByType()`**: Testet das Filtern nach Gerätetypen.
15. **`testFilterByStatus()`**: Prüft die korrekte Unterscheidung zwischen aktiven und inaktiven Geräten.
16. **`testFilterFavorites()`**: Validiert das Filtern nach Favoriten.
17. **`testSortByName()`**: Testet die alphabetische Sortierung der Geräte.
18. **`testSortByPowerUsage()`**: Überprüft die aufsteigende Sortierung nach Stromverbrauch.
19. **`testGroupByRoom()`**: Verifiziert die korrekte Einteilung der Geräte in Räume per Map.
20. **`testGroupByType()`**: Verifiziert die Einteilung in Gerätetypen per Map.
21. **`testPowerUsageStats()`**: Testet die Berechnung von Gesamtverbrauch, Durchschnittsverbrauch, das Finden des Geräts mit dem höchsten Verbrauch und die Anzahl aktiver Geräte (auch bei leerer Geräteliste).
22. **`testFilterByRoomAndSortByPowerUsage()`**: Validiert das korrekte Zusammenspiel von Raumfilterung und anschließender Stromverbrauch-Sortierung.
23. **`testFavoritesManagement()`**: Testet das Setzen und Entfernen der Favoritenmarkierung über den Manager.
