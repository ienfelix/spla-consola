package com.grupogloria.splaconsola.Modelo;

public class ArchivoMO {
    private String NombreArchivo;
    private String Mensaje;
    private String FechaInicio;
    private String FechaFin;

    public String getNombreArchivo() {
        return NombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        NombreArchivo = nombreArchivo;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public String getFechaInicio() {
        return FechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.FechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return FechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.FechaFin = fechaFin;
    }
}
