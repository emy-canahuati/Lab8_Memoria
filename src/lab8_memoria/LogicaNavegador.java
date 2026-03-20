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
            if(carpetaSelec.isDirectory()){
                    for(File archivo: carpetaSelec.listFiles()){
                        //listaArchivos.add(archivo);
                    }
            }else{
                    return null;
     
            }
            return listaArchivos;
        }
        
        public void organizar(){
            if(carpetaSelec.isDirectory()){
                Lista lista= getFilesList(carpetaSelec);
                for(int contador=0; contador<lista.size();contador++){
                    File archivo =lista.get(contador);
                    if(archivo.isFile()){
                        String tipo= archivo.getTipo();
                        File folder;
                        switch(tipo){
                            case ("IMAGENES"):
                                folder= new File(getPathRaiz()+"/Imagenes");
                                if(!folder.exists())
                                    folder.mkdir();
                                break;
                            case("DOCUMENTOS"):    
                                folder= new File(getPathRaiz()+"/Documentos");
                                if(!folder.exists())
                                    folder.mkdir();
                                break;
                            case ("MUSICA"):
                                folder= new File(getPathRaiz()+"/Musica");
                                if(!folder.exists())
                                    folder.mkdir();
                                break;
                        }
                        File nuevaRuta= new File(folder.getPath()+archivo.getName());
                        archivo.renameTo(nuevaRuta);
                    }
                }
            }else{
                JOptionPane.showMessageDialog(null, "El archivo seleccionado debe ser una carpeta.");
            }
        }
        
        public void crearCarpeta(String nombre){
            if(nombre.isBlank()){
                JOptionPane.showMessageDialog(null, "Debe ingresar un nombre para la nueva carpeta");
                return;
            }
            File carpetaNueva= new File(getPathRaiz()+nombre);
            if(!carpetaNueva.exists())
                carpetaNueva.mkdir();
        }
        
        public void renombrar(File elemento, String nuevoNom){
            File nuevoNombre= new File(getPathRaiz()+nuevoNom);
            elemento.renameTo(nuevoNombre);
            Lista lista=getFilesList(carpetaSelec);
            for(int contador=0;contador<lista.size(); contador++){
                File archivo= lista.get(contador);
                if(archivo.)
            }
        }
        
        
        
        
        
        
}
