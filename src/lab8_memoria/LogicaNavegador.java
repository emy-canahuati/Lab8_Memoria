package lab8_memoria;

import java.util.ArrayList;
import java.util.List;

public class LogicaNavegador {

    private final Nodo raiz;
    public Nodo carpetaActual;
    private final List<Nodo> portapapeles = new ArrayList<>();

    public LogicaNavegador() {
        raiz = construirArbolDefault();
        carpetaActual = raiz;
    }

    private Nodo construirArbolDefault() {
        Nodo docs = carpeta("Documentos", null);

        Nodo trabajo = carpeta("Trabajo", docs);
        archivo("Informe_marzo.pdf", 512_000, dias(5), trabajo);
        archivo("foto_equipo.jpg", 1_800_000, dias(3), trabajo);
        archivo("presupuesto_2024.xlsx", 128_000, dias(7), trabajo);
        archivo("jingle_empresa.mp3", 3_200_000, dias(2), trabajo);
        archivo("logo_nuevo.png", 240_000, dias(9), trabajo);
        archivo("acta_reunion.txt", 5_100, dias(1), trabajo);
        archivo("contrato_cliente.docx", 98_000, dias(12), trabajo);
        archivo("captura_error.png", 620_000, dias(4), trabajo);

        Nodo personal = carpeta("Personal", docs);
        archivo("vacaciones_playa.jpg", 3_400_000, dias(20), personal);
        archivo("cancion_favorita.mp3", 4_900_000, dias(15), personal);
        archivo("curriculum.docx", 72_000, dias(8), personal);
        archivo("receta_pasta.txt", 3_800, dias(6), personal);
        archivo("foto_cumple.gif", 2_100_000, dias(30), personal);
        archivo("factura_internet.pdf", 44_000, dias(10), personal);
        archivo("playlist_verano.wav", 11_000_000, dias(5), personal);
        archivo("selfie.png", 980_000, dias(18), personal);

        Nodo tareas = carpeta("Tareas", docs);
        archivo("tarea_calculo.pdf", 310_000, dias(2), tareas);
        archivo("diagrama_clases.png", 450_000, dias(1), tareas);
        archivo("exposicion_fisica.pptx", 1_700_000, dias(3), tareas);
        archivo("apuntes_quimica.txt", 9_200, dias(4), tareas);
        archivo("foto_pizarron.jpg", 870_000, dias(2), tareas);
        archivo("grabacion_clase.wav", 14_500_000, dias(6), tareas);
        archivo("informe_bio.docx", 85_000, dias(5), tareas);
        archivo("meme_estudio.gif", 730_000, dias(1), tareas);

        Nodo descargas = carpeta("Descargas", docs);
        archivo("wallpaper_4k.jpg", 5_200_000, dias(1), descargas);
        archivo("podcast_ep3.mp3", 9_800_000, dias(2), descargas);
        archivo("manual_java.pdf", 820_000, dias(3), descargas);
        archivo("icono_app.png", 55_000, dias(1), descargas);
        archivo("notas_viaje.txt", 6_700, dias(4), descargas);
        archivo("animacion_logo.gif", 1_300_000, dias(2), descargas);
        archivo("cancion_nueva.wav", 7_600_000, dias(5), descargas);
        archivo("contrato_gym.docx", 41_000, dias(7), descargas);
        archivo("foto_mascota.jpg", 2_400_000, dias(3), descargas);
        archivo("reporte_lab.pdf", 195_000, dias(6), descargas);

        return docs;
    }

    private Nodo carpeta(String nombre, Nodo padre) {
        Nodo n = new Nodo(nombre, true, 0, System.currentTimeMillis(), padre);
        if (padre != null) {
            padre.hijos.add(n);
        }
        return n;
    }

    private void archivo(String nombre, long tam, long fecha, Nodo padre) {
        padre.hijos.add(new Nodo(nombre, false, tam, fecha, padre));
    }

    private long dias(int d) {
        return System.currentTimeMillis() - (long) d * 86_400_000L;
    }

    public Nodo getRaiz() {
        return raiz;
    }

    public void setCarpetaActual(Nodo carpeta) {
        if (carpeta != null && carpeta.esDirectorio) {
            this.carpetaActual = carpeta;
        }
    }

