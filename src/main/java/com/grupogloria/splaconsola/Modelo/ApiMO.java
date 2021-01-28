package com.grupogloria.splaconsola.Modelo;

public class ApiMO
{
    private String ApiEnlace;
    private String ApiControlador;
    private String ApiMetodo;
    private String De;
    private String Para;
    private String Asunto;

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

    public String getDe() {
        return De;
    }

    public void setDe(String de) {
        this.De = de;
    }

    public String getPara() {
        return Para;
    }

    public void setPara(String para) {
        Para = para;
    }

    public String getAsunto() {
        return Asunto;
    }

    public void setAsunto(String asunto) {
        Asunto = asunto;
    }
}
