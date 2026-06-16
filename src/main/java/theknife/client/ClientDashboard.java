package theknife.client;

import theknife.common.Recensione;
import theknife.common.Risposta;
import theknife.common.Ristorante;
import theknife.common.Utente;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClientDashboard extends JFrame {
    private ClientTK client;
    private Utente loggedInUser;
    private JEditorPane displayArea;

    public ClientDashboard(ClientTK client, Utente user) {
        this.client = client;
        this.loggedInUser = user;

        setTitle("The Knife - Dashboard Cliente - Benvenuto, " + loggedInUser.getNome());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu Cliente"));
        mainPanel.add(menuPanel, BorderLayout.WEST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JButton addReviewButton = new JButton("Aggiungi Recensione");
        JButton modifyReviewButton = new JButton("Modifica Recensione");
        JButton deleteReviewButton = new JButton("Elimina Recensione");
        JButton viewMyReviewsButton = new JButton("Vedi Tue Recensioni");
        JButton addFavoriteButton = new JButton("Aggiungi Preferito");
        JButton removeFavoriteButton = new JButton("Rimuovi Preferito");
        JButton viewFavoritesButton = new JButton("Ristoranti Preferiti");
        JButton searchRestaurantsButton = new JButton("Cerca Ristoranti");
        JButton logoutButton = new JButton("Logout");

        styleButton(addReviewButton);
        styleButton(modifyReviewButton);
        styleButton(deleteReviewButton);
        styleButton(viewMyReviewsButton);
        styleButton(addFavoriteButton);
        styleButton(removeFavoriteButton);
        styleButton(viewFavoritesButton);
        styleButton(searchRestaurantsButton);
        styleButton(logoutButton);

        gbc.gridy = 0; menuPanel.add(addReviewButton, gbc);
        gbc.gridy = 1; menuPanel.add(modifyReviewButton, gbc);
        gbc.gridy = 2; menuPanel.add(deleteReviewButton, gbc);
        gbc.gridy = 3; menuPanel.add(viewMyReviewsButton, gbc);
        gbc.gridy = 4; menuPanel.add(addFavoriteButton, gbc);
        gbc.gridy = 5; menuPanel.add(removeFavoriteButton, gbc);
        gbc.gridy = 6; menuPanel.add(viewFavoritesButton, gbc);
        gbc.gridy = 7; menuPanel.add(searchRestaurantsButton, gbc);
        gbc.gridy = 8; menuPanel.add(logoutButton, gbc);

        displayArea = new JEditorPane();
        displayArea.setContentType("text/html");
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        addReviewButton.addActionListener(e -> aggiungiRecensione());
        modifyReviewButton.addActionListener(e -> modificaRecensione());
        deleteReviewButton.addActionListener(e -> eliminaRecensione());
        viewMyReviewsButton.addActionListener(e -> visualizzaMieRecensioni());
        addFavoriteButton.addActionListener(e -> aggiungiPreferito());
        removeFavoriteButton.addActionListener(e -> rimuoviPreferito());
        viewFavoritesButton.addActionListener(e -> visualizzaPreferiti());
        searchRestaurantsButton.addActionListener(e -> cercaRistoranti());
        logoutButton.addActionListener(e -> eseguiLogout());

        displayArea.setText("<html><body style='font-family: sans-serif; padding: 10px;'><h2>Benvenuto, " + loggedInUser.getNome() + "!</h2><p>Seleziona un'opzione dal menu.</p></body></html>");
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
    }

    private void mostraListaRistoranti(List<Ristorante> ristoranti, String titolo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif; padding: 10px;'>");
        sb.append("<h2>").append(titolo).append("</h2>");
        if (ristoranti.isEmpty()) {
            sb.append("<p>Nessun ristorante trovato.</p>");
        } else {
            for (Ristorante r : ristoranti) {
                sb.append("<div style='border-bottom: 1px solid #ccc; padding: 10px 0;'>");
                sb.append("<h3>").append(r.getNome()).append("</h3>");
                sb.append("<p><strong>Cucina:</strong> ").append(r.getCucina()).append("<br>");
                sb.append("<strong>Località:</strong> ").append(r.getLocation()).append("<br>");
                sb.append("<strong>Green Star:</strong> ").append(r.getGreenStar()).append("<br>");
                sb.append("<strong>Telefono:</strong> ").append(r.getTelefono()).append("</p>");
                sb.append("</div>");
            }
        }
        sb.append("</body></html>");
        displayArea.setText(sb.toString());
    }

    private void aggiungiRecensione() {
        String restaurantName = JOptionPane.showInputDialog(this, "Nome Ristorante:");
        if (restaurantName == null || restaurantName.isEmpty()) return;

        String starsStr = JOptionPane.showInputDialog(this, "Stelle (1-5):");
        if (starsStr == null) return;
        int stars = Integer.parseInt(starsStr);

        String text = JOptionPane.showInputDialog(this, "Commento:");
        if (text == null) return;

        Risposta res = client.aggiungiRecensione(loggedInUser.getUsername(), restaurantName, stars, text);
        JOptionPane.showMessageDialog(this, res.getMessaggio());
    }

    private void modificaRecensione() {
        Risposta resList = client.getRecensioniUtente(loggedInUser.getUsername());
        List<Recensione> userReviews = (List<Recensione>) resList.getDati();
        
        if (userReviews.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non hai recensioni da modificare.");
            return;
        }

        String[] options = userReviews.stream().map(Recensione::getNomeRistorante).toArray(String[]::new);
        String selection = (String) JOptionPane.showInputDialog(this, "Scegli ristorante:", "Modifica", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (selection != null) {
            String starsStr = JOptionPane.showInputDialog(this, "Nuove stelle (1-5):");
            int stars = Integer.parseInt(starsStr);
            String text = JOptionPane.showInputDialog(this, "Nuovo commento:");
            Risposta res = client.modificaRecensione(loggedInUser.getUsername(), selection, stars, text);
            JOptionPane.showMessageDialog(this, res.getMessaggio());
        }
    }

    private void eliminaRecensione() {
        Risposta resList = client.getRecensioniUtente(loggedInUser.getUsername());
        List<Recensione> userReviews = (List<Recensione>) resList.getDati();
        
        if (userReviews.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non hai recensioni da eliminare.");
            return;
        }

        String[] options = userReviews.stream().map(Recensione::getNomeRistorante).toArray(String[]::new);
        String selection = (String) JOptionPane.showInputDialog(this, "Scegli ristorante:", "Elimina", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (selection != null) {
            Risposta res = client.cancellaRecensione(loggedInUser.getUsername(), selection);
            JOptionPane.showMessageDialog(this, res.getMessaggio());
        }
    }

    private void visualizzaMieRecensioni() {
        Risposta res = client.getRecensioniUtente(loggedInUser.getUsername());
        List<Recensione> userReviews = (List<Recensione>) res.getDati();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif; padding: 10px;'><h2>Le Tue Recensioni</h2>");
        for (Recensione rec : userReviews) {
            sb.append("<p><strong>").append(rec.getNomeRistorante()).append("</strong>: ").append(rec.getStelle()).append(" stelle<br>");
            sb.append("<em>").append(rec.getTesto()).append("</em></p>");
            if (rec.getRisposta() != null) sb.append("<p style='color:blue;'>Risposta: ").append(rec.getRisposta()).append("</p>");
            sb.append("<hr>");
        }
        sb.append("</body></html>");
        displayArea.setText(sb.toString());
    }

    private void aggiungiPreferito() {
        String name = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (name != null) {
            Risposta res = client.aggiungiPreferito(loggedInUser.getUsername(), name);
            JOptionPane.showMessageDialog(this, res.getMessaggio());
        }
    }

    private void rimuoviPreferito() {
        String name = JOptionPane.showInputDialog(this, "Nome ristorante:");
        if (name != null) {
            Risposta res = client.rimuoviPreferito(loggedInUser.getUsername(), name);
            JOptionPane.showMessageDialog(this, res.getMessaggio());
        }
    }

    private void visualizzaPreferiti() {
        Risposta res = client.getPreferiti(loggedInUser.getUsername());
        List<String> prefs = (List<String>) res.getDati();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif; padding: 10px;'><h2>I Tuoi Preferiti</h2><ul>");
        for (String p : prefs) sb.append("<li>").append(p).append("</li>");
        sb.append("</ul></body></html>");
        displayArea.setText(sb.toString());
    }

    private void cercaRistoranti() {
        JTextField nameField = new JTextField();
        JTextField locField = new JTextField();
        JTextField cucField = new JTextField();
        JComboBox<String> priceBox = new JComboBox<>(new String[]{"", "€", "€€", "€€€", "€€€€"});
        JTextField serviceField = new JTextField();

        JPanel p = new JPanel(new GridLayout(0, 2, 5, 5));
        p.add(new JLabel("Nome Ristorante:")); p.add(nameField);
        p.add(new JLabel("Località:")); p.add(locField);
        p.add(new JLabel("Cucina:")); p.add(cucField);
        p.add(new JLabel("Fascia Prezzo:")); p.add(priceBox);
        p.add(new JLabel("Servizi (es: delivery):")); p.add(serviceField);
        
        int result = JOptionPane.showConfirmDialog(this, p, "Ricerca Ristoranti", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            java.util.Map<String, String> filtri = new java.util.HashMap<>();
            filtri.put("nome", nameField.getText().trim());
            filtri.put("location", locField.getText().trim());
            filtri.put("cucina", cucField.getText().trim());
            filtri.put("fasciaPrezzo", (String) priceBox.getSelectedItem());
            filtri.put("servizi", serviceField.getText().trim());

            Risposta res = client.cercaRistoranti(filtri);
            mostraListaRistoranti((List<Ristorante>) res.getDati(), "Risultati Ricerca");
        }
    }

    private void eseguiLogout() {
        dispose();
        new TheKnifeGUI(client).setVisible(true);
    }
}
