package com.grupogloria.splaconsola.Interfaz;

import com.grupogloria.splaconsola.Modelo.NotificacionMO;
import com.grupogloria.splaconsola.Modelo.ObjetoNotificacionMO;

public interface NotificacionIN
{
    public ObjetoNotificacionMO EnviarNotificacion(NotificacionMO notificacionMO) throws Exception;
}
