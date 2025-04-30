package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorXat {
  private static final int PORT = 9999;
  private static final String HOST = "localhost";
  private static final String MSG_SORTIR = "sortir";
  private ServerSocket serverSocket;

  public void iniciarServidor() throws IOException {
    serverSocket = new ServerSocket(PORT);
    System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
  }

  public void pararServidor() throws IOException {
    if (serverSocket != null && !serverSocket.isClosed()) {
      serverSocket.close();
      System.out.println("Servidor aturat.");
    }
  }

  public String getNom(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
    return (String) entrada.readObject();
  }

  public static void main(String[] args) {
    ServidorXat servidor = new ServidorXat();
    try {
      servidor.iniciarServidor();
      Socket clientSocket = servidor.serverSocket.accept();
      System.out.println("Client connectat: " + clientSocket.getRemoteSocketAddress());

      ObjectOutputStream sortida = new ObjectOutputStream(clientSocket.getOutputStream());
      ObjectInputStream entrada = new ObjectInputStream(clientSocket.getInputStream());

      // Solicitar y obtener el nombre del cliente
      sortida.writeObject("Escriu el teu nom:");
      String nomClient = servidor.getNom(entrada);
      System.out.println("Nom rebut: " + nomClient);

      FilServidorXat fil = new FilServidorXat(entrada);
      System.out.println("Fil de xat creat.");
      System.out.println("Fil de " + nomClient + " iniciat");
      fil.start();

      // Leer mensajes de la consola y enviarlos al cliente
      BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
      String missatge;
      while (true) {
        System.out.print("Missatge ('sortir' per tancar): ");
        missatge = consola.readLine();
        if (missatge.equalsIgnoreCase(MSG_SORTIR)) {
          break;
        }
        sortida.writeObject(missatge);
      }

      fil.join(); // Esperar a que el hilo termine
      clientSocket.close();
      servidor.pararServidor();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

