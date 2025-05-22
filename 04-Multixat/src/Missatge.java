package src;

// Missatge.java
public class Missatge {
    // Códigos de operación para diferentes tipos de mensajes
    public static final String CODI_SORTIR_TOTS = "0000";   // Para finalizar todos los clientes
    public static final String CODI_CONECTAR = "1000";      // Para conectar un nuevo cliente
    public static final String CODI_MSG_PERSONAL = "1001";  // Para mensajes privados
    public static final String CODI_MSG_GRUP = "1002";      // Para mensajes de grupo
    public static final String CODI_SORTIR_CLIENT = "1003"; // Para salir de un cliente

    // Métodos para construir mensajes formateados
    public static String getMissatgePersonal(String destinatari, String missatge) {
        return CODI_MSG_PERSONAL + "#" + destinatari + "#" + missatge;
    }

    public static String getMissatgeGrup(String missatge) {
        return CODI_MSG_GRUP + "#" + missatge;
    }

    public static String getMissatgeSortirClient(String missatge) {
        return CODI_SORTIR_CLIENT + "#" + missatge;
    }

    public static String getMissatgeSortirTots(String missatge) {
        return CODI_SORTIR_TOTS + "#" + missatge;
    }

    public static String getMissatgeConectar(String nom) {
        return CODI_CONECTAR + "#" + nom;
    }
      public static String getCodiMissatge(String missatgeRaw) {
        // Extrae el codigo de operación del mensaje completo
        String msg = missatgeRaw.trim();
        if (msg.isEmpty() || msg.indexOf("#") == -1) {
            System.out.println("WARN: missatge buit o incorrecte");
            return null;
        }
        return msg.split("#")[0];
    }
    
    public static String[] getPartsMissatge(String missatgeRaw) {
        // Divide el mensaje en sus partes separadas por #
        String msg = missatgeRaw.trim();
        if (msg.isEmpty() || msg.indexOf("#") == -1) {
            System.out.println("WARN: missatge buit o incorrecte");
            return null;
        }
        return msg.split("#");
    }
}