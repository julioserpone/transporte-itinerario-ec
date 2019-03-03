package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;

public class Licencia {

    @SerializedName("codigo")
    private String Codigo;

    public String getCodigo() {
        return this.Codigo;
    }

    public void setCodigo(String Codigo) {
        this.Codigo = Codigo;
    }

}
