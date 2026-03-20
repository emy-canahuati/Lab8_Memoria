/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

/**
 *
 * @author janinadiaz
 */
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileExplorerController {

    private final Mainframe frame;
    private final Toolbarpanel toolbar;
    private final Foldertreepanel treePanel;
    private final Filelistpanel listPanel;
    private final Actioninputpanel inputPanel;
    private final Statusbarpanel statusBar;

    private File currentFolder;
    private final Deque<File> historyBack = new ArrayDeque<>();
    private final Deque<File> historyForward = new ArrayDeque<>();

    private List<File> clipboard = new ArrayList<>();
    private boolean cutMode = false;

    private Lista listaActual = new Lista();

    private static final SimpleDateFormat SDF
            = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public FileExplorerController(Mainframe frame) {
        this.frame = frame;
        this.toolbar = frame.getToolbarPanel();
        this.treePanel = frame.getFolderTreePanel();
        this.listPanel = frame.getFileListPanel();
        this.inputPanel = frame.getActionInputPanel();
        this.statusBar = frame.getStatusBarPanel();

        bindToolbar();
        bindTree();
        bindTable();
        bindConfirm();
    }

    public void init(File root) {
        DefaultMutableTreeNode rootNode = buildTreeNode(root);
        treePanel.setRootNode(rootNode);
        navigateTo(root);
    }

    private void bindToolbar() {
        toolbar.addNewFolderListener(e -> {
            if (currentFolder == null) {
                return;
            }
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
            clipboard.add(new File(currentFolder, sel));
            cutMode = false;
            toolbar.setPasteEnabled(true);
            inputPanel.setMode(Actioninputpanel.ActionMode.COPY);
            statusBar.setMessage("Copiado: " + sel);
        });

        toolbar.addPasteListener(e -> {
            if (clipboard.isEmpty()) {
                return;
            }
            inputPanel.setMode(Actioninputpanel.ActionMode.PASTE);
        });

        toolbar.addOrganizeListener(e
                -> inputPanel.setMode(Actioninputpanel.ActionMode.ORGANIZE)
        );

        toolbar.addBackListener(e -> navigateBack());
        toolbar.addForwardListener(e -> navigateForward());
        toolbar.addUpListener(e -> navigateUp());

        toolbar.addGoAddressListener(e -> {
            File target = new File(toolbar.getAddressText());
            if (target.isDirectory()) {
                navigateTo(target);
            } else {
                statusBar.setErrorMessage("Ruta no válida.");
            }
        });

        toolbar.addSortByNameListener(e -> sortAndRefresh(Lista.Criterios.NOMBRE));
        toolbar.addSortBySizeListener(e -> sortAndRefresh(Lista.Criterios.TAMANIO));
        toolbar.addSortByDateListener(e -> sortAndRefresh(Lista.Criterios.FECHA));
        toolbar.addSortByTypeListener(e -> sortAndRefresh(Lista.Criterios.TIPO));
    }

    private void bindTree() {
        treePanel.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node
                    = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            if (node == null) {
                return;
            }
            Object obj = node.getUserObject();
            if (obj instanceof File) {
                navigateTo((File) obj);
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
            File target = new File(currentFolder, name);
            if (target.isDirectory()) {
                navigateTo(target);
            }
        });
    }

    private void bindConfirm() {
        inputPanel.addConfirmListener(e -> {
            switch (inputPanel.getCurrentMode()) {
                case NEW_FOLDER:
                    doCreateFolder();
                    break;
                case RENAME:
                    doRename();
                    break;
                case PASTE:
                    doPaste();
                    break;
                case ORGANIZE:
                    doOrganize();
                    break;
                default:
                    break;
            }
        });
    }

    public void navigateTo(File folder) {
        if (folder == null || !folder.isDirectory()) {

            if (folder != null && folder.isFile()) {
                navigateTo(folder.getParentFile());
            }
            return;
        }

        if (currentFolder != null && !currentFolder.equals(folder)) {
            historyBack.push(currentFolder);
            historyForward.clear();
        }

        currentFolder = folder;
        toolbar.setAddressText(folder.getAbsolutePath());
        toolbar.setBackEnabled(!historyBack.isEmpty());
        toolbar.setForwardEnabled(!historyForward.isEmpty());

        loadFolderContent(folder);
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        statusBar.setMessage("  " + folder.getAbsolutePath());
    }

    private void navigateBack() {
        if (historyBack.isEmpty()) {
            return;
        }
        historyForward.push(currentFolder);
        File prev = historyBack.pop();
        currentFolder = null;
        navigateTo(prev);
    }

    private void navigateForward() {
        if (historyForward.isEmpty()) {
            return;
        }
        historyBack.push(currentFolder);
        File next = historyForward.pop();
        currentFolder = null;
        navigateTo(next);
    }

    private void navigateUp() {
        if (currentFolder == null) {
            return;
        }
        File parent = currentFolder.getParentFile();
        if (parent != null) {
            navigateTo(parent);
        }
    }

    private void loadFolderContent(File folder) {
        listaActual.clear();

        File[] files = folder.listFiles();
        if (files != null) {

            Arrays.sort(files, (a, b) -> {
                if (a.isDirectory() && !b.isDirectory()) {
                    return -1;
                }
                if (!a.isDirectory() && b.isDirectory()) {
                    return 1;
                }
                return a.getName().compareToIgnoreCase(b.getName());
            });
            for (File f : files) {
                listaActual.add(new Nodo(f));
            }
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
            rows[i][2] = n.getArchivo().isDirectory() ? "—" : formatSize(n.getTamanio());
            rows[i][3] = SDF.format(new Date(n.getFechaModificacion()));
        }

        String folderName = currentFolder != null ? currentFolder.getName() : "";
        listPanel.setFiles(rows, folderName);
        statusBar.setItemCount(nodos.size());
    }

    private void doCreateFolder() {
        String name = inputPanel.getInputText();

        if (name.isEmpty()) {
            inputPanel.showFeedback("El nombre no puede estar vacío.", false);
            return;
        }

        File newDir = new File(currentFolder, name);

        if (newDir.exists()) {
            inputPanel.showFeedback("Ya existe una carpeta con ese nombre.", false);
            return;
        }

        if (newDir.mkdir()) {

            refreshTree();

            listaActual.add(new Nodo(newDir));
            renderLista();
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

        File src = new File(currentFolder, oldName);
        File dest = new File(currentFolder, newName);

        if (dest.exists()) {
            inputPanel.showFeedback("Ya existe un elemento con ese nombre.", false);
            return;
        }

        if (src.renameTo(dest)) {
            listPanel.renameSelectedRow(newName);
            // Actualizar nodo en lista enlazada
            Nodo nodo = listaActual.get(oldName);
            if (nodo != null) {
                listaActual.remove(oldName);
                listaActual.add(new Nodo(dest));
                renderLista();
            }
            refreshTree();
            inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
            statusBar.setSuccessMessage("Renombrado a '" + newName + "'.");
        } else {
            inputPanel.showFeedback("No se pudo renombrar.", false);
        }
    }

    private void doPaste() {
        if (clipboard.isEmpty()) {
            return;
        }

        int ok = 0, fail = 0;
        for (File src : clipboard) {
            File dest = new File(currentFolder, src.getName());

            dest = resolveConflict(dest);
            try {
                if (src.isDirectory()) {
                    copyDirectory(src.toPath(), dest.toPath());
                } else {
                    Files.copy(src.toPath(), dest.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                if (cutMode) {
                    src.delete();
                }
                ok++;
                listaActual.add(new Nodo(dest));
            } catch (IOException ex) {
                fail++;
            }
        }

        if (!cutMode) {
            clipboard.clear();
        }
        cutMode = false;
        toolbar.setPasteEnabled(false);

        renderLista();
        refreshTree();
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);

        if (fail == 0) {
            statusBar.setSuccessMessage(ok + " elemento(s) pegados.");
        } else {
            statusBar.setErrorMessage(fail + " elemento(s) fallaron.");
        }
    }

    private void doOrganize() {
        if (currentFolder == null) {
            return;
        }

        Map<String, String[]> rules = new LinkedHashMap<>();
        rules.put("Imagenes", new String[]{"jpg", "jpeg", "png", "gif", "bmp"});
        rules.put("Documentos", new String[]{"pdf", "docx", "doc", "txt", "xlsx", "pptx"});
        rules.put("Musica", new String[]{"mp3", "wav", "flac", "ogg", "aac"});

        int moved = 0;
        File[] files = currentFolder.listFiles(File::isFile);
        if (files == null) {
            statusBar.setErrorMessage("No hay archivos para organizar.");
            return;
        }

        statusBar.setProgressVisible(true);

        for (File file : files) {
            String ext = getExtension(file.getName());
            for (Map.Entry<String, String[]> entry : rules.entrySet()) {
                if (Arrays.asList(entry.getValue()).contains(ext)) {
                    File destDir = new File(currentFolder, entry.getKey());
                    if (!destDir.exists()) {
                        destDir.mkdir();
                    }
                    File dest = resolveConflict(new File(destDir, file.getName()));
                    try {
                        Files.move(file.toPath(), dest.toPath(),
                                StandardCopyOption.REPLACE_EXISTING);
                        moved++;
                    } catch (IOException ignored) {
                    }
                    break;
                }
            }
        }

        statusBar.setProgressVisible(false);
        loadFolderContent(currentFolder);
        refreshTree();
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        statusBar.setSuccessMessage(moved + " archivo(s) organizados.");
    }

    private void sortAndRefresh(Lista.Criterios criterio) {
        if (listaActual.isEmpty()) {
            return;
        }
        listaActual.mergeSort(criterio);
        renderLista();
        statusBar.setMessage("Ordenado por " + criterio.name().toLowerCase() + ".");
    }

    public DefaultMutableTreeNode buildTreeNode(File folder) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder) {
            @Override
            public String toString() {
                return ((File) getUserObject()).getName();
            }
        };

        File[] children = folder.listFiles(File::isDirectory);
        if (children != null) {
            Arrays.sort(children,
                    (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            for (File child : children) {
                node.add(buildTreeNode(child));
            }
        }
        return node;
    }

    private void refreshTree() {

        File root = currentFolder;

        DefaultMutableTreeNode rootNode = buildTreeNode(root);
        treePanel.setRootNode(rootNode);
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        }
        if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024));
        }
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    private String formatTipo(String tipo) {
        switch (tipo.toLowerCase()) {
            case "carpeta":
                return "Carpeta";
            case "jpg":
            case "jpeg":
            case "png":
            case "gif":
            case "bmp":
                return "Imagen";
            case "mp3":
            case "wav":
            case "flac":
            case "ogg":
                return "Audio";
            case "doc":
            case "docx":
                return "Documento Word";
            case "pdf":
                return "PDF";
            case "txt":
                return "Texto";
            default:
                return tipo.toUpperCase();
        }
    }

    private String getExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot >= 0 && dot < name.length() - 1) {
            return name.substring(dot + 1).toLowerCase();
        }
        return "";
    }

    private File resolveConflict(File dest) {
        if (!dest.exists()) {
            return dest;
        }
        String name = dest.getName();
        String base = name.contains(".") ? name.substring(0, name.lastIndexOf('.')) : name;
        String ext = name.contains(".") ? name.substring(name.lastIndexOf('.')) : "";
        int counter = 1;
        File candidate;
        do {
            candidate = new File(dest.getParent(), base + "_" + counter + ext);
            counter++;
        } while (candidate.exists());
        return candidate;
    }

    private void copyDirectory(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(source -> {
            try {
                Files.copy(source, dest.resolve(src.relativize(source)),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ignored) {
            }
        });
    }
}
