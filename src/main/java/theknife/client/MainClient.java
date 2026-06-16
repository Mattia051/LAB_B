package theknife.client;

import javax.swing.*;

/**
 * MainClient è la classe d'ingresso principale per l'utente finale.
 * Avvia immediatamente l'interfaccia grafica (GUI) di The Knife.
 */
public class MainClient {
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
            // Crea l'istanza del client per la rete sulla porta 12345
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
