package com.example.administrativoujed.model;

import org.jetbrains.annotations.NotNull;

public class Persona {



    public String id;
    public  String nombre;

    public String apellidoPaterno;
    public String apellidoMaterno;
    public String matricula;
    public String turno;
    public  String rama;
    @NotNull
    public String correo;

    public Persona(String nombre, String apellidoPaterno, String apellidoMaterno, String matricula, String turno, String rama, String rama1) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getRama() {
        return rama;
    }

    public void setRama(String rama) {
        this.rama = rama;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
