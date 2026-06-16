package theknife.server;

import theknife.common.Recensione;
import theknife.common.Ristorante;
import theknife.common.Utente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private String url;
    private String user;
    private String password;

    public DatabaseManager(String host, String user, String password) {
        this.url = "jdbc:postgresql://" + host + ":5432/theknife";
        this.user = user;
        this.password = password;

        try {
            // Carica il driver (opzionale nelle versioni recenti di JDBC)
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[ERRORE] Driver PostgreSQL non trovato.");
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

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
                        rs.getString("password"), // password cifrata
                        rs.getString("data_nascita"),
                        rs.getString("domicilio"),
                        rs.getString("ruolo"),
                        true // già cifrata
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

    public List<Ristorante> cercaRistoranti(java.util.Map<String, String> filtri) {
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

    public List<Ristorante> cercaRistoranti(String location, String cucina) {
        java.util.Map<String, String> f = new java.util.HashMap<>();
        f.put("location", location);
        f.put("cucina", cucina);
        return cercaRistoranti(f);
    }

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
