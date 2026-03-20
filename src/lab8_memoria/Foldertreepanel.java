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
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;


public class Foldertreepanel extends JPanel {

    private JTree                  folderTree;
    private DefaultTreeModel       treeModel;
    private DefaultMutableTreeNode rootNode;
    private JScrollPane            scrollPane;
    private JLabel                 headerLabel;

    private static final Color COLOR_BG     = new Color(250, 250, 250);
    private static final Color COLOR_HEADER = new Color(230, 230, 230);
    private static final Color COLOR_SELECT = new Color(204, 232, 255);
    private static final Color COLOR_BORDER = new Color(210, 210, 210);

    public Foldertreepanel() {
        initComponents();
        buildLayout();
        loadDemoTree();
    }

    private void initComponents() {
        rootNode  = new DefaultMutableTreeNode("Documentos");
        treeModel = new DefaultTreeModel(rootNode);

        folderTree = new JTree(treeModel);
        folderTree.setRootVisible(true);
        folderTree.setShowsRootHandles(true);
        folderTree.setBackground(COLOR_BG);
        folderTree.setRowHeight(26);
        folderTree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        folderTree.setBorder(new EmptyBorder(4, 4, 4, 4));
        folderTree.setCellRenderer(new FolderTreeCellRenderer());
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
        add(scrollPane,  BorderLayout.CENTER);
    }

    private void loadDemoTree() {
        DefaultMutableTreeNode trabajo   = new DefaultMutableTreeNode("Trabajo");
        DefaultMutableTreeNode personal  = new DefaultMutableTreeNode("Personal");
        DefaultMutableTreeNode musica    = new DefaultMutableTreeNode("Música");
        DefaultMutableTreeNode imagenes  = new DefaultMutableTreeNode("Imágenes");

        trabajo.add(new DefaultMutableTreeNode("Informes"));
        trabajo.add(new DefaultMutableTreeNode("Presentaciones"));
        personal.add(new DefaultMutableTreeNode("Facturas"));
        musica.add(new DefaultMutableTreeNode("Rock"));
        musica.add(new DefaultMutableTreeNode("Jazz"));
        imagenes.add(new DefaultMutableTreeNode("Vacaciones"));

        rootNode.add(trabajo);
        rootNode.add(personal);
        rootNode.add(musica);
        rootNode.add(imagenes);

        treeModel.reload();
        folderTree.expandRow(0);
    }

    

    public void addFolderToSelected(String folderName) {
        DefaultMutableTreeNode selected = getSelectedNode();
        if (selected == null) selected = rootNode;
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(folderName);
        treeModel.insertNodeInto(newNode, selected, selected.getChildCount());
        TreePath path = new TreePath(newNode.getPath());
        folderTree.scrollPathToVisible(path);
        folderTree.setSelectionPath(path);
    }

    public void renameSelectedNode(String newName) {
        DefaultMutableTreeNode node = getSelectedNode();
        if (node == null || node == rootNode) return;
        node.setUserObject(newName);
        treeModel.nodeChanged(node);
    }

    public void removeSelectedNode() {
        DefaultMutableTreeNode node = getSelectedNode();
        if (node == null || node == rootNode) return;
        treeModel.removeNodeFromParent(node);
    }

    public void setRootNode(DefaultMutableTreeNode newRoot) {
        treeModel.setRoot(newRoot);
        treeModel.reload();
        folderTree.expandRow(0);
    }

    public DefaultMutableTreeNode getSelectedNode() {
        TreePath path = folderTree.getSelectionPath();
        if (path == null) return null;
        return (DefaultMutableTreeNode) path.getLastPathComponent();
    }

    public String getSelectedFolderName() {
        DefaultMutableTreeNode node = getSelectedNode();
        return (node != null) ? node.getUserObject().toString() : null;
    }

    public void addTreeSelectionListener(TreeSelectionListener l) {
        folderTree.addTreeSelectionListener(l);
    }

    

    private static class FolderTreeCellRenderer extends DefaultTreeCellRenderer {

        public FolderTreeCellRenderer() {
            setBackgroundSelectionColor(COLOR_SELECT);
            setBorderSelectionColor(COLOR_SELECT);
            setTextSelectionColor(Color.BLACK);
        }

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean selected,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);

            String name = value.toString();
            ImageIcon icon = IconUtil.folderIconFor(name, expanded, IconUtil.SIZE_TREE);
            if (icon != null) setIcon(icon);

            setText(name);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(new EmptyBorder(2, 4, 2, 4));
            return this;
        }
    }
}
