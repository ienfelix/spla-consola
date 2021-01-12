package com.grupogloria.splaconsola.Interfaz;

import java.util.List;

import com.grupogloria.splaconsola.Modelo.ColaboradorMO;
import com.grupogloria.splaconsola.Modelo.ObjetoColaboradorMO;

public interface ColaboradorIN
{
    public ObjetoColaboradorMO ProcesarColaboradores(List<String> listaColaboradores, String nombreArchivoSinExtension, String rutaProcesado, String rutaNoProcesado) throws Exception;
    public ObjetoColaboradorMO CrearColaborador(ColaboradorMO clienteMO) throws Exception;
    public ObjetoColaboradorMO EditarColaborador(ColaboradorMO clienteMO) throws Exception;
    public ObjetoColaboradorMO AnularColaborador(String idInternoCliente) throws Exception;
}
