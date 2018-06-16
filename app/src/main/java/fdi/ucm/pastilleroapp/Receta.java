package fdi.ucm.pastilleroapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Receta implements Parcelable{

    private ArrayList<Medicina> array_receta;
    //private String[] semana;
    private ArrayList<String> semana;
    private String nombre;
    private int hora;
    private int minuto;

    public Receta(String nombre) {
        this.nombre = nombre;
        array_receta = new ArrayList<Medicina>();
    }

    public Receta(String nombre, ArrayList<Medicina> array_receta) {
        this.nombre = nombre;
        this.array_receta = array_receta;
    }

    public Receta(String nombre, ArrayList<Medicina> array_receta, ArrayList<String> semana, int hora, int minuto) {
        this.array_receta = array_receta;
        this.semana = semana;
        this.nombre = nombre;
        this.hora = hora;
        this.minuto = minuto;
    }


    protected Receta(Parcel in) {
        array_receta = in.createTypedArrayList(Medicina.CREATOR);
        semana = in.createStringArrayList();
        nombre = in.readString();
        hora = in.readInt();
        minuto = in.readInt();
    }

    public static final Creator<Receta> CREATOR = new Creator<Receta>() {
        @Override
        public Receta createFromParcel(Parcel in) {
            return new Receta(in);
        }

        @Override
        public Receta[] newArray(int size) {
            return new Receta[size];
        }
    };

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

    public void setArray_receta(ArrayList<Medicina> array_receta) {
        this.array_receta = array_receta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<String> getSemana() {
        return semana;
    }

    public void setSemana(ArrayList<String> semana) {
        this.semana = semana;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(array_receta);
        dest.writeStringList(semana);
        dest.writeString(nombre);
        dest.writeInt(hora);
        dest.writeInt(minuto);
    }
}
