/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife;

import theknife.server.DatabaseManager;
import theknife.common.Ristorante;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * La classe TestDatabaseFiltri esegue test interattivi di integrazione sul database.
 * Verifica in particolare:
 * 1. La ricerca dei ristoranti per sola Città/Località.
 * 2. La ricerca avanzata combinando Città/Località e tipo di cucina.
 * 3. La ricerca case-insensitive per garantire la tolleranza al maiuscolo/minuscolo.
 */
public class TestDatabaseFiltri {
    /**
     * Entry point per l'esecuzione del test interattivo.
     * Richiede le credenziali del database PostgreSQL da tastiera.
     * 
     * @param args Argomenti da riga di comando.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Test Filtri Database ===");
        
        System.out.print("Host DB: ");
        String host = scanner.nextLine();
        System.out.print("User DB: ");
        String user = scanner.nextLine();
        System.out.print("Pass DB: ");
        String pass = scanner.nextLine();

        DatabaseManager db = new DatabaseManager(host, user, pass);

        System.out.println("\n--- 1. Test Ricerca per sola Città (es. 'Milan') ---");
        System.out.print("Inserisci città da cercare: ");
        String citta = scanner.nextLine();
        List<Ristorante> r1 = db.cercaRistoranti(citta, "");
        System.out.println("Trovati: " + r1.size());
        if (!r1.isEmpty()) {
            System.out.println("Esempio: " + r1.get(0).getNome() + " in " + r1.get(0).getLocation());
        }

        System.out.println("\n--- 2. Test Ricerca Avanzata (Città + Cucina) ---");
        System.out.print("Inserisci città: ");
        String loc = scanner.nextLine();
        System.out.print("Inserisci cucina: ");
        String cuc = scanner.nextLine();
        List<Ristorante> r2 = db.cercaRistoranti(loc, cuc);
        System.out.println("Trovati: " + r2.size());
        for (Ristorante r : r2) {
            System.out.println("- " + r.getNome() + " (" + r.getCucina() + ")");
            if (r2.indexOf(r) > 5) { System.out.println("... e altri"); break; }
        }

        System.out.println("\n--- 3. Test Ricerca Case-Insensitive ---");
        String locLower = loc.toLowerCase();
        List<Ristorante> r3 = db.cercaRistoranti(locLower, "");
        System.out.println("Ricerca con '" + locLower + "': " + r3.size() + " risultati.");

        System.out.println("\nTest completato.");
        scanner.close();
    }
}
