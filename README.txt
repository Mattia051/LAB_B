========================================================================
             THE KNIFE - PIATTAFORMA DI RECENSIONE RISTORANTI
========================================================================

The Knife è un'applicazione client-server in linguaggio Java per la 
ricerca e la recensione di ristoranti. Il sistema utilizza un'interfaccia 
grafica sviluppata in Java Swing (lato client) ed integra la persistenza 
dei dati tramite un database relazionale PostgreSQL accessibile tramite 
JDBC (lato server).

Tutte le scelte architetturali, i modelli di dati, i diagrammi UML 
(classi, sequenza, casi d'uso) e lo schema ER sono documentati 
dettagliatamente nella relazione di progetto:
* RELAZIONE.md (nella root del progetto)

------------------------------------------------------------------------
1. PREREQUISITI
------------------------------------------------------------------------
Per eseguire l'applicazione sono necessari:
* Java Development Kit (JDK) versione 17 o superiore.
* Apache Maven per la compilazione e la gestione delle dipendenze.
* PostgreSQL attivo localmente o in rete.

------------------------------------------------------------------------
2. CONFIGURAZIONE DEL DATABASE
------------------------------------------------------------------------
1. Accedere al proprio server PostgreSQL (ad esempio tramite pgAdmin).
2. Creare un database vuoto denominato "theknife":
   CREATE DATABASE theknife;
3. Eseguire sul database appena creato lo script SQL di inizializzazione 
   per creare le tabelle ed i vincoli presenti nel file:
   * schema.sql (nella root del progetto)

------------------------------------------------------------------------
3. COMPILAZIONE DEL PROGETTO
------------------------------------------------------------------------
Per scaricare le dipendenze (tra cui il driver PostgreSQL) e compilare 
il codice sorgente, eseguire da terminale nella root del progetto:
   mvn clean compile

------------------------------------------------------------------------
4. ESECUZIONE DELL'APPLICAZIONE
------------------------------------------------------------------------

4.1 Avvio del Server:
Il server deve essere avviato prima dei client. All'avvio, il server 
richiederà nel terminale le credenziali di accesso al database (Host, 
Username e Password).
Eseguire il comando:
   mvn exec:java -Dexec.mainClass="theknife.server.ServerTK"

4.2 Avvio del Client:
Una volta che il server è in ascolto sulla porta 12346, è possibile 
avviare una o più istanze del client GUI Swing.
Eseguire il comando:
   mvn exec:java -Dexec.mainClass="theknife.client.MainClient"

------------------------------------------------------------------------
5. GENERAZIONE JAVADOC
------------------------------------------------------------------------
Per generare la documentazione Javadoc del progetto nella cartella 
target/site/apidocs, eseguire:
   mvn javadoc:javadoc
========================================================================
