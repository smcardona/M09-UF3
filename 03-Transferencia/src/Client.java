package src;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    private static final String DIR_ARRIBADA = "C:\\tmp";
    
    private Socket socket;
    private ObjectOutputStream sortida;
    private ObjectInputStream entrada;
    
    // Método para establecer la conexión
    public void connectar() throws IOException {
        socket = new Socket("localhost", 9999);
        sortida = new ObjectOutputStream(socket.getOutputStream());
        entrada = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connectant a -> localhost:9999");
        System.out.println("Connexio acceptada: " + socket.getRemoteSocketAddress());
    }
    
    // Método para recibir archivos
    public void rebreFitxers() throws IOException, ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("Nom del fitxer a rebre ('sortir' per sortir): ");
            String nomFitxer = scanner.nextLine();
            
            if (nomFitxer.equalsIgnoreCase("sortir")) {
                sortida.writeObject("");
                System.out.println("Sortint...");
                break;
            }
            
            // Enviar el nombre del archivo al servidor
            sortida.writeObject(nomFitxer);
            sortida.flush();
            
            // Recibir el archivo como byte[]
            byte[] contingut = (byte[]) entrada.readObject();
            
            if (contingut != null && contingut.length > 0) {
                System.out.print("Nom del fitxer a guardar: ");
                String nomGuardar = DIR_ARRIBADA + File.separator + new File(nomFitxer).getName();
                System.out.println(nomGuardar);
                
                try (FileOutputStream fos = new FileOutputStream(nomGuardar)) {
                    fos.write(contingut);
                    System.out.println("Fitxer rebut i guardat com: " + nomGuardar);
                } catch (IOException e) {
                    System.out.println("Error guardant el fitxer: " + e.getMessage());
                }
            }
        }
        scanner.close();
    }
    
    // Método para cerrar la conexión
    public void tancarConnexio() throws IOException {
        if (sortida != null) sortida.close();
        if (entrada != null) entrada.close();
        if (socket != null && !socket.isClosed()) {
            System.out.println("Connexio tancada.");
            socket.close();
        }
    }
    
    // Método principal
    public static void main(String[] args) {
        Client client = new Client();
        
        try {
            client.connectar();
            client.rebreFitxers();
        } catch (Exception e) {
            System.out.println("Error al client: " + e.getMessage());
        } finally {
            try {
                client.tancarConnexio();
            } catch (IOException e) {
                System.out.println("Error tancant la connexió: " + e.getMessage());
            }
        }
    }
}
