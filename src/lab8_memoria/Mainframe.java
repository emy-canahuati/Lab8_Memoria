/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import javax.swing.*;
import java.awt.*;

public class Mainframe extends JFrame {

    private ToolbarPanel toolbarPanel;
    private FolderTreePanel folderTreePanel;
    private FileListPanel fileListPanel;
    private ActionInputPanel actionInputPanel;
    private StatusBarPanel statusBarPanel;

    
    private JSplitPane splitPane;

    
    private static final int WINDOW_WIDTH = 1100;
    private static final int WINDOW_HEIGHT = 680;
    private static final int DIVIDER_LOCATION = 260;

    public Mainframe() {
        super("Explorador de Archivos");
        initComponents();
        buildLayout();
        configureFrame();
    }

    private void initComponents() {
        toolbarPanel = new ToolbarPanel();
        folderTreePanel = new FolderTreePanel();
        fileListPanel = new FileListPanel();
        actionInputPanel = new ActionInputPanel();
        statusBarPanel = new StatusBarPanel();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,folderTreePanel,fileListPanel);
        splitPane.setDividerLocation(DIVIDER_LOCATION);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);
    }

    private void buildLayout() {
        setLayout(new BorderLayout(0, 0));

        add(toolbarPanel, BorderLayout.NORTH);

        add(splitPane, BorderLayout.CENTER);

        JPanel southWrapper = new JPanel(new BorderLayout(0, 0));
        southWrapper.add(actionInputPanel, BorderLayout.NORTH);
        southWrapper.add(statusBarPanel, BorderLayout.SOUTH);
        add(southWrapper, BorderLayout.SOUTH);
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
    }

    public ToolbarPanel getToolbarPanel() {
        return toolbarPanel;
    }

    public FolderTreePanel getFolderTreePanel() {
        return folderTreePanel;
    }

    public FileListPanel getFileListPanel() {
        return fileListPanel;
    }

    public ActionInputPanel getActionInputPanel() {
        return actionInputPanel;
    }

    public StatusBarPanel getStatusBarPanel() {
        return statusBarPanel;
    }
}
