package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;
import android.os.Parcel;
import android.os.Parcelable;

public class Ruta implements Parcelable {

    @SerializedName("id")
    private int Id;

    @SerializedName("origen")
    private String Origen;

    @SerializedName("destino")
    private String Destino;

    public static final Creator<Ruta> CREATOR = new Creator<Ruta>() {

        @Override
        public Ruta createFromParcel(Parcel in) {
            return new Ruta(in);
        }

        @Override
        public Ruta[] newArray(int size) {
            return new Ruta[size];
        }
    };

    public static Creator<Ruta> getCREATOR() {
        return CREATOR;
    }

    protected Ruta(Parcel in) {
        Id = in.readInt();
        Origen = in.readString();
        Destino = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeString(Origen);
        dest.writeString(Destino);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return Origen + " - " + Destino;
    }

    public int getId() {
        return this.Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getOrigen() {
        return this.Origen;
    }

    public void setOrigen(String Origen) {
        this.Origen = Origen;
    }

    public String getDestino() {
        return this.Destino;
    }

    public void setDestino(String Destino) {
        this.Destino = Destino;
    }

}
