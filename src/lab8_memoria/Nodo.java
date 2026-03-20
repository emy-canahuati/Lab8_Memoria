package lab8_memoria;

import java.util.ArrayList;
import java.util.List;

public class Nodo {

    public String nombre;
    public String tipo;
    public long tamanio;
    public long fechaModificacion;
    public boolean esDirectorio;
    public Nodo padre;
    public List<Nodo> hijos = new ArrayList<>();
    public Nodo next;

    public Nodo(String nombre, boolean esDirectorio, long tamanio, long fechaModificacion, Nodo padre) {
        this.nombre = nombre;
        this.esDirectorio = esDirectorio;
        this.tamanio = esDirectorio ? 0 : tamanio;
        this.fechaModificacion = fechaModificacion;
        this.tipo = esDirectorio ? "Carpeta" : extraerExtension(nombre);
        this.padre = padre;
        this.next = null;
    }

    public static String extraerExtension(String nombre) {
        int dot = nombre.lastIndexOf('.');
        if (dot >= 0 && dot < nombre.length() - 1) {
            return nombre.substring(dot + 1).toLowerCase();
        }
        return "Archivo";
    }

    public String getRuta() {
        if (padre == null) {
            return nombre;
        }
        return padre.getRuta() + "/" + nombre;
    }

    public boolean existeHijo(String childNombre) {
        for (Nodo nodo : hijos) {
            if (nodo.nombre.equalsIgnoreCase(childNombre)) {
                return true;
            }
        }
        return false;
    }

    public Nodo buscarHijo(String childNombre) {
        for (Nodo nodo : hijos) {
            if (nodo.nombre.equalsIgnoreCase(childNombre)) {
                return nodo;
            }
        }
        return null;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public long getTamanio() {
        return tamanio;
    }

    public long getFechaModificacion() {
        return fechaModificacion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
