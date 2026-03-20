/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab8_memoria;

import java.util.*;
/**
 *
 * @author emyca
 */
public class Lista {
    
    public enum Criterios{
        NOMBRE, FECHA, TIPO, TAMANIO;
    }
    
    private Nodo inicio = null;
    private int size = 0;
    
    public boolean isEmpty(){
        return inicio == null;
    }
    
    public int size(){
        return size;
    }
    
    public void add(Nodo data){
        if(data==null) return;
        data.next = null;
        if(isEmpty()){
            inicio = data;
        } else {
            Nodo tmp = inicio;
            while(tmp.next != null) tmp = tmp.next;
            tmp.next = data;
        }
        size++;
    }
    
    public Nodo get(String nombre){
        Nodo tmp = inicio;
        while (tmp != null){
            if(tmp.getNombre().equalsIgnoreCase(nombre)) return tmp;
            tmp=tmp.next;
        }
        return null;
    }
    
    public boolean remove(String nombre){
        if(isEmpty()) return false;
        if(inicio.getNombre().equalsIgnoreCase(nombre)){
            inicio = inicio.next;
            size--;
            return true;
        }
        Nodo tmp = inicio;
        while(tmp.next != null){
            if(tmp.next.getNombre().equalsIgnoreCase(nombre)){
                tmp.next = tmp.next.next;
                size--;
                return true;
            }
            tmp = tmp.next;
        }
        return false;
    }
    
    public void clear(){
        inicio = null;
        size = 0;
    }
    
    public void print(){
        Nodo tmp = inicio;
        while(tmp != null){
            System.out.println(tmp);
            tmp = tmp.next;
        }
    } 
    
    public List<Nodo> toList(){
        List<Nodo> lista = new ArrayList<>();
        Nodo tmp = inicio;
        while(tmp!=null){
            lista.add(tmp);
            tmp = tmp.next;
        }
        return lista;
    }
    
    private int comparar(Nodo a, Nodo b, Criterios criterio){
        switch(criterio){
            case NOMBRE:
                return a.getNombre().compareToIgnoreCase(b.getNombre());
            case FECHA:
                return Long.compare(a.getFechaModificacion(), b.getFechaModificacion());
            case TIPO:
                return a.getTipo().compareToIgnoreCase(b.getTipo());
            case TAMANIO:
                return Long.compare(a.getTamanio(), b.getTamanio());
            default:
                return 0;
        }
    }
    
    public void mergeSort(Criterios criterio){
        inicio = mergeSortRec(inicio, criterio);
    }
    
    private Nodo mergeSortRec(Nodo head, Criterios criterio){
        if(head == null || head.next == null) return head;
        
        Nodo mitad = obtenerMitad(head);
        Nodo segunda = mitad.next;
        mitad.next = null;
        Nodo izq = mergeSortRec(head, criterio);
        Nodo der = mergeSortRec(segunda, criterio);
        
        return merge(izq, der, criterio);
    }
    
    private Nodo merge(Nodo izq, Nodo der, Criterios criterio){
        Nodo centinela = new Nodo(izq != null ? izq.archivo : der.archivo);
        Nodo actual = centinela;
        while(izq != null && der != null){
            if(comparar(izq, der, criterio) <= 0){
                actual.next = izq;
                izq = izq.next;
            } else {
                actual.next = der;
                der = der.next;
            }
            actual = actual.next;
        }
        actual.next = (izq != null) ? izq : der;
        return centinela.next;
    }
    
    private Nodo obtenerMitad(Nodo head){
        Nodo lento = head, rapido = head.next;
        while(rapido != null && rapido.next != null){
            lento = lento.next;
            rapido = rapido.next.next;
        }
        return lento;
    }
    
}
