package lab8_memoria;

import java.util.*;

/**
 * Sistema de archivos completamente simulado en memoria.
 * No lee ni escribe en el disco del usuario.
 */
public class VirtualFileSystem {

    // ── Nodo virtual ─────────────────────────────────────────────────────────
    public static class VNode {
        public String  name;
        public boolean isDirectory;
        public long    size;          // bytes (0 para carpetas)
        public long    lastModified;  // epoch ms
        public String  extension;     // "" para carpetas
        public VNode   parent;
        public List<VNode> children = new ArrayList<>();

        // Constructor carpeta
        public VNode(String name, VNode parent) {
            this.name        = name;
            this.isDirectory = true;
            this.size        = 0;
            this.lastModified = System.currentTimeMillis();
            this.extension   = "";
            this.parent      = parent;
        }

        // Constructor archivo
        public VNode(String name, long size, long lastModified, VNode parent) {
            this.name        = name;
            this.isDirectory = false;
            this.size        = size;
            this.lastModified = lastModified;
            this.extension   = extractExt(name);
            this.parent      = parent;
        }

        public String getPath() {
            if (parent == null) return name;
            return parent.getPath() + "/" + name;
        }

        public boolean exists(String childName) {
            for (VNode c : children)
                if (c.name.equalsIgnoreCase(childName)) return true;
            return false;
        }

        public VNode findChild(String childName) {
            for (VNode c : children)
                if (c.name.equalsIgnoreCase(childName)) return c;
            return null;
        }

        private static String extractExt(String name) {
            int dot = name.lastIndexOf('.');
            if (dot >= 0 && dot < name.length() - 1)
                return name.substring(dot + 1).toLowerCase();
            return "";
        }

        @Override public String toString() { return name; }
    }

    // ── Raíz del sistema virtual ──────────────────────────────────────────────
    private final VNode root;

    public VirtualFileSystem() {
        root = buildDefaultTree();
    }

    public VNode getRoot() { return root; }

    // ── Construcción del árbol predeterminado ─────────────────────────────────
    private VNode buildDefaultTree() {
        VNode docs = new VNode("Documentos", null);

        // — Trabajo
        VNode trabajo = addDir(docs, "Trabajo");
        addFile(trabajo, "Informe_anual.pdf",    512_000,  daysAgo(5));
        addFile(trabajo, "Presupuesto.xlsx",     128_000,  daysAgo(3));
        addFile(trabajo, "Presentacion.pptx",    2_400_000, daysAgo(1));
        addFile(trabajo, "Notas_reunion.txt",    4_200,    daysAgo(2));
        addFile(trabajo, "Contrato.docx",        98_000,   daysAgo(10));

        // — Personal
        VNode personal = addDir(docs, "Personal");
        addFile(personal, "Factura_luz.pdf",     45_000,   daysAgo(15));
        addFile(personal, "Curriculum.docx",     72_000,   daysAgo(7));
        addFile(personal, "Diario.txt",          8_500,    daysAgo(1));
        addFile(personal, "foto_perfil.jpg",     1_200_000, daysAgo(20));
        addFile(personal, "receta.txt",          3_100,    daysAgo(4));

        // — Música
        VNode musica = addDir(docs, "Musica");
        addFile(musica, "cancion_favorita.mp3",  4_800_000, daysAgo(30));
        addFile(musica, "podcast_ep1.mp3",       9_200_000, daysAgo(12));
        addFile(musica, "efecto_sonido.wav",     220_000,   daysAgo(8));
        addFile(musica, "bach_suite.wav",        18_000_000,daysAgo(45));

        // — Imágenes (mezcladas con otros tipos para que Organizar las mueva)
        VNode imagenes = addDir(docs, "Imagenes");
        addFile(imagenes, "vacaciones.jpg",      3_100_000, daysAgo(60));
        addFile(imagenes, "captura.png",         840_000,   daysAgo(2));
        addFile(imagenes, "logo.png",            120_000,   daysAgo(14));
        addFile(imagenes, "animacion.gif",       2_500_000, daysAgo(20));

        // — Carpeta mixta: ideal para probar Organizar
        VNode mixta = addDir(docs, "Sin_organizar");
        addFile(mixta, "foto1.jpg",       1_500_000, daysAgo(3));
        addFile(mixta, "foto2.png",       900_000,   daysAgo(4));
        addFile(mixta, "cancion.mp3",     5_000_000, daysAgo(5));
        addFile(mixta, "reporte.pdf",     300_000,   daysAgo(6));
        addFile(mixta, "apuntes.txt",     12_000,    daysAgo(7));
        addFile(mixta, "contrato.docx",   80_000,    daysAgo(8));
        addFile(mixta, "efecto.wav",      400_000,   daysAgo(9));
        addFile(mixta, "banner.gif",      600_000,   daysAgo(10));

        return docs;
    }

