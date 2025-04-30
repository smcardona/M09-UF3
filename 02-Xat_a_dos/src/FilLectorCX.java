package src;

import java.io.ObjectInputStream;

public class FilLectorCX extends Thread {
  private ObjectInputStream entrada;

  public FilLectorCX(ObjectInputStream entrada) {
      this.entrada = entrada;
  }

  @Override
  public void run() {
    try {
      System.out.println("Fil de lectura iniciat");

      String missatge;
      while (true) {
        missatge = (String) entrada.readObject();
        System.out.println("Rebut: " + missatge);
        if (missatge.equalsIgnoreCase("sortir")) {
          break;
        }
      }
    } catch (Exception e) {
      System.out.println("El servidor ha tancat la connexio.");
    }
  }
}