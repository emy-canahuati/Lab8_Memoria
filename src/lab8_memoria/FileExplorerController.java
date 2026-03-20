package lab8_memoria;

import javax.swing.*;
import javax.swing.tree.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileExplorerController {

    private final Mainframe frame;
    private final Toolbarpanel toolbar;
    private final Foldertreepanel treePanel;
    private final Filelistpanel listPanel;
    private final Actioninputpanel inputPanel;
    private final Statusbarpanel statusBar;

    private final LogicaNavegador logica = new LogicaNavegador();

    private final Deque<Nodo> historialAtras = new ArrayDeque<>();
    private final Deque<Nodo> historialAdelante = new ArrayDeque<>();

    private Lista listaActual = new Lista();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

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

    public void init() {
        refrescarArbol();
        navegarA(logica.getRaiz());
    }

    private void bindToolbar() {
        toolbar.addNewFolderListener(e -> {
            if (logica.carpetaActual == null) {
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
            List<String> seleccionados = listPanel.getSelectedFileNames();
            if (seleccionados.isEmpty()) {
                statusBar.setErrorMessage("Selecciona al menos un elemento para copiar.");
                return;
            }
            List<Nodo> nodos = new ArrayList<>();
            for (String nombre : seleccionados) {
                Nodo n = logica.carpetaActual.buscarHijo(nombre);
                if (n != null) {
                    nodos.add(n);
                }
            }
            if (nodos.isEmpty()) {
                return;
            }
            logica.copiar(nodos);
            toolbar.setPasteEnabled(true);
            inputPanel.setMode(Actioninputpanel.ActionMode.COPY);
            statusBar.setMessage(nodos.size() + " elemento(s) copiados.");
        });

        toolbar.addPasteListener(e -> {
            if (logica.portapapelesVacio()) {
                return;
            }
            inputPanel.setMode(Actioninputpanel.ActionMode.PASTE);
        });

        toolbar.addOrganizeListener(e -> inputPanel.setMode(Actioninputpanel.ActionMode.ORGANIZE));

        toolbar.addBackListener(e -> navegarAtras());
        toolbar.addForwardListener(e -> navegarAdelante());
        toolbar.addUpListener(e -> navegarArriba());

        toolbar.addGoAddressListener(e -> {
            String ruta = toolbar.getAddressText().trim();
            Nodo target = logica.buscarPorRuta(ruta);
            if (target != null && target.esDirectorio) {
                navegarA(target);
            } else {
                statusBar.setErrorMessage("Ruta no encontrada: " + ruta);
            }
        });

        toolbar.addSortByNameListener(e -> ordenarYRefrescar(Lista.Criterios.NOMBRE));
        toolbar.addSortBySizeListener(e -> ordenarYRefrescar(Lista.Criterios.TAMANIO));
        toolbar.addSortByDateListener(e -> ordenarYRefrescar(Lista.Criterios.FECHA));
        toolbar.addSortByTypeListener(e -> ordenarYRefrescar(Lista.Criterios.TIPO));
    }

    private void bindTree() {
        treePanel.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            if (node == null) {
                return;
            }
            Object obj = node.getUserObject();
            if (obj instanceof Nodo) {
                Nodo nodo = (Nodo) obj;
                if (nodo.esDirectorio) {
                    navegarA(nodo);
                }
            }
        });
    }

    private void bindTable() {
        listPanel.addSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int count = listPanel.getSelectedFileNames().size();
                statusBar.setSelectedCount(count);
            }
        });
        listPanel.addDoubleClickListener(nombre -> {
            if (logica.carpetaActual == null) {
                return;
            }
            Nodo target = logica.carpetaActual.buscarHijo(nombre);
            if (target != null && target.esDirectorio) {
                navegarA(target);
            }
        });
    }

    private void bindConfirm() {
        inputPanel.addConfirmListener(e -> {
            switch (inputPanel.getCurrentMode()) {
                case NEW_FOLDER:
                    hacerCrearCarpeta();
                    break;
                case RENAME:
                    hacerRenombrar();
                    break;
                case PASTE:
                    hacerPegar();
                    break;
                case ORGANIZE:
                    hacerOrganizar();
                    break;
                default:
                    break;
            }
        });
    }

    public void navegarA(Nodo carpeta) {
        if (carpeta == null || !carpeta.esDirectorio) {
            return;
        }
        Nodo actual = logica.carpetaActual;
        if (actual != null && actual != carpeta) {
            historialAtras.push(actual);
            historialAdelante.clear();
        }
        logica.setCarpetaActual(carpeta);
        toolbar.setAddressText(carpeta.getRuta());
        toolbar.setBackEnabled(!historialAtras.isEmpty());
        toolbar.setForwardEnabled(!historialAdelante.isEmpty());
        cargarContenido(carpeta);
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        statusBar.setMessage("  " + carpeta.getRuta());
    }

    private void navegarAtras() {
        if (historialAtras.isEmpty()) {
            return;
        }
        historialAdelante.push(logica.carpetaActual);
        Nodo prev = historialAtras.pop();
        logica.setCarpetaActual(null);
        navegarA(prev);
    }

    private void navegarAdelante() {
        if (historialAdelante.isEmpty()) {
            return;
        }
        historialAtras.push(logica.carpetaActual);
        Nodo next = historialAdelante.pop();
        logica.setCarpetaActual(null);
        navegarA(next);
    }

    private void navegarArriba() {
        if (logica.carpetaActual == null || logica.carpetaActual.padre == null) {
            return;
        }
        navegarA(logica.carpetaActual.padre);
    }

    private void cargarContenido(Nodo carpeta) {
        listaActual = logica.getContenido(carpeta);
        renderizarLista();
        statusBar.setItemCount(listaActual.size());
    }

    private void renderizarLista() {
        List<Nodo> nodos = listaActual.toList();
        Object[][] filas = new Object[nodos.size()][4];
        for (int i = 0; i < nodos.size(); i++) {
            Nodo n = nodos.get(i);
            filas[i][0] = n.getNombre();
            filas[i][1] = formatTipo(n.getTipo());
            filas[i][2] = n.esDirectorio ? "—" : formatSize(n.getTamanio());
            filas[i][3] = SDF.format(new Date(n.getFechaModificacion()));
        }
        String nombreCarpeta = logica.carpetaActual != null ? logica.carpetaActual.nombre : "";
        listPanel.setFiles(filas, nombreCarpeta);
        statusBar.setItemCount(nodos.size());
    }

    private void hacerCrearCarpeta() {
        String nombre = inputPanel.getInputText();
        if (nombre.isEmpty()) {
            inputPanel.showFeedback("El nombre no puede estar vacio.", false);
            return;
        }
        if (logica.carpetaActual.existeHijo(nombre)) {
            inputPanel.showFeedback("Ya existe una carpeta con ese nombre.", false);
            return;
        }
        Nodo nueva = logica.crearCarpeta(nombre);
        if (nueva != null) {
            cargarContenido(logica.carpetaActual);
            refrescarArbol();
            inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
            statusBar.setSuccessMessage("Carpeta '" + nombre + "' creada.");
        } else {
            inputPanel.showFeedback("No se pudo crear la carpeta.", false);
        }
    }

    private void hacerRenombrar() {
        String nuevoNombre = inputPanel.getInputText();
        String nombreViejo = listPanel.getSelectedFileName();
        if (nuevoNombre.isEmpty()) {
            inputPanel.showFeedback("El nuevo nombre no puede estar vacio.", false);
            return;
        }
        if (nombreViejo == null) {
            inputPanel.showFeedback("Selecciona un elemento primero.", false);
            return;
        }
        if (nuevoNombre.equalsIgnoreCase(nombreViejo)) {
            inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
            return;
        }
        if (logica.carpetaActual.existeHijo(nuevoNombre)) {
            inputPanel.showFeedback("Ya existe un elemento con ese nombre.", false);
            return;
        }
        Nodo target = logica.carpetaActual.buscarHijo(nombreViejo);
        if (target != null && logica.renombrar(target, nuevoNombre)) {
            cargarContenido(logica.carpetaActual);
            refrescarArbol();
            inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
            statusBar.setSuccessMessage("Renombrado a '" + nuevoNombre + "'.");
        } else {
            inputPanel.showFeedback("No se pudo renombrar.", false);
        }
    }

    private void hacerPegar() {
        int pegados = logica.pegar();
        toolbar.setPasteEnabled(false);
        cargarContenido(logica.carpetaActual);
        refrescarArbol();
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        statusBar.setSuccessMessage(pegados + " elemento(s) pegados.");
    }

    private void hacerOrganizar() {
        int movidos = logica.organizar();
        cargarContenido(logica.carpetaActual);
        refrescarArbol();
        inputPanel.setMode(Actioninputpanel.ActionMode.IDLE);
        if (movidos > 0) {
            statusBar.setSuccessMessage(movidos + " archivo(s) organizados por tipo.");
        } else {
            statusBar.setMessage("No habia archivos para organizar en esta carpeta.");
        }
    }

    private void ordenarYRefrescar(Lista.Criterios criterio) {
        if (listaActual.isEmpty()) {
            return;
        }
        listaActual.mergeSort(criterio);
        renderizarLista();
        statusBar.setMessage("Ordenado por " + criterio.name().toLowerCase() + ".");
    }

    private void refrescarArbol() {
        DefaultMutableTreeNode raiz = construirNodoArbol(logica.getRaiz());
        treePanel.setRootNode(raiz);
    }

    private DefaultMutableTreeNode construirNodoArbol(Nodo nodo) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(nodo) {
            @Override
            public String toString() {
                return ((Nodo) getUserObject()).nombre;
            }
        };
        for (Nodo hijo : nodo.hijos) {
            treeNode.add(construirNodoArbol(hijo));
        }
        return treeNode;
    }

    private String formatSize(long bytes) {
        if (bytes < 1_024) {
            return bytes + " B";
        }
        if (bytes < 1_048_576) {
            return String.format("%.1f KB", bytes / 1_024.0);
        }
        if (bytes < 1_073_741_824L) {
            return String.format("%.1f MB", bytes / 1_048_576.0);
        }
        return String.format("%.1f GB", bytes / 1_073_741_824.0);
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
            case "aac":
                return "Audio";
            case "docx":
            case "doc":
                return "Documento Word";
            case "pdf":
                return "PDF";
            case "txt":
                return "Texto";
            case "xlsx":
                return "Hoja de calculo";
            case "pptx":
                return "Presentacion";
            default:
                return tipo.isEmpty() ? "Archivo" : tipo.toUpperCase();
        }
    }
}
