package src;

// GestorClients.java
import java.io.*;
import java.net.Socket;

public class GestorClients implements Runnable {
    // Atributos para manejar la conexión con el cliente
    private Socket client;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    // Constructor que inicializa los streams de entrada/salida
    public GestorClients(Socket socket, ServidorXat servidor) throws IOException {
        this.client = socket;
        this.servidor = servidor;
        this.out = new ObjectOutputStream(client.getOutputStream());
        this.in = new ObjectInputStream(client.getInputStream());
    }    
    
    @Override
    public void run() {
        try {
            // Bucle principal. Recibe mensajes mientras el cliente este conectado
            while (!sortir && !servidor.isSortir()) {
                String missatge = (String) in.readObject();
                processaMissatge(missatge);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!sortir) e.printStackTrace();
        } finally {
            try { 
                client.close(); 
            } catch (IOException e) { 
                e.printStackTrace(); 
            }
        }
    }   
    
    private void processaMissatge(String missatge) {
        // Extrae el codigo de operación del mensaje
        String codi = Missatge.getCodiMissatge(missatge);
        if (codi == null) return;
        
        // Divide el mensaje en sus partes
        String[] parts = Missatge.getPartsMissatge(missatge);

        // Procesa el mensaje según su codigo
        switch (codi) {
            case Missatge.CODI_CONECTAR:
                nom = parts[1];
                servidor.afegirClient(this);
                break;
            case Missatge.CODI_SORTIR_CLIENT:
                servidor.eliminarClient(nom);
                sortir = true;
                break;
            case Missatge.CODI_SORTIR_TOTS:
                sortir = true;
                servidor.finalitzarXat();
                break;            
            case Missatge.CODI_MSG_PERSONAL:
                if (parts.length >= 3) {
                    // Envia un mensaje a un destinatario específico
                    String destinatari = parts[1];
                    String contingutMissatge = parts[2];
                    servidor.enviarMissatgePersonal(destinatari, nom, contingutMissatge);
                }
                break;
            case Missatge.CODI_MSG_GRUP:
                if (parts.length >= 2) {
                    // Envia un mensaje a todos los clientes conectados
                    servidor.enviarMissatgeGrup(Missatge.getMissatgeGrup(nom + ": " + parts[1]));
                }
                break;
            default:
                System.out.println("Codi desconegut: " + codi);
        }
    }
    
    public void enviarMissatge(String remitent, String missatge) {
        try {
            // Envia el mensaje a través del stream de salida
            out.writeObject(missatge/* Missatge.getMissatgePersonal(remitent, missatge) */);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public String getNom() { 
        return nom; 
    }
}