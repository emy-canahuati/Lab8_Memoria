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
import java.io.File;

public class Main {
    /*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Mainframe frame = new Mainframe();

            FileExplorerController controller = new FileExplorerController(frame);

            File root = new File(System.getProperty("user.home") + File.separator + "Documentos");
            if (!root.exists()) root = new File(System.getProperty("user.home"));

            controller.init(root);
            frame.setVisible(true);
        });
    }
*/
    public static void main(String[] args) {
    java.awt.EventQueue.invokeLater(() -> {
        new Mainframe().setVisible(true);
    });
}
}
