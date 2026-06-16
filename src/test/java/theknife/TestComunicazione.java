package theknife;

import theknife.common.Richiesta;
import theknife.common.Risposta;
import theknife.common.Utente;
import java.io.*;

public class TestComunicazione {
    public static void main(String[] args) {
        System.out.println("--- Inizio Test Protocollo Comunicazione ---");

        try {
            // Simuliamo il flusso di rete con pipe locali
            PipedInputStream pis = new PipedInputStream();
            PipedOutputStream pos = new PipedOutputStream(pis);

            // 1. Il Client invia una Richiesta di Login
            ObjectOutputStream oout = new ObjectOutputStream(pos);
            String[] credenziali = {"m.polato", "password123"};
            Richiesta req = new Richiesta(Richiesta.Tipo.LOGIN, credenziali);
            
            oout.writeObject(req);
            oout.flush();
            System.out.println("[CLIENT] Richiesta LOGIN inviata correttamente.");

            // 2. Il Server riceve la Richiesta e invia una Risposta (simuliamo)
            ObjectInputStream oin = new ObjectInputStream(pis);
            Richiesta reqRicevuta = (Richiesta) oin.readObject();
            
            if (reqRicevuta.getTipo() == Richiesta.Tipo.LOGIN) {
                System.out.println("[SERVER] Ricevuto LOGIN per utente: " + ((String[])reqRicevuta.getDati())[0]);
                
                // Il server crea una risposta con un oggetto Utente simulato
                Utente uSimulato = new Utente("Mattia", "Polato", "m.polato", "hashed_pass", "01/01/1990", "Milano", "cliente", true);
                Risposta res = new Risposta(true, "Login OK", uSimulato);
                
                // Invio risposta al client (riusando lo stream o un altro canale, qui simuliamo con la stessa pipe per brevità)
                // Nota: In una rete vera avremmo canali IN e OUT distinti, qui facciamo un passaggio sequenziale.
                
                // Reset della pipe per simulare il ritorno
                pis = new PipedInputStream();
                pos = new PipedOutputStream(pis);
                oout = new ObjectOutputStream(pos);
                oout.writeObject(res);
                oout.flush();

                oin = new ObjectInputStream(pis);
                Risposta resRicevuta = (Risposta) oin.readObject();
                
                if (resRicevuta.isSuccesso() && resRicevuta.getDati() instanceof Utente) {
                    System.out.println("[CLIENT] Ricevuta Risposta: " + resRicevuta.getMessaggio());
                    System.out.println("[CLIENT] Utente caricato: " + ((Utente)resRicevuta.getDati()).getNome());
                    System.out.println("[OK] Test Protocollo Superato.");
                } else {
                    System.out.println("[FALLITO] Risposta non valida.");
                }
            }

        } catch (Exception e) {
            System.out.println("[FALLITO] Errore: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("--- Fine Test Protocollo Comunicazione ---");
    }
}
