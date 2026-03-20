package lab8_memoria;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Controlador principal — conecta toda la GUI con el VirtualFileSystem.
 * No toca el disco en ningún momento.
 */
public class FileExplorerController {

    private final Mainframe      frame;
    private final Toolbarpanel   toolbar;
    private final Foldertreepanel treePanel;
    private final Filelistpanel  listPanel;
    private final Actioninputpanel inputPanel;
    private final Statusbarpanel statusBar;

    private final VirtualFileSystem vfs = new VirtualFileSystem();
    private VirtualFileSystem.VNode currentFolder;
    private VirtualFileSystem.VNode rootFolder;

    // Historial de navegación
    private final Deque<VirtualFileSystem.VNode> historyBack    = new ArrayDeque<>();
    private final Deque<VirtualFileSystem.VNode> historyForward = new ArrayDeque<>();

    // Portapapeles virtual
    private final List<VirtualFileSystem.VNode> clipboard = new ArrayList<>();

    // Lista enlazada con los nodos del directorio actual
    private Lista listaActual = new Lista();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // ── Constructor ───────────────────────────────────────────────────────────
    public FileExplorerController(Mainframe frame) {
        this.frame      = frame;
        this.toolbar    = frame.getToolbarPanel();
        this.treePanel  = frame.getFolderTreePanel();
        this.listPanel  = frame.getFileListPanel();
        this.inputPanel = frame.getActionInputPanel();
        this.statusBar  = frame.getStatusBarPanel();

        bindToolbar();
        bindTree();
        bindTable();
        bindConfirm();
    }

    /** Punto de entrada — carga el árbol y navega a la raíz virtual. */
    public void init() {
        rootFolder = vfs.getRoot();
        refreshTree();
        navigateTo(rootFolder);
    }

    // ── Binding de eventos ────────────────────────────────────────────────────

    private void bindToolbar() {

        toolbar.addNewFolderListener(e -> {
            if (currentFolder == null) return;
            inputPanel.setMode(Actioninputpanel.ActionMode.NEW_FOLDER);
            inputPanel.clearInput();
        });

        toolbar.addRenameListener(e -> {
            String sel = listPanel.getSelectedFileName();
            if (sel == null) {
                statusBar.setErrorMessage("Selecciona un elemento para renombrar.");
                return;
            }
            inputPanel.setMode(Actioninputpanel.ActionMode.RENAME);
            inputPanel.setInputText(sel);
        });

        toolbar.addCopyListener(e -> {
            String sel = listPanel.getSelectedFileName();
            if (sel == null) {
                statusBar.setErrorMessage("Selecciona un elemento para copiar.");
                return;
            }
            clipboard.clear();
            VirtualFileSystem.VNode node = currentFolder.findChild(sel);
            if (node != null) {
                clipboard.add(node);
                toolbar.setPasteEnabled(true);
                inputPanel.setMode(Actioninputpanel.ActionMode.COPY);
                statusBar.setMessage("Copiado: " + sel);
            }
        });

        toolbar.addPasteListener(e -> {
            if (clipboard.isEmpty()) return;
            inputPanel.setMode(Actioninputpanel.ActionMode.PASTE);
        });

        toolbar.addOrganizeListener(e ->
            inputPanel.setMode(Actioninputpanel.ActionMode.ORGANIZE)
        );

        toolbar.addBackListener(e    -> navigateBack());
        toolbar.addForwardListener(e -> navigateForward());
        toolbar.addUpListener(e      -> navigateUp());

        toolbar.addGoAddressListener(e -> {
            String path = toolbar.getAddressText().trim();
            VirtualFileSystem.VNode target = findNodeByPath(path);
            if (target != null && target.isDirectory) {
                navigateTo(target);
            } else {
                statusBar.setErrorMessage("Ruta virtual no encontrada: " + path);
            }
        });

        toolbar.addSortByNameListener(e -> sortAndRefresh(Lista.Criterios.NOMBRE));
        toolbar.addSortBySizeListener(e -> sortAndRefresh(Lista.Criterios.TAMANIO));
        toolbar.addSortByDateListener(e -> sortAndRefresh(Lista.Criterios.FECHA));
        toolbar.addSortByTypeListener(e -> sortAndRefresh(Lista.Criterios.TIPO));
    }

