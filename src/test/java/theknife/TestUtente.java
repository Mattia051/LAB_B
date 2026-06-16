package theknife;

import theknife.common.Utente;
import java.io.*;

public class TestUtente {
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
