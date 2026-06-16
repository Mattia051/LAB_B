/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.common;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * La classe Utente rappresenta un utente all'interno della piattaforma "The Knife".
 * Gestisce i dati anagrafici, le credenziali (con password cifrata), il ruolo 
 * (cliente o ristoratore) e la lista dei ristoranti preferiti.
 * Implementa Serializable per consentire l'invio dell'oggetto tramite Socket.
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Nome dell'utente. */
    private String nome;
    
    /** Cognome dell'utente. */
    private String cognome;
    
    /** Identificativo univoco (username) dell'utente. */
    private String username;
    
    /** Password dell'utente memorizzata sotto forma di hash SHA-256. */
    private String passwordCifrata;
    
    /** Data di nascita dell'utente (formato facoltativo). */
    private String dataNascita;
    
    /** Luogo di domicilio dell'utente. */
    private String domicilio;
    
    /** Ruolo dell'utente ("cliente" o "ristoratore"). */
    private String ruolo; 
    
    /** Insieme dei nomi dei ristoranti aggiunti ai preferiti. */
    private Set<String> preferiti = new HashSet<>();

    /**
     * Costruttore per creare un nuovo utente a partire da una password in chiaro.
     * La password viene automaticamente cifrata tramite hash SHA-256.
     * 
     * @param nome            Nome dell'utente.
     * @param cognome         Cognome dell'utente.
     * @param username        Username univoco.
     * @param passwordChiara  Password in chiaro da cifrare.
     * @param dataNascita     Data di nascita (facoltativa).
     * @param domicilio       Luogo di domicilio.
     * @param ruolo           Ruolo dell'utente ("cliente" o "ristoratore").
     */
    public Utente(String nome, String cognome, String username, String passwordChiara,
                  String dataNascita, String domicilio, String ruolo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordCifrata = cifraPassword(passwordChiara);
        this.dataNascita = dataNascita == null ? "" : dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo.toLowerCase();
    }

    /**
     * Costruttore per caricare un utente esistente dal database (con password già cifrata).
     * 
     * @param nome            Nome dell'utente.
     * @param cognome         Cognome dell'utente.
     * @param username        Username dell'utente.
     * @param passwordCifrata Password già cifrata nel DB.
     * @param dataNascita     Data di nascita.
     * @param domicilio       Luogo di domicilio.
     * @param ruolo           Ruolo dell'utente.
     * @param giaCifrata      Booleano che indica che la password è già cifrata.
     */
    public Utente(String nome, String cognome, String username, String passwordCifrata,
                  String dataNascita, String domicilio, String ruolo, boolean giaCifrata) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.passwordCifrata = passwordCifrata;
        this.dataNascita = dataNascita == null ? "" : dataNascita;
        this.domicilio = domicilio;
        this.ruolo = ruolo.toLowerCase();
    }

    /** @return il nome dell'utente. */
    public String getNome() { return nome; }
    
    /** @return il cognome dell'utente. */
    public String getCognome() { return cognome; }
    
    /** @return lo username dell'utente. */
    public String getUsername() { return username; }
    
    /** @return la password cifrata dell'utente. */
    public String getPasswordCifrata() { return passwordCifrata; }
    
    /** @return la data di nascita dell'utente. */
    public String getDataNascita() { return dataNascita; }
    
    /** @return il domicilio dell'utente. */
    public String getDomicilio() { return domicilio; }
    
    /** @return il ruolo dell'utente. */
    public String getRuolo() { return ruolo; }

    /**
     * Esegue la cifratura a una via di una stringa (password) utilizzando 
     * l'algoritmo di hash SHA-256.
     * 
     * @param password La password in chiaro da cifrare.
     * @return La stringa esadecimale rappresentante l'hash SHA-256.
     */
    public static String cifraPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Errore cifratura password", e);
        }
    }

    /**
     * Verifica se la password fornita in chiaro corrisponde a quella cifrata salvata.
     * 
     * @param passwordChiara La password in chiaro da verificare.
     * @return true se corrisponde, false altrimenti.
     */
    public boolean verificaPassword(String passwordChiara) {
        return cifraPassword(passwordChiara).equals(this.passwordCifrata);
    }

    /**
     * Aggiunge un ristorante alla lista dei preferiti dell'utente.
     * 
     * @param nomeRistorante Nome del ristorante da aggiungere.
     * @return true se aggiunto con successo, false se già presente.
     */
    public boolean aggiungiPreferito(String nomeRistorante) {
        return preferiti.add(nomeRistorante);
    }

    /**
     * Rimuove un ristorante dalla lista dei preferiti dell'utente.
     * 
     * @param nomeRistorante Nome del ristorante da rimuovere.
     * @return true se rimosso con successo, false altrimenti.
     */
    public boolean rimuoviPreferito(String nomeRistorante) {
        return preferiti.remove(nomeRistorante);
    }

    /**
     * Ritorna una copia dell'insieme dei ristoranti preferiti dell'utente.
     * 
     * @return Un Set di stringhe contenente i preferiti.
     */
    public Set<String> getPreferiti() {
        return new HashSet<>(preferiti);
    }

    @Override
    public String toString() {
        return String.format("%s %s (@%s) [%s]", nome, cognome, username, ruolo);
    }
}
