# 🎓 Der ultimative Spickzettel für das Fachgespräch

Dieses Dokument ist eure "Lebensversicherung" für die Prüfung. Es erklärt exakt **wo** alles liegt, **warum** ihr es genau so programmiert habt und beantwortet die fiesesten Lehrer-Fragen im Voraus. Lasst diese Datei oder die Zettel am besten während des Gesprächs offen!

---

## 📂 1. Wo ist was? (Die Struktur)

Euer Projekt ist sehr professionell in verschiedene Pakete unterteilt (`src/main/java/com/example/smarthome/`). Das nennt man **Schichtenarchitektur**.

### 🧱 Das Modell (Die Geräte)
**Wo:** Direkt im Ordner `smarthome`
*   `SmartDevice.java`: Der Bauplan für alles.
*   `SmartLight.java`, `SmartThermostat.java`, `SmartSpeaker.java`: Die konkreten Geräte.
*   *Warum hier?* Das sind die reinen Datenklassen. Sie wissen absolut nichts von einer Benutzeroberfläche oder einer CSV-Datei.

### 🧠 Die Geschäftslogik (Der Service)
**Wo:** `SmartHomeManager.java`
*   *Was passiert hier?* Hier ist das "Gehirn" der App. Er hält die Liste der Geräte und bietet Methoden zum Filtern, Sortieren und Zählen.
*   *Warum getrennt?* Wenn sich die Regeln ändern (z.B. "Man darf maximal 10 Geräte haben"), muss das nur hier im Manager programmiert werden, nicht in den Ansichten.

### 💾 Die Persistenz (Datenbank / Speicherung)
**Wo:** `SmartHomeFileHandler.java`
*   *Was passiert hier?* Schreibt die Java-Objekte in Text um und speichert sie als `devices.csv`.
*   *Warum getrennt?* Weil es den Geräten egal ist, ob sie in einer CSV, einer Cloud-Datenbank oder auf einem USB-Stick gespeichert werden. Der FileHandler ist unser "Übersetzer".

### 🖥️ Das Frontend (Die Ansicht / Vaadin)
**Wo:** Im Unterordner `ui` (`DashboardView`, `DeviceListView`, `DeviceFormView`)
*   *Was passiert hier?* Diese Klassen zeichnen die Knöpfe und Tabellen auf den Bildschirm.
*   *Die Goldene Regel:* Die UI-Klassen **dürfen niemals direkt Dinge berechnen**. Wenn in der Liste ein Filter geklickt wird, ruft die UI den `SmartHomeManager` auf.

---

## 🤔 2. WARUM wurde es genau SO gemacht? (Die Design-Entscheidungen)

Hier sind die Antworten auf die Frage: *"Warum habt ihr das so programmiert?"*

> [!TIP]
> **Warum 3 Schichten (Modell - Logik - UI)?**
> *Antwort:* Wir wollten eine lose Kopplung. Wenn wir uns morgen entscheiden, statt einer Website eine Handy-App zu bauen, können wir den `ui`-Ordner einfach wegschmeißen und neu programmieren. Die Logik (`SmartHomeManager`) und die Geräte bleiben exakt gleich! Das spart massiv Arbeit.

> [!TIP]
> **Warum ist `SmartDevice` als `abstract` markiert?**
> *Antwort:* In der Realität gibt es kein generisches "Smartes Gerät", das man im Laden kaufen kann. Es gibt nur Lampen, Boxen etc. Die Klasse `SmartDevice` bündelt nur gemeinsamen Code (wie ID, Name, Raum), damit wir ihn nicht dreimal abtippen müssen (Codeduplizierung vermeiden). Das `abstract` verhindert, dass jemand aus Versehen mit `new SmartDevice()` ein "Nichts" erstellt.

> [!TIP]
> **Warum setzt ihr Variablen auf `private` (Kapselung)?**
> *Antwort:* Wenn wir Helligkeit `public` machen würden, könnte jede andere Klasse im Programm einfach `lampe.brightness = -500;` machen. Das würde das System sprengen. Indem wir Variablen `private` machen und nur über unsere Setter (`setBrightness`) freigeben, können wir jede Eingabe sofort kontrollieren und abblocken!

> [!TIP]
> **Warum benutzt ihr Java Streams (z.B. `.stream().filter()`) im Manager statt `for`-Schleifen?**
> *Antwort:* Streams sind deklarativ. Wir beschreiben *WAS* wir filtern wollen (z.B. "nur Favoriten") und nicht *WIE* der Computer die Schleife durchgehen muss. Das ist weniger fehleranfällig, kürzer, besser lesbar und kann bei tausenden Geräten von Java sogar parallel ausgeführt werden.

