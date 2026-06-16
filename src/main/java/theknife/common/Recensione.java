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
 * La classe Recensione rappresenta una valutazione inserita da un utente 
 * cliente per un determinato ristorante nel sistema "The Knife".
 * Comprende un identificativo numerico, lo username dell'autore, il nome del locale 
 * recensito, un voto in stelle (da 1 a 5), un commento testuale ed un eventuale 
 * testo di risposta fornito dal ristoratore.
 * Implementa Serializable per consentire l'invio su rete.
 */
public class Recensione implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID univoco della recensione autogenerato dal database. */
    private int id; 
    
    /** Username dell'utente cliente che ha scritto la recensione. */
    private String usernameUtente;
    
    /** Nome del ristorante recensito. */
    private String nomeRistorante;
    
    /** Valutazione quantitativa espressa in numero di stelle (da 1 a 5). */
    private int stelle;
    
    /** Commento testuale inserito dall'utente cliente. */
    private String testo;
    
    /** Risposta ufficiale inserita dal ristoratore proprietario del locale. */
    private String risposta;

    /**
     * Costruttore completo per istanziare una recensione recuperata dal database.
     * 
     * @param id             Identificativo univoco della recensione.
     * @param usernameUtente Username del cliente autore.
     * @param nomeRistorante Nome del ristorante recensito.
     * @param stelle         Numero di stelle (voto da 1 a 5).
     * @param testo          Testo del commento.
     * @param risposta       Testo della risposta del ristoratore (può essere null).
     */
    public Recensione(int id, String usernameUtente, String nomeRistorante, int stelle, String testo, String risposta) {
        this.id = id;
        this.usernameUtente = usernameUtente;
        this.nomeRistorante = nomeRistorante;
        this.stelle = stelle;
        this.testo = testo;
        this.risposta = risposta;
    }

    /**
     * Costruttore per creare una nuova recensione lato client (senza ID database associato).
     * 
     * @param usernameUtente Username del cliente autore.
     * @param nomeRistorante Nome del ristorante recensito.
     * @param stelle         Numero di stelle (voto da 1 a 5).
     * @param testo          Testo del commento.
     */
    public Recensione(String usernameUtente, String nomeRistorante, int stelle, String testo) {
        this.usernameUtente = usernameUtente;
        this.nomeRistorante = nomeRistorante;
        this.stelle = stelle;
        this.testo = testo;
    }

    /** @return l'ID della recensione. */
    public int getId() { return id; }
    
    /** @return lo username dell'autore. */
    public String getUsernameUtente() { return usernameUtente; }
    
    /** @return il nome del ristorante recensito. */
    public String getNomeRistorante() { return nomeRistorante; }
    
    /** @return il punteggio in stelle. */
    public int getStelle() { return stelle; }
    
    /** @return il commento testuale. */
    public String getTesto() { return testo; }
    
    /** @return la risposta del ristoratore. */
    public String getRisposta() { return risposta; }

    /**
     * Imposta o aggiorna il testo di risposta del ristoratore.
     * 
     * @param risposta Il testo del messaggio di risposta.
     */
    public void setRisposta(String risposta) { this.risposta = risposta; }

    @Override
    public String toString() {
        String s = String.format("[%d] %d/5 stelle - \"%s\" (da %s)", id, stelle, testo, usernameUtente);
        if (risposta != null && !risposta.isEmpty()) {
            s += "\n    -> Risposta: " + risposta;
        }
        return s;
    }
}
