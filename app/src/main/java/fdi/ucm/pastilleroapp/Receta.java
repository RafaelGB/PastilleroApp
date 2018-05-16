package fdi.ucm.pastilleroapp;

import java.util.ArrayList;

public class Receta {

    private ArrayList<Medicina> array_receta;
    private String nombre;

    public Receta(String nombre) {
        this.nombre = nombre;
        array_receta = new ArrayList<Medicina>();
    }

    public Receta(String nombre, ArrayList<Medicina> array_receta) {
        this.nombre = nombre;
        this.array_receta = array_receta;
    }

    public void agregar_medicina(Medicina medicina) {
        array_receta.add(medicina);
   }

    public void eliminar_medicina(int posicion) {
        array_receta.remove(posicion);
   }

    public Medicina getMedicina(int posicion) {
        return array_receta.get(posicion);
   }

    public ArrayList<Medicina> getArray_receta() {
        return array_receta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
