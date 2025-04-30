package src;

import java.io.ObjectInputStream;

public class FilServidorXat extends Thread {
  private ObjectInputStream entrada;

  public FilServidorXat(ObjectInputStream entrada) {
    this.entrada = entrada;
  }

  @Override
  public void run() {
    try {
      String missatge;
      while (true) {
        missatge = (String) entrada.readObject();
        System.out.println("Rebut: " + missatge);
        if (missatge.equalsIgnoreCase("sortir")) {
          System.out.println("Fil de xat finalitzat.");
          break;
        }
      }
    } catch (Exception e) {
      System.out.println("El client ha tancat la connexio.");
    }
  }
}