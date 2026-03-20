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
import java.awt.*;
import java.awt.event.ActionListener;

public class ActionInputPanel extends JPanel {

    public enum ActionMode {
        IDLE, NEW_FOLDER, RENAME, COPY, PASTE, ORGANIZE
    }

    private JLabel lblInstruction;
    private JTextField txtInput;
    private JButton btnConfirm;
    private JButton btnCancel;
    private JPanel organizePanel;

    private JRadioButton rdoByType;
    private JRadioButton rdoByDate;
    private JRadioButton rdoByName;
    private ButtonGroup radioGroup;

    private ActionMode currentMode = ActionMode.IDLE;

    private static final Color COLOR_BG = new Color(240, 244, 250);
    private static final Color COLOR_BORDER_TOP = new Color(180, 200, 230);
    private static final Color COLOR_ACCENT = new Color(0, 102, 204);
    private static final Color COLOR_CANCEL_BG = new Color(230, 230, 230);
    private static final Color COLOR_IDLE_TEXT = new Color(100, 100, 120);

    public ActionInputPanel() {
        initComponents();
        buildLayout();
        setMode(ActionMode.IDLE);
    }

    private void initComponents() {

        lblInstruction = new JLabel("Selecciona una acción de la barra de herramientas.");
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInstruction.setForeground(COLOR_IDLE_TEXT);

        txtInput = new JTextField();
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtInput.setPreferredSize(new Dimension(260, 30));
        txtInput.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 180, 200), 1, true), new EmptyBorder(3, 8, 3, 8)));

        btnConfirm = new JButton("Confirmar");
        btnConfirm.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnConfirm.setBackground(COLOR_ACCENT);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 80, 170), 1, true), new EmptyBorder(5, 18, 5, 18)));
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCancel = new JButton("Cancelar");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setBackground(COLOR_CANCEL_BG);
        btnCancel.setForeground(new Color(60, 60, 60));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true), new EmptyBorder(5, 14, 5, 14)));
        btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> setMode(ActionMode.IDLE));

        buildOrganizePanel();
    }

    private void buildOrganizePanel() {
        rdoByType = new JRadioButton("Por tipo de archivo");
        rdoByDate = new JRadioButton("Por fecha");
        rdoByName = new JRadioButton("Por nombre");

        rdoByType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdoByDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rdoByName.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        rdoByType.setBackground(COLOR_BG);
        rdoByDate.setBackground(COLOR_BG);
        rdoByName.setBackground(COLOR_BG);

        rdoByType.setSelected(true);

        radioGroup = new ButtonGroup();
        radioGroup.add(rdoByType);
        radioGroup.add(rdoByDate);
        radioGroup.add(rdoByName);

        organizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        organizePanel.setBackground(COLOR_BG);
        organizePanel.add(new JLabel("Organizar:"));
        organizePanel.add(rdoByType);
        organizePanel.add(rdoByDate);
        organizePanel.add(rdoByName);
    }

    private void buildLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER_TOP), new EmptyBorder(10, 14, 10, 14)));
        setPreferredSize(new Dimension(0, 62));
    }

    public void setMode(ActionMode mode) {
        this.currentMode = mode;
        removeAll();

        switch (mode) {

            case IDLE:
                setIdleLayout();
                break;

            case NEW_FOLDER:
                txtInput.setText("");
                txtInput.setToolTipText("Escribe el nombre de la nueva carpeta");
                lblInstruction.setText("   Nombre para la nueva carpeta:");
                setInputLayout();
                break;

            case RENAME:
                txtInput.setText("");
                txtInput.setToolTipText("Escribe el nuevo nombre");
                lblInstruction.setText("  Nuevo nombre para el elemento seleccionado:");
                setInputLayout();
                break;

            case COPY:
                txtInput.setVisible(false);
                lblInstruction.setText("  Elemento copiado. Navega a la carpeta destino y presiona Pegar.");
                setConfirmOnlyLayout();
                break;

            case PASTE:
                txtInput.setVisible(false);
                lblInstruction.setText("  ¿Confirmar pegado en esta carpeta?");
                setConfirmOnlyLayout();
                break;

            case ORGANIZE:
                lblInstruction.setText("  Elige el criterio de organización:");
                setOrganizeLayout();
                break;
        }

        revalidate();
        repaint();

        if (mode != ActionMode.IDLE && mode != ActionMode.COPY) {
            txtInput.requestFocusInWindow();
        }
    }

    private void setIdleLayout() {
        setLayout(new BorderLayout());
        JLabel idle = new JLabel("  Selecciona una acción de la barra de herramientas.");
        idle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        idle.setForeground(COLOR_IDLE_TEXT);
        add(idle, BorderLayout.CENTER);
    }

    private void setInputLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        txtInput.setVisible(true);
        add(lblInstruction);
        add(txtInput);
        add(btnConfirm);
        add(btnCancel);
    }

    private void setConfirmOnlyLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        add(lblInstruction);
        add(btnConfirm);
        add(btnCancel);
    }

    private void setOrganizeLayout() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        add(lblInstruction);
        add(organizePanel);
        add(btnConfirm);
        add(btnCancel);
    }

    public String getInputText() {
        return txtInput.getText().trim();
    }

    public void clearInput() {
        txtInput.setText("");
    }

    public void setInputText(String text) {
        txtInput.setText(text);
        txtInput.selectAll();
    }

    public ActionMode getCurrentMode() {
        return currentMode;
    }

    public String getOrganizeCriteria() {
        if (rdoByType.isSelected()) {
            return "TYPE";
        }
        if (rdoByDate.isSelected()) {
            return "DATE";
        }
        if (rdoByName.isSelected()) {
            return "NAME";
        }
        return "TYPE";
    }

    public void addConfirmListener(ActionListener l) {
        btnConfirm.addActionListener(l);
    }

    public void showFeedback(String message, boolean success) {
        lblInstruction.setText(message);
        lblInstruction.setForeground(success? new Color(0, 140, 0): new Color(200, 0, 0));
    }

    public void resetFeedbackColor() {
        lblInstruction.setForeground(COLOR_IDLE_TEXT);
    }
}