    // ── Helpers de construcción ───────────────────────────────────────────────
    private VNode addDir(VNode parent, String name) {
        VNode d = new VNode(name, parent);
        parent.children.add(d);
        return d;
    }

    private void addFile(VNode parent, String name, long size, long modified) {
        parent.children.add(new VNode(name, size, modified, parent));
    }

    private long daysAgo(int days) {
        return System.currentTimeMillis() - (long) days * 86_400_000L;
    }

    // ── Operaciones sobre el VFS ──────────────────────────────────────────────

    /** Lista los hijos de un nodo (nunca null). */
    public List<VNode> listChildren(VNode dir) {
        if (dir == null || !dir.isDirectory) return Collections.emptyList();
        return new ArrayList<>(dir.children);
    }

    /** Crea una carpeta. Devuelve el nodo creado o null si ya existe / padre inválido. */
    public VNode createFolder(VNode parent, String name) {
        if (parent == null || !parent.isDirectory) return null;
        if (name == null || name.isBlank()) return null;
        if (parent.exists(name)) return null;
        VNode d = new VNode(name.trim(), parent);
        parent.children.add(d);
        return d;
    }

    /** Renombra un nodo. Devuelve true si tuvo éxito. */
    public boolean rename(VNode node, String newName) {
        if (node == null || newName == null || newName.isBlank()) return false;
        if (node.parent == null) return false;          // no renombrar la raíz
        if (node.parent.exists(newName)) return false;  // nombre duplicado
        node.name = newName.trim();
        if (!node.isDirectory)
            node.extension = VNode.extractExt(node.name);
        return true;
    }

    /**
     * Copia una lista de nodos al directorio destino.
     * Agrega sufijo "_copia" si el nombre ya existe.
     */
    public void copyNodes(List<VNode> nodes, VNode destDir) {
        if (destDir == null || !destDir.isDirectory) return;
        for (VNode src : nodes) {
            String name = src.name;
            if (destDir.exists(name)) name = addSuffix(name);
            VNode copy = deepCopy(src, destDir, name);
            destDir.children.add(copy);
        }
    }

    /**
     * Organiza archivos del directorio en subcarpetas según tipo.
     * Solo mueve archivos (no carpetas).
     * Devuelve cuántos archivos se movieron.
     */
    public int organize(VNode dir) {
        if (dir == null || !dir.isDirectory) return 0;

        Map<String, String[]> rules = new LinkedHashMap<>();
        rules.put("Imagenes",   new String[]{"jpg","jpeg","png","gif","bmp"});
        rules.put("Documentos", new String[]{"pdf","docx","doc","txt","xlsx","pptx"});
        rules.put("Musica",     new String[]{"mp3","wav","flac","ogg","aac"});

        List<VNode> toMove = new ArrayList<>();
        for (VNode child : new ArrayList<>(dir.children)) {
            if (!child.isDirectory) toMove.add(child);
        }

        int moved = 0;
        for (VNode file : toMove) {
            String ext = file.extension;
            for (Map.Entry<String, String[]> rule : rules.entrySet()) {
                if (Arrays.asList(rule.getValue()).contains(ext)) {
                    // Obtener o crear subcarpeta destino
                    VNode destDir = dir.findChild(rule.getKey());
                    if (destDir == null) {
                        destDir = createFolder(dir, rule.getKey());
                    }
                    // Mover: quitar del padre, agregar al destino
                    dir.children.remove(file);
                    String name = file.name;
                    if (destDir.exists(name)) name = addSuffix(name);
                    file.name = name;
                    file.parent = destDir;
                    destDir.children.add(file);
                    moved++;
                    break;
                }
            }
        }
        return moved;
    }

    // ── Utilidades privadas ───────────────────────────────────────────────────

    private String addSuffix(String name) {
        int dot = name.lastIndexOf('.');
        if (dot < 0) return name + "_copia";
        return name.substring(0, dot) + "_copia" + name.substring(dot);
    }

    private VNode deepCopy(VNode src, VNode newParent, String newName) {
        if (src.isDirectory) {
            VNode dir = new VNode(newName, newParent);
            for (VNode child : src.children)
                dir.children.add(deepCopy(child, dir, child.name));
            return dir;
        } else {
            return new VNode(newName, src.size, src.lastModified, newParent);
        }
    }
}