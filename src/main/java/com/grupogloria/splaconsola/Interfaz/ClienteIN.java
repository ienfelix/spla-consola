package com.grupogloria.splaconsola.Interfaz;

import java.util.List;

import com.grupogloria.splaconsola.Modelo.ClienteMO;
import com.grupogloria.splaconsola.Modelo.ObjetoClienteMO;

public interface ClienteIN
{
    public ObjetoClienteMO ProcesarClientes(List<String> listaClientes, String nombreArchivoSinExtension, String rutaProcesado, String rutaNoProcesado) throws Exception;
    public ObjetoClienteMO CrearCliente(ClienteMO clienteMO) throws Exception;
    public ObjetoClienteMO EditarCliente(ClienteMO clienteMO) throws Exception;
    public ObjetoClienteMO AnularCliente(String idInternoCliente) throws Exception;
}
