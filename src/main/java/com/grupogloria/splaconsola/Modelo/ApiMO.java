package com.grupogloria.splaconsola.Modelo;

public class ApiMO
{
    private String ApiEnlace;
    private String ApiControlador;
    private String ApiMetodo;
    private String Destinatario;

    public String getApiEnlace() {
        return ApiEnlace;
    }

    public void setApiEnlace(String apiEnlace) {
        ApiEnlace = apiEnlace;
    }

    public String getApiControlador() {
        return ApiControlador;
    }

    public void setApiControlador(String apiControlador) {
        ApiControlador = apiControlador;
    }

    public String getApiMetodo() {
        return ApiMetodo;
    }

    public void setApiMetodo(String apiMetodo) {
        ApiMetodo = apiMetodo;
    }

    public String getDestinatario() {
        return Destinatario;
    }

    public void setDestinatario(String destinatario) {
        Destinatario = destinatario;
    }
}
