package theknife.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerTK {
    private static final int PORT = 12346;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Avvio Piattaforma TheKnife (Server) ===");
        System.out.println("[INFO] Controllo configurazione...");

        // 1. Richiesta configurazione DB all'avvio (come da specifiche)
        System.out.print("Inserisci l'host del database (es. localhost): ");
        String dbHost = scanner.nextLine();

        System.out.print("Inserisci l'username del DB (es. postgres): ");
        String dbUser = scanner.nextLine();

        System.out.print("Inserisci la password del DB: ");
        String dbPassword = scanner.nextLine();

        System.out.println("\n[!] Configurazione acquisita. Inizializzazione in corso...");
        DatabaseManager dbManager = new DatabaseManager(dbHost, dbUser, dbPassword);

        // 2. Avvio del Server in ascolto
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[OK] ServerTK in ascolto sulla porta " + PORT);
            // 3. Ciclo infinito per accettare connessioni multiple
            while (true) {
                // Il programma si "mette in pausa" qui finché un client non si connette
                Socket clientSocket = serverSocket.accept();
                System.out.println("[+] Nuovo client connesso da: " + clientSocket.getInetAddress().getHostAddress());

                // 4. Gestione della concorrenza: creiamo un Thread per ogni utente
                ClientHandler handler = new ClientHandler(clientSocket, dbManager);
                new Thread(handler).start();
            }

        } catch (IOException e) {
            System.err.println("[ERRORE] Impossibile avviare il server sulla porta " + PORT);
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}