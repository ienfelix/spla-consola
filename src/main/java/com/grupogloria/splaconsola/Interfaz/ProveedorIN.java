package com.grupogloria.splaconsola.Interfaz;

import java.util.List;

import com.grupogloria.splaconsola.Modelo.ObjetoProveedorMO;
import com.grupogloria.splaconsola.Modelo.ProveedorMO;

public interface ProveedorIN
{
    public ObjetoProveedorMO ProcesarProveedores(List<String> listaClientes, String nombreArchivoSinExtension, String rutaProcesado, String rutaNoProcesado) throws Exception;
    public ObjetoProveedorMO CrearProveedor(ProveedorMO clienteMO) throws Exception;
    public ObjetoProveedorMO EditarProveedor(ProveedorMO clienteMO) throws Exception;
    public ObjetoProveedorMO AnularProveedor(String idInternoCliente) throws Exception;
}
