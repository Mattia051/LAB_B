/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife.client;

import javax.swing.*;

/**
 * 
 * MainClient è il punto d'ingresso (entry point) per l'applicazione client finale.
 * Configura il look and feel grafico di sistema (preferendo Nimbus) e avvia il client 
 * connettendosi al ServerTK all'indirizzo localhost sulla porta 12346.
 * Se la connessione ha successo, mostra la finestra di autenticazione (TheKnifeGUI).
 */
public class MainClient {
    /**
     * Metodo di ingresso principale per l'esecuzione dell'applicazione Client.
     * 
     * @param args Argomenti passati da riga di comando (non utilizzati).
     */
    public static void main(String[] args) {
        // Imposta il Look and Feel di Nimbus per uno stile moderno
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se Nimbus non è disponibile, usa il Look and Feel di sistema
        }

        SwingUtilities.invokeLater(() -> {
            // Crea l'istanza del client per la rete sulla porta 12346
            ClientTK client = new ClientTK("localhost", 12346);
            
            // Prova a connettersi al ServerTK
            if (client.connetti()) {
                // Avvia la finestra di accesso
                new TheKnifeGUI(client).setVisible(true);
            } else {
                // Se il server è spento, avvisa l'utente
                JOptionPane.showMessageDialog(null, 
                    "Impossibile connettersi al ServerTK (Porta 12346).\nAssicurati che il server sia attivo prima di avviare il client.", 
                    "Errore di Connessione", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
