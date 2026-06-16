/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.common;

import java.io.Serializable;

/**
 * 
 * La classe Richiesta rappresenta un Data Transfer Object (DTO) inviato 
 * dal Client al Server tramite Socket TCP.
 * Consiste in un tipo di operazione definita dall'enum Tipo ed un carico utile 
 * generico (Object) contenente i parametri della richiesta.
 * Implementa Serializable per consentire l'invio su rete.
 */
public class Richiesta implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Tipi di operazioni supportate dal protocollo di comunicazione del sistema The Knife.
     */
    public enum Tipo {
        /** Richiesta di Login dell'utente. */
        LOGIN,
        
        /** Richiesta di registrazione di un nuovo utente. */
        REGISTRAZIONE,
        
        /** Richiesta di ricerca dei ristoranti tramite filtri. */
        CERCA_RISTORANTI,
        
        /** Inserimento di una nuova recensione. */
        AGGIUNGI_RECENSIONE,
        
        /** Invio della risposta del ristoratore ad una recensione. */
        RISPONDI_RECENSIONE,
        
        /** Aggiunta di un ristorante ai preferiti del cliente. */
        AGGIUNGI_PREFERITO,
        
        /** Rimozione di un ristorante dai preferiti del cliente. */
        RIMUOVI_PREFERITO,
        
        /** Recupero dell'elenco dei ristoranti preferiti di un utente. */
        GET_PREFERITI,
        
        /** Recupero di tutte le recensioni per un singolo ristorante. */
        GET_RECENSIONI,
        
        /** Recupero dei ristoranti di proprietà di un ristoratore. */
        GET_MIEI_RISTORANTI,
        
        /** Modifica di una recensione esistente. */
        MODIFICA_RECENSIONE,
        
        /** Cancellazione di una recensione. */
        CANCELLA_RECENSIONE,
        
        /** Recupero di tutte le recensioni scritte da un utente specifico. */
        GET_RECENSIONI_UTENTE,
        
        /** Registrazione di un nuovo ristorante nel database. */
        AGGIUNGI_RISTORANTE,
        
        /** Recupero di tutti i ristoranti presenti a sistema. */
        GET_TUTTI_RISTORANTI
    }

    /** Tipo di operazione richiesta. */
    private Tipo tipo;
    
    /** Dati associati alla richiesta (può contenere oggetti come Utente, Recensione, stringhe o array). */
    private Object dati; 

    /**
     * Costruisce una nuova istanza di una richiesta di rete.
     * 
     * @param tipo Il tipo di operazione di rete da eseguire.
     * @param dati Il carico utile (payload) contenente i dati a supporto dell'operazione.
     */
    public Richiesta(Tipo tipo, Object dati) {
        this.tipo = tipo;
        this.dati = dati;
    }

    /** @return il tipo di richiesta. */
    public Tipo getTipo() { return tipo; }
    
    /** @return i dati allegati alla richiesta. */
    public Object getDati() { return dati; }
}
