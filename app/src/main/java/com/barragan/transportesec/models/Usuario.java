package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;

public class Usuario {

    @SerializedName("id")
    private int Id;

    @SerializedName("nombres")
    private String Nombres;

    @SerializedName("cedula")
    private String Cedula;

    @SerializedName("fecha_nacimiento")
    private String FechaNacimiento;

    @SerializedName("celular")
    private String Celular;

    public int getId() {
        return this.Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getNombres() {
        return this.Nombres;
    }

    public void setNombres(String Nombres) {
        this.Nombres = Nombres;
    }

    public String getCedula() {
        return this.Cedula;
    }

    public void setCedula(String Cedula) {
        this.Cedula = Cedula;
    }

    public String getFechaNacimiento() {
        return this.FechaNacimiento;
    }

    public void setFechaNacimiento(String FechaNacimiento) {
        this.FechaNacimiento = FechaNacimiento;
    }

    public String getCelular() {
        return this.Celular;
    }

    public void setCelular(String Celular) {
        this.Celular = Celular;
    }
}
