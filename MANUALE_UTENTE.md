# Manuale Utente: The Knife

Benvenuto nella guida utente dell'applicazione **The Knife**. Questo manuale illustra come installare, avviare e utilizzare le funzionalità della piattaforma per la recensione e la gestione dei ristoranti.

---

## 1. Come Avviare l'Applicazione

L'applicazione si compone di due moduli: il **Server** (che gestisce la persistenza dei dati) ed il **Client** (l'interfaccia grafica per l'utente).

### 1.1 Avvio del Server
1. Assicurati che PostgreSQL sia attivo sul computer.
2. Apri il terminale nella cartella del progetto ed esegui il comando:
   ```bash
   mvn exec:java -Dexec.mainClass="theknife.server.ServerTK"
   ```
3. Il server ti chiederà di inserire nel terminale i dati del tuo database:
   * **Host**: (es. `localhost`)
   * **Username**: (es. `postgres`)
   * **Password**: (la password del tuo utente del DB)
4. Una volta stabilita la connessione con il database, il server rimarrà in attesa sulla porta `12346`.

### 1.2 Avvio del Client
1. Apri un altro terminale nella cartella del progetto ed esegui il comando:
   ```bash
   mvn exec:java -Dexec.mainClass="theknife.client.MainClient"
   ```
2. Verrà visualizzata la schermata di login iniziale.

---

## 2. Schermata di Accesso e Registrazione

All'avvio del client viene mostrata la finestra principale per l'autenticazione.

<!-- INSERIRE QUI LO SCREENSHOT DELLA SCHERMATA DI ACCESSO -->
> **[Screenshot Consigliato]**: Cattura la schermata iniziale contenente i campi Username, Password e i bottoni "Accedi", "Registrati" e "Ospite".

### 2.1 Autenticazione (Login)
* Inserisci il tuo **Username** e la tua **Password**.
* Clicca su **Accedi**.
* In base al tuo ruolo, verrai reindirizzato alla dashboard dedicata.

### 2.2 Registrazione Nuovo Utente
* Fai clic sul pulsante **Registrati**.
* Verrà aperta una finestra pop-up in cui inserire i seguenti dati:
  * Nome e Cognome
  * Username desiderato
  * Password
  * Data di Nascita (opzionale)
  * Domicilio
  * Ruolo (selezionabile tra `cliente` o `ristoratore`)
* Compila i campi e premi **Registrati** per completare la procedura.

<!-- INSERIRE QUI LO SCREENSHOT DEL POPUP DI REGISTRAZIONE -->
> **[Screenshot Consigliato]**: Cattura la finestra di dialogo di registrazione dei dati utente.

### 2.3 Accesso come Ospite (Guest)
* Fai clic sul pulsante **Ospite**.
* Verrà richiesto di inserire una **località iniziale** per navigare sul portale in modo anonimo.
* Si aprirà la dashboard dell'ospite filtrata per quella località.

---

## 3. Guida alle Funzionalità dell'Ospite (Utente non registrato)

L'ospite può navigare all'interno dell'applicazione per consultare ristoranti e recensioni, ma non può inserire nuovi contenuti.

<!-- INSERIRE QUI LO SCREENSHOT DELLA DASHBOARD OSPITE -->
> **[Screenshot Consigliato]**: Cattura la dashboard dell'ospite (GuestDashboard) con i risultati della ricerca ristoranti.

### 3.1 Ricerca dei Ristoranti
* **Ricerca per Località**: Inserisci il nome di una città o provincia nella casella di testo a sinistra e clicca su **Cerca**. L'elenco al centro mostrerà i ristoranti trovati in quel luogo.
* **Ricerca Avanzata**: Seleziona "Ricerca Avanzata" dal menu a tendina. Si aprirà un modulo per cercare combinando filtri come il nome del ristorante, la tipologia di cucina, la fascia di prezzo (€, €€, €€€, €€€€) e servizi extra (es. `delivery`).

### 3.2 Visualizzazione Recensioni in Forma Anonima
* Clicca sul pulsante **Vedi Recensioni**.
* Inserisci il nome del ristorante di cui vuoi visualizzare il feedback.
* La schermata centrale mostrerà l'elenco delle recensioni con le stelle e i commenti scritti dai clienti.

---

## 4. Guida alle Funzionalità del Cliente (Utente registrato)

Il cliente ha accesso a tutte le funzionalità interattive di recensione e gestione dei propri preferiti.

<!-- INSERIRE QUI LO SCREENSHOT DELLA DASHBOARD CLIENTE -->
> **[Screenshot Consigliato]**: Cattura la dashboard del cliente (ClientDashboard) mostrando il menu laterale a sinistra e la visualizzazione del domicilio al centro.

### 4.1 Visualizzazione automatica dei ristoranti vicini al Domicilio
* All'accesso, la schermata visualizza automaticamente l'elenco dei ristoranti che si trovano nella stessa città inserita come domicilio nel profilo del cliente.

### 4.2 Gestione dei Ristoranti Preferiti
* **Aggiungi Preferito**: Clicca su "Aggiungi Preferito", inserisci il nome del locale e premi OK per salvarlo.
* **Rimuovi Preferito**: Clicca su "Rimuovi Preferito" ed inserisci il nome del locale per eliminarlo dalla lista dei preferiti.
* **Ristoranti Preferiti**: Mostra l'elenco dei locali salvati come preferiti.

### 4.3 Gestione delle Recensioni
* **Aggiungi Recensione**: Clicca sul pulsante, inserisci il nome del ristorante, seleziona una valutazione da 1 a 5 stelle e digita il commento testuale.
* **Modifica Recensione**: Permette di modificare il punteggio in stelle ed il testo di una recensione già inserita in precedenza per un ristorante.
* **Elimina Recensione**: Permette di cancellare in modo definitivo una delle proprie recensioni.
* **Vedi Tue Recensioni**: Mostra un riepilogo di tutte le recensioni che hai scritto, comprese le risposte ufficiali fornite dai ristoratori.

---

## 5. Guida alle Funzionalità del Ristoratore (Utente registrato)

Il ristoratore gestisce i propri locali ed esamina il feedback dei clienti.

<!-- INSERIRE QUI LO SCREENSHOT DELLA DASHBOARD RISTORATORE -->
> **[Screenshot Consigliato]**: Cattura la dashboard del ristoratore (RestaurateurDashboard) con l'elenco dei ristoranti registrati.

### 5.1 Inserimento Nuovo Ristorante
* Clicca su **Nuovo Ristorante**.
* Compila i campi relativi al locale: Nome, Indirizzo, Città (Località), Tipo di Cucina, Telefono, Fascia di Prezzo.
* Conferma cliccando su OK. Il ristorante verrà associato al tuo profilo proprietario.

### 5.2 Gestione Recensioni e Risposte
* **Visualizza Recensioni**: Seleziona uno dei tuoi ristoranti dall'elenco per consultare tutte le recensioni ricevute dai clienti.
* **Rispondi a Recensione**: Inserisci l'ID della recensione a cui vuoi rispondere e scrivi il testo del messaggio (è possibile inserire al massimo una risposta per ciascuna recensione).