    private void bindTree() {
        treePanel.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node =
                (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            if (node == null) return;
            Object obj = node.getUserObject();
            if (obj instanceof VirtualFileSystem.VNode) {
                VirtualFileSystem.VNode vnode = (VirtualFileSystem.VNode) obj;
                if (vnode.isDirectory) navigateTo(vnode);
            }
        });
    }

    private void bindTable() {
        listPanel.addSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String sel = listPanel.getSelectedFileName();
                statusBar.setSelectedCount(sel != null ? 1 : 0);
            }
        });

        listPanel.addDoubleClickListener(name -> {
            if (currentFolder == null) return;
            VirtualFileSystem.VNode target = currentFolder.findChild(name);
            if (target != null && target.isDirectory) navigateTo(target);
        });
    }

    private void bindConfirm() {
        inputPanel.addConfirmListener(e -> {
            switch (inputPanel.getCurrentMode()) {
                case NEW_FOLDER: doCreateFolder(); break;
                case RENAME:     doRename();       break;
                case PASTE:      doPaste();        break;
                case ORGANIZE:   doOrganize();     break;
                default: break;
            }
        });
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    public void navigateTo(VirtualFileSystem.VNode folder) {
        if (folder == null || !folder.isDirectory) return;

        if (currentFolder != null && currentFolder != folder) {
            historyBack.push(currentFolder);
            historyForward.clear();
        }

        currentFolder = folder;
        toolbar.setAddressText(folder.getPath());
        toolbar.setBackEnabled(!historyBack.isEmpty());
        toolbar.setForwardEnabled(!historyForward.isEmpty());

        loadFolderContent(folder);
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        statusBar.setMessage("  " + folder.getPath());
    }

    private void navigateBack() {
        if (historyBack.isEmpty()) return;
        historyForward.push(currentFolder);
        VirtualFileSystem.VNode prev = historyBack.pop();
        currentFolder = null;
        navigateTo(prev);
    }

    private void navigateForward() {
        if (historyForward.isEmpty()) return;
        historyBack.push(currentFolder);
        VirtualFileSystem.VNode next = historyForward.pop();
        currentFolder = null;
        navigateTo(next);
    }

    private void navigateUp() {
        if (currentFolder == null || currentFolder.parent == null) return;
        navigateTo(currentFolder.parent);
    }

    // ── Carga de contenido ────────────────────────────────────────────────────

    private void loadFolderContent(VirtualFileSystem.VNode folder) {
        listaActual.clear();

        List<VirtualFileSystem.VNode> children = vfs.listChildren(folder);
        // Carpetas primero, luego archivos, ambos alfabéticos
        children.sort((a, b) -> {
            if (a.isDirectory && !b.isDirectory) return -1;
            if (!a.isDirectory && b.isDirectory) return 1;
            return a.name.compareToIgnoreCase(b.name);
        });

        for (VirtualFileSystem.VNode vnode : children) {
            listaActual.add(vnodeToNodo(vnode));
        }

        renderLista();
        statusBar.setItemCount(listaActual.size());
    }

    private void renderLista() {
        List<Nodo> nodos = listaActual.toList();
        Object[][] rows = new Object[nodos.size()][4];

        for (int i = 0; i < nodos.size(); i++) {
            Nodo n = nodos.get(i);
            rows[i][0] = n.getNombre();
            rows[i][1] = formatTipo(n.getTipo());
            rows[i][2] = n.getTamanio() == 0 && n.getTipo().equals("Carpeta")
                         ? "—" : formatSize(n.getTamanio());
            rows[i][3] = SDF.format(new java.util.Date(n.getFechaModificacion()));
        }

        String folderName = currentFolder != null ? currentFolder.name : "";
        listPanel.setFiles(rows, folderName);
        statusBar.setItemCount(nodos.size());
    }

    // ── Acciones ──────────────────────────────────────────────────────────────

    private void doCreateFolder() {
        String name = inputPanel.getInputText();
        if (name.isEmpty()) {
            inputPanel.showFeedback("El nombre no puede estar vacío.", false);
            return;
        }
        if (currentFolder.exists(name)) {
            inputPanel.showFeedback("Ya existe una carpeta con ese nombre.", false);
            return;
        }
        VirtualFileSystem.VNode newDir = vfs.createFolder(currentFolder, name);
        if (newDir != null) {
            listaActual.add(vnodeToNodo(newDir));
            renderLista();
            refreshTree();
            inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
            statusBar.setSuccessMessage("Carpeta '" + name + "' creada.");
        } else {
            inputPanel.showFeedback("No se pudo crear la carpeta.", false);
        }
    }

    private void doRename() {
        String newName = inputPanel.getInputText();
        String oldName = listPanel.getSelectedFileName();

        if (newName.isEmpty()) {
            inputPanel.showFeedback("El nuevo nombre no puede estar vacío.", false);
            return;
        }
        if (oldName == null) {
            inputPanel.showFeedback("Selecciona un elemento primero.", false);
            return;
        }
        if (newName.equalsIgnoreCase(oldName)) {
            inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
            return;
        }
        if (currentFolder.exists(newName)) {
            inputPanel.showFeedback("Ya existe un elemento con ese nombre.", false);
            return;
        }

        VirtualFileSystem.VNode target = currentFolder.findChild(oldName);
        if (target != null && vfs.rename(target, newName)) {
            loadFolderContent(currentFolder);   // recarga con nuevo nombre
            refreshTree();
            inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
            statusBar.setSuccessMessage("Renombrado a '" + newName + "'.");
        } else {
            inputPanel.showFeedback("No se pudo renombrar.", false);
        }
    }

    private void doPaste() {
        if (clipboard.isEmpty()) return;
        vfs.copyNodes(clipboard, currentFolder);
        loadFolderContent(currentFolder);
        refreshTree();
        toolbar.setPasteEnabled(false);
        clipboard.clear();
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        statusBar.setSuccessMessage(clipboard.size() + " elemento(s) pegados.");
    }

    private void doOrganize() {
        if (currentFolder == null) return;
        int moved = vfs.organize(currentFolder);
        loadFolderContent(currentFolder);
        refreshTree();
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        if (moved > 0)
            statusBar.setSuccessMessage(moved + " archivo(s) organizados.");
        else
            statusBar.setMessage("No había archivos para organizar.");
    }

    private void sortAndRefresh(Lista.Criterios criterio) {
        if (listaActual.isEmpty()) return;
        listaActual.mergeSort(criterio);
        renderLista();
        statusBar.setMessage("Ordenado por " + criterio.name().toLowerCase() + ".");
    }

    // ── Árbol ─────────────────────────────────────────────────────────────────

    private void refreshTree() {
        DefaultMutableTreeNode rootNode = buildTreeNode(rootFolder);
        treePanel.setRootNode(rootNode);
    }

    private DefaultMutableTreeNode buildTreeNode(VirtualFileSystem.VNode vnode) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(vnode) {
            @Override public String toString() {
                return ((VirtualFileSystem.VNode) getUserObject()).name;
            }
        };
        for (VirtualFileSystem.VNode child : vnode.children) {
            if (child.isDirectory) node.add(buildTreeNode(child));
        }
        return node;
    }

    // ── Conversión VNode → Nodo (lista enlazada) ──────────────────────────────

    /**
     * Crea un Nodo de la lista enlazada a partir de un VNode virtual.
     * Usa un File ficticio como contenedor; los valores reales vienen del VNode.
     */
    private Nodo vnodeToNodo(VirtualFileSystem.VNode vnode) {
        // Creamos un File "fantasma" con la ruta virtual como nombre
        // El Nodo sobreescribirá sus campos con los del VNode
        return new VirtualNodo(vnode);
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private VirtualFileSystem.VNode findNodeByPath(String path) {
        if (path == null || path.isBlank()) return null;
        // Buscar partiendo de la raíz por los segmentos de la ruta
        String[] parts = path.replace("\\", "/").split("/");
        VirtualFileSystem.VNode cur = rootFolder;
        for (String part : parts) {
            if (part.isBlank() || part.equalsIgnoreCase(cur.name)) continue;
            cur = cur.findChild(part);
            if (cur == null) return null;
        }
        return cur;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private String formatTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "carpeta": return "Carpeta";
            case "jpg": case "jpeg": case "png": case "gif": case "bmp": return "Imagen";
            case "mp3": case "wav": case "flac": case "ogg": return "Audio";
            case "docx": case "doc": return "Documento Word";
            case "pdf":  return "PDF";
            case "txt":  return "Texto";
            case "xlsx": return "Hoja de cálculo";
            case "pptx": return "Presentación";
            default: return tipo.isEmpty() ? "Archivo" : tipo.toUpperCase();
        }
    }
}