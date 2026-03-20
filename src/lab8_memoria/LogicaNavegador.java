/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.io.File;
import javax.swing.JOptionPane;

/**
 *
 * @author emyca
 */
public class LogicaNavegador {
        File carpetaSelec;
        
        public void setCarpetaRaiz(String path){
            carpetaSelec= new File(path);
        }
        
        public String getPathRaiz(){
            return carpetaSelec.getPath();
        }
        
        public Lista getFilesList(File carpetaSelec){
            Lista listaArchivos= new Lista();
            
            if (carpetaSelec==null || !carpetaSelec.isDirectory()) {
                return null;
            }
            
            if(carpetaSelec.listFiles()==null)
                return null;
            
            for(File archivo : carpetaSelec.listFiles()) {
                listaArchivos.add(new Nodo(archivo));
            }
     
            return listaArchivos;
        }

    public void organizar() {
        if (carpetaSelec.isDirectory()) {
            JOptionPane.showMessageDialog(null, "El archivo seleccionado debe ser una carpeta.");

            Lista lista = getFilesList(carpetaSelec);
            for (Nodo nodo : lista.toList()) {
                File archivo = nodo.getArchivo();
                if (archivo.isFile()) {
                    String tipo = nodo.getTipo();
                    String folder = null;
                    switch (tipo) {
                        case ("png"), ("jpg"), ("gif") -> folder = "Imagenes";
                        case ("pdf"), ("docx"), ("txt") -> folder = "Documentos";
                        case ("mp3"), ("wav") -> folder = "Musica";
                    }
                    File carpetaFolder = new File(getPathRaiz()+"/"+folder);
                    if (!carpetaFolder.exists()) {
                        carpetaFolder.mkdir();
                    }
                    File nuevaRuta = new File(carpetaFolder.getPath()+"/"+archivo.getName());
                    archivo.renameTo(nuevaRuta);
                }
            }
        }
    }    
        

    public void crearCarpeta(String nombre) {
        if (nombre.isBlank() || nombre.equals(" ")) {
            JOptionPane.showMessageDialog(null, "Debe ingresar un nombre para la nueva carpeta");
            return;
        }
        File carpetaNueva = new File(getPathRaiz()+"/"+nombre);
        if (!carpetaNueva.exists()) {
            carpetaNueva.mkdir();
        }
    }
      
    public void renombrar(File elemento, String nuevoNom) {
        File nuevoNombre = new File(getPathRaiz()+"/"+ nuevoNom);
        elemento.renameTo(nuevoNombre);
    }    
}
    
        
        
        

