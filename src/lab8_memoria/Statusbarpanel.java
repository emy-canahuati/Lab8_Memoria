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

public class StatusBarPanel extends JPanel {

    private JLabel lblItemCount;
    private JLabel lblSelected;
    private JLabel lblMessage;
    private JProgressBar progressBar;

    private static final Color COLOR_BG = new Color(240, 240, 240);
    private static final Color COLOR_BORDER = new Color(200, 200, 200);
    private static final Color COLOR_TEXT = new Color(80, 80, 80);

    public StatusBarPanel() {
        initComponents();
        buildLayout();
    }

    private void initComponents() {
        Font statusFont = new Font("Segoe UI", Font.PLAIN, 12);

        lblItemCount = new JLabel("0 elementos");
        lblItemCount.setFont(statusFont);
        lblItemCount.setForeground(COLOR_TEXT);

        lblSelected = new JLabel("");
        lblSelected.setFont(statusFont);
        lblSelected.setForeground(COLOR_TEXT);

        lblMessage = new JLabel("Listo");
        lblMessage.setFont(statusFont);
        lblMessage.setForeground(COLOR_TEXT);
        lblMessage.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(120, 14));
        progressBar.setStringPainted(true);
    }

    private void buildLayout() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_BG);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER), new EmptyBorder(3, 10, 3, 10)
        ));
        setPreferredSize(new Dimension(0, 26));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setBackground(COLOR_BG);
        leftPanel.add(lblItemCount);
        leftPanel.add(new JSeparator(JSeparator.VERTICAL) {
            {
                setPreferredSize(new Dimension(1, 14));
                setForeground(COLOR_BORDER);
            }
        });
        leftPanel.add(lblSelected);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        rightPanel.setBackground(COLOR_BG);
        rightPanel.add(progressBar);

        add(leftPanel, BorderLayout.WEST);
        add(lblMessage, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setItemCount(int count) {
        lblItemCount.setText(count + " elemento" + (count != 1 ? "s" : ""));
    }

    public void setSelectedCount(int count) {
        lblSelected.setText(count > 0 ? count + " seleccionado" + (count != 1 ? "s" : "") : "");
    }

    public void setMessage(String message) {
        lblMessage.setText(message);
        lblMessage.setForeground(COLOR_TEXT);
    }

    public void setSuccessMessage(String message) {
        lblMessage.setText("✔ " + message);
        lblMessage.setForeground(new Color(0, 140, 0));
    }

    public void setErrorMessage(String message) {
        lblMessage.setText("✖ " + message);
        lblMessage.setForeground(new Color(190, 0, 0));
    }

    public void setProgressVisible(boolean visible) {
        progressBar.setVisible(visible);
    }

    public void setProgress(int percent) {
        progressBar.setValue(percent);
        progressBar.setString(percent + "%");
    }
}
