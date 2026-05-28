# Aufgabenverteilung IoT Dashboard

## Aktueller Stand

Das Projekt hat bereits eine gute Grundlage:

- UML-Datei und Beschreibung sind vorhanden.
- Die Branches `benni`, `Robi` und `Sibi` existieren bereits.
- Die Grundklassen fuer Smart-Home-Geraete existieren unter `src/main/java/com/example/smarthome`.
- Der aktuelle Maven-Testlauf funktioniert.

Das Strukturproblem wurde bereinigt: Die Smart-Home-Views und der FileHandler liegen jetzt im `src/main/java`-Pfad und werden von Maven kompiliert. Die alten Root-Duplikate und leeren Platzhalterdateien wurden entfernt.

## Gemeinsame Regel fuer alle

Alle arbeiten auf ihrem eigenen Branch:

- Benni arbeitet auf `benni`
- Robi arbeitet auf `Robi`
- Sibi arbeitet auf `Sibi`

Vor dem Arbeiten sollte jeder seinen Branch vom aktuellen `main` aktualisieren. Niemand soll direkt auf `main` entwickeln. Am Ende werden die Branches nacheinander in `main` gemerged.

## Start-Anleitung fuer alle

Wenn jemand das Projekt neu herunterlaedt oder frisch anfangen will:

```bash
git clone <repository-url>
cd "IoT Dashboard Projekt"
```

Wenn das Projekt schon vorhanden ist:

```bash
git checkout main
git pull origin main
```

Danach auf den eigenen Branch wechseln:

```bash
git checkout <eigener-branch>
git merge main
```

Vor dem Coden pruefen, ob das Projekt noch laeuft:

```bash
./mvnw test
```

Unter Windows kann stattdessen verwendet werden:

```bash
.\mvnw.cmd test
```

Wenn Tests rot sind, zuerst klaeren, warum. Nicht einfach weiterarbeiten und spaeter alles zusammenwerfen.

## Allgemeiner Arbeitsablauf

1. Auf den eigenen Branch wechseln.
2. Aktuellen `main` in den eigenen Branch holen.
3. Nur an den eigenen Aufgaben arbeiten.
4. Kleine sinnvolle Commits machen.
5. Vor dem Pushen Tests ausfuehren.
6. Branch pushen.
7. Danach Merge in `main` erst machen, wenn die eigene Arbeit stabil ist.

Wichtige Befehle:

```bash
git status
git add .
git commit -m "kurze beschreibung der aenderung"
git push origin <eigener-branch>
```

Wenn Git beim Merge einen Konflikt meldet:

1. Datei mit Konflikt oeffnen.
2. Stellen mit `<<<<<<<`, `=======`, `>>>>>>>` suchen.
3. Entscheiden, welche Version richtig ist.
4. Konflikt-Markierungen entfernen.
5. Danach:

```bash
git add .
git commit
```

Bei Unsicherheit nicht blind loeschen, sondern kurz im Team fragen.

## Benni: Geraeteklassen und Validierung

Branch: `benni`

Benni kuemmert sich um die Fachlogik der einzelnen Geraete.

### Dateien

- `src/main/java/com/example/smarthome/SmartDevice.java`
- `src/main/java/com/example/smarthome/SmartLight.java`
- `src/main/java/com/example/smarthome/SmartThermostat.java`
- `src/main/java/com/example/smarthome/SmartSpeaker.java`
- `src/main/java/com/example/smarthome/SmartHomeException.java`

### Aufgaben

- Konstruktoren nach UML fertigstellen.
- Validierungen einbauen:
  - ID darf nicht negativ sein.
  - Name darf nicht leer sein.
  - Raum darf nicht leer sein.
  - Stromverbrauch darf nicht negativ sein.
  - Helligkeit muss zwischen 0 und 100 liegen.
  - Lautstaerke muss zwischen 0 und 100 liegen.
  - Temperaturen sollen in einem sinnvollen Bereich liegen.
- Fehlende Setter ergaenzen:
  - `setName`
  - `setRoom`
  - `setPowerUsage`
  - `setBrightness`
  - `setColor`
  - `setCurrentTemperature`
  - `setTargetTemperature`
  - `setVolume`
  - `setCurrentSong`
- Fehlende Methoden umsetzen:
  - `calculateDailyConsumption`
  - `dim`
  - `changeColor`
  - `increaseTargetTemperature`
  - `decreaseTargetTemperature`
  - `isHeatingNeeded`
  - `increaseVolume`
  - `decreaseVolume`
  - `playSong`
