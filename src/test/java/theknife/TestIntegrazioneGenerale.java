package theknife;

import theknife.client.ClientTK;
import theknife.common.Risposta;
import theknife.common.Ristorante;
import theknife.server.ServerTK;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

public class TestIntegrazioneGenerale {
    public static void main(String[] args) {
        System.out.println("=== Inizio Test Integrazione Generale (Socket + DB) ===");

        // Backup di System.in
        InputStream originalSystemIn = System.in;

        // Simuliamo l'input dell'utente al ServerTK per la configurazione DB
        String simulatedInput = "localhost\npostgres\nPolpetta03!\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(bais);

        // Avviamo il ServerTK in un thread separato
        Thread serverThread = new Thread(() -> {
            try {
                ServerTK.main(new String[0]);
            } catch (Exception e) {
                System.out.println("[SERVER TEST] Errore Server: " + e.getMessage());
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();

        // Diamo al server il tempo di avviarsi e mettersi in ascolto sulla porta 12346
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ripristiniamo System.in
        System.setIn(originalSystemIn);

        // Avviamo il client e proviamo a connetterci
        ClientTK client = new ClientTK("localhost", 12346);
        if (client.connetti()) {
            System.out.println("[CLIENT TEST] Connesso al server correttamente.");

            // Test 1: Ricerca ristoranti a Milano
            System.out.println("[CLIENT TEST] Invio richiesta di ricerca per 'Milan'...");
            Risposta res = client.cercaRistoranti("Milan", "");
            
            if (res.isSuccesso()) {
                List<Ristorante> ristoranti = (List<Ristorante>) res.getDati();
                System.out.println("[CLIENT TEST] Successo! Trovati " + ristoranti.size() + " ristoranti.");
                if (!ristoranti.isEmpty()) {
                    System.out.println("[CLIENT TEST] Primo ristorante trovato: " + ristoranti.get(0).getNome());
                    System.out.println("[OK] Test di integrazione superato con successo!");
                } else {
                    System.out.println("[FALLITO] Nessun ristorante trovato nel DB.");
                }
            } else {
                System.out.println("[FALLITO] Ricerca fallita: " + res.getMessaggio());
            }

            // Disconnessione
            client.disconnetti();
            System.out.println("[CLIENT TEST] Disconnesso.");
        } else {
            System.out.println("[FALLITO] Impossibile connettersi al ServerTK sulla porta 12346.");
        }

        System.out.println("=== Fine Test Integrazione Generale ===");
        System.exit(0); // Forza la chiusura del server daemon e termina il processo di test
    }
}
