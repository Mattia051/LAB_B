/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.server;

import theknife.common.Richiesta;
import theknife.common.Risposta;
import theknife.common.Utente;
import theknife.common.Ristorante;
import theknife.common.Recensione;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * 
 * La classe ClientHandler implementa Runnable ed è responsabile della gestione 
 * della sessione di comunicazione con un singolo client connesso al server.
 * Legge gli oggetti Richiesta ricevuti in rete, esegue le relative operazioni 
 * sul database richiamando il DatabaseManager e restituisce una Risposta al client.
 */
public class ClientHandler implements Runnable {
    /** Canale di comunicazione Socket verso il client. */
    private Socket clientSocket;
    
    /** Riferimento al manager del database condiviso per le operazioni SQL. */
    private DatabaseManager db;

    /**
     * Costruisce un gestore di connessione per un singolo client.
     * 
     * @param socket Il socket del client.
     * @param db     Il gestore del database.
     */
    public ClientHandler(Socket socket, DatabaseManager db) {
        this.clientSocket = socket;
        this.db = db;
    }

    /**
     * Ciclo principale di ascolto ed elaborazione dei messaggi del client.
     * Esegue la lettura asincrona degli oggetti serializzati dal flusso di I/O.
     */
    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            System.out.println("[INFO] ClientHandler pronto per " + clientSocket.getInetAddress());

            while (true) {
                Object obj = in.readObject();
                if (obj instanceof Richiesta richiesta) {
                    gestisciRichiesta(richiesta, out);
                }
            }

        } catch (Exception e) {
            System.out.println("[-] Client disconnesso.");
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Riconosce il tipo di richiesta ricevuta ed esegue l'operazione opportuna, 
     * scrivendo e inviando l'oggetto di risposta serializzato al client.
     * 
     * @param req L'oggetto Richiesta inviato dal client.
     * @param out Lo stream di output associato al socket per la risposta.
     * @throws Exception se si verifica un errore durante l'I/O o l'elaborazione.
     */
    @SuppressWarnings("unchecked")
    private void gestisciRichiesta(Richiesta req, ObjectOutputStream out) throws Exception {
        Risposta res;
        switch (req.getTipo()) {
            case LOGIN:
                String[] credenziali = (String[]) req.getDati();
                Utente u = db.login(credenziali[0], credenziali[1]);
                if (u != null) {
                    res = new Risposta(true, "Login effettuato", u);
                } else {
                    res = new Risposta(false, "Credenziali errate", null);
                }
                break;

            case REGISTRAZIONE:
                Utente nuovoUtente = (Utente) req.getDati();
                if (db.registraUtente(nuovoUtente)) {
                    res = new Risposta(true, "Registrazione completata", null);
                } else {
                    res = new Risposta(false, "Errore registrazione (username esistente?)", null);
                }
                break;

            case CERCA_RISTORANTI:
                List<Ristorante> risultati;
                if (req.getDati() instanceof String[]) {
                    String[] filtri = (String[]) req.getDati(); // [location, cucina]
                    risultati = db.cercaRistoranti(filtri[0], filtri[1]);
                } else {
                    Map<String, String> filtriMappa = (Map<String, String>) req.getDati();
                    risultati = db.cercaRistoranti(filtriMappa);
                }
                res = new Risposta(true, "Ricerca completata", risultati);
                break;

            case AGGIUNGI_RECENSIONE:
                Recensione rec = (Recensione) req.getDati();
                if (db.aggiungiRecensione(rec)) {
                    res = new Risposta(true, "Recensione aggiunta", null);
                } else {
                    res = new Risposta(false, "Errore aggiunta recensione", null);
                }
                break;

            case RISPONDI_RECENSIONE:
                Map<String, Object> datiRisposta = (Map<String, Object>) req.getDati();
                if (db.rispondiRecensione((int) datiRisposta.get("id"), (String) datiRisposta.get("testo"))) {
                    res = new Risposta(true, "Risposta inviata", null);
                } else {
                    res = new Risposta(false, "Errore invio risposta", null);
                }
                break;

            case AGGIUNGI_PREFERITO:
                String[] datiPref = (String[]) req.getDati();
                if (db.aggiungiPreferito(datiPref[0], datiPref[1])) {
                    res = new Risposta(true, "Aggiunto ai preferiti", null);
                } else {
                    res = new Risposta(false, "Errore aggiunta preferito", null);
                }
                break;

            case RIMUOVI_PREFERITO:
                String[] datiRimPref = (String[]) req.getDati();
                if (db.rimuoviPreferito(datiRimPref[0], datiRimPref[1])) {
                    res = new Risposta(true, "Rimosso dai preferiti", null);
                } else {
                    res = new Risposta(false, "Errore rimozione preferito", null);
                }
                break;

            case GET_PREFERITI:
                String usernamePref = (String) req.getDati();
                res = new Risposta(true, "Preferiti recuperati", db.getPreferiti(usernamePref));
                break;

            case GET_RECENSIONI:
                String nomeRist = (String) req.getDati();
                res = new Risposta(true, "Recensioni recuperate", db.getRecensioni(nomeRist));
                break;

            case GET_MIEI_RISTORANTI:
                String usernameProp = (String) req.getDati();
                res = new Risposta(true, "I tuoi ristoranti recuperati", db.getRistorantiProprietario(usernameProp));
                break;

            case MODIFICA_RECENSIONE:
                Map<String, Object> datiMod = (Map<String, Object>) req.getDati();
                if (db.modificaRecensione((String) datiMod.get("username"), (String) datiMod.get("ristorante"), (int) datiMod.get("stelle"), (String) datiMod.get("testo"))) {
                    res = new Risposta(true, "Recensione modificata", null);
                } else {
                    res = new Risposta(false, "Errore modifica recensione", null);
                }
                break;

            case CANCELLA_RECENSIONE:
                String[] datiCanc = (String[]) req.getDati();
                if (db.cancellaRecensione(datiCanc[0], datiCanc[1])) {
                    res = new Risposta(true, "Recensione cancellata", null);
                } else {
                    res = new Risposta(false, "Errore cancellazione recensione", null);
                }
                break;

            case GET_RECENSIONI_UTENTE:
                String userRec = (String) req.getDati();
                res = new Risposta(true, "Recensioni utente recuperate", db.getRecensioniUtente(userRec));
                break;

            case AGGIUNGI_RISTORANTE:
                Ristorante nuovoRist = (Ristorante) req.getDati();
                if (db.aggiungiRistorante(nuovoRist)) {
                    res = new Risposta(true, "Ristorante aggiunto", null);
                } else {
                    res = new Risposta(false, "Errore aggiunta ristorante", null);
                }
                break;

            case GET_TUTTI_RISTORANTI:
                res = new Risposta(true, "Tutti i ristoranti recuperati", db.getTuttiRistoranti());
                break;

            default:
                res = new Risposta(false, "Operazione non supportata", null);
        }
        out.writeObject(res);
        out.flush();
    }
}