    public Lista getContenido(Nodo carpeta) {
        Lista lista = new Lista();
        if (carpeta == null || !carpeta.esDirectorio) {
            return lista;
        }
        List<Nodo> hijos = new ArrayList<>(carpeta.hijos);
        hijos.sort((a, b) -> {
            if (a.esDirectorio && !b.esDirectorio) {
                return -1;
            }
            if (!a.esDirectorio && b.esDirectorio) {
                return 1;
            }
            return a.nombre.compareToIgnoreCase(b.nombre);
        });
        for (Nodo n : hijos) {
            lista.add(n);
        }
        return lista;
    }

    public int organizar() {
        if (carpetaActual == null || !carpetaActual.esDirectorio) {
            return 0;
        }
        List<Nodo> snap = new ArrayList<>(carpetaActual.hijos);
        int movidos = 0;
        for (Nodo nodo : snap) {
            if (nodo.esDirectorio) {
                continue;
            }
            String destNombre = getCarpetaDestino(nodo.tipo);
            if (destNombre == null) {
                continue;
            }
            Nodo destino = carpetaActual.buscarHijo(destNombre);
            if (destino == null) {
                destino = crearCarpetaEn(carpetaActual, destNombre);
            }
            carpetaActual.hijos.remove(nodo);
            String nombre = nodo.nombre;
            if (destino.existeHijo(nombre)) {
                nombre = agregarSufijo(nombre);
            }
            nodo.nombre = nombre;
            nodo.padre = destino;
            destino.hijos.add(nodo);
            movidos++;
        }
        return movidos;
    }

    private String getCarpetaDestino(String tipo) {
        switch (tipo) {
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "Imagenes";
            case "pdf":
            case "docx":
            case "doc":
            case "txt":
            case "xlsx":
            case "pptx":
                return "Documentos";
            case "mp3":
            case "wav":
            case "flac":
            case "ogg":
            case "aac":
                return "Musica";
            default:
                return null;
        }
    }

    public Nodo crearCarpeta(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }
        if (carpetaActual.existeHijo(nombre)) {
            return null;
        }
        return crearCarpetaEn(carpetaActual, nombre.trim());
    }

    private Nodo crearCarpetaEn(Nodo padre, String nombre) {
        Nodo nueva = new Nodo(nombre, true, 0, System.currentTimeMillis(), padre);
        padre.hijos.add(nueva);
        return nueva;
    }

    public boolean renombrar(Nodo nodo, String nuevoNombre) {
        if (nodo == null || nuevoNombre == null || nuevoNombre.isBlank()) {
            return false;
        }
        if (nodo.padre == null || nodo.padre.existeHijo(nuevoNombre)) {
            return false;
        }
        nodo.nombre = nuevoNombre.trim();
        if (!nodo.esDirectorio) {
            nodo.tipo = Nodo.extraerExtension(nodo.nombre);
        }
        return true;
    }

    public void copiar(List<Nodo> nodos) {
        portapapeles.clear();
        portapapeles.addAll(nodos);
    }

    public int pegar() {
        if (portapapeles.isEmpty()) {
            return 0;
        }
        int pegados = 0;
        for (Nodo origen : portapapeles) {
            String nombre = origen.nombre;
            if (carpetaActual.existeHijo(nombre)) {
                nombre = agregarSufijo(nombre);
            }
            carpetaActual.hijos.add(copiarProfundo(origen, carpetaActual, nombre));
            pegados++;
        }
        return pegados;
    }

    public boolean portapapelesVacio() {
        return portapapeles.isEmpty();
    }

    private Nodo copiarProfundo(Nodo src, Nodo nuevoPadre, String nuevoNombre) {
        Nodo copia = new Nodo(nuevoNombre, src.esDirectorio, src.tamanio, src.fechaModificacion, nuevoPadre);
        for (Nodo hijo : src.hijos) {
            copia.hijos.add(copiarProfundo(hijo, copia, hijo.nombre));
        }
        return copia;
    }

    private String agregarSufijo(String nombre) {
        int dot = nombre.lastIndexOf('.');
        return dot < 0 ? nombre + "_copia"
                : nombre.substring(0, dot) + "_copia" + nombre.substring(dot);
    }

    public Nodo buscarPorRuta(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        String[] partes = ruta.replace("\\", "/").split("/");
        Nodo cur = raiz;
        for (String parte : partes) {
            if (parte.isBlank() || parte.equalsIgnoreCase(cur.nombre)) {
                continue;
            }
            cur = cur.buscarHijo(parte);
            if (cur == null) {
                return null;
            }
        }
        return cur;
    }
}
