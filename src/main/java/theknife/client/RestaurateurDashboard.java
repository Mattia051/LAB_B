package theknife.client;

import theknife.common.Recensione;
import theknife.common.Risposta;
import theknife.common.Ristorante;
import theknife.common.Utente;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RestaurateurDashboard extends JFrame {
    private ClientTK client;
    private Utente loggedInUser;
    private JEditorPane displayArea;

    public RestaurateurDashboard(ClientTK client, Utente user) {
        this.client = client;
        this.loggedInUser = user;

        setTitle("The Knife - Dashboard Ristoratore - Benvenuto, " + loggedInUser.getNome());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel);

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu Ristoratore"));
        mainPanel.add(menuPanel, BorderLayout.WEST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JButton viewMyRestaurantsButton = new JButton("I Miei Ristoranti");
        JButton addRestaurantButton = new JButton("Nuovo Ristorante");
        JButton viewRestaurantReviewsButton = new JButton("Visualizza Recensioni");
        JButton replyToReviewButton = new JButton("Rispondi a Recensione");
        JButton logoutButton = new JButton("Logout");

        styleButton(viewMyRestaurantsButton);
        styleButton(addRestaurantButton);
        styleButton(viewRestaurantReviewsButton);
        styleButton(replyToReviewButton);
        styleButton(logoutButton);

        gbc.gridy = 0; menuPanel.add(viewMyRestaurantsButton, gbc);
        gbc.gridy = 1; menuPanel.add(addRestaurantButton, gbc);
        gbc.gridy = 2; menuPanel.add(viewRestaurantReviewsButton, gbc);
        gbc.gridy = 3; menuPanel.add(replyToReviewButton, gbc);
        gbc.gridy = 4; menuPanel.add(logoutButton, gbc);

        displayArea = new JEditorPane();
        displayArea.setContentType("text/html");
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        viewMyRestaurantsButton.addActionListener(e -> visualizzaMieiRistoranti());
        addRestaurantButton.addActionListener(e -> aggiungiRistorante());
        viewRestaurantReviewsButton.addActionListener(e -> visualizzaRecensioniRistorante());
        replyToReviewButton.addActionListener(e -> rispondiARecensione());
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

    private void visualizzaMieiRistoranti() {
        Risposta res = client.getMieiRistoranti(loggedInUser.getUsername());
        List<Ristorante> miei = (List<Ristorante>) res.getDati();
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif; padding: 10px;'><h2>I Tuoi Ristoranti</h2>");
        if (miei.isEmpty()) {
            sb.append("<p>Nessun ristorante registrato.</p>");
        } else {
            for (Ristorante r : miei) {
                sb.append("<div style='border-bottom: 1px solid #ccc; padding: 10px 0;'>");
                sb.append("<h3>").append(r.getNome()).append("</h3>");
                sb.append("<p><strong>Indirizzo:</strong> ").append(r.getIndirizzo()).append(", ").append(r.getLocation()).append("<br>");
                sb.append("<strong>Cucina:</strong> ").append(r.getCucina()).append("</p></div>");
            }
        }
        sb.append("</body></html>");
        displayArea.setText(sb.toString());
    }

    private void aggiungiRistorante() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField cuisineField = new JTextField();
        JTextField phoneField = new JTextField();
        String[] prices = {"€", "€€", "€€€", "€€€€"};
        JComboBox<String> priceBox = new JComboBox<>(prices);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nome:")); panel.add(nameField);
        panel.add(new JLabel("Indirizzo:")); panel.add(addressField);
        panel.add(new JLabel("Località:")); panel.add(locationField);
        panel.add(new JLabel("Cucina:")); panel.add(cuisineField);
        panel.add(new JLabel("Telefono:")); panel.add(phoneField);
        panel.add(new JLabel("Fascia Prezzo:")); panel.add(priceBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Nuovo Ristorante", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Ristorante r = new Ristorante(nameField.getText(), addressField.getText(), locationField.getText(), (String)priceBox.getSelectedItem(),
                    cuisineField.getText(), 0, 0, phoneField.getText(), "", "", "", "", "", "", loggedInUser.getUsername());
            Risposta res = client.aggiungiRistorante(r);
            JOptionPane.showMessageDialog(this, res.getMessaggio());
        }
    }

    private void visualizzaRecensioniRistorante() {
        Risposta resMiei = client.getMieiRistoranti(loggedInUser.getUsername());
        List<Ristorante> miei = (List<Ristorante>) resMiei.getDati();
        if (miei.isEmpty()) return;

        String[] options = miei.stream().map(Ristorante::getNome).toArray(String[]::new);
        String selection = (String) JOptionPane.showInputDialog(this, "Scegli ristorante:", "Recensioni", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        
        if (selection != null) {
            Risposta resRec = client.getRecensioni(selection);
            List<Recensione> recs = (List<Recensione>) resRec.getDati();
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body style='font-family: sans-serif; padding: 10px;'><h2>Recensioni per ").append(selection).append("</h2>");
            for (Recensione rec : recs) {
                sb.append("<p><strong>").append(rec.getUsernameUtente()).append("</strong> (ID: ").append(rec.getId()).append("): ").append(rec.getStelle()).append(" stelle<br>");
                sb.append("<em>").append(rec.getTesto()).append("</em></p>");
                if (rec.getRisposta() != null) sb.append("<p style='color:blue;'>Tua Risposta: ").append(rec.getRisposta()).append("</p>");
                sb.append("<hr>");
            }
            sb.append("</body></html>");
            displayArea.setText(sb.toString());
        }
    }

    private void rispondiARecensione() {
        String idStr = JOptionPane.showInputDialog(this, "ID Recensione:");
        if (idStr != null) {
            int id = Integer.parseInt(idStr);
            String text = JOptionPane.showInputDialog(this, "Tua Risposta:");
            Risposta res = client.rispondiRecensione(id, text);
            JOptionPane.showMessageDialog(this, res.getMessaggio());
        }
    }

    private void eseguiLogout() {
        dispose();
        new TheKnifeGUI(client).setVisible(true);
    }
}
