/*
 * Progetto: The Knife
 * Autori:
 * - Mattia Polato (Matricola: 757923, Sede: VA)
 * - Andrea Luigi Mariani (Matricola: 757369, Sede: VA)
 */
package theknife;

import theknife.common.Utente;
import java.io.*;

/**
 * 
 * La classe TestUtente esegue test di unità sulla classe Utente.
 * In particolare verifica:
 * 1. La corretta cifratura a una via SHA-256 della password.
 * 2. La corrispondenza della password corretta ed errata.
 * 3. La serializzazione e deserializzazione dell'oggetto Utente per verificare 
 *    che mantenga l'integrità dei dati per il transito di rete.
 */
public class TestUtente {
    /**
     * Entry point per l'esecuzione del test.
     * 
     * @param args Argomenti da riga di comando.
     */
    public static void main(String[] args) {
        System.out.println("--- Inizio Test Modello Utente ---");

        // 1. Test Hash Password
        Utente u = new Utente("Mattia", "Polato", "m.polato", "password123", "01/01/1990", "Milano", "cliente");
        
        System.out.print("Test verifica password corretta: ");
        if (u.verificaPassword("password123")) {
            System.out.println("[OK]");
        } else {
            System.out.println("[FALLITO]");
        }

        System.out.print("Test verifica password errata: ");
        if (!u.verificaPassword("wrongpass")) {
            System.out.println("[OK]");
        } else {
            System.out.println("[FALLITO]");
        }

        // 2. Test Serializzazione
        System.out.print("Test Serializzazione: ");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(u);
            oos.flush();
            
            byte[] bytes = bos.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Utente u2 = (Utente) ois.readObject();
            
            if (u2.getUsername().equals(u.getUsername())) {
                System.out.println("[OK]");
            } else {
                System.out.println("[FALLITO] Username non corrisponde.");
            }
        } catch (Exception e) {
            System.out.println("[FALLITO] Eccezione: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("--- Fine Test Modello Utente ---");
    }
}
