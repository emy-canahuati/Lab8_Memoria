/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.*;
import java.util.*;
/**
 *
 * @author emyca
 */
public class Nodo {
    String nombre;
    File archivo;
    long tamanio;
    long fechaModificacion;
    String tipo;
    Nodo next;
    
    public Nodo(File archivo){
        this.archivo = archivo;
        this.nombre = archivo.getName();
        this.tamanio = archivo.isFile() ? archivo.length() : 0;
        this.fechaModificacion = archivo.lastModified();
        this.tipo = obtenerTipo(archivo);
        this.next = null;
    }
    
    private String obtenerTipo(File file){
        if(file.isDirectory()) return "Carpeta";
        String name = file.getName();
        int dot = name.lastIndexOf('.');
        if(dot >= 0 && dot < name.length() - 1)
            return name.substring(dot + 1).toLowerCase();
        return "Archivo";
    }

    public String getNombre() {
        return nombre;
    }

    public File getArchivo() {
        return archivo;
    }

    public long getTamanio() {
        return tamanio;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    public String getTipo() {
        return tipo;
    }
    
    
}
