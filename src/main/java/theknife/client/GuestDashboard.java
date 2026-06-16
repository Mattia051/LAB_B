package theknife.client;

import theknife.common.Recensione;
import theknife.common.Risposta;
import theknife.common.Ristorante;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GuestDashboard extends JFrame {

    private ClientTK client;
    private JEditorPane displayArea;
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;

    public GuestDashboard(ClientTK client) {
        this.client = client;

        setTitle("The Knife - Ospite");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Esplora Ristoranti (Ospite)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Display Area
        displayArea = new JEditorPane();
        displayArea.setContentType("text/html");
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        add(scrollPane, BorderLayout.CENTER);

        // Pannello menu a sinistra
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(BorderFactory.createTitledBorder("Menu Ospite"));
        add(menuPanel, BorderLayout.WEST);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Search Type
        gbc.gridy = 0;
        menuPanel.add(new JLabel("Tipo di Ricerca:"), gbc);
        gbc.gridy = 1;
        searchTypeComboBox = new JComboBox<>(new String[]{"Località", "Ricerca Avanzata"});
        menuPanel.add(searchTypeComboBox, gbc);

        // Search Term
        gbc.gridy = 2;
        menuPanel.add(new JLabel("Termine (per Località):"), gbc);
        gbc.gridy = 3;
        searchField = new JTextField(15);
        menuPanel.add(searchField, gbc);

        // Search Button
        gbc.gridy = 4;
        JButton searchButton = new JButton("Cerca");
        styleButton(searchButton);
        menuPanel.add(searchButton, gbc);

        // View Reviews Button
        gbc.gridy = 5;
        JButton viewReviewsButton = new JButton("Vedi Recensioni");
        styleButton(viewReviewsButton);
        menuPanel.add(viewReviewsButton, gbc);

        searchButton.addActionListener(e -> performSearch());
        
        viewReviewsButton.addActionListener(e -> {
            String restaurantName = JOptionPane.showInputDialog(this, "Inserisci il nome del ristorante:");
            if (restaurantName != null && !restaurantName.trim().isEmpty()) {
                displayReviewsForRestaurant(restaurantName.trim());
            }
        });

        displayArea.setText("<html><body style='font-family: sans-serif; padding: 10px;'><p>Benvenuto! Usa il menu a sinistra per esplorare i ristoranti.</p></body></html>");
    }

    public GuestDashboard(ClientTK client, String defaultLocation) {
        this(client);
        searchField.setText(defaultLocation);
        performSearch();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
    }

    private void performSearch() {
        String type = (String) searchTypeComboBox.getSelectedItem();
        String term = searchField.getText().trim();

        if (type.equals("Località")) {
            Risposta res = client.cercaRistoranti(term, "");
            mostraRisultati((List<Ristorante>) res.getDati());
        } else {
            // Ricerca combinata (Popup)
            performCombinedSearch();
        }
    }

    private void mostraRisultati(List<Ristorante> risultati) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif; padding: 10px;'><h2>Risultati:</h2>");
        if (risultati.isEmpty()) {
            sb.append("<p>Nessun ristorante trovato.</p>");
        } else {
            for (Ristorante r : risultati) {
                sb.append("<div style='border-bottom: 1px solid #ccc; padding: 10px 0;'>");
                sb.append("<h3>").append(r.getNome()).append("</h3>");
                sb.append("<p><strong>Cucina:</strong> ").append(r.getCucina()).append("<br>");
                sb.append("<strong>Località:</strong> ").append(r.getLocation()).append("<br>");
                sb.append("<strong>Green Star:</strong> ").append(r.getGreenStar()).append("</p></div>");
            }
        }
        sb.append("</body></html>");
        displayArea.setText(sb.toString());
    }

    private void displayReviewsForRestaurant(String restaurantName) {
        Risposta res = client.getRecensioni(restaurantName);
        List<Recensione> reviews = (List<Recensione>) res.getDati();
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: sans-serif; padding: 10px;'><h2>Recensioni per ").append(restaurantName).append("</h2>");
        if (reviews.isEmpty()) {
            sb.append("<p>Nessuna recensione trovata.</p>");
        } else {
            for (Recensione r : reviews) {
                sb.append("<p><strong>").append(r.getUsernameUtente()).append("</strong>: ").append(r.getStelle()).append(" stelle<br>");
                sb.append("<em>").append(r.getTesto()).append("</em></p><hr>");
            }
        }
        sb.append("</body></html>");
        displayArea.setText(sb.toString());
    }

    private void performCombinedSearch() {
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
        
        int result = JOptionPane.showConfirmDialog(this, p, "Ricerca Avanzata", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            java.util.Map<String, String> filtri = new java.util.HashMap<>();
            filtri.put("nome", nameField.getText().trim());
            filtri.put("location", locField.getText().trim());
            filtri.put("cucina", cucField.getText().trim());
            filtri.put("fasciaPrezzo", (String) priceBox.getSelectedItem());
            filtri.put("servizi", serviceField.getText().trim());

            Risposta res = client.cercaRistoranti(filtri);
            mostraRisultati((List<Ristorante>) res.getDati());
        }
    }
}
