package theknife.common;

import java.io.Serializable;

/**
 * La classe Ristorante rappresenta un ristorante nel sistema The Knife.
 * Implementa Serializable per permettere l'invio su rete.
 */
public class Ristorante implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String indirizzo;
    private String location;
    private String fasciaPrezzo;
    private String cucina;
    private double longitude;
    private double latitude;
    private String telefono;
    private String url;
    private String websiteUrl;
    private String award;
    private String greenStar;
    private String servizi;
    private String descrizione;
    private String proprietario;

    public Ristorante(String nome, String indirizzo, String location, String fasciaPrezzo,
                      String cucina, double longitude, double latitude, String telefono,
                      String url, String websiteUrl, String award, String greenStar,
                      String servizi, String descrizione, String proprietario) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.location = location;
        this.fasciaPrezzo = fasciaPrezzo;
        this.cucina = cucina;
        this.longitude = longitude;
        this.latitude = latitude;
        this.telefono = telefono;
        this.url = url;
        this.websiteUrl = websiteUrl;
        this.award = award;
        this.greenStar = greenStar;
        this.servizi = servizi;
        this.descrizione = descrizione;
        this.proprietario = proprietario;
    }

    // --- Getter e Setter ---

    public String getNome() { return nome; }
    public String getIndirizzo() { return indirizzo; }
    public String getLocation() { return location; }
    public String getFasciaPrezzo() { return fasciaPrezzo; }
    public String getCucina() { return cucina; }
    public double getLongitude() { return longitude; }
    public double getLatitude() { return latitude; }
    public String getTelefono() { return telefono; }
    public String getUrl() { return url; }
    public String getWebsiteUrl() { return websiteUrl; }
    public String getAward() { return award; }
    public String getGreenStar() { return greenStar; }
    public String getServizi() { return servizi; }
    public String getDescrizione() { return descrizione; }
    public String getProprietario() { return proprietario; }

    public void setProprietario(String proprietario) { this.proprietario = proprietario; }

    // --- Metodi dal vecchio progetto ---

    public boolean isDeliveryDisponibile() {
        return servizi != null && servizi.toLowerCase().contains("delivery");
    }

    public boolean isPrenotazioneOnlineDisponibile() {
        return servizi != null && servizi.toLowerCase().contains("prenotazione");
    }

    /**
     * Calcola la media delle stelle da una lista di recensioni.
     */
    public double calcolaMediaStelle(java.util.List<Recensione> recensioni) {
        if (recensioni == null || recensioni.isEmpty()) return 0.0;
        double somma = 0;
        for (Recensione r : recensioni) {
            somma += r.getStelle();
        }
        return somma / recensioni.size();
    }

    @Override
    public String toString() {
        return nome + " (" + location + ") - " + cucina;
    }
}
