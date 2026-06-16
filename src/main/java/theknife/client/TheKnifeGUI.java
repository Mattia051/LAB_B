/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.client;

import theknife.common.Risposta;
import theknife.common.Utente;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * La classe TheKnifeGUI rappresenta l'interfaccia grafica principale dell'applicazione "The Knife".
 * Gestisce le schermate iniziali di accesso (Login), registrazione (Sign up) 
 * e l'accesso come ospite anonimo, indirizzando ciascun utente alle rispettive 
 * dashboard di pertinenza.
 */
public class TheKnifeGUI extends JFrame {

    /** Riferimento al manager di comunicazione client di rete. */
    private ClientTK client;
    
    /** Campo di testo per l'inserimento dell'username. */
    private JTextField usernameField;
    
    /** Campo per l'inserimento della password (mascherata). */
    private JPasswordField passwordField;
    
    /** Area di testo per la visualizzazione di messaggi informativi o di errore. */
    private JEditorPane messageArea;

    /**
     * Costruisce ed inizializza la schermata principale di accesso del client.
     * 
     * @param client L'istanza ClientTK per inviare richieste di rete.
     */
    public TheKnifeGUI(ClientTK client) {
        this.client = client;
        setTitle("The Knife - Accesso");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Pannello principale con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        // Titolo "THE KNIFE"
        JLabel titleLabel = new JLabel("THE KNIFE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(139, 69, 19));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Pannello centrale per input e bottoni
        JPanel centerPanel = new JPanel(new GridBagLayout());
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        centerPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        centerPanel.add(passwordField, gbc);

        // Pannello bottoni
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton loginButton = new JButton("Accedi");
        styleButton(loginButton);
        buttonPanel.add(loginButton);

        JButton registerButton = new JButton("Registrati");
        styleButton(registerButton);
        buttonPanel.add(registerButton);

        JButton guestButton = new JButton("Ospite");
        styleButton(guestButton);
        buttonPanel.add(guestButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(buttonPanel, gbc);

        // Message Area
        messageArea = new JEditorPane();
        messageArea.setContentType("text/html");
        messageArea.setEditable(false);
        messageArea.setBackground(UIManager.getColor("Panel.background"));
        messageArea.setText("<html><body style='font-family: sans-serif; padding: 10px;'><p>Inserire credenziali accesso o fare registrazione</p></body></html>");
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        centerPanel.add(scrollPane, gbc);

        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> showRegisterDialog());
        guestButton.addActionListener(e -> new GuestDashboard(client).setVisible(true));
    }

    /**
     * Applica uno stile grafico comune ad un JButton (sfondo, font, dimensioni).
     * 
     * @param button Il pulsante da stilizzare.
     */
    private void styleButton(JButton button) {
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(null);
        button.setPreferredSize(new Dimension(150, 40));
    }

    /**
     * Esegue l'azione di login prelevando i dati dai campi di testo.
     * Cifra la password e interroga il server: in caso di successo indirizza
     * alla dashboard corretta a seconda del ruolo dell'utente loggato.
     */
    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        Risposta res = client.login(username, password);

        if (res.isSuccesso()) {
            Utente loggedInUser = (Utente) res.getDati();
            messageArea.setText("<html><body style='font-family: sans-serif; padding: 10px;'><p>Accesso avvenuto con successo! Benvenuto, " + loggedInUser.getNome() + "</p></body></html>");
            
            if (loggedInUser.getRuolo().equalsIgnoreCase("cliente")) {
                new ClientDashboard(client, loggedInUser).setVisible(true);
                dispose();
            } else if (loggedInUser.getRuolo().equalsIgnoreCase("ristoratore")) {
                new RestaurateurDashboard(client, loggedInUser).setVisible(true);
                dispose();
            }
        } else {
            messageArea.setText("<html><body style='font-family: sans-serif; padding: 10px; color: red;'><p>" + res.getMessaggio() + "</p></body></html>");
        }
    }

    /**
     * Mostra una finestra di dialogo (JDialog) pop-up per la registrazione 
     * di un nuovo utente, acquisendo i dati anagrafici, domicilio e ruolo.
     */
    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "Registra Nuovo Utente", true);
        registerDialog.setSize(450, 450);
        registerDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        registerDialog.add(panel);

        JTextField regNameField = new JTextField();
        JTextField regSurnameField = new JTextField();
        JTextField regUsernameField = new JTextField();
        JPasswordField regPasswordField = new JPasswordField();
        JTextField regBirthDateField = new JTextField();
        JTextField regDomicileField = new JTextField();
        JComboBox<String> regRoleComboBox = new JComboBox<>(new String[]{"cliente", "ristoratore"});

        panel.add(new JLabel("Nome:"));
        panel.add(regNameField);
        panel.add(new JLabel("Cognome:"));
        panel.add(regSurnameField);
        panel.add(new JLabel("Username:"));
        panel.add(regUsernameField);
        panel.add(new JLabel("Password:"));
        panel.add(regPasswordField);
        panel.add(new JLabel("Data di Nascita:"));
        panel.add(regBirthDateField);
        panel.add(new JLabel("Domicilio:"));
        panel.add(regDomicileField);
        panel.add(new JLabel("Ruolo:"));
        panel.add(regRoleComboBox);

        JButton registerSubmitButton = new JButton("Registrati");
        styleButton(registerSubmitButton);
        panel.add(registerSubmitButton);

        registerSubmitButton.addActionListener(e -> {
            String name = regNameField.getText();
            String surname = regSurnameField.getText();
            String username = regUsernameField.getText();
            String password = new String(regPasswordField.getPassword());
            String birthDate = regBirthDateField.getText();
            String domicile = regDomicileField.getText();
            String role = (String) regRoleComboBox.getSelectedItem();

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || domicile.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "Compila tutti i campi richiesti.", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Utente newUser = new Utente(name, surname, username, password, birthDate, domicile, role);
            Risposta res = client.registra(newUser);
            if (res.isSuccesso()) {
                JOptionPane.showMessageDialog(registerDialog, "Registrazione avvenuta con successo!", "Successo", JOptionPane.INFORMATION_MESSAGE);
                registerDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(registerDialog, res.getMessaggio(), "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerDialog.setVisible(true);
    }
}
