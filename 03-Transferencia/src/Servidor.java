package src;

import java.io.*;
import java.net.*;

public class Servidor {
    // Constantes según especificaciones
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    
    private ServerSocket serverSocket;
    private Socket socket;
    
    // Método para establecer la conexión
    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant connexio...");
        socket = serverSocket.accept();
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
        return socket;
    }
    
    // Método para cerrar la conexión
    public void tancarConnexio(Socket socket) throws IOException {
        if (socket != null && !socket.isClosed()) {
            System.out.println("Tancant connexió amb el client: " + socket.getRemoteSocketAddress());
            socket.close();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }
    
    // Método para enviar archivos al cliente
    public void enviarFitxers(Socket socket) throws IOException, ClassNotFoundException {
        ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());
        
        System.out.println("Esperant el nom del fitxer del client...");
        String nomFitxer = (String) entrada.readObject();
        
        if (nomFitxer == null || nomFitxer.isEmpty()) {
            System.out.println("Nom del fitxer buit o nul. Sortint...");
            return;
        }
        
        System.out.println("Nomfitxer rebut: " + nomFitxer);
        
        try {
            Fitxer fitxer = new Fitxer(nomFitxer);
            byte[] contingut = fitxer.getContingut();
            
            if (contingut != null) {
                System.out.println("Contingut del fitxer a enviar: " + contingut.length + " bytes");
                sortida.writeObject(contingut);
                sortida.flush();
                System.out.println("Fitxer enviat al client: " + nomFitxer);
            }
        } catch (Exception e) {
            System.out.println("Error llegint el fitxer del client: " + e.getMessage());
        }
    }
    
    // Método principal
    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        Socket socket = null;
        
        try {
            socket = servidor.connectar();
            servidor.enviarFitxers(socket);
        } catch (ClassNotFoundException | IOException e) {
            System.out.println("Error al servidor: " + e.getMessage());
        } finally {
            try {
                if (socket != null) {
                    servidor.tancarConnexio(socket);
                }
            } catch (IOException e) {
                System.out.println("Error tancant la connexió: " + e.getMessage());
            }
        }
    }
}