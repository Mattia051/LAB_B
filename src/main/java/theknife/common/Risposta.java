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
 * La classe Risposta rappresenta la risposta inviata dal Server al Client 
 * a seguito di una richiesta di rete ricevuta via Socket.
 * Contiene l'esito dell'operazione (booleano), un messaggio testuale descrittivo 
 * ed eventuali dati richiesti dal client (ad esempio l'utente autenticato o la 
 * lista dei ristoranti trovati).
 * Implementa Serializable per consentire l'invio su rete.
 */
public class Risposta implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Indica se l'operazione richiesta ha avuto successo (true) o è fallita (false). */
    private boolean successo;
    
    /** Messaggio testuale descrittivo dell'esito dell'operazione. */
    private String messaggio;
    
    /** Dati di risposta (es. un oggetto Utente, una List di Ristorante, ecc.). */
    private Object dati; 

    /**
     * Costruisce una nuova istanza di una risposta di rete.
     * 
     * @param successo   L'esito dell'operazione.
     * @param messaggio  Il messaggio esplicativo del risultato.
     * @param dati       Il carico utile di ritorno con i risultati (può essere null).
     */
    public Risposta(boolean successo, String messaggio, Object dati) {
        this.successo = successo;
        this.messaggio = messaggio;
        this.dati = dati;
    }

    /** @return true se l'operazione è andata a buon fine, false altrimenti. */
    public boolean isSuccesso() { return successo; }
    
    /** @return il messaggio esplicativo dell'esito. */
    public String getMessaggio() { return messaggio; }
    
    /** @return i dati di risposta allegati. */
    public Object getDati() { return dati; }
}
