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
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class IconUtil {

    private static final Map<String, ImageIcon> cache = new HashMap<>();

    public static final int SIZE_TREE = 16;
    public static final int SIZE_TABLE = 20;
    public static final int SIZE_BTN = 16;

    public static final String FOLDER = "folder";
    public static final String FOLDER_OPEN = "folder_open";
    public static final String FOLDER_MUSIC = "folder_music";
    public static final String FOLDER_IMAGES = "folder_images";
    public static final String FOLDER_DOCUMENTS = "folder_documents";

    public static final String FILE_DOC = "file_doc";
    public static final String FILE_TXT = "file_txt";
    public static final String FILE_AUDIO = "file_audio";
    public static final String FILE_IMAGE = "file_image";
    public static final String FILE_GENERIC = "file_generic";

    private IconUtil() {
    }

    public static ImageIcon get(String name, int size) {
        String key = name + "_" + size;
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        String path = "/lab8_memoria/resources/" + name + ".png";
        URL url = IconUtil.class.getResource(path);

        if (url == null) {
            System.err.println("[IconUtil] No se encontró: " + path);
            return null;
        }

        ImageIcon raw = new ImageIcon(url);
        ImageIcon scaled = scale(raw, size);
        cache.put(key, scaled);
        return scaled;
    }

    public static ImageIcon forTree(String name) {
        return get(name, SIZE_TREE);
    }

    public static ImageIcon forTable(String name) {
        return get(name, SIZE_TABLE);
    }

    public static ImageIcon folderIconFor(String folderName, boolean open, int size) {
        String lower = folderName.toLowerCase();

        if (lower.contains("música") || lower.contains("musica") || lower.contains("music")) {
            return get(FOLDER_MUSIC, size);
        }
        if (lower.contains("imagen") || lower.contains("foto") || lower.contains("image")) {
            return get(FOLDER_IMAGES, size);
        }
        if (lower.contains("document") || lower.contains("trabajo") || lower.contains("personal")) {
            return get(FOLDER_DOCUMENTS, size);
        }

        return open ? get(FOLDER_OPEN, size) : get(FOLDER, size);
    }

    public static ImageIcon fileIconFor(String fileName, int size) {
        String lower = fileName.toLowerCase();

        if (lower.endsWith(".doc") || lower.endsWith(".docx")) {
            return get(FILE_DOC, size);
        }
        if (lower.endsWith(".txt")) {
            return get(FILE_TXT, size);
        }
        if (lower.endsWith(".mp3") || lower.endsWith(".wav")
                || lower.endsWith(".flac") || lower.endsWith(".ogg")) {
            return get(FILE_AUDIO, size);
        }
        if (lower.endsWith(".png") || lower.endsWith(".jpg")
                || lower.endsWith(".jpeg") || lower.endsWith(".gif")
                || lower.endsWith(".bmp")) {
            return get(FILE_IMAGE, size);
        }

        return get(FILE_GENERIC, size);
    }

    private static ImageIcon scale(ImageIcon icon, int size) {
        Image scaled = icon.getImage()
                .getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }
}
