package theknife.common;

import java.io.Serializable;

/**
 * La classe Risposta rappresenta un messaggio inviato dal Server al Client.
 */
public class Risposta implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean successo;
    private String messaggio;
    private Object dati; // Risultato della query (List<Ristorante>, Utente, ecc.)

    public Risposta(boolean successo, String messaggio, Object dati) {
        this.successo = successo;
        this.messaggio = messaggio;
        this.dati = dati;
    }

    public boolean isSuccesso() { return successo; }
    public String getMessaggio() { return messaggio; }
    public Object getDati() { return dati; }
}