- `performAction()` bei allen Geraetetypen passend zur Beschreibung ausformulieren.
- JUnit-Tests fuer die Geraeteklassen schreiben.

### Ergebnis

Nach Bennis Arbeit sollen alle Geraete stabil erstellt, geaendert und getestet werden koennen.

### Git-Anleitung fuer Benni

Vor dem Arbeiten:

```bash
git checkout main
git pull origin main
git checkout benni
git merge main
```

Nach dem Arbeiten:

```bash
.\mvnw.cmd test
git status
git add src/main/java/com/example/smarthome/SmartDevice.java
git add src/main/java/com/example/smarthome/SmartLight.java
git add src/main/java/com/example/smarthome/SmartThermostat.java
git add src/main/java/com/example/smarthome/SmartSpeaker.java
git add src/main/java/com/example/smarthome/SmartHomeException.java
git add src/test/java/com/example/smarthome/
git commit -m "Geraeteklassen und Validierung fertigstellen"
git push origin benni
```

Benni sollte moeglichst nicht an UI-Dateien oder am `SmartHomeManager` arbeiten, damit Robi und Sibi keine unnoetigen Merge-Konflikte bekommen.

## Robi: SmartHomeManager

Branch: `Robi`

Robi kuemmert sich um die zentrale Verwaltung aller Geraete.

### Dateien

- `src/main/java/com/example/smarthome/SmartHomeManager.java`
- neue Testdatei, z. B. `src/test/java/com/example/smarthome/SmartHomeManagerTest.java`

### Aufgaben

- `addDevice` erweitern:
  - Geraet darf nicht `null` sein.
  - ID darf nicht doppelt vorkommen.
- CRUD-Methoden fertigstellen:
  - `removeDevice(int id)`
  - `updateDevice(SmartDevice device)`
  - `findDeviceById(int id)`
  - `getAllDevices()`
- Such- und Filtermethoden umsetzen:
  - `searchByName(String name)`
  - `filterByRoom(String room)`
  - `filterByType(String type)`
  - `filterByStatus(boolean isOn)`
  - `filterFavorites()`
- Sortiermethoden umsetzen:
  - `sortByName()`
  - `sortByPowerUsage()`
- Gruppierungsmethoden umsetzen:
  - `groupByRoom()`
  - `groupByType()`
- Auswertungen umsetzen:
  - `getTotalPowerUsage()`
  - `getAveragePowerUsage()`
  - `getHighestPowerUsageDevice()`
  - `getActiveDeviceCount()`
- Kombinierte Methode umsetzen:
  - `filterByRoomAndSortByPowerUsage(String room)`
- Favoritenfunktionen umsetzen:
  - `markAsFavorite(int id)`
  - `unmarkAsFavorite(int id)`
- Logfunktion umsetzen:
  - `addLog(String message)`
  - `getLogs()`
- JUnit-Tests fuer den Manager schreiben.

### Ergebnis

Nach Robis Arbeit soll die komplette Verwaltung der Geraete funktionieren. Die UI und der FileHandler sollen den Manager verwenden koennen, ohne eigene Fachlogik zu duplizieren.

### Git-Anleitung fuer Robi

Vor dem Arbeiten:

```bash
git checkout main
git pull origin main
git checkout Robi
git merge main
```

Nach dem Arbeiten:

```bash
.\mvnw.cmd test
git status
git add src/main/java/com/example/smarthome/SmartHomeManager.java
git add src/test/java/com/example/smarthome/SmartHomeManagerTest.java
git commit -m "SmartHomeManager fertigstellen"
git push origin Robi
```

Robi soll Manager-Methoden so bauen, dass Sibi sie direkt in der UI verwenden kann. Wichtig ist, Methodennamen und Rueckgabetypen aus der UML moeglichst nicht umzubenennen.

## Sibi: UI und Datei-Speicherung

Branch: `Sibi`

Sibi kuemmert sich um die Vaadin-Oberflaeche und das Speichern/Laden der Geraete.

### Dateien

- `src/main/java/com/example/smarthome/ui/DashboardView.java`
- `src/main/java/com/example/smarthome/ui/DeviceListView.java`
- `src/main/java/com/example/smarthome/ui/DeviceFormView.java`
- `src/main/java/com/example/smarthome/SmartHomeFileHandler.java`
- eventuell `src/main/java/com/example/examplefeature/...`, falls die Beispiel-App entfernt oder ersetzt wird

