package src;

import java.io.PrintWriter;
import java.net.Socket;

public class Client {

  private static final String HOST = Servidor.HOST;
  private static final int PORT = Servidor.PORT;
  private Socket socket;
  private PrintWriter out;


  public void connecta() throws Exception {
    socket = new Socket(HOST, PORT);
    System.out.printf("Connectat a servidor en %s:%d\n", HOST, PORT);
    out = new PrintWriter(socket.getOutputStream(), true);
  }

  public void tanca() throws Exception {
    out.close();
    socket.close();
  }

  public void envia(String msg) {
    out.println(msg);
    System.out.println("Enviat al servidor: " + msg);
  }

  public static void main(String[] args) {
    Client client = new Client();
    try {
      client.connecta();
      client.envia("Prova d'enviament 1");
      client.envia("Prova d'enviament 2");
      client.envia("Prova d'enviament 3");
      client.envia("Adeu!");

      System.out.println("Prem ENTER per tancar el client...");
      System.in.read(); // espera a que l'usuari prem ENTER

    } catch (Exception e) {
      System.err.println("Error connectant: " + e.getMessage());
    } finally {
      try {
        client.tanca();
      } catch (Exception e) {
        System.err.println("Error tancant: " + e.getMessage());
      }
    }
  }

}