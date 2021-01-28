package com.grupogloria.splaconsola.Interfaz;

import java.util.List;

import com.grupogloria.splaconsola.Modelo.ArchivoMO;
import com.grupogloria.splaconsola.Modelo.ObjetoNotificacionMO;

public interface NotificacionIN
{
    public ObjetoNotificacionMO EnviarNotificacion(String nombreArchivo, List<ArchivoMO> listaArchivos, String entidad) throws Exception;
}
