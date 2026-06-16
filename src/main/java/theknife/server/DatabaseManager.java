/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.server;

import theknife.common.Recensione;
import theknife.common.Ristorante;
import theknife.common.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * La classe DatabaseManager gestisce tutte le operazioni di persistenza 
 * verso il database relazionale PostgreSQL tramite JDBC.
 * Si occupa dell'apertura delle connessioni, dell'esecuzione di query sicure 
 * (tramite PreparedStatement) e dell'inserimento, aggiornamento o rimozione 
 * di record relativi a utenti, ristoranti, recensioni e preferiti.
 */
public class DatabaseManager {
    /** URL JDBC per la connessione al database PostgreSQL. */
    private String url;
    
    /** Username di accesso al DBMS. */
    private String user;
    
    /** Password di accesso al DBMS. */
    private String password;

    /**
     * Costruisce il gestore del database inizializzando l'URL JDBC del DBMS PostgreSQL.
     * Tenta inoltre di caricare esplicitamente il driver PostgreSQL.
     * 
     * @param host     Indirizzo host del database server (es. localhost).
     * @param user     Username del database.
     * @param password Password del database.
     */
    public DatabaseManager(String host, String user, String password) {
        this.url = "jdbc:postgresql://" + host + ":5432/theknife";
        this.user = user;
        this.password = password;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERRORE] Driver PostgreSQL non trovato.");
        }
    }

    /**
     * Stabilisce ed apre una nuova connessione con il database PostgreSQL.
     * 
     * @return L'oggetto Connection aperto.
     * @throws SQLException se si verifica un errore durante l'apertura.
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Registra un nuovo utente nel database eseguendo una query di INSERT.
     * 
     * @param u L'oggetto Utente da salvare (con password già cifrata).
     * @return true se l'inserimento avviene con successo, false altrimenti.
     */
    public boolean registraUtente(Utente u) {
        String sql = "INSERT INTO utenti (username, password, nome, cognome, data_nascita, domicilio, ruolo) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, u.getUsername());
            pstmt.setString(2, u.getPasswordCifrata());
            pstmt.setString(3, u.getNome());
            pstmt.setString(4, u.getCognome());
            pstmt.setString(5, u.getDataNascita());
            pstmt.setString(6, u.getDomicilio());
            pstmt.setString(7, u.getRuolo());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("[ERRORE DB] Registrazione fallita: " + e.getMessage());
            return false;
        }
    }

    /**
     * Esegue l'autenticazione dell'utente confrontando lo username e la password in chiaro.
     * Effettua la cifratura a runtime per confrontarla con l'hash presente nel DB.
     * 
     * @param username       Username inserito dall'utente.
     * @param passwordChiara Password inserita in chiaro dall'utente.
     * @return L'oggetto Utente loggato con i suoi dati in caso di successo, null altrimenti.
     */
    public Utente login(String username, String passwordChiara) {
        String sql = "SELECT * FROM utenti WHERE username = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Utente u = new Utente(
                        rs.getString("nome"),
                        rs.getString("cognome"),
                        rs.getString("username"),
                        rs.getString("password"), 
                        rs.getString("data_nascita"),
                        rs.getString("domicilio"),
                        rs.getString("ruolo"),
                        true 
                );
                if (u.verificaPassword(passwordChiara)) {
                    return u;
                } else {
                    System.out.println("[DB] Password errata per utente: " + username);
                }
            } else {
                System.out.println("[DB] Utente non trovato: " + username);
            }
        } catch (SQLException e) {
            System.err.println("[ERRORE DB] Login fallito: " + e.getMessage());
        }
        return null;
    }

    /**
     * Esegue una ricerca avanzata dei ristoranti applicando molteplici filtri in AND.
     * 
     * @param filtri Mappa chiave-valore contenente i filtri ("nome", "location", "cucina", "fasciaPrezzo", "servizi").
     * @return Una List di oggetti Ristorante corrispondenti ai criteri di ricerca.
     */
    public List<Ristorante> cercaRistoranti(Map<String, String> filtri) {
        List<Ristorante> risultati = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM ristoranti WHERE 1=1");
        List<String> params = new ArrayList<>();

        if (filtri.containsKey("nome") && !filtri.get("nome").isEmpty()) {
            sql.append(" AND nome ILIKE ?");
            params.add("%" + filtri.get("nome") + "%");
        }
        if (filtri.containsKey("location") && !filtri.get("location").isEmpty()) {
            sql.append(" AND location ILIKE ?");
            params.add("%" + filtri.get("location") + "%");
        }
        if (filtri.containsKey("cucina") && !filtri.get("cucina").isEmpty()) {
            sql.append(" AND cucina ILIKE ?");
            params.add("%" + filtri.get("cucina") + "%");
        }
        if (filtri.containsKey("fasciaPrezzo") && !filtri.get("fasciaPrezzo").isEmpty()) {
            sql.append(" AND fascia_prezzo = ?");
            params.add(filtri.get("fasciaPrezzo"));
        }
        if (filtri.containsKey("servizi") && !filtri.get("servizi").isEmpty()) {
            sql.append(" AND servizi ILIKE ?");
            params.add("%" + filtri.get("servizi") + "%");
        }

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setString(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                risultati.add(new Ristorante(
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("location"),
                        rs.getString("fascia_prezzo"),
                        rs.getString("cucina"),
                        rs.getDouble("longitude"),
                        rs.getDouble("latitude"),
                        rs.getString("telefono"),
                        rs.getString("url"),
                        rs.getString("website_url"),
                        rs.getString("award"),
                        rs.getString("green_star"),
                        rs.getString("servizi"),
                        rs.getString("descrizione"),
                        rs.getString("proprietario")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ERRORE DB] Ricerca avanzata fallita: " + e.getMessage());
        }
        return risultati;
    }

    /**
     * Esegue una ricerca base dei ristoranti per città (location) e tipologia di cucina.
     * 
     * @param location Località geografica.
     * @param cucina   Tipo di cucina.
     * @return Una List di oggetti Ristorante trovati.
     */
    public List<Ristorante> cercaRistoranti(String location, String cucina) {
        java.util.Map<String, String> f = new java.util.HashMap<>();
        f.put("location", location);
        f.put("cucina", cucina);
        return cercaRistoranti(f);
    }

    /**
     * Inserisce una nuova recensione scritta da un utente nel database.
     * 
     * @param r Oggetto Recensione da memorizzare.
     * @return true se inserita correttamente, false altrimenti.
     */
    public boolean aggiungiRecensione(Recensione r) {
        String sql = "INSERT INTO recensioni (username_utente, nome_ristorante, stelle, testo) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getUsernameUtente());
            pstmt.setString(2, r.getNomeRistorante());
            pstmt.setInt(3, r.getStelle());
            pstmt.setString(4, r.getTesto());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Consente ad un ristoratore di rispondere ad una specifica recensione identificata dal suo ID.
     * 
     * @param idRecensione ID della recensione a cui rispondere.
     * @param risposta     Il testo del messaggio di risposta.
     * @return true se aggiornata correttamente, false altrimenti.
     */
    public boolean rispondiRecensione(int idRecensione, String risposta) {
        String sql = "UPDATE recensioni SET risposta = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, risposta);
            pstmt.setInt(2, idRecensione);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permette ad un cliente di modificare una propria recensione inserita in precedenza per un ristorante.
     * Azzera automaticamente la risposta precedentemente fornita dal ristoratore.
     * 
     * @param username       Username dell'utente autore.
     * @param nomeRistorante Nome del locale recensito.
     * @param stelle         Nuovo valore in stelle.
     * @param testo          Nuovo commento testuale.
     * @return true se aggiornata con successo, false altrimenti.
     */
    public boolean modificaRecensione(String username, String nomeRistorante, int stelle, String testo) {
        String sql = "UPDATE recensioni SET stelle = ?, testo = ?, risposta = NULL WHERE username_utente = ? AND nome_ristorante = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, stelle);
            pstmt.setString(2, testo);
            pstmt.setString(3, username);
            pstmt.setString(4, nomeRistorante);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permette ad un cliente di cancellare una propria recensione dal database.
     * 
     * @param username       Username dell'utente.
     * @param nomeRistorante Nome del ristorante di cui eliminare la recensione.
     * @return true se eliminata con successo, false altrimenti.
     */
    public boolean cancellaRecensione(String username, String nomeRistorante) {
        String sql = "DELETE FROM recensioni WHERE username_utente = ? AND nome_ristorante = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, nomeRistorante);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera l'elenco di tutte le recensioni inserite da un determinato utente.
     * 
     * @param username Username dell'utente.
     * @return Una List di oggetti Recensione.
     */
    public List<Recensione> getRecensioniUtente(String username) {
        List<Recensione> recensioni = new ArrayList<>();
        String sql = "SELECT * FROM recensioni WHERE username_utente = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                recensioni.add(new Recensione(
                        rs.getInt("id"),
                        rs.getString("username_utente"),
                        rs.getString("nome_ristorante"),
                        rs.getInt("stelle"),
                        rs.getString("testo"),
                        rs.getString("risposta")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recensioni;
    }

    /**
     * Registra un nuovo ristorante nel database (inserito da un ristoratore).
     * 
     * @param r L'oggetto Ristorante da salvare.
     * @return true se inserito correttamente, false altrimenti.
     */
    public boolean aggiungiRistorante(Ristorante r) {
        String sql = "INSERT INTO ristoranti (nome, indirizzo, location, fascia_prezzo, cucina, longitude, latitude, telefono, url, website_url, award, green_star, servizi, descrizione, proprietario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, r.getNome());
            pstmt.setString(2, r.getIndirizzo());
            pstmt.setString(3, r.getLocation());
            pstmt.setString(4, r.getFasciaPrezzo());
            pstmt.setString(5, r.getCucina());
            pstmt.setDouble(6, r.getLongitude());
            pstmt.setDouble(7, r.getLatitude());
            pstmt.setString(8, r.getTelefono());
            pstmt.setString(9, r.getUrl());
            pstmt.setString(10, r.getWebsiteUrl());
            pstmt.setString(11, r.getAward());
            pstmt.setString(12, r.getGreenStar());
            pstmt.setString(13, r.getServizi());
            pstmt.setString(14, r.getDescrizione());
            pstmt.setString(15, r.getProprietario());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera l'elenco completo di tutti i ristoranti memorizzati nel database.
     * 
     * @return Una List di tutti gli oggetti Ristorante.
     */
    public List<Ristorante> getTuttiRistoranti() {
        List<Ristorante> risultati = new ArrayList<>();
        String sql = "SELECT * FROM ristoranti";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                risultati.add(new Ristorante(
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("location"),
                        rs.getString("fascia_prezzo"),
                        rs.getString("cucina"),
                        rs.getDouble("longitude"),
                        rs.getDouble("latitude"),
                        rs.getString("telefono"),
                        rs.getString("url"),
                        rs.getString("website_url"),
                        rs.getString("award"),
                        rs.getString("green_star"),
                        rs.getString("servizi"),
                        rs.getString("descrizione"),
                        rs.getString("proprietario")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultati;
    }

    /**
     * Inserisce un'associazione preferito collegando un utente ad un ristorante.
     * 
     * @param username       Username dell'utente cliente.
     * @param nomeRistorante Nome del ristorante da salvare nei preferiti.
     * @return true se l'inserimento è riuscito, false altrimenti.
     */
    public boolean aggiungiPreferito(String username, String nomeRistorante) {
        String sql = "INSERT INTO preferiti (username_utente, nome_ristorante) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, nomeRistorante);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rimuove un'associazione preferito per un determinato utente ed un ristorante.
     * 
     * @param username       Username dell'utente cliente.
     * @param nomeRistorante Nome del ristorante da rimuovere dai preferiti.
     * @return true se la rimozione è riuscita, false altrimenti.
     */
    public boolean rimuoviPreferito(String username, String nomeRistorante) {
        String sql = "DELETE FROM preferiti WHERE username_utente = ? AND nome_ristorante = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, nomeRistorante);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recupera l'elenco dei nomi dei ristoranti preferiti inseriti da un utente.
     * 
     * @param username Username dell'utente cliente.
     * @return Una List di stringhe rappresentanti i nomi dei ristoranti preferiti.
     */
    public List<String> getPreferiti(String username) {
        List<String> preferiti = new ArrayList<>();
        String sql = "SELECT nome_ristorante FROM preferiti WHERE username_utente = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                preferiti.add(rs.getString("nome_ristorante"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preferiti;
    }

    /**
     * Recupera l'elenco di tutte le recensioni relative ad un singolo ristorante.
     * 
     * @param nomeRistorante Nome del ristorante.
     * @return Una List di oggetti Recensione associati al locale.
     */
    public List<Recensione> getRecensioni(String nomeRistorante) {
        List<Recensione> recensioni = new ArrayList<>();
        String sql = "SELECT * FROM recensioni WHERE nome_ristorante = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomeRistorante);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                recensioni.add(new Recensione(
                        rs.getInt("id"),
                        rs.getString("username_utente"),
                        rs.getString("nome_ristorante"),
                        rs.getInt("stelle"),
                        rs.getString("testo"),
                        rs.getString("risposta")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recensioni;
    }

    /**
     * Recupera l'elenco dei ristoranti di proprietà inseriti da un determinato ristoratore.
     * 
     * @param username Username del ristoratore proprietario.
     * @return Una List di oggetti Ristorante associati al proprietario.
     */
    public List<Ristorante> getRistorantiProprietario(String username) {
        List<Ristorante> risultati = new ArrayList<>();
        String sql = "SELECT * FROM ristoranti WHERE proprietario = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                risultati.add(new Ristorante(
                        rs.getString("nome"),
                        rs.getString("indirizzo"),
                        rs.getString("location"),
                        rs.getString("fascia_prezzo"),
                        rs.getString("cucina"),
                        rs.getDouble("longitude"),
                        rs.getDouble("latitude"),
                        rs.getString("telefono"),
                        rs.getString("url"),
                        rs.getString("website_url"),
                        rs.getString("award"),
                        rs.getString("green_star"),
                        rs.getString("servizi"),
                        rs.getString("descrizione"),
                        rs.getString("proprietario")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultati;
    }
}
