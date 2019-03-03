package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;

public class Bus {

    @SerializedName("id")
    private int Id;

    @SerializedName("marca")
    private String Marca;

    @SerializedName("modelo")
    private String Modelo;

    @SerializedName("numero_unidad")
    private String NumeroUnidad;

    public int getId() {
        return this.Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getMarca() {
        return this.Marca;
    }

    public void setMarca(String Marca) {
        this.Marca = Marca;
    }

    public String getModelo() {
        return this.Modelo;
    }

    public void setModelo(String Modelo) {
        this.Modelo = Modelo;
    }

    public String getNumeroUnidad() {
        return this.NumeroUnidad;
    }

    public void setNumeroUnidad(String NumeroUnidad) {
        this.NumeroUnidad = NumeroUnidad;
    }
}