### Aufgaben UI

- Die bestehenden Smart-Home-Views erweitern.
- Dashboard anzeigen:
  - Gesamtverbrauch
  - Durchschnittsverbrauch
  - aktive Geraete
  - Favoritenanzahl
- Geraeteliste anzeigen:
  - ID
  - Name
  - Raum
  - Typ
  - Status
  - Stromverbrauch
  - Favorit
- Aktionen in der Liste:
  - Geraet loeschen
  - Favorit setzen/entfernen
  - Geraet ein-/ausschalten, falls sinnvoll
- Filter in der Liste:
  - Raum
  - Typ
  - Status
  - Favoriten
- Formular erweitern:
  - Auswahl fuer `Light`, `Thermostat`, `Speaker`
  - passende Felder je nach Typ anzeigen
  - Eingaben pruefen und Fehler als Notification anzeigen

### Aufgaben Datei-Speicherung

- Den vorhandenen `SmartHomeFileHandler` erweitern und testen.
- CSV-Speichern fuer alle Geraetetypen umsetzen.
- CSV-Laden fuer alle Geraetetypen umsetzen.
- Favoriten und Status mitspeichern.
- Fehler beim Laden/Speichern mit `SmartHomeException` behandeln.
- Tests fuer Speichern und Laden schreiben.

### Ergebnis

Nach Sibis Arbeit soll die Anwendung nicht mehr wie das Vaadin-Beispiel wirken, sondern als echtes IoT-Dashboard bedienbar sein. Geraete sollen angelegt, angezeigt, gefiltert und gespeichert werden koennen.

### Git-Anleitung fuer Sibi

Vor dem Arbeiten:

```bash
git checkout main
git pull origin main
git checkout Sibi
git merge main
```

Nach dem Arbeiten:

```bash
.\mvnw.cmd test
git status
git add src/main/java/com/example/smarthome/ui/
git add src/main/java/com/example/smarthome/SmartHomeFileHandler.java
git add src/test/java/com/example/smarthome/
git commit -m "UI und Datei-Speicherung erweitern"
git push origin Sibi
```

Sibi soll Fachlogik nicht direkt in die UI schreiben. Die UI soll nach Moeglichkeit nur Eingaben lesen, Manager-Methoden aufrufen und Ergebnisse anzeigen.

## Empfohlene Reihenfolge

1. Benni merged die fertigen Geraeteklassen.
2. Robi merged den fertigen `SmartHomeManager`.
3. Sibi merged UI und Datei-Speicherung.
4. Danach gemeinsam testen:
   - `mvnw test`
   - Anwendung starten
   - Dashboard im Browser pruefen

## Merge-Anleitung in main

Wenn eine Person fertig ist und ihr Branch getestet wurde, wird in `main` gemerged.

Empfohlener Ablauf:

```bash
git checkout main
git pull origin main
git merge <branch-name>
.\mvnw.cmd test
git push origin main
```

Reihenfolge:

1. `benni` in `main` mergen.
2. `Robi` mit neuem `main` aktualisieren, testen, dann in `main` mergen.
3. `Sibi` mit neuem `main` aktualisieren, testen, dann in `main` mergen.

Robi und Sibi sollten vor ihrem finalen Merge nochmal den neuesten `main` holen:

```bash
git checkout Robi
git merge main
.\mvnw.cmd test
```

Fuer Sibi entsprechend:

```bash
git checkout Sibi
git merge main
.\mvnw.cmd test
```

Erst wenn die Tests funktionieren, sollte der Branch nach `main` gemerged werden.

## Anwendung starten

Zum Starten der Anwendung:

```bash
.\mvnw.cmd spring-boot:run
```

Danach im Browser oeffnen:

```text
http://localhost:8080/
```

Wichtige Seiten:

- Dashboard: `http://localhost:8080/`
- Geraeteliste: `http://localhost:8080/devices`
- Neues Geraet: `http://localhost:8080/add-device`
- Alte Beispiel-Taskliste: `http://localhost:8080/tasks`

## Wichtige Hinweise

- Methodennamen sollen zur UML passen.
- Fachlogik gehoert in die Geraeteklassen und in den `SmartHomeManager`, nicht direkt in die UI.
- Die UI soll Manager-Methoden verwenden.
- Tests sollen nach Moeglichkeit direkt die Smart-Home-Klassen testen, nicht nur das alte Task-Beispiel.
- Vor jedem Merge sollte `mvnw test` erfolgreich laufen.
