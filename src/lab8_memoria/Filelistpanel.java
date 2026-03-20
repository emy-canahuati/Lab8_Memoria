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
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FileListPanel extends JPanel {

    private static final String[] COLUMN_NAMES = {"Nombre", "Tipo", "Tamaño", "Fecha de modificación"};
    private static final int[] COLUMN_WIDTHS = {300, 120, 90, 180};

    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JLabel headerLabel;
    private JLabel emptyLabel;
    private JPanel contentPanel;

    private static final Color COLOR_BG = Color.WHITE;
    private static final Color COLOR_HEADER_BG = new Color(230, 230, 230);
    private static final Color COLOR_ROW_ALT = new Color(247, 247, 247);
    private static final Color COLOR_SELECT = new Color(204, 232, 255);
    private static final Color COLOR_GRID = new Color(235, 235, 235);
    private static final Color COLOR_EMPTY = new Color(160, 160, 160);

    public FileListPanel() {
        initComponents();
        buildLayout();
        loadDemoData();
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                return c == 0 ? ImageIcon.class : Object.class;
            }
        };

        fileTable = new JTable(tableModel);
        fileTable.setBackground(COLOR_BG);
        fileTable.setSelectionBackground(COLOR_SELECT);
        fileTable.setSelectionForeground(Color.BLACK);
        fileTable.setGridColor(COLOR_GRID);
        fileTable.setRowHeight(28);
        fileTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fileTable.setShowVerticalLines(false);
        fileTable.setIntercellSpacing(new Dimension(0, 1));
        fileTable.setFillsViewportHeight(true);
        fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JTableHeader header = fileTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(COLOR_HEADER_BG);
        header.setForeground(new Color(60, 60, 60));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 30));

        for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
            fileTable.getColumnModel().getColumn(i).setPreferredWidth(COLUMN_WIDTHS[i]);
        }

        fileTable.getColumnModel().getColumn(0).setCellRenderer(new FileNameCellRenderer());
        TableCellRenderer altRenderer = new AltRowRenderer();
        fileTable.getColumnModel().getColumn(1).setCellRenderer(altRenderer);
        fileTable.getColumnModel().getColumn(2).setCellRenderer(new SizeColumnRenderer());
        fileTable.getColumnModel().getColumn(3).setCellRenderer(altRenderer);

        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onRowDoubleClicked(fileTable.getSelectedRow());
                }
            }
        });

        scrollPane = new JScrollPane(fileTable);
        scrollPane.setBorder(null);
        scrollPane.setBackground(COLOR_BG);

        headerLabel = new JLabel("  Contenido");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerLabel.setForeground(new Color(80, 80, 80));
        headerLabel.setBackground(COLOR_HEADER_BG);
        headerLabel.setOpaque(true);
        headerLabel.setBorder(new EmptyBorder(6, 8, 6, 8));
        headerLabel.setPreferredSize(new Dimension(0, 30));

        emptyLabel = new JLabel("Esta carpeta está vacía", SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        emptyLabel.setForeground(COLOR_EMPTY);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(COLOR_BG);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void buildLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BG);
        add(headerLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void loadDemoData() {

        Object[][] rows = {
            {"Informes", "Carpeta", "—", "20/03/2025 10:30"},
            {"Presentaciones", "Carpeta", "—", "19/03/2025 14:00"},
            {"Proyecto_final.docx", "Documento Word", "128 KB", "18/03/2025 09:15"},
            {"Notas.txt", "Texto", "4 KB", "17/03/2025 11:45"},
            {"cancion.mp3", "Audio", "4.2 MB", "15/03/2025 08:00"},
            {"foto_verano.png", "Imagen", "2.8 MB", "10/03/2025 16:20"},};
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
        updateHeader("Documentos", tableModel.getRowCount());
    }

    private void onRowDoubleClicked(int rowIndex) {
        if (rowIndex < 0) {
            return;
        }
        String name = tableModel.getValueAt(rowIndex, 0).toString();
        String type = tableModel.getValueAt(rowIndex, 1).toString();

        System.out.println("[UI] Doble clic en: " + name + " (" + type + ")");
    }

    public void setFiles(Object[][] rows, String folderName) {
        tableModel.setRowCount(0);
        for (Object[] row : rows) {
            tableModel.addRow(row);
        }
        showEmptyState(rows.length == 0);
        updateHeader(folderName, rows.length);
    }

    public void addFileRow(Object[] row) {
        tableModel.addRow(row);
        showEmptyState(false);
        int last = tableModel.getRowCount() - 1;
        fileTable.scrollRectToVisible(fileTable.getCellRect(last, 0, true));
        updateHeader(getCurrentFolderName(), tableModel.getRowCount());
    }

    public void removeSelectedRow() {
        int row = fileTable.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
            if (tableModel.getRowCount() == 0) {
                showEmptyState(true);
            }
            updateHeader(getCurrentFolderName(), tableModel.getRowCount());
        }
    }

    public void renameSelectedRow(String newName) {
        int row = fileTable.getSelectedRow();
        if (row >= 0) {
            tableModel.setValueAt(newName, row, 0);
        }
    }

    public String getSelectedFileName() {
        int row = fileTable.getSelectedRow();
        return row < 0 ? null : tableModel.getValueAt(row, 0).toString();
    }

    public int getSelectedRowIndex() {
        return fileTable.getSelectedRow();
    }

    public void addDoubleClickListener(java.util.function.Consumer<String> listener) {
        fileTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = fileTable.getSelectedRow();
                    if (row >= 0) {
                        listener.accept(tableModel.getValueAt(row, 0).toString());
                    }
                }
            }
        });
    }

    public void addSelectionListener(javax.swing.event.ListSelectionListener l) {
        fileTable.getSelectionModel().addListSelectionListener(l);
    }

    private void updateHeader(String folder, int count) {
        headerLabel.setText("  " + folder + "   (" + count + " elemento" + (count != 1 ? "s" : "") + ")");
    }

    private String getCurrentFolderName() {
        String t = headerLabel.getText().trim();
        int i = t.indexOf("(");
        return i > 0 ? t.substring(0, i).trim() : t;
    }

    private void showEmptyState(boolean empty) {
        contentPanel.removeAll();
        contentPanel.add(empty ? emptyLabel : scrollPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private class FileNameCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {

            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

            String name = value != null ? value.toString() : "";
            String type = tableModel.getValueAt(row, 1).toString();

            ImageIcon icon;
            if (type.equalsIgnoreCase("Carpeta")) {
                icon = IconUtil.folderIconFor(name, false, IconUtil.SIZE_TABLE);
            } else {
                icon = IconUtil.fileIconFor(name, IconUtil.SIZE_TABLE);
            }
            setIcon(icon);
            setText(name);

            styleCell(this, isSelected, row);
            setHorizontalAlignment(LEFT);
            setBorder(new EmptyBorder(0, 6, 0, 4));
            return this;
        }
    }

    private class AltRowRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            styleCell(this, isSelected, row);
            setBorder(new EmptyBorder(0, 8, 0, 4));
            return this;
        }
    }

    private class SizeColumnRenderer extends AltRowRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(RIGHT);
            setBorder(new EmptyBorder(0, 4, 0, 12));
            return this;
        }
    }

    private void styleCell(JLabel label, boolean selected, int row) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (!selected) {
            label.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_ROW_ALT);
            label.setForeground(Color.BLACK);
        } else {
            label.setBackground(COLOR_SELECT);
            label.setForeground(Color.BLACK);
        }
    }
}
