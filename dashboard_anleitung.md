# Anleitung zur Navigation im IoT Dashboard

Diese Anleitung beschreibt, wie man das Vaadin-Dashboard startet, welche Seiten es gibt und welche Funktionen auf den einzelnen Seiten benutzt werden koennen.

## Anwendung starten

Im Projektordner folgenden Befehl ausfuehren:

```powershell
.\mvnw.cmd spring-boot:run
```

Danach die Anwendung im Browser oeffnen:

```text
http://localhost:8080/
```

Der Server kann im Terminal mit `Ctrl + C` wieder gestoppt werden.

## Navigation

Die Anwendung hat links ein Navigationsmenue. Darueber kann zwischen den wichtigsten Seiten gewechselt werden:

- `Dashboard`
- `Geraeteliste`
- `Geraet hinzufuegen`
- `Tasks`

Die Seite `Tasks` ist noch die alte Vaadin-Beispielseite und gehoert nicht direkt zum IoT-Dashboard.

## Dashboard

Adresse:

```text
http://localhost:8080/
```

Das Dashboard ist die Startseite der Anwendung. Dort sieht man eine kurze Uebersicht ueber alle gespeicherten Smart-Home-Geraete.

Angezeigt werden:

- Gesamtverbrauch aller Geraete in Watt
- Durchschnittlicher Verbrauch pro Geraet
- Anzahl der eingeschalteten Geraete
- Anzahl der als Favorit markierten Geraete

Unten auf der Seite gibt es zwei CSV-Funktionen:

- `Geraete speichern (CSV)`: Speichert alle aktuellen Geraete in die Datei `devices.csv`.
- `Geraete laden (CSV)`: Laedt Geraete aus `devices.csv` wieder in das Dashboard.

Wenn beim Speichern oder Laden ein Fehler passiert, wird eine Benachrichtigung angezeigt.

## Geraeteliste

Adresse:

```text
http://localhost:8080/devices
```

Die Geraeteliste zeigt alle vorhandenen Smart-Home-Geraete in einer Tabelle.

In der Tabelle sieht man:

- ID
- Name
- Raum
- Typ
- Status
- Stromverbrauch in Watt
- Favoritenstatus

### Filter

Oberhalb der Tabelle gibt es mehrere Filter:

- `Raum`: Filtert nach Raum. Es reicht ein Teil des Raumnamens, zum Beispiel `Kueche`.
- `Typ`: Filtert nach `Light`, `Thermostat` oder `Speaker`.
- `Status`: Filtert nach `Ein` oder `Aus`.
- `Nur Favoriten`: Zeigt nur Geraete, die als Favorit markiert sind.

Mit `Filter zuruecksetzen` werden alle Filter wieder entfernt.

### Aktionen in der Tabelle

Bei jedem Geraet gibt es Aktionsbuttons:

- `Einschalten` oder `Ausschalten`: Aendert den Status des Geraets.
- `Favorit setzen` oder `Favorit entfernen`: Markiert ein Geraet als Favorit oder entfernt die Markierung.
- `Loeschen`: Entfernt das Geraet aus der Liste.

Nach jeder Aktion wird die Tabelle automatisch aktualisiert.

## Geraet hinzufuegen

Adresse:

```text
http://localhost:8080/add-device
```

Auf dieser Seite koennen neue Smart-Home-Geraete angelegt werden.

Zuerst wird der Typ ausgewaehlt:

- `Light`
- `Thermostat`
- `Speaker`

Danach werden die gemeinsamen Felder ausgefuellt:

- ID
- Name
- Raum
- Stromverbrauch in Watt

Je nach Typ erscheinen zusaetzliche Felder.

### Light

Fuer Lampen gibt es:

- Helligkeit in Prozent
- Farbe

Die Helligkeit muss zwischen `0` und `100` liegen.

### Thermostat

Fuer Thermostate gibt es:

- Aktuelle Temperatur
- Zieltemperatur

Die Temperaturen muessen in einem sinnvollen Bereich liegen.

### Speaker

Fuer Lautsprecher gibt es:

- Lautstaerke in Prozent
- Aktueller Song

Die Lautstaerke muss zwischen `0` und `100` liegen.

### Speichern

Mit `Speichern` wird das neue Geraet erstellt und dem Dashboard hinzugefuegt.

Wenn Eingaben fehlen oder ungueltig sind, wird eine Fehlermeldung angezeigt. Das Formular bleibt dann offen, damit die Eingaben korrigiert werden koennen.

## Tasks

Adresse:

```text
http://localhost:8080/tasks
```

Diese Seite ist noch aus dem Vaadin-Beispielprojekt vorhanden. Sie zeigt eine einfache Aufgabenliste und hat keinen direkten Bezug zum Smart-Home-Dashboard.

Die Seite kann fuer das eigentliche IoT-Projekt ignoriert werden.

## Typischer Ablauf

1. Anwendung mit `.\mvnw.cmd spring-boot:run` starten.
2. Im Browser `http://localhost:8080/` oeffnen.
3. Auf `Geraet hinzufuegen` ein neues Geraet anlegen.
4. In der `Geraeteliste` pruefen, ob das Geraet angezeigt wird.
5. Geraete einschalten, ausschalten, favorisieren oder loeschen.
6. Zurueck zum `Dashboard` wechseln und die aktualisierten Kennzahlen ansehen.
7. Bei Bedarf die Geraete als CSV speichern.

