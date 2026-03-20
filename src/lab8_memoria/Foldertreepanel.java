package lab8_memoria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;

public class Foldertreepanel extends JPanel {

    private JTree folderTree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JScrollPane scrollPane;
    private JLabel headerLabel;

    private static final Color COLOR_BG = new Color(250, 250, 250);
    private static final Color COLOR_HEADER = new Color(230, 230, 230);
    private static final Color COLOR_SELECT = new Color(204, 232, 255);
    private static final Color COLOR_BORDER = new Color(210, 210, 210);

    public Foldertreepanel() {
        initComponents();
        buildLayout();
    }

    private void initComponents() {
        rootNode = new DefaultMutableTreeNode("Documentos");
        treeModel = new DefaultTreeModel(rootNode);

        folderTree = new JTree(treeModel);
        folderTree.setRootVisible(true);
        folderTree.setShowsRootHandles(true);
        folderTree.setBackground(COLOR_BG);
        folderTree.setRowHeight(26);
        folderTree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        folderTree.setBorder(new EmptyBorder(4, 4, 4, 4));
        folderTree.setCellRenderer(new TreeCellRenderer());
        folderTree.getSelectionModel()
                .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        scrollPane = new JScrollPane(folderTree);
        scrollPane.setBorder(null);

        headerLabel = new JLabel("  Carpetas");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerLabel.setForeground(new Color(80, 80, 80));
        headerLabel.setBackground(COLOR_HEADER);
        headerLabel.setOpaque(true);
        headerLabel.setBorder(new EmptyBorder(6, 8, 6, 8));
        headerLabel.setPreferredSize(new Dimension(0, 30));
    }

    private void buildLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_BORDER));
        setMinimumSize(new Dimension(180, 0));
        setPreferredSize(new Dimension(260, 0));
        add(headerLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setRootNode(DefaultMutableTreeNode newRoot) {
        treeModel.setRoot(newRoot);
        treeModel.reload();
        folderTree.expandRow(0);
    }

    public DefaultMutableTreeNode getSelectedNode() {
        TreePath path = folderTree.getSelectionPath();
        return path == null ? null : (DefaultMutableTreeNode) path.getLastPathComponent();
    }

    public void addTreeSelectionListener(TreeSelectionListener l) {
        folderTree.addTreeSelectionListener(l);
    }

    private static class TreeCellRenderer extends DefaultTreeCellRenderer {

        TreeCellRenderer() {
            setBackgroundSelectionColor(COLOR_SELECT);
            setBorderSelectionColor(COLOR_SELECT);
            setTextSelectionColor(Color.BLACK);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            if (value instanceof DefaultMutableTreeNode) {
                Object obj = ((DefaultMutableTreeNode) value).getUserObject();
                if (obj instanceof Nodo) {
                    Nodo nodo = (Nodo) obj;
                    setText(nodo.nombre);
                    if (nodo.esDirectorio) {
                        ImageIcon icon = IconUtil.folderIconFor(nodo.nombre, expanded, IconUtil.SIZE_TREE);
                        if (icon != null) {
                            setIcon(icon);
                        }
                    } else {
                        ImageIcon icon = IconUtil.fileIconFor(nodo.nombre, IconUtil.SIZE_TREE);
                        if (icon != null) {
                            setIcon(icon);
                        }
                    }
                } else {
                    setText(obj.toString());
                    ImageIcon icon = IconUtil.folderIconFor(obj.toString(), expanded, IconUtil.SIZE_TREE);
                    if (icon != null) {
                        setIcon(icon);
                    }
                }
            }

            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(new EmptyBorder(2, 4, 2, 4));
            return this;
        }
    }
}
