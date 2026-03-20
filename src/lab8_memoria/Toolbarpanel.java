package lab8_memoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class Toolbarpanel extends JPanel {

    private JButton btnNewFolder;
    private JButton btnRename;
    private JButton btnCopy;
    private JButton btnPaste;
    private JButton btnOrganize;
    private JButton btnSort;

    private JPopupMenu sortMenu;
    private JMenuItem sortByName;
    private JMenuItem sortBySize;
    private JMenuItem sortByDate;
    private JMenuItem sortByType;

    private JTextField txtAddressBar;
    private JButton btnGoAddress;
    private JButton btnBack;
    private JButton btnForward;
    private JButton btnUp;

    private static final Color COLOR_BG = new Color(245, 245, 245);
    private static final Color COLOR_BTN_NORMAL = new Color(255, 255, 255);
    private static final Color COLOR_BTN_BORDER = new Color(200, 200, 200);
    private static final Color COLOR_SEPARATOR = new Color(210, 210, 210);

    public Toolbarpanel() {
        initComponents();
        buildLayout();
        applyStyles();
    }

    private void initComponents() {
        btnNewFolder = createToolButton("Nueva Carpeta", "Crear una nueva carpeta en la ubicacion actual");
        btnRename = createToolButton("Renombrar", "Cambiar el nombre del elemento seleccionado");
        btnCopy = createToolButton("Copiar", "Copiar el elemento seleccionado al portapapeles");
        btnPaste = createToolButton("Pegar", "Pegar el contenido del portapapeles aqui");
        btnOrganize = createToolButton("Organizar", "Mover archivos a subcarpetas segun su tipo");
        btnSort = createToolButton("Ordenar  v", "Ordenar el contenido por nombre, tipo, fecha o tamanio");

        btnBack = createNavButton("Atras");
        btnForward = createNavButton("Adelante");
        btnUp = createNavButton("Subir");
        btnGoAddress = createNavButton("Ir");

        txtAddressBar = new JTextField("Documentos");
        txtAddressBar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        buildSortMenu();
        btnSort.addActionListener(e -> sortMenu.show(btnSort, 0, btnSort.getHeight()));
    }

    private void buildSortMenu() {
        sortMenu = new JPopupMenu();
        sortByName = new JMenuItem("Por nombre");
        sortBySize = new JMenuItem("Por tamaño");
        sortByDate = new JMenuItem("Por fecha");
        sortByType = new JMenuItem("Por tipo");
        sortMenu.add(sortByName);
        sortMenu.add(sortBySize);
        sortMenu.add(sortByDate);
        sortMenu.add(sortByType);
    }

    private void buildLayout() {
        setLayout(new BorderLayout(0, 0));

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        actionRow.setBackground(COLOR_BG);
        actionRow.setBorder(new EmptyBorder(4, 6, 2, 6));
        actionRow.add(btnNewFolder);
        actionRow.add(makeSeparator());
        actionRow.add(btnRename);
        actionRow.add(btnCopy);
        actionRow.add(btnPaste);
        actionRow.add(makeSeparator());
        actionRow.add(btnOrganize);
        actionRow.add(btnSort);

        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        navButtons.setBackground(COLOR_BG);
        navButtons.add(btnBack);
        navButtons.add(btnForward);
        navButtons.add(btnUp);

        JPanel navRow = new JPanel(new BorderLayout(4, 0));
        navRow.setBackground(COLOR_BG);
        navRow.setBorder(new EmptyBorder(2, 6, 6, 6));
        navRow.add(navButtons, BorderLayout.WEST);
        navRow.add(txtAddressBar, BorderLayout.CENTER);
        navRow.add(btnGoAddress, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(COLOR_BG);
        wrapper.add(actionRow, BorderLayout.NORTH);
        wrapper.add(navRow, BorderLayout.SOUTH);
        add(wrapper, BorderLayout.CENTER);

        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_SEPARATOR);
        add(sep, BorderLayout.SOUTH);
    }

    private void applyStyles() {
        setBackground(COLOR_BG);
        setBorder(null);
        btnPaste.setEnabled(false);
        btnBack.setEnabled(false);
        btnForward.setEnabled(false);
    }

    private JButton createToolButton(String text, String tooltip) {
        JButton btn = new JButton(text);
        btn.setToolTipText(tooltip);
        btn.setFocusPainted(false);
        btn.setBackground(COLOR_BTN_NORMAL);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BTN_BORDER, 1, true),
                new EmptyBorder(4, 10, 4, 10)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setBackground(COLOR_BTN_NORMAL);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BTN_BORDER, 1, true),
                new EmptyBorder(3, 8, 3, 8)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 28));
        return btn;
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setPreferredSize(new Dimension(1, 24));
        sep.setForeground(COLOR_SEPARATOR);
        return sep;
    }

    public void setAddressText(String path) {
        txtAddressBar.setText(path);
    }

    public String getAddressText() {
        return txtAddressBar.getText();
    }

    public void setPasteEnabled(boolean enabled) {
        btnPaste.setEnabled(enabled);
    }

    public void setBackEnabled(boolean enabled) {
        btnBack.setEnabled(enabled);
    }

    public void setForwardEnabled(boolean enabled) {
        btnForward.setEnabled(enabled);
    }

    public void addNewFolderListener(ActionListener l) {
        btnNewFolder.addActionListener(l);
    }

    public void addRenameListener(ActionListener l) {
        btnRename.addActionListener(l);
    }

    public void addCopyListener(ActionListener l) {
        btnCopy.addActionListener(l);
    }

    public void addPasteListener(ActionListener l) {
        btnPaste.addActionListener(l);
    }

    public void addOrganizeListener(ActionListener l) {
        btnOrganize.addActionListener(l);
    }

    public void addBackListener(ActionListener l) {
        btnBack.addActionListener(l);
    }

    public void addForwardListener(ActionListener l) {
        btnForward.addActionListener(l);
    }

    public void addUpListener(ActionListener l) {
        btnUp.addActionListener(l);
    }

    public void addGoAddressListener(ActionListener l) {
        btnGoAddress.addActionListener(l);
    }

    public void addSortByNameListener(ActionListener l) {
        sortByName.addActionListener(l);
    }

    public void addSortBySizeListener(ActionListener l) {
        sortBySize.addActionListener(l);
    }

    public void addSortByDateListener(ActionListener l) {
        sortByDate.addActionListener(l);
    }

    public void addSortByTypeListener(ActionListener l) {
        sortByType.addActionListener(l);
    }
}
