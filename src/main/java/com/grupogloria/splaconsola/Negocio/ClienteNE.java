package com.grupogloria.splaconsola.Negocio;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.grupogloria.splaconsola.Comun.Constante;
import com.grupogloria.splaconsola.Comun.Log;
import com.grupogloria.splaconsola.Comun.Util;
import com.grupogloria.splaconsola.Interfaz.ClienteIN;
import com.grupogloria.splaconsola.Modelo.ApiMO;
import com.grupogloria.splaconsola.Modelo.ClienteMO;
import com.grupogloria.splaconsola.Modelo.ObjetoClienteMO;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class ClienteNE implements ClienteIN
{
    private Util _util = null;
    private Log _log = null;

    public ClienteNE() throws Exception
    {
        _util = new Util();
        _log = new Log(ClienteNE.class.getName(), Constante.ENTIDAD_CLIENTE);
    }

    public ObjetoClienteMO ProcesarClientes(List<String> listaClientes, String nombreArchivoSinExtension, String rutaProcesado, String rutaNoProcesado) throws Exception
    {
        ObjetoClienteMO objetoClienteMO = new ObjetoClienteMO();
        try
        {
            if (listaClientes != null && listaClientes.size() > Constante._0)
            {
                List<String> linesOk = new ArrayList<String>();
                List<String> linesNoOk = new ArrayList<String>();
                Integer contador = Constante._0;

                for (int i = Constante._0; i < listaClientes.size(); i++)
                {
                    String cadena = "";
                    try
                    {
                        cadena = listaClientes.get(i).trim();
                        _log.info(String.format(Constante.PROCESANDO_ENTIDAD, Constante.ENTIDAD_CLIENTE, cadena));

                        if (cadena.equals(""))
                        {
                            _log.info(String.format(Constante.VACIO_ARCHIVO, nombreArchivoSinExtension, i + Constante._1));
                            contador++;
                        }
                        else
                        {
                            String primeraColumna = cadena.split(Constante.DELIMITADOR_BARRA)[Constante._0];
                            Integer tipoOperacion = Integer.parseInt(primeraColumna);
                            ClienteMO clienteMO = null;
                            ObjetoClienteMO respuestaMO = new ObjetoClienteMO();

                            switch (tipoOperacion)
                            {
                                case Constante.TIPO_OPERACION_CREAR:
                                    clienteMO = _util.ObtenerCliente(cadena);
                                    respuestaMO = CrearCliente(clienteMO);
                                    break;
                                case Constante.TIPO_OPERACION_EDITAR:
                                    clienteMO = _util.ObtenerCliente(cadena);
                                    respuestaMO = EditarCliente(clienteMO);
                                    break;
                                case Constante.TIPO_OPERACION_ANULAR:
                                    String[] columnas = cadena.split(Constante.DELIMITER_ESCAPE + Constante.DELIMITADOR_BARRA, Constante.NO_LIMIT);
                                    String idInternoCliente = columnas[Constante._1].trim();
                                    respuestaMO = AnularCliente(idInternoCliente);
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
                
                objetoClienteMO.setCodigo(contador == listaClientes.size() ? Constante.CODIGO_OK : Constante.CODIGO_NO_OK);
                objetoClienteMO.setMensaje(String.format(Constante.PROCESO_RESULTADO, contador == listaClientes.size() ? Constante.MENSAJE_SI : Constante.MENSAJE_NO, Constante.ENTIDAD_CLIENTE, linesOk.size(), linesNoOk.size(), listaClientes.size()));
                _log.info(objetoClienteMO.getMensaje());
                linesOk.clear();
                linesNoOk.clear();
            }
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoClienteMO;
    }

    public ObjetoClienteMO CrearCliente(ClienteMO clienteMO) throws Exception
    {
        ObjetoClienteMO objetoClienteMO = new ObjetoClienteMO();
        try
        {
            ApiMO apiMO = _util.ObtenerApi(Constante.ENTIDAD_CLIENTE, Constante.TIPO_OPERACION_CREAR);
            String enlace = apiMO.getApiEnlace();
            String metodo = Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiControlador() + Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiMetodo();
            WebClient webClient = WebClient.create(enlace);
            var requestHeadersSpec = webClient.post().uri(metodo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(Mono.just(clienteMO), ObjetoClienteMO.class);
            var responseSpec = requestHeadersSpec.retrieve();
            objetoClienteMO = responseSpec.bodyToMono(ObjetoClienteMO.class).block();
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoClienteMO;
    }

    public ObjetoClienteMO EditarCliente(ClienteMO clienteMO) throws Exception
    {
        ObjetoClienteMO objetoClienteMO = new ObjetoClienteMO();
        try
        {
            ApiMO apiMO = _util.ObtenerApi(Constante.ENTIDAD_CLIENTE, Constante.TIPO_OPERACION_EDITAR);
            String enlace = apiMO.getApiEnlace();
            String metodo = Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiControlador() + Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiMetodo();
            WebClient webClient = WebClient.create(enlace);
            var requestHeadersSpec = webClient.put().uri(metodo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(Mono.just(clienteMO), ObjetoClienteMO.class);
            var responseSpec = requestHeadersSpec.retrieve();
            objetoClienteMO = responseSpec.bodyToMono(ObjetoClienteMO.class).block();
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoClienteMO;
    }

    public ObjetoClienteMO AnularCliente(String idInternoCliente) throws Exception
    {
        ObjetoClienteMO objetoClienteMO = new ObjetoClienteMO();
        try
        {
            ApiMO apiMO = _util.ObtenerApi(Constante.ENTIDAD_CLIENTE, Constante.TIPO_OPERACION_ANULAR);
            String enlace = apiMO.getApiEnlace();
            String metodo = Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiControlador() + Constante.DELIMITADOR_BARRA_OBLICUA + apiMO.getApiMetodo() + Constante.DELIMITADOR_PREGUNTA + Constante.ID_INTERNO_CLIENTE + Constante.DELIMITADOR_IGUAL + idInternoCliente;
            WebClient webClient = WebClient.create(enlace);
            var requestHeadersSpec = webClient.delete().uri(metodo).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            var responseSpec = requestHeadersSpec.retrieve();
            objetoClienteMO = responseSpec.bodyToMono(ObjetoClienteMO.class).block();
        }
        catch (Exception e)
        {
            _log.error(e);
            throw e;
        }
        return objetoClienteMO;
    }
}
