package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientXat {
  private Socket socket;
  private ObjectOutputStream sortida;
  private ObjectInputStream entrada;

  public void connecta() throws IOException {
    socket = new Socket("localhost", 9999);
    System.out.println("Client connectat a localhost:9999");
    
    sortida = new ObjectOutputStream(socket.getOutputStream());
    entrada = new ObjectInputStream(socket.getInputStream());
    System.out.println("Flux d'entrada i sortida creat.");
  }

  public void enviarMissatge(String missatge) throws IOException {
    sortida.writeObject(missatge);
    System.out.println("Enviant missatge: " + missatge);
  }

  public void tancarClient() throws IOException {
    System.out.println("Tancant client...");
    if (sortida != null) sortida.close();
    if (entrada != null) entrada.close();
    if (socket != null) socket.close();
    System.out.println("Client tancat.");
  }

  public static void main(String[] args) {
    ClientXat client = new ClientXat();
    try {
      client.connecta();
      
      FilLectorCX filLector = new FilLectorCX(client.entrada);
      filLector.start();

      Scanner scanner = new Scanner(System.in);
      String missatge;
      while (true) {
        System.out.print("Missatge ('sortir' per tancar): ");
        missatge = scanner.nextLine();
        client.enviarMissatge(missatge);
        if (missatge.equalsIgnoreCase("sortir")) {
          client.tancarClient();
          break;
        }
      }
      
      scanner.close();
      filLector.join(); // Esperar a que el hilo termine
    } catch (Exception e) {
      System.out.println("El servidor ha tancat la connexió.");
    }
  }
}

