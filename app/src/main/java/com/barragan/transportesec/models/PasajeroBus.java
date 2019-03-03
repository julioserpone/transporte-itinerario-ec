package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PasajeroBus {

    @SerializedName("cooperativa_rutas_id")
    private int CooperativaRutaId;

    @SerializedName("chofer_id")
    private int ChoferId;

    @SerializedName("pasajeros")
    public List<Pasajero> ListaPasajeros;

    @SerializedName("lista_cerrada")
    private int ListaCerrada;

    public int getCooperativaRutaId() {
        return this.CooperativaRutaId;
    }

    public void setCooperativaRutaId(int id) {
        this.CooperativaRutaId = id;
    }

    public int getChoferId() {
        return this.ChoferId;
    }

    public void setChoferId(int id) {
        this.ChoferId = id;
    }

    public int getListaCerrada() {
        return this.ListaCerrada;
    }

    public void setListaCerrada(int lista_cerrada) {
        this.ListaCerrada = lista_cerrada;
    }

}
