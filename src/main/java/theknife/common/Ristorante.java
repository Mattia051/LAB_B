/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.common;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * La classe Ristorante rappresenta un ristorante all'interno del sistema "The Knife".
 * Contiene i dati descrittivi del locale (nome, indirizzo, tipologia di cucina, ecc.),
 * le coordinate geografiche, i dettagli sui servizi offerti (come asporto e prenotazioni)
 * e l'username del proprietario ristoratore.
 * Implementa Serializable per consentire l'invio su rete.
 */
public class Ristorante implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Nome univoco del ristorante. */
    private String nome;
    
    /** Indirizzo stradale del ristorante. */
    private String indirizzo;
    
    /** Località geografica (città/comune) in cui si trova il ristorante. */
    private String location;
    
    /** Fascia di prezzo del ristorante (es. €, €€, €€€, €€€€). */
    private String fasciaPrezzo;
    
    /** Tipologia di cucina offerta (es. Italiana, Giapponese, Vegana). */
    private String cucina;
    
    /** Longitudine geografica della posizione del ristorante. */
    private double longitude;
    
    /** Latitudine geografica della posizione del ristorante. */
    private double latitude;
    
    /** Numero di telefono del ristorante. */
    private String telefono;
    
    /** URL di riferimento generico per il locale. */
    private String url;
    
    /** URL del sito web ufficiale del ristorante. */
    private String websiteUrl;
    
    /** Riconoscimenti o premi vinti (es. Stella Michelin). */
    private String award;
    
    /** Eventuale presenza della Green Star (Stella Verde Michelin per la sostenibilità). */
    private String greenStar;
    
    /** Stringa descrittiva contenente l'elenco dei servizi (es. "delivery, prenotazione"). */
    private String servizi;
    
    /** Descrizione testuale estesa del ristorante. */
    private String descrizione;
    
    /** Username del ristoratore che possiede ed amministra il locale nel sistema. */
    private String proprietario;

    /**
     * Costruttore completo per inizializzare un oggetto Ristorante.
     * 
     * @param nome         Nome univoco.
     * @param indirizzo    Indirizzo.
     * @param location     Città o località.
     * @param fasciaPrezzo Fascia di costo.
     * @param cucina       Tipo di cucina.
     * @param longitude    Longitudine.
     * @param latitude     Latitudine.
     * @param telefono     Telefono.
     * @param url          URL generico.
     * @param websiteUrl   Sito web ufficiale.
     * @param award        Premi o riconoscimenti.
     * @param greenStar    Certificazione Green Star.
     * @param servizi      Servizi aggiuntivi separati da virgola.
     * @param descrizione  Descrizione del locale.
     * @param proprietario Username del ristoratore proprietario.
     */
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

    /** @return il nome del ristorante. */
    public String getNome() { return nome; }
    
    /** @return l'indirizzo del ristorante. */
    public String getIndirizzo() { return indirizzo; }
    
    /** @return la località geografica. */
    public String getLocation() { return location; }
    
    /** @return la fascia di prezzo. */
    public String getFasciaPrezzo() { return fasciaPrezzo; }
    
    /** @return la cucina offerta. */
    public String getCucina() { return cucina; }
    
    /** @return la longitudine. */
    public double getLongitude() { return longitude; }
    
    /** @return la latitudine. */
    public double getLatitude() { return latitude; }
    
    /** @return il numero di telefono. */
    public String getTelefono() { return telefono; }
    
    /** @return l'URL del ristorante. */
    public String getUrl() { return url; }
    
    /** @return l'URL del sito web. */
    public String getWebsiteUrl() { return websiteUrl; }
    
    /** @return i riconoscimenti ricevuti. */
    public String getAward() { return award; }
    
    /** @return la presenza della Green Star. */
    public String getGreenStar() { return greenStar; }
    
    /** @return l'elenco dei servizi sotto forma di stringa. */
    public String getServizi() { return servizi; }
    
    /** @return la descrizione estesa. */
    public String getDescrizione() { return descrizione; }
    
    /** @return l'username del proprietario. */
    public String getProprietario() { return proprietario; }

    /**
     * Imposta il proprietario ristoratore del locale.
     * 
     * @param proprietario Username del ristoratore.
     */
    public void setProprietario(String proprietario) { this.proprietario = proprietario; }

    /**
     * Verifica se il ristorante offre il servizio di asporto (delivery) 
     * controllando l'attributo servizi.
     * 
     * @return true se il servizio è disponibile, false altrimenti.
     */
    public boolean isDeliveryDisponibile() {
        return servizi != null && servizi.toLowerCase().contains("delivery");
    }

    /**
     * Verifica se il ristorante supporta le prenotazioni del tavolo online 
     * controllando l'attributo servizi.
     * 
     * @return true se disponibile, false altrimenti.
     */
    public boolean isPrenotazioneOnlineDisponibile() {
        return servizi != null && servizi.toLowerCase().contains("prenotazione");
    }

    /**
     * Calcola la media aritmetica dei punteggi delle stelle ricevuti da una 
     * determinata lista di recensioni passata come parametro.
     * 
     * @param recensioni Lista di recensioni del ristorante.
     * @return Il valore medio delle stelle (double), 0.0 se non ci sono recensioni.
     */
    public double calcolaMediaStelle(List<Recensione> recensioni) {
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
