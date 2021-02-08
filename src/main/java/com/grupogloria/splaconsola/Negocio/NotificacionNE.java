package com.grupogloria.splaconsola.Negocio;

import java.util.List;

import com.grupogloria.splaconsola.Comun.Constante;
import com.grupogloria.splaconsola.Comun.Log;
import com.grupogloria.splaconsola.Comun.Util;
import com.grupogloria.splaconsola.Interfaz.NotificacionIN;
import com.grupogloria.splaconsola.Modelo.ApiMO;
import com.grupogloria.splaconsola.Modelo.ArchivoMO;
import com.grupogloria.splaconsola.Modelo.NotificacionMO;
import com.grupogloria.splaconsola.Modelo.ObjetoNotificacionMO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class NotificacionNE implements NotificacionIN
{
    private Util _util = null;
    private Log _log = null;

    public NotificacionNE() throws Exception
    {
        _util = new Util();
        _log = new Log(NotificacionNE.class.getName(), "");
    }

    public ObjetoNotificacionMO EnviarNotificacion(String nombreArchivo, List<ArchivoMO> listaArchivos, String entidad, String fechaInicial, String fechaFinal) throws Exception
    {
        ObjetoNotificacionMO objetoNotificacionMO = new ObjetoNotificacionMO();
        try
        {
            ApiMO apiMO = _util.ObtenerApi(Constante.ENTIDAD_NOTIFICACION, Constante._0);
            NotificacionMO notificacionMO = new NotificacionMO();
            notificacionMO.setNombreArchivo(nombreArchivo);
            notificacionMO.setListaArchivos(listaArchivos);
            notificacionMO.setDe(apiMO.getDe());
            notificacionMO.setPara(apiMO.getPara());
            notificacionMO.setAsunto(apiMO.getAsunto());
            notificacionMO.setEntidad(entidad);
            notificacionMO.setFechaInicial(fechaInicial);
            notificacionMO.setFechaFinal(fechaFinal);
            String enlace = apiMO.getApiEnlace();
            String metodo = Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiControlador() + Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiMetodo();
            WebClient webClient = WebClient.create(enlace);
            var requestHeadersSpec = webClient.post().uri(metodo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(Mono.just(notificacionMO), ObjetoNotificacionMO.class);
            var responseSpec = requestHeadersSpec.retrieve();
            objetoNotificacionMO = responseSpec.bodyToMono(ObjetoNotificacionMO.class).block();
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoNotificacionMO;
    }
}
