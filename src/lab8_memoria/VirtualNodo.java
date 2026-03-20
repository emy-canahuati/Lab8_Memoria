package lab8_memoria;

import java.io.File;

/**
 * Subclase de Nodo que representa un archivo/carpeta virtual.
 * No accede al disco — todos los datos vienen del VirtualFileSystem.VNode.
 */
public class VirtualNodo extends Nodo {

    private final VirtualFileSystem.VNode vnode;

    public VirtualNodo(VirtualFileSystem.VNode vnode) {
        // Pasamos un File ficticio al super — sus campos serán ignorados
        super(new File(vnode.getPath()));
        this.vnode = vnode;
        // Sobreescribir los campos del Nodo padre con los valores virtuales
        this.nombre           = vnode.name;
        this.tamanio          = vnode.size;
        this.fechaModificacion = vnode.lastModified;
        this.tipo             = vnode.isDirectory ? "Carpeta" : vnode.extension;
        this.next             = null;
    }

    public VirtualFileSystem.VNode getVNode() {
        return vnode;
    }

    @Override
    public String getNombre()          { return vnode.name; }
    @Override
    public long   getTamanio()         { return vnode.size; }
    @Override
    public long   getFechaModificacion(){ return vnode.lastModified; }
    @Override
    public String getTipo()            { return vnode.isDirectory ? "Carpeta" : vnode.extension; }

    // getArchivo() devuelve null — no existe archivo real en disco
    @Override
    public File getArchivo()           { return null; }
}