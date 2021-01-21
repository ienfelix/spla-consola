package com.grupogloria.splaconsola.Negocio;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.grupogloria.splaconsola.Comun.Constante;
import com.grupogloria.splaconsola.Comun.Log;
import com.grupogloria.splaconsola.Comun.Util;
import com.grupogloria.splaconsola.Interfaz.ColaboradorIN;
import com.grupogloria.splaconsola.Modelo.ApiMO;
import com.grupogloria.splaconsola.Modelo.ColaboradorMO;
import com.grupogloria.splaconsola.Modelo.ObjetoColaboradorMO;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class ColaboradorNE implements ColaboradorIN
{
    private Util _util = null;
    private Log _log = null;

    public ColaboradorNE() throws Exception
    {
        _util = new Util();
        _log = new Log(ColaboradorNE.class.getName(), Constante.ENTIDAD_COLABORADOR);
    }

    public ObjetoColaboradorMO ProcesarColaboradores(List<String> listaColaboradores, String nombreArchivoSinExtension, String rutaProcesado, String rutaNoProcesado) throws Exception
    {
        ObjetoColaboradorMO objetoColaboradorMO = new ObjetoColaboradorMO();
        try
        {
            if (listaColaboradores != null && listaColaboradores.size() > Constante._0)
            {
                List<String> linesOk = new ArrayList<String>();
                List<String> linesNoOk = new ArrayList<String>();
                Integer contador = Constante._0;

                for (int i = Constante._0; i < listaColaboradores.size(); i++)
                {
                    String cadena = "";
                    try
                    {
                        cadena = listaColaboradores.get(i).trim();
                        _log.info(String.format(Constante.PROCESANDO_ENTIDAD, Constante.ENTIDAD_COLABORADOR, cadena));
                        
                        if (cadena.equals(""))
                        {
                            _log.info(String.format(Constante.VACIO_ARCHIVO, nombreArchivoSinExtension, i + Constante._1));
                            contador++;
                        }
                        else
                        {
                            String primeraColumna = cadena.split(Constante.DELIMITADOR_BARRA)[Constante._0];
                            Integer tipoOperacion = Integer.parseInt(primeraColumna);
                            ColaboradorMO colaboradorMO = null;
                            ObjetoColaboradorMO respuestaMO = new ObjetoColaboradorMO();

                            switch (tipoOperacion)
                            {
                                case Constante.TIPO_OPERACION_CREAR:
                                    colaboradorMO = _util.ObtenerColaborador(cadena);
                                    respuestaMO = CrearColaborador(colaboradorMO);
                                    break;
                                case Constante.TIPO_OPERACION_EDITAR:
                                    colaboradorMO = _util.ObtenerColaborador(cadena);
                                    respuestaMO = EditarColaborador(colaboradorMO);
                                    break;
                                case Constante.TIPO_OPERACION_ANULAR:
                                    String[] columnas = cadena.split(Constante.DELIMITER_SCAPE + Constante.DELIMITADOR_BARRA, Constante.NO_LIMIT);
                                    String idInternoCliente = columnas[Constante._1].trim();
                                    respuestaMO = AnularColaborador(idInternoCliente);
                                    break;
                            }

                            _log.info(respuestaMO.getMensaje());
                            
                            if (respuestaMO.getCodigo() == Constante.CODIGO_OK)
                            {
                                contador++;
                                linesOk.add(cadena);
                            }
                            else
                            {
                                linesNoOk.add(cadena);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        linesNoOk.add(cadena);
                        _log.error(e);
                        continue;
                    }
                }

                String nombreArchivoOk = Constante.MENSAJE_OK + Constante.DELIMITADOR_GUION_BAJO + nombreArchivoSinExtension + Constante.DELIMITADOR_PUNTO + Constante.EXTENSION_TXT;
                String nombreArchivoNoOk = Constante.MENSAJE_NO_OK + Constante.DELIMITADOR_GUION_BAJO + nombreArchivoSinExtension + Constante.DELIMITADOR_PUNTO + Constante.EXTENSION_TXT;
                String rutaArchivoOk = rutaProcesado + Constante.DELIMITADOR_BARRA_OBLICUA + nombreArchivoOk;
                String rutaArchivoNoOk = rutaNoProcesado + Constante.DELIMITADOR_BARRA_OBLICUA + nombreArchivoNoOk;
                
                if (linesOk.size() > Constante._0)
                {
                    File fileOk = new File(rutaArchivoOk);
                    FileWriter fileWriterOk = new FileWriter(fileOk, true);
                    IOUtils.writeLines(linesOk, Constante.DELIMITADOR_SALTO_LINEA, fileWriterOk);
                    fileWriterOk.close();
                    _log.info(String.format(Constante.TRAMITANDO_ARCHIVO, fileOk.length() > Constante._0 ? Constante.MENSAJE_SI : Constante.MENSAJE_NO, rutaArchivoOk));
                }
                if (linesNoOk.size() > Constante._0)
                {
                    File fileNoOk = new File(rutaArchivoNoOk);
                    FileWriter fileWriterNoOk = new FileWriter(fileNoOk, true);
                    IOUtils.writeLines(linesNoOk, Constante.DELIMITADOR_SALTO_LINEA, fileWriterNoOk);
                    fileWriterNoOk.close();
                    _log.info(String.format(Constante.TRAMITANDO_ARCHIVO, fileNoOk.length() > Constante._0 ? Constante.MENSAJE_SI : Constante.MENSAJE_NO, rutaArchivoNoOk));
                }
                
                objetoColaboradorMO.setCodigo(contador == listaColaboradores.size() ? Constante.CODIGO_OK : Constante.CODIGO_NO_OK);
                objetoColaboradorMO.setMensaje(String.format(Constante.PROCESO_RESULTADO, contador == listaColaboradores.size() ? Constante.MENSAJE_SI : Constante.MENSAJE_NO, Constante.ENTIDAD_COLABORADOR, linesOk.size(), linesNoOk.size(), listaColaboradores.size()));
                _log.info(objetoColaboradorMO.getMensaje());
                linesOk.clear();
                linesNoOk.clear();
            }
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoColaboradorMO;
    }

    public ObjetoColaboradorMO CrearColaborador(ColaboradorMO colaboradorMO) throws Exception
    {
        ObjetoColaboradorMO objetoColaboradorMO = new ObjetoColaboradorMO();
        try
        {
            ApiMO apiMO = _util.ObtenerApi(Constante.ENTIDAD_COLABORADOR, Constante.TIPO_OPERACION_CREAR);
            String enlace = apiMO.getApiEnlace();
            String metodo = Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiControlador() + Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiMetodo();
            WebClient webClient = WebClient.create(enlace);
            var requestHeadersSpec = webClient.post().uri(metodo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(Mono.just(colaboradorMO), ObjetoColaboradorMO.class);
            var responseSpec = requestHeadersSpec.retrieve();
            objetoColaboradorMO = responseSpec.bodyToMono(ObjetoColaboradorMO.class).block();
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoColaboradorMO;
    }

    public ObjetoColaboradorMO EditarColaborador(ColaboradorMO colaboradorMO) throws Exception
    {
        ObjetoColaboradorMO objetoColaboradorMO = new ObjetoColaboradorMO();
        try
        {
            ApiMO apiMO = _util.ObtenerApi(Constante.ENTIDAD_COLABORADOR, Constante.TIPO_OPERACION_EDITAR);
            String enlace = apiMO.getApiEnlace();
            String metodo = Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiControlador() + Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiMetodo();
            WebClient webClient = WebClient.create(enlace);
            var requestHeadersSpec = webClient.put().uri(metodo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(Mono.just(colaboradorMO), ObjetoColaboradorMO.class);
            var responseSpec = requestHeadersSpec.retrieve();
            objetoColaboradorMO = responseSpec.bodyToMono(ObjetoColaboradorMO.class).block();
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoColaboradorMO;
    }

    public ObjetoColaboradorMO AnularColaborador(String idInternoCliente) throws Exception
    {
        ObjetoColaboradorMO objetoColaboradorMO = new ObjetoColaboradorMO();
        try
        {
            ApiMO apiMO = _util.ObtenerApi(Constante.ENTIDAD_COLABORADOR, Constante.TIPO_OPERACION_ANULAR);
            String enlace = apiMO.getApiEnlace();
            String metodo = Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiControlador() + Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiMetodo() + Constante.DELIMITADOR_PREGUNTA + Constante.ID_INTERNO_CLIENTE + Constante.DELIMITADOR_IGUAL + idInternoCliente;
            WebClient webClient = WebClient.create(enlace);
            var requestHeadersSpec = webClient.delete().uri(metodo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            var responseSpec = requestHeadersSpec.retrieve();
            objetoColaboradorMO = responseSpec.bodyToMono(ObjetoColaboradorMO.class).block();
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoColaboradorMO;
    }
}
