package theknife.client;

import theknife.common.Richiesta;
import theknife.common.Risposta;
import theknife.common.Utente;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * ClientTK gestisce la connessione con il ServerTK e fornisce i metodi
 * per interagire con il sistema (Login, Ricerca, ecc.)
 */
public class ClientTK {
    private String host;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientTK(String host, int port) {
        this.host = host;
        this.port = port;
    }

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

    public void disconnetti() {
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Esempi di metodi di alto livello per l'interfaccia
    public Risposta login(String username, String password) {
        return inviaRichiesta(Richiesta.Tipo.LOGIN, new String[]{username, password});
    }

    public Risposta registra(Utente u) {
        return inviaRichiesta(Richiesta.Tipo.REGISTRAZIONE, u);
    }

    public Risposta cercaRistoranti(String location, String cucina) {
        return inviaRichiesta(Richiesta.Tipo.CERCA_RISTORANTI, new String[]{location, cucina});
    }

    public Risposta cercaRistoranti(java.util.Map<String, String> filtri) {
        return inviaRichiesta(Richiesta.Tipo.CERCA_RISTORANTI, filtri);
    }

    public Risposta aggiungiRecensione(String user, String ristorante, int stelle, String testo) {
        return inviaRichiesta(Richiesta.Tipo.AGGIUNGI_RECENSIONE, new theknife.common.Recensione(user, ristorante, stelle, testo));
    }

    public Risposta getRecensioni(String nomeRistorante) {
        return inviaRichiesta(Richiesta.Tipo.GET_RECENSIONI, nomeRistorante);
    }

    public Risposta aggiungiPreferito(String user, String ristorante) {
        return inviaRichiesta(Richiesta.Tipo.AGGIUNGI_PREFERITO, new String[]{user, ristorante});
    }

    public Risposta getPreferiti(String user) {
        return inviaRichiesta(Richiesta.Tipo.GET_PREFERITI, user);
    }

    public Risposta getMieiRistoranti(String user) {
        return inviaRichiesta(Richiesta.Tipo.GET_MIEI_RISTORANTI, user);
    }

    public Risposta rispondiRecensione(int idRecensione, String risposta) {
        java.util.Map<String, Object> dati = new java.util.HashMap<>();
        dati.put("id", idRecensione);
        dati.put("testo", risposta);
        return inviaRichiesta(Richiesta.Tipo.RISPONDI_RECENSIONE, dati);
    }

    public Risposta modificaRecensione(String user, String ristorante, int stelle, String testo) {
        java.util.Map<String, Object> dati = new java.util.HashMap<>();
        dati.put("username", user);
        dati.put("ristorante", ristorante);
        dati.put("stelle", stelle);
        dati.put("testo", testo);
        return inviaRichiesta(Richiesta.Tipo.MODIFICA_RECENSIONE, dati);
    }

    public Risposta cancellaRecensione(String user, String ristorante) {
        return inviaRichiesta(Richiesta.Tipo.CANCELLA_RECENSIONE, new String[]{user, ristorante});
    }

    public Risposta rimuoviPreferito(String user, String ristorante) {
        return inviaRichiesta(Richiesta.Tipo.RIMUOVI_PREFERITO, new String[]{user, ristorante});
    }

    public Risposta getRecensioniUtente(String user) {
        return inviaRichiesta(Richiesta.Tipo.GET_RECENSIONI_UTENTE, user);
    }

    public Risposta aggiungiRistorante(theknife.common.Ristorante r) {
        return inviaRichiesta(Richiesta.Tipo.AGGIUNGI_RISTORANTE, r);
    }

    public Risposta getTuttiRistoranti() {
        return inviaRichiesta(Richiesta.Tipo.GET_TUTTI_RISTORANTI, null);
    }
}
