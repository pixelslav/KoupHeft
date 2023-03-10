## Über dieses Projekt

Dies ist ein einfaches Demo-Projekt. Die Idee der Seite stammt von MyStark. MyStark ist eine Website für Käufer von Stark-Produkten, um Ergänzungen für ihre Bücher zu finden.

In diesem Projekt habe ich versucht, die folgenden Funktionen zu implementieren
1. Die Möglichkeit, ein Konto zu registrieren und Benutzer dann bei ihren Konten anzumelden
2. ein einfaches, aber verständliches Design
3. Testen
4. das Hinzufügen von Produkten mithilfe von Code

### Screenshots

**Auf dem Computer**

<img height="300" width="400" src="https://i.imgur.com/N2bfwNI.png" /><img height="300" width="500" src="https://i.imgur.com/wH2yxp5.png" />
<img height="250" width="400" src="https://i.imgur.com/9KEOb9q.png" />
<img height="250" width="550" src="https://i.imgur.com/oG7Tm12.png" />

**Am Telefon**

<img height="303" width="203" src="https://i.imgur.com/kkiREzq.png" /><img height="353" width="283" src="https://i.imgur.com/nyHanZI.png" /><img height="353" width="283" src="https://i.imgur.com/MZpZIe7.png" />

### Registrierung
Dies ist eine vollständige Registrierung mit E-Mail-Validierung mit Spring Mail Sender und Google Captcha, die Validierung der Registrierungsdaten erfolgt mit Spring Boot Validation.

Das Registrierungsformular und der Bestätigungscode werden in der Sitzung gespeichert, sodass sie gespeichert werden, bis der Benutzer den Browser-Tab schließt, und nicht auf dem Server, was Platz beansprucht und Probleme verursacht.

### Anmeldung 
Ein benutzerdefinierter Filter wurde für die Anmeldung erstellt, Kontoanmeldung basierend auf der E-Mail und dem Passwort des Benutzers. RemeberMeService wurde ebenfalls implementiert.

### Hinzufügen von Produkten mit Code
Es wurden drei Entitäten erstellt: User, Product, ProductCode. Alle Entitäten im Projekt wurden mit Hibernate Persitence erstellt, die mit dem Spring Boot Data JPA-Framework verwendet werden.

### Testen
Das Testen erfolgt mit dem beliebtesten JUnit-Testtool sowie Testcontainern für Integrationstests und Mockito zum Stubben von Abhängigkeiten. In diesem Projekt habe ich Unit-Tests angewendet und kritische Teile des Systems getestet. Mit Hilfe von Integrationstests habe ich den Betrieb des Systems komplett in Teilen getestet, z. B.: Registrierung (von der Eingabe der Registrierungsdaten durch den Benutzer bis zur Speicherung in der Datenbank)

### Ausruhen
Die Datenbank ist PostgreSQL, das als Container in Docker ausgeführt wird

Als CSS-Framework wurde Tailwind verwendet