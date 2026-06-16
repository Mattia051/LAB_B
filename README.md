# The Knife - Piattaforma di Recensione Ristoranti

**The Knife** è un'applicazione client-server in linguaggio **Java** per la ricerca e la recensione di ristoranti. Il sistema utilizza un'interfaccia grafica sviluppata in **Java Swing** (lato client) ed integra la persistenza dei dati tramite un database relazionale **PostgreSQL** accessibile tramite **JDBC** (lato server).

Tutte le scelte architetturali, i modelli di dati, i diagrammi UML (classi, sequenza, casi d'uso) e lo schema ER sono documentati dettagliatamente nella relazione di progetto:
* **[RELAZIONE.md](file:///C:/Users/Mattia/OneDrive/Desktop/BACKUP%20LAB_B/The-Knife/RELAZIONE.md)**

---

## 1. Prerequisiti

Per eseguire l'applicazione sono necessari:
* **Java Development Kit (JDK)** versione 17 o superiore.
* **Apache Maven** per la compilazione e la gestione delle dipendenze.
* **PostgreSQL** attivo localmente o in rete.

---

## 2. Configurazione del Database

1. Accedere al proprio server PostgreSQL (ad esempio tramite `pgAdmin` o terminale `psql`).
2. Creare un database vuoto denominato `theknife`:
   ```sql
   CREATE DATABASE theknife;
   ```
3. Eseguire sul database appena creato lo script SQL di inizializzazione per creare le tabelle ed i vincoli:
   * Lo script è presente nel file **[schema.sql](file:///C:/Users/Mattia/OneDrive/Desktop/BACKUP%20LAB_B/The-Knife/schema.sql)**.

---

## 3. Compilazione del Progetto

Compilare il codice sorgente del progetto ed importare le dipendenze (tra cui il driver JDBC di PostgreSQL) eseguendo dalla root del progetto:
```bash
mvn clean compile
```

---

## 4. Esecuzione dell'Applicazione

### Passo 4.1: Avvio del Server
Il server deve essere avviato prima dei client. All'avvio, il server richiederà nel terminale le credenziali di accesso al database (Host, Username e Password).

Per avviare il server, eseguire:
```bash
mvn exec:java -Dexec.mainClass="theknife.server.ServerTK"
```

### Passo 4.2: Avvio del Client
Una volta che il server è in ascolto sulla porta `12346`, è possibile avviare una o più istanze del client GUI Swing.

Per avviare il client, eseguire:
```bash
mvn exec:java -Dexec.mainClass="theknife.client.MainClient"
```
