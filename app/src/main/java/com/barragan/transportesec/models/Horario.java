package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;
import android.os.Parcel;
import android.os.Parcelable;

public class Horario implements Parcelable {

    @SerializedName("id")
    private int Id;

    @SerializedName("ruta_id")
    private int RutaId;

    @SerializedName("hora_salida")
    private String HoraSalida;

    public static final Creator<Horario> CREATOR = new Creator<Horario>() {

        @Override
        public Horario createFromParcel(Parcel in) {
            return new Horario(in);
        }

        @Override
        public Horario[] newArray(int size) {
            return new Horario[size];
        }
    };

    public static Creator<Horario> getCREATOR() {
        return CREATOR;
    }

    protected Horario(Parcel in) {
        Id = in.readInt();
        RutaId = in.readInt();
        HoraSalida = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Id);
        dest.writeInt(RutaId);
        dest.writeString(HoraSalida);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return HoraSalida;
    }

    public int getId() {
        return this.Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public int getRutaId() {
        return this.RutaId;
    }

    public void setRutaId(int RutaId) {
        this.RutaId = RutaId;
    }

    public String getHoraSalida() { return HoraSalida; }

    public void setHoraSalida (String horaSalida) { this.HoraSalida = horaSalida; }

}
