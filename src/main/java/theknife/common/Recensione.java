package theknife.common;

import java.io.Serializable;

/**
 * La classe Recensione rappresenta una recensione nel sistema The Knife.
 */
public class Recensione implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id; // ID del database
    private String usernameUtente;
    private String nomeRistorante;
    private int stelle;
    private String testo;
    private String risposta;

    public Recensione(int id, String usernameUtente, String nomeRistorante, int stelle, String testo, String risposta) {
        this.id = id;
        this.usernameUtente = usernameUtente;
        this.nomeRistorante = nomeRistorante;
        this.stelle = stelle;
        this.testo = testo;
        this.risposta = risposta;
    }

    // Costruttore per nuove recensioni (senza ID)
    public Recensione(String usernameUtente, String nomeRistorante, int stelle, String testo) {
        this.usernameUtente = usernameUtente;
        this.nomeRistorante = nomeRistorante;
        this.stelle = stelle;
        this.testo = testo;
    }

    public int getId() { return id; }
    public String getUsernameUtente() { return usernameUtente; }
    public String getNomeRistorante() { return nomeRistorante; }
    public int getStelle() { return stelle; }
    public String getTesto() { return testo; }
    public String getRisposta() { return risposta; }

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