> [!TIP]
> **Warum werft ihr bei Fehlern eine eigene `SmartHomeException`?**
> *Antwort:* So trennen wir fachliche Fehler (User gibt leeren Namen ein) von echten Systemfehlern (Datenbank abgestürzt). Die UI merkt: "Oh, das ist eine SmartHomeException", und zeigt dem User eine schöne rote Infobox, statt das Programm zum Absturz zu bringen.

> [!TIP]
> **Warum speichert ihr in einer CSV-Datei mit Semikolons (`;`)?**
> *Antwort:* Das ist ein extrem leichtgewichtiges Standardformat (Comma-Separated Values). Im deutschen Sprachraum nimmt man oft ein Semikolon, da wir das normale Komma `,` für Kommazahlen (wie 22,5 Grad) brauchen. Beim Einlesen schneiden wir die Zeile einfach beim Semikolon ab (`line.split(";")`) und haben ein fertiges Array unserer Daten.

---

## 🎯 3. Die fiesesten Prüfungsfragen (Q&A)

Lehrer prüfen oft, ob ihr die dahinterliegenden Konzepte von Spring Boot und Java verstanden habt.

**Lehrer:** *"Über dem SmartHomeManager steht `@Service` und über dem FileHandler `@Component`. Was macht das?"*
**Ihr:** "Das gehört zum Spring Boot Framework und nennt sich **Dependency Injection**. Das heißt, wir müssen nirgendwo im Code `new SmartHomeManager()` schreiben. Spring Boot erstellt automatisch beim Start *ein einziges* Objekt davon im Hintergrund (als Singleton) und reicht es an alle UI-Klassen weiter, die danach fragen. So arbeiten alle unsere Views auf denselben Daten."

**Lehrer:** *"Erklärt mir mal den Begriff Polymorphismus anhand eures Codes."*
**Ihr:** "Polymorphismus bedeutet Vielgestaltigkeit. Ein perfektes Beispiel ist unsere Geräteliste: Wenn wir eine Liste vom Typ `SmartDevice` durchgehen und bei jedem Gerät `.performAction()` aufrufen, verhält sich jedes Gerät völlig anders! Die Lampe gibt ihre Farbe aus, der Speaker die Musik – obwohl wir für alle Geräte denselben Methodenaufruf verwenden. Java entscheidet zur Laufzeit, welches Gerät gerade vorliegt."

**Lehrer:** *"Robi, was passiert in deinem Manager, wenn ich alle Geräte mit `getAllDevices()` abfrage? Krieg ich die Original-Liste?"*
**Ihr:** "Nein! Würde ich das Original rausgeben, könnte das Frontend einfach `liste.clear()` aufrufen und die internen Daten des Managers manipulieren. Deshalb gebe ich eine neue Kopie zurück: `return new ArrayList<>(devices)`. Das schützt die echten Daten."

**Lehrer:** *"Sibi, wie verhinderst du, dass das Programm crasht, wenn die CSV-Datei beim ersten Start noch gar nicht existiert?"*
**Ihr:** "Im FileHandler prüfe ich ganz oben mit `Files.exists(filePath)`, ob die Datei da ist. Wenn nicht, gebe ich einfach eine leere Liste zurück, statt einen Fehler zu werfen. So kann das Dashboard beim allerersten Start trotzdem problemlos laden."

---

## 🧘‍♂️ Letzte Tipps für das Gespräch
1.  **Lasst euch nicht aus der Ruhe bringen!** Wenn euch eine Frage gestellt wird, zu der euch sofort nichts einfällt: Lest euch den Code durch. Dank meiner Kommentare, die wir überall hinzugefügt haben, steht die Antwort zu 90% direkt über der Code-Zeile!
2.  Wenn ihr die Live-Demo zeigt, sprecht souverän. Zeigt bewusst, dass Fehler abgefangen werden (Tippt mal `-10` bei der Helligkeit ein und sagt: *"Hier greift jetzt unsere Kapselung..."*).
3. Euer Projekt kratzt nicht nur an der Oberfläche, ihr habt *Clean Code*, *MVC (Model-View-Controller, also die Schichten)* und *Persistenz* implementiert. Darauf könnt ihr stolz sein!
