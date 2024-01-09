package com.tramites.administrativoujed.model;

public class InformacionHijo {
    public String id;
    public  String matricula;
    public String actaNacimientoUri;



    public InformacionHijo(String matricula, String actaNacimientoUri, String toString) {
        this.matricula = matricula;
        this.actaNacimientoUri = actaNacimientoUri;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getActaNacimientoUri() {
        return actaNacimientoUri;
    }

    public void setActaNacimientoUri(String actaNacimientoUri) {
        this.actaNacimientoUri = actaNacimientoUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
