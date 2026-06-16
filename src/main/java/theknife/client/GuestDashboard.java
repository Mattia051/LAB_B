/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.client;

import theknife.common.Recensione;
import theknife.common.Risposta;
import theknife.common.Ristorante;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 
 * GuestDashboard rappresenta l'interfaccia grafica (JFrame) per gli utenti 
 * ospiti (non registrati).
 * Consente la consultazione delle informazioni dei ristoranti tramite 
 * ricerca per località o tramite ricerca avanzata (cucina, prezzo, servizi), 
 * e la visualizzazione anonima delle recensioni con relative risposte dei ristoratori.
 */
public class GuestDashboard extends JFrame {

    /** Riferimento al manager di comunicazione client di rete. */
    private ClientTK client;
    
    /** Area di testo HTML centrale per l'elenco dei ristoranti o delle recensioni. */
    private JEditorPane displayArea;
    
    /** Casella di testo per inserire la località di ricerca. */
    private JTextField searchField;
    
    /** Menu a tendina per scegliere la tipologia di ricerca (Località / Avanzata). */
    private JComboBox<String> searchTypeComboBox;

    /**
     * Costruisce ed inizializza l'interfaccia grafica della dashboard dell'ospite.
     * 
     * @param client L'istanza ClientTK per inviare richieste di rete.
     */
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

    /**
     * Costruttore secondario per avviare la dashboard dell'ospite pre-impostando
     * una località di ricerca iniziale.
     * 
     * @param client          L'istanza ClientTK per inviare richieste di rete.
     * @param defaultLocation Località geografica di partenza da cercare.
     */
    public GuestDashboard(ClientTK client, String defaultLocation) {
        this(client);
        searchField.setText(defaultLocation);
        performSearch();
    }

    /**
     * Applica uno stile grafico comune ad un JButton (colore, font, dimensioni).
     * 
     * @param button Il pulsante da stilizzare.
     */
    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(180, 40));
    }

    /**
     * Esegue la ricerca in base al tipo selezionato (Località o Ricerca Avanzata).
     */
    private void performSearch() {
        String type = (String) searchTypeComboBox.getSelectedItem();
        String term = searchField.getText().trim();

        if (type.equals("Località")) {
            Risposta res = client.cercaRistoranti(term, "");
            mostraRisultati((List<Ristorante>) res.getDati());
        } else {
            performCombinedSearch();
        }
    }

    /**
     * Disegna all'interno della schermata centrale l'elenco dei ristoranti trovati in HTML.
     * 
     * @param risultati Lista di ristoranti.
     */
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

    /**
     * Recupera le recensioni per un dato ristorante e le elenca nell'area centrale
     * (comprese le risposte dei ristoratori proprietari).
     * 
     * @param restaurantName Nome del ristorante di cui visualizzare le recensioni.
     */
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

    /**
     * Mostra un pannello pop-up di ricerca avanzata con più campi e invia 
     * i parametri del form per interrogare il server.
     */
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
