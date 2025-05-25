package src;

// ClientXat.java
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
    // Atributos para la conexión con el servidor
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean sortir = false;

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();
        // Inicia un hilo para recibir mensajes del servidor
        new Thread(client::executa).start();
        client.processaEntrada();
    }    
    
    private void connecta() {
        try {
            // Establece conexión con el servidor y crea los streams
            socket = new Socket(ServidorXat.HOST, ServidorXat.PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            System.out.println("Client connectat a " + ServidorXat.HOST + ":" + ServidorXat.PORT);
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            System.err.println("Error connectant: " + e.getMessage());
        }
    }    
    
    private void executa() {
        try {
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            while (!sortir) {
                // Recibe y procesa los mensajes del servidor
                String missatge = (String) in.readObject();
                String[] parts = Missatge.getPartsMissatge(missatge);
                
                if (parts == null) continue;

                // Procesa según el tipo de mensaje
                switch (parts[0]) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;
                    case Missatge.CODI_MSG_PERSONAL:
                        System.out.println("Missatge de (" + parts[1] + "): " + parts[2]);
                        break;
                    case Missatge.CODI_MSG_GRUP:
                        System.out.println("Missatge grup: " + parts[1]);
                        break;
                    default:
                        System.out.println("Missatge desconegut: " + missatge);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (!sortir) System.err.println("Error rebent missatge. Sortint...");
        } finally {
            tancarClient();
        }
    }   
    
    private void processaEntrada() {
        Scanner scanner = new Scanner(System.in);
        while (!sortir) {
            this.ajuda();

            System.out.print("> ");
            String linia = scanner.nextLine().trim();
            
            // Si la linea está vacía, salir del chat
            if (linia.isEmpty()) {
                sortir = true;
                enviarMissatge(Missatge.getMissatgeSortirClient("Adeu"));
            } else {
                // Procesar comandos del usuario
                switch (linia) {
                    case "1":
                        // Conectar con nombre de usuario
                        String nom = getLinea(scanner, "Introdueix el nom: ", true);
                        String missatgeConectar = Missatge.getMissatgeConectar(nom);
                        System.out.println("Enviant missatge: " + missatgeConectar);
                        enviarMissatge(missatgeConectar);
                        break;
                    case "2":
                        String desti = getLinea(scanner, "Destinatari: ", true);
                        String msg = getLinea(scanner, "Missatge a enviar: ", true);
                        String missatgePersonal = Missatge.getMissatgePersonal(desti, msg);
                        System.out.println("Enviant missatge: " + missatgePersonal);
                        enviarMissatge(missatgePersonal);
                        break;                    case "3":
                        // Enviar mensaje al grupo
                        String missatgeGrupText = getLinea(scanner, "Missatge grup: ", true);
                        String missatgeGrup = Missatge.getMissatgeGrup(missatgeGrupText);
                        System.out.println("Enviant missatge: " + missatgeGrup);
                        enviarMissatge(missatgeGrup);
                        break;
                    case "4":
                        // Salir del cliente
                        sortir = true;
                        String missatgeSortir = Missatge.getMissatgeSortirClient("Adéu");
                        System.out.println("Enviant missatge: " + missatgeSortir);
                        enviarMissatge(missatgeSortir);
                        break;
                    case "5":
                        // Finalizar el chat para todos
                        sortir = true;
                        String missatgeSortirTots = Missatge.getMissatgeSortirTots("Adéu");
                        System.out.println("Enviant missatge: " + missatgeSortirTots);
                        enviarMissatge(missatgeSortirTots);
                        break;
                    default:
                        System.out.println("Opcio no vàlida");
                }
            }
        }
        scanner.close();
    }    
    
    private void enviarMissatge(String missatge) {
        try {
            // Envia el mensaje codificado al servidor
            out.writeObject(missatge);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error enviant missatge: " + e.getMessage());
        }
    }

    private void ajuda() {
        System.out.println("""
            ---------------------
            Comandes disponibles:
            1.- Conectar al servidor (primer pass obligatori)
            2.- Enviar missatge personal
            3.- Enviar missatge al grup
            4.- (o linia en blanc)-> Sortir del client
            5.- Finalitzar tothom
            ---------------------""");
    }    
    
    private String getLinea(Scanner scanner, String missatge, boolean obligatori) {
        String linia;
        // Solicita entrada al usuario hasta que sea válida (si es obligatoria)
        do {
            System.out.print(missatge);
            linia = scanner.nextLine().trim();
        } while (obligatori && linia.isEmpty());
        return linia;
    }

    private void tancarClient() {
        try {
            System.out.println("Tancant client...");
            if (in != null) {
                in.close();
                System.out.println("Flux d'entrada tancat.");
            }
            if (out != null) {
                out.close();
                System.out.println("Flux de sortida tancat.");
            } else {
                System.out.println("oos null. Sortint...");
            }
            // Cierra el socket al final
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error tancant client: " + e.getMessage());
        }
    }
}