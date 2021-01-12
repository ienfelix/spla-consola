package com.grupogloria.splaconsola.Modelo;

import java.util.List;

public class NotificacionMO
{
    private String NombreArchivo;
    private List<ArchivoMO> ListaArchivos;
    private String Destinatario;

    public String getNombreArchivo() {
        return NombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        NombreArchivo = nombreArchivo;
    }

    public List<ArchivoMO> getListaArchivos() {
        return ListaArchivos;
    }

    public void setListaArchivos(List<ArchivoMO> listaArchivos) {
        ListaArchivos = listaArchivos;
    }

    public String getDestinatario() {
        return Destinatario;
    }

    public void setDestinatario(String destinatario) {
        Destinatario = destinatario;
    }
}
