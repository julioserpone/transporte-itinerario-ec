package com.barragan.transportesec.models;

import com.google.gson.annotations.SerializedName;

public class Pasajero {

    @SerializedName("cedula")
    private String Cedula;

    @SerializedName("nombres")
    private String Nombres;

    @SerializedName("apellidos")
    private String Apellidos;

    @SerializedName("fecha_nacimiento")
    private String FechaNacimiento;

    public String getCedula() {
        return this.Cedula;
    }

    public void setCedula(String Cedula) {
        this.Cedula = Cedula;
    }

    public String getNombres() {
        return this.Nombres;
    }

    public void setNombres(String Nombres) {
        this.Nombres = Nombres;
    }

    public String getApellidos() {
        return this.Apellidos;
    }

    public void setApellidos(String Apellidos) {
        this.Apellidos = Apellidos;
    }

    public String getFechaNacimiento() {
        return this.FechaNacimiento;
    }

    public void setFechaNacimiento(String FechaNacimiento) {
        this.FechaNacimiento = FechaNacimiento;
    }
}
