package src;

// ServidorXat.java
import java.io.IOException;
import java.net.*;
import java.util.Hashtable;

public class ServidorXat {
    // Configuración del servidor
    public static final int PORT = 9999;
    public static final String HOST = "localhost";
    public static final String MSG_SORTIR = "sortir";
    
    private ServerSocket serverSocket;
    // Hashtable para almacenar los clientes conectados por nombre
    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;

    // Método principal que inicia el servidor
    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
            
            while (!sortir) {
                // Espera y acepta conexiones de clientes
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());
                
                // Crea un gestor de cliente y lo inicia en un nuevo hilo
                GestorClients gestor = new GestorClients(clientSocket, this);
                new Thread(gestor).start();
            }
        } catch (IOException e) {
            if (!sortir) e.printStackTrace();
        }
    }    // Métodos para gestionar clientes y mensajes
    public synchronized void afegirClient(GestorClients client) {
        // Añade el cliente a la hashtable y notifica a todos
        clients.put(client.getNom(), client);
        System.out.println(client.getNom() + " connectat.");
        System.out.println("DEBUG: multicast Entra: " + client.getNom());
        enviarMissatgeGrup(Missatge.getMissatgeGrup(client.getNom() + " s'ha unit al xat"));
    }

    public synchronized void eliminarClient(String nom) {
        // Elimina el cliente si existe
        if (clients.containsKey(nom)) {
            clients.remove(nom);
            System.out.println(nom + " ha sortit del xat");
        }
    }   
    
    public synchronized void enviarMissatgeGrup(String missatge) {
        // Envía el mensaje a todos los clientes conectados
        clients.forEach((nom, client) -> client.enviarMissatge("Servidor", missatge));
    }

    public synchronized void enviarMissatgePersonal(String destinatari, String remitent, String missatge) {
        // Envía un mensaje privado al destinatario si existe
        if (clients.containsKey(destinatari)) {
            System.out.println("Missatge personal per (" + destinatari + ") de (" + remitent + "): " + missatge);
            clients.get(destinatari).enviarMissatge(remitent, 
                Missatge.getMissatgePersonal(remitent, missatge));
        }
    }    public void finalitzarXat() {
        // Finaliza el chat para todos los usuarios conectados
        sortir = true;
        System.out.println("Tancant tots els clients.");
        System.out.println("DEBUG: multicast sortir");
        enviarMissatgeGrup(Missatge.getMissatgeSortirTots(MSG_SORTIR));
        clients.clear();
        pararServidor();
        System.exit(0);
    }

    private void pararServidor() {
        // Cierra el socket del servidor
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isSortir() { return sortir; }
}