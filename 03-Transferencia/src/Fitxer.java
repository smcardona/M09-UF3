package src;

import java.io.*;
import java.nio.file.Files;

public class Fitxer {
    private String nom;
    private byte[] contingut;
    
    // Constructor con nombre de archivo
    public Fitxer(String nom) {
        this.nom = nom;
    }
    
    // Método para obtener el contenido del archivo
    public byte[] getContingut() throws IOException {
        File file = new File(nom);
        
        if (!file.exists() || !file.canRead()) {
            System.out.println("El fitxer no existeix o no es pot llegir: " + nom);
            return null;
        }
        
        contingut = Files.readAllBytes(file.toPath());
        return contingut;
    }
}