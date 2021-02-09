package com.grupogloria.splaconsola.Modelo;

public class ConexionMO
{
    private String FtpServer;
    private String FtpUsername;
    private String FtpPassword;
    private Integer FtpPort;
    private String FtpWorkspace;
    private String RutaProcesado;
    private String RutaNoProcesado;
    private String RutaTrabajo;
    
    public String getFtpServer() {
        return FtpServer;
    }

    public void setFtpServer(String ftpServer) {
        FtpServer = ftpServer;
    }

    public String getFtpUsername() {
        return FtpUsername;
    }

    public void setFtpUsername(String ftpUsername) {
        FtpUsername = ftpUsername;
    }

    public String getFtpPassword() {
        return FtpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        FtpPassword = ftpPassword;
    }

    public Integer getFtpPort() {
        return FtpPort;
    }

    public void setFtpPort(Integer ftpPort) {
        FtpPort = ftpPort;
    }

    public String getFtpWorkspace() {
        return FtpWorkspace;
    }

    public void setFtpWorkspace(String ftpWorkspace) {
        FtpWorkspace = ftpWorkspace;
    }

    public String getRutaProcesado() {
        return RutaProcesado;
    }

    public void setRutaProcesado(String rutaProcesado) {
        RutaProcesado = rutaProcesado;
    }

    public String getRutaNoProcesado() {
        return RutaNoProcesado;
    }

    public void setRutaNoProcesado(String rutaNoProcesado) {
        RutaNoProcesado = rutaNoProcesado;
    }

    public String getRutaTrabajo() {
        return RutaTrabajo;
    }

    public void setRutaTrabajo(String rutaTrabajo) {
        RutaTrabajo = rutaTrabajo;
    }
}
