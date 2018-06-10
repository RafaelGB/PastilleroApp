package fdi.ucm.pastilleroapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Medicina implements Parcelable{

    private String nombre;
    private double cantidad;

    public Medicina(String nombre, double cantidad) {
        this.nombre = nombre;
        this.cantidad = cantidad;
    }

    protected Medicina(Parcel in) {
        nombre = in.readString();
        cantidad = in.readDouble();
    }

    public static final Creator<Medicina> CREATOR = new Creator<Medicina>() {
        @Override
        public Medicina createFromParcel(Parcel in) {
            return new Medicina(in);
        }

        @Override
        public Medicina[] newArray(int size) {
            return new Medicina[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeDouble(cantidad);
    }
}
