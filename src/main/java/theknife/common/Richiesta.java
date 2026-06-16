package theknife.common;

import java.io.Serializable;

/**
 * La classe Richiesta rappresenta un messaggio inviato dal Client al Server.
 */
public class Richiesta implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Tipo {
        LOGIN,
        REGISTRAZIONE,
        CERCA_RISTORANTI,
        AGGIUNGI_RECENSIONE,
        RISPONDI_RECENSIONE,
        AGGIUNGI_PREFERITO,
        RIMUOVI_PREFERITO,
        GET_PREFERITI,
        GET_RECENSIONI,
        GET_MIEI_RISTORANTI,
        MODIFICA_RECENSIONE,
        CANCELLA_RECENSIONE,
        GET_RECENSIONI_UTENTE,
        AGGIUNGI_RISTORANTE,
        GET_TUTTI_RISTORANTI
    }

    private Tipo tipo;
    private Object dati; // Può contenere String, Utente, Ristorante, ecc.

    public Richiesta(Tipo tipo, Object dati) {
        this.tipo = tipo;
        this.dati = dati;
    }

    public Tipo getTipo() { return tipo; }
    public Object getDati() { return dati; }
}
