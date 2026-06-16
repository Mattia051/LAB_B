/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.client;

import theknife.common.Richiesta;
import theknife.common.Risposta;
import theknife.common.Utente;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 
 * ClientTK gestisce la connessione socket attiva con il server ServerTK.
 * Fornisce un'interfaccia ad alto livello per l'invio delle richieste (Richiesta)
 * e la ricezione sincrona delle risposte (Risposta) da parte dell'applicazione GUI,
 * nascondendo la complessità di rete e di I/O.
 */
public class ClientTK {
    /** L'indirizzo IP o host del server backend. */
    private String host;
    
    /** La porta TCP del server backend. */
    private int port;
    
    /** Oggetto Socket per la connessione di rete TCP. */
    private Socket socket;
    
    /** Stream per l'invio di oggetti serializzati in rete. */
    private ObjectOutputStream out;
    
    /** Stream per la ricezione di oggetti serializzati dalla rete. */
    private ObjectInputStream in;

    /**
     * Inizializza le impostazioni di connessione di rete per il client.
     * 
     * @param host L'indirizzo IP del server.
     * @param port La porta del server.
     */
    public ClientTK(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Tenta di stabilire una connessione socket persistente con il ServerTK 
     * e di inizializzare gli stream di input e output per gli oggetti.
     * 
     * @return true se la connessione è stata stabilita con successo, false altrimenti.
     */
    public boolean connetti() {
        try {
            this.socket = new Socket(host, port);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            System.out.println("[OK] Connesso al server " + host + ":" + port);
            return true;
        } catch (Exception e) {
            System.err.println("[ERRORE] Impossibile connettersi al server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Invia un oggetto Richiesta serializzato in rete e attende la Risposta del server.
     * 
     * @param tipo Il tipo di operazione.
     * @param dati Il carico utile (payload).
     * @return La Risposta inviata dal server o una risposta di errore in caso di fallimento.
     */
    public Risposta inviaRichiesta(Richiesta.Tipo tipo, Object dati) {
        try {
            Richiesta req = new Richiesta(tipo, dati);
            out.writeObject(req);
            out.flush();
            return (Risposta) in.readObject();
        } catch (Exception e) {
            System.err.println("[ERRORE] Comunicazione fallita: " + e.getMessage());
            return new Risposta(false, "Errore di rete", null);
        }
    }

    /**
     * Chiude in modo sicuro la connessione Socket attiva con il server.
     */
    public void disconnetti() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Invia una richiesta di Login al server.
     * 
     * @param username Lo username dell'utente.
     * @param password La password in chiaro dell'utente.
     * @return La Risposta contenente l'esito ed eventualmente l'oggetto Utente loggato.
     */
    public Risposta login(String username, String password) {
        return inviaRichiesta(Richiesta.Tipo.LOGIN, new String[]{username, password});
    }

    /**
     * Invia una richiesta di Registrazione per un nuovo utente.
     * 
     * @param u L'utente da registrare.
     * @return La Risposta del server.
     */
    public Risposta registra(Utente u) {
        return inviaRichiesta(Richiesta.Tipo.REGISTRAZIONE, u);
    }

    /**
     * Esegue una ricerca base dei ristoranti per città (location) e tipo di cucina.
     * 
     * @param location La località del ristorante.
     * @param cucina   Il tipo di cucina.
     * @return La Risposta contenente l'elenco dei ristoranti trovati.
     */
    public Risposta cercaRistoranti(String location, String cucina) {
        return inviaRichiesta(Richiesta.Tipo.CERCA_RISTORANTI, new String[]{location, cucina});
    }

    /**
     * Esegue una ricerca avanzata sul server passando una mappa di filtri.
     * 
     * @param filtri Mappa chiave-valore contenente i filtri.
     * @return La Risposta contenente l'elenco dei ristoranti trovati.
     */
    public Risposta cercaRistoranti(java.util.Map<String, String> filtri) {
        return inviaRichiesta(Richiesta.Tipo.CERCA_RISTORANTI, filtri);
    }

    /**
     * Invia una nuova recensione scritta da un utente per un ristorante.
     * 
     * @param user       Lo username del cliente autore.
     * @param ristorante Il nome del ristorante recensito.
     * @param stelle     Il punteggio (voto da 1 a 5).
     * @param testo      Il commento.
     * @return La Risposta del server.
     */
    public Risposta aggiungiRecensione(String user, String ristorante, int stelle, String testo) {
        return inviaRichiesta(Richiesta.Tipo.AGGIUNGI_RECENSIONE, new theknife.common.Recensione(user, ristorante, stelle, testo));
    }

    /**
     * Richiede l'elenco di tutte le recensioni relative ad un ristorante.
     * 
     * @param nomeRistorante Il nome del ristorante.
     * @return La Risposta contenente l'elenco delle recensioni.
     */
    public Risposta getRecensioni(String nomeRistorante) {
        return inviaRichiesta(Richiesta.Tipo.GET_RECENSIONI, nomeRistorante);
    }

    /**
     * Aggiunge un ristorante alla lista dei preferiti dell'utente.
     * 
     * @param user       Lo username dell'utente.
     * @param ristorante Il nome del ristorante.
     * @return La Risposta del server.
     */
    public Risposta aggiungiPreferito(String user, String ristorante) {
        return inviaRichiesta(Richiesta.Tipo.AGGIUNGI_PREFERITO, new String[]{user, ristorante});
    }

    /**
     * Richiede l'elenco dei ristoranti preferiti di un utente.
     * 
     * @param user Lo username dell'utente.
     * @return La Risposta con l'elenco dei preferiti.
     */
    public Risposta getPreferiti(String user) {
        return inviaRichiesta(Richiesta.Tipo.GET_PREFERITI, user);
    }

    /**
     * Richiede l'elenco dei ristoranti di proprietà di un ristoratore.
     * 
     * @param user Lo username del ristoratore.
     * @return La Risposta con l'elenco dei suoi ristoranti.
     */
    public Risposta getMieiRistoranti(String user) {
        return inviaRichiesta(Richiesta.Tipo.GET_MIEI_RISTORANTI, user);
    }

    /**
     * Consente ad un ristoratore di inviare la risposta ad una determinata recensione.
     * 
     * @param idRecensione L'identificativo della recensione.
     * @param risposta     Il testo del messaggio di risposta.
     * @return La Risposta del server.
     */
    public Risposta rispondiRecensione(int idRecensione, String risposta) {
        java.util.Map<String, Object> dati = new java.util.HashMap<>();
        dati.put("id", idRecensione);
        dati.put("testo", risposta);
        return inviaRichiesta(Richiesta.Tipo.RISPONDI_RECENSIONE, dati);
    }

    /**
     * Permette ad un cliente di modificare una propria recensione già inserita.
     * 
     * @param user       Lo username del cliente autore.
     * @param ristorante Il nome del ristorante.
     * @param stelle     La nuova valutazione (1-5).
     * @param testo      Il nuovo testo.
     * @return La Risposta del server.
     */
    public Risposta modificaRecensione(String user, String ristorante, int stelle, String testo) {
        java.util.Map<String, Object> dati = new java.util.HashMap<>();
        dati.put("username", user);
        dati.put("ristorante", ristorante);
        dati.put("stelle", stelle);
        dati.put("testo", testo);
        return inviaRichiesta(Richiesta.Tipo.MODIFICA_RECENSIONE, dati);
    }

    /**
     * Consente ad un cliente di cancellare una propria recensione.
     * 
     * @param user       Lo username dell'utente.
     * @param ristorante Il nome del ristorante.
     * @return La Risposta del server.
     */
    public Risposta cancellaRecensione(String user, String ristorante) {
        return inviaRichiesta(Richiesta.Tipo.CANCELLA_RECENSIONE, new String[]{user, ristorante});
    }

    /**
     * Rimuove un ristorante dalla lista dei preferiti dell'utente.
     * 
     * @param user       Lo username dell'utente.
     * @param ristorante Il nome del ristorante.
     * @return La Risposta del server.
     */
    public Risposta rimuoviPreferito(String user, String ristorante) {
        return inviaRichiesta(Richiesta.Tipo.RIMUOVI_PREFERITO, new String[]{user, ristorante});
    }

    /**
     * Richiede tutte le recensioni scritte da un utente specifico.
     * 
     * @param user Lo username dell'utente.
     * @return La Risposta con l'elenco delle recensioni dell'utente.
     */
    public Risposta getRecensioniUtente(String user) {
        return inviaRichiesta(Richiesta.Tipo.GET_RECENSIONI_UTENTE, user);
    }

    /**
     * Registra un nuovo ristorante di proprietà di un ristoratore.
     * 
     * @param r L'oggetto Ristorante.
     * @return La Risposta del server.
     */
    public Risposta aggiungiRistorante(theknife.common.Ristorante r) {
        return inviaRichiesta(Richiesta.Tipo.AGGIUNGI_RISTORANTE, r);
    }

    /**
     * Richiede l'elenco di tutti i ristoranti registrati a sistema.
     * 
     * @return La Risposta contenente l'elenco completo dei ristoranti.
     */
    public Risposta getTuttiRistoranti() {
        return inviaRichiesta(Richiesta.Tipo.GET_TUTTI_RISTORANTI, null);
    }
}
