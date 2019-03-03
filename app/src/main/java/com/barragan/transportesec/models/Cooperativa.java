package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;

public class Cooperativa {

    @SerializedName("ruc")
    private String Ruc;

    @SerializedName("nombre")
    private String Nombre;

    public String getRuc() {
        return this.Ruc;
    }

    public void setRuc(String Ruc) {
        this.Ruc = Ruc;
    }

    public String getNombre() {
        return this.Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

}
