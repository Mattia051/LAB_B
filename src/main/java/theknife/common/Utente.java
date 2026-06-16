package theknife.common;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;

/**
 * La classe Utente rappresenta un utente del sistema The Knife.
 * Implementa Serializable per permettere l'invio su rete.
 */
public class Utente implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cognome;
    private String username;
    private String passwordCifrata;
    private String dataNascita;
    private String domicilio;
    private String ruolo; // "cliente" o "ristoratore"
    private Set<String> preferiti = new HashSet<>();

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

    // Costruttore per caricamento da DB (password già cifrata)
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

    public String getNome() { return nome; }
    public String getCognome() { return cognome; }
    public String getUsername() { return username; }
    public String getPasswordCifrata() { return passwordCifrata; }
    public String getDataNascita() { return dataNascita; }
    public String getDomicilio() { return domicilio; }
    public String getRuolo() { return ruolo; }

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

    public boolean verificaPassword(String passwordChiara) {
        return cifraPassword(passwordChiara).equals(this.passwordCifrata);
    }

    public boolean aggiungiPreferito(String nomeRistorante) {
        return preferiti.add(nomeRistorante);
    }

    public boolean rimuoviPreferito(String nomeRistorante) {
        return preferiti.remove(nomeRistorante);
    }

    public Set<String> getPreferiti() {
        return new HashSet<>(preferiti);
    }

    @Override
    public String toString() {
        return String.format("%s %s (@%s) [%s]", nome, cognome, username, ruolo);
    }
}
