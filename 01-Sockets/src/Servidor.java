package src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

  public static final int PORT = 7777;
  public static final String HOST = "localhost";
  private ServerSocket srvSocket;
  private Socket socket;
  
  public void connecta() throws IOException {
    srvSocket = new ServerSocket(PORT);
    // espera una conexion, se bloquea
    System.out.printf("Esperant connexions  a %s:%d\n", HOST, PORT);
    socket = srvSocket.accept();

    System.out.printf("Client connectat: %s\n", socket.getInetAddress());
    // procesa la conexion
    repDades(socket);    
  }

  public void tanca() throws IOException {
    socket.close();
    srvSocket.close();
    System.out.println("Servidor tancat.");
  }


  public void repDades(Socket socket) {
    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      String line;
      while ((line = in.readLine()) != null) {
        System.out.println("Rebut: " + line);
      }
    } catch (IOException e) {
      System.err.println("Error llegint socket: " + e.getMessage());
    }
  }

  public static void main(String[] args) {
    
    Servidor servidor = new Servidor();
    System.out.printf("Servidor en marxa a %s:%d\n", HOST, PORT);

    try {
      servidor.connecta();
    } catch (IOException e) {
      System.err.println("Error connectant: " + e.getMessage());
    } finally {
      try {
        servidor.tanca();
      } catch (IOException e) {
        System.err.println("Error tancant: " + e.getMessage());
      }
    }

  }

}
