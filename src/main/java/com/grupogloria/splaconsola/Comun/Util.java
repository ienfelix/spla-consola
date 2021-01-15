package com.grupogloria.splaconsola.Comun;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.grupogloria.splaconsola.Modelo.ApiMO;
import com.grupogloria.splaconsola.Modelo.ClienteMO;
import com.grupogloria.splaconsola.Modelo.ColaboradorMO;
import com.grupogloria.splaconsola.Modelo.ConexionMO;
import com.grupogloria.splaconsola.Modelo.ProveedorMO;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

public class Util
{
    public Util()
    {
    }

    public ConexionMO ObtenerConexion() throws Exception
    {
        ConexionMO conexionMO = new ConexionMO();
        InputStream inputStream = null, inputStreamTemp = null;
        try
        {
            ClassPathResource classPathResource = new ClassPathResource(Constante.APPLICATION_PROPERTIES);
            inputStreamTemp = classPathResource.getInputStream();
            File tempFile = File.createTempFile(Constante.APPLICATION_PROPERTIES, null);
            FileUtils.copyInputStreamToFile(inputStreamTemp, tempFile);
            inputStream = new FileInputStream(tempFile);
            tempFile.delete();
            Properties properties = new Properties();
            properties.load(inputStream);
            String ftpServer = IsNullOrEmpty(properties.getProperty(Constante.FTP_SERVER)) ? "" : properties.getProperty(Constante.FTP_SERVER);
            String ftpUsername = IsNullOrEmpty(properties.getProperty(Constante.FTP_USERNAME)) ? "" : properties.getProperty(Constante.FTP_USERNAME);
            String ftpPassword = IsNullOrEmpty(properties.getProperty(Constante.FTP_PASSWORD)) ? "" : properties.getProperty(Constante.FTP_PASSWORD);
            String ftpPort = IsNullOrEmpty(properties.getProperty(Constante.FTP_PORT)) ? "" : properties.getProperty(Constante.FTP_PORT);
            Integer port = Integer.parseInt(ftpPort);
            String environment = IsNullOrEmpty(properties.getProperty(Constante.FTP_ENVIRONMENT)) ? "" : properties.getProperty(Constante.FTP_ENVIRONMENT);
            String directory = IsNullOrEmpty(properties.getProperty(Constante.FTP_DIRECTORY)) ? "" : properties.getProperty(Constante.FTP_DIRECTORY);
            String workSpace = Constante.DELIMITADOR_BARRA_OBLICUA + environment + Constante.DELIMITADOR_BARRA_OBLICUA + directory;
            String rutaProcesado = IsNullOrEmpty(properties.getProperty(Constante.API_RUTA_PROCESADO)) ? "" : properties.getProperty(Constante.API_RUTA_PROCESADO);
            String rutaNoProcesado = IsNullOrEmpty(properties.getProperty(Constante.API_RUTA_NO_PROCESADO)) ? "" : properties.getProperty(Constante.API_RUTA_NO_PROCESADO);
            conexionMO.setFtpServer(ftpServer);
            conexionMO.setFtpUsername(ftpUsername);
            conexionMO.setFtpPassword(ftpPassword);
            conexionMO.setFtpPort(port);
            conexionMO.setFtpDirectory(workSpace);
            conexionMO.setRutaProcesado(rutaProcesado);
            conexionMO.setRutaNoProcesado(rutaNoProcesado);
            properties.clear();
            CrearCarpetaTrabajo(rutaProcesado, rutaNoProcesado);
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
            if (inputStreamTemp != null)
            {
                inputStreamTemp.close();
            }
        }
        return conexionMO;
    }

    private Boolean CrearCarpetaTrabajo(String rutaProcesado, String rutaNoProcesado) throws Exception
    {
        Boolean esCorrecto = false;
        try
        {
            File fileProcesado = new File(rutaProcesado);
            File fileNoProcesado = new File(rutaNoProcesado);

            if (!fileProcesado.exists())
            {
                fileProcesado.mkdirs();
            }
            if (!fileNoProcesado.exists())
            {
                fileNoProcesado.mkdirs();
            }
            if (fileProcesado.exists() && fileNoProcesado.exists())
            {
                esCorrecto = true;
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        return esCorrecto;
    }

    public ApiMO ObtenerApi(String entidad, Integer tipoOperacion) throws Exception
    {
        ApiMO apiMO = new ApiMO();
        InputStream inputStream = null, inputStreamTemp = null;
        try
        {
            ClassPathResource classPathResource = new ClassPathResource(Constante.APPLICATION_PROPERTIES);
            inputStreamTemp = classPathResource.getInputStream();
            File tempFile = File.createTempFile(Constante.APPLICATION_PROPERTIES, null);
            FileUtils.copyInputStreamToFile(inputStreamTemp, tempFile);
            inputStream = new FileInputStream(tempFile);
            tempFile.delete();
            Properties properties = new Properties();
            properties.load(inputStream);
            String apiUrl = IsNullOrEmpty(properties.getProperty(Constante.API_URL)) ? "" : properties.getProperty(Constante.API_URL);
            String recipients = IsNullOrEmpty(properties.getProperty(Constante.EMAIL_RECIPIENTS)) ? "" : properties.getProperty(Constante.EMAIL_RECIPIENTS);
            properties.clear();
            apiMO.setApiEnlace(apiUrl);
            
            if (entidad == Constante.ENTIDAD_CLIENTE)
            {
                apiMO.setApiControlador(Constante.CONTROLADOR_CLIENTE);

                switch (tipoOperacion)
                {
                    case Constante.TIPO_OPERACION_CREAR:
                        apiMO.setApiMetodo(Constante.METODO_CREAR_CLIENTE);
                        break;
                    case Constante.TIPO_OPERACION_EDITAR:
                        apiMO.setApiMetodo(Constante.METODO_EDITAR_CLIENTE);
                        break;
                    case Constante.TIPO_OPERACION_ANULAR:
                        apiMO.setApiMetodo(Constante.METODO_ANULAR_CLIENTE);
                        break;
                }
            }
            else if (entidad == Constante.ENTIDAD_PROVEEDOR)
            {
                apiMO.setApiControlador(Constante.CONTROLADOR_PROVEEDOR);
                
                switch (tipoOperacion)
                {
                    case Constante.TIPO_OPERACION_CREAR:
                        apiMO.setApiMetodo(Constante.METODO_CREAR_PROVEEDOR);
                        break;
                    case Constante.TIPO_OPERACION_EDITAR:
                        apiMO.setApiMetodo(Constante.METODO_EDITAR_PROVEEDOR);
                        break;
                    case Constante.TIPO_OPERACION_ANULAR:
                        apiMO.setApiMetodo(Constante.METODO_ANULAR_PROVEEDOR);
                        break;
                }
            }
            else if (entidad == Constante.ENTIDAD_COLABORADOR)
            {
                apiMO.setApiControlador(Constante.CONTROLADOR_COLABORADOR);
                
                switch (tipoOperacion)
                {
                    case Constante.TIPO_OPERACION_CREAR:
                        apiMO.setApiMetodo(Constante.METODO_CREAR_COLABORADOR);
                        break;
                    case Constante.TIPO_OPERACION_EDITAR:
                        apiMO.setApiMetodo(Constante.METODO_EDITAR_COLABORADOR);
                        break;
                    case Constante.TIPO_OPERACION_ANULAR:
                        apiMO.setApiMetodo(Constante.METODO_ANULAR_COLABORADOR);
                        break;
                }
            }
            else if (entidad == Constante.ENTIDAD_NOTIFICACION)
            {
                apiMO.setApiControlador(Constante.CONTROLADOR_NOTIFICACION);
                apiMO.setApiMetodo(Constante.METODO_ENVIAR_NOTIFICACION);
                apiMO.setDestinatario(recipients);
            }
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
            if (inputStreamTemp != null)
            {
                inputStreamTemp.close();
            }
        }
        return apiMO;
    }

    public ClienteMO ObtenerCliente(String cadena) throws Exception
	{
		ClienteMO clienteMO = new ClienteMO();
		try
		{
            String[] columnas = cadena.split(Constante.DELIMITER_SCAPE + Constante.DELIMITADOR_BARRA, Constante.NO_LIMIT);
            clienteMO.setIdInternoCliente(columnas[Constante._1].trim());
            clienteMO.setDenominacion(columnas[Constante._2].trim());
            clienteMO.setDocumento(columnas[Constante._3].trim());
            Integer idTipoDocumento = columnas[Constante._4].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._4].trim());
            if (idTipoDocumento != Constante.NEGATIVO)
            {
                clienteMO.setIdTipoDocumento(idTipoDocumento);
            }
            Integer idTipoEntidad = columnas[Constante._5].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._5].trim());
            if (idTipoEntidad != Constante.NEGATIVO)
            {
                clienteMO.setIdTipoEntidad(idTipoEntidad);
            }
            Integer idCategoria = columnas[Constante._6].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._6].trim());
            if (idCategoria != Constante.NEGATIVO)
            {
                clienteMO.setIdCategoria(idCategoria);
            }
            Integer idEmpresa = columnas[Constante._7].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._7].trim());
            if (idEmpresa != Constante.NEGATIVO)
            {
                clienteMO.setIdEmpresa(idEmpresa);
            }
            String direccion = columnas[Constante._8].trim();
            if (!IsNullOrEmpty(direccion))
            {
                clienteMO.setDireccion(direccion);
            }
            String telefono = columnas[Constante._9].trim();
            if (!IsNullOrEmpty(telefono))
            {
                clienteMO.setTelefono(telefono);
            }
            String email = columnas[Constante._10].trim();
            if (!IsNullOrEmpty(email))
            {
                clienteMO.setEmail(email);
            }
            String descripcionTipoDocumento = columnas[Constante._11].trim();
            if (!IsNullOrEmpty(descripcionTipoDocumento))
            {
                clienteMO.setDescripcionTipoDocumento(descripcionTipoDocumento);
            }
            Integer idPaisDocumento = columnas[Constante._12].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._12].trim());
            if (idPaisDocumento != Constante.NEGATIVO)
            {
                clienteMO.setIdPaisDocumento(idPaisDocumento);
            }
            String descripcionPaisDocumento = columnas[Constante._13].trim();
            if (!IsNullOrEmpty(descripcionPaisDocumento))
            {
                clienteMO.setDescripcionPaisDocumento(descripcionPaisDocumento);
            }
            String descripcionTipoEntidad = columnas[Constante._14].trim();
            if (!IsNullOrEmpty(descripcionTipoEntidad))
            {
                clienteMO.setDescripcionTipoEntidad(descripcionTipoEntidad);
            }
            String nombreCliente = columnas[Constante._15].trim();
            if (!IsNullOrEmpty(nombreCliente))
            {
                clienteMO.setNombreCliente(nombreCliente);
            }
            String primerNombre = columnas[Constante._16].trim();
            if (!IsNullOrEmpty(primerNombre))
            {
                clienteMO.setPrimerNombre(primerNombre);
            }
            String segundoNombre = columnas[Constante._17].trim();
            if (!IsNullOrEmpty(segundoNombre))
            {
                clienteMO.setSegundoNombre(segundoNombre);
            }
            String primerApellido = columnas[Constante._18].trim();
            if (!IsNullOrEmpty(primerApellido))
            {
                clienteMO.setPrimerApellido(primerApellido);
            }
            String segundoApellido = columnas[Constante._19].trim();
            if (!IsNullOrEmpty(segundoApellido))
            {
                clienteMO.setSegundoApellido(segundoApellido);
            }
            String contacto = columnas[Constante._20].trim();
            if (!IsNullOrEmpty(contacto))
            {
                clienteMO.setContacto(contacto);
            }
            Integer idPaisResidencia = columnas[Constante._21].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._21].trim());
            if (idPaisResidencia != Constante.NEGATIVO)
            {
                clienteMO.setIdPaisResidencia(idPaisResidencia);
            }
            String descripcionPaisResidencia = columnas[Constante._22].trim();
            if (!IsNullOrEmpty(descripcionPaisResidencia))
            {
                clienteMO.setDescripcionPaisResidencia(descripcionPaisResidencia);
            }
            Integer idPaisOrigen = columnas[Constante._23].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._23].trim());
            if (idPaisOrigen != Constante.NEGATIVO)
            {
                clienteMO.setIdPaisOrigen(idPaisOrigen);
            }
            String descripcionPaisOrigen = columnas[Constante._24].trim();
            if (!IsNullOrEmpty(descripcionPaisOrigen))
            {
                clienteMO.setDescripcionPaisOrigen(descripcionPaisOrigen);
            }
            String observaciones = columnas[Constante._25].trim();
            if (!IsNullOrEmpty(observaciones))
            {
                clienteMO.setObservaciones(observaciones);
            }
            String descripcionCategoria = columnas[Constante._26].trim();
            if (!IsNullOrEmpty(descripcionCategoria))
            {
                clienteMO.setDescripcionCategoria(descripcionCategoria);
            }
            String cargoPEP = columnas[Constante._27].trim();
            if (!IsNullOrEmpty(cargoPEP))
            {
                clienteMO.setCargoPEP(cargoPEP);
            }
            String pep = columnas[Constante._28].trim();
            if (!IsNullOrEmpty(pep))
            {
                Boolean PEP = Boolean.parseBoolean(pep);
                clienteMO.setPEP(PEP);
            }
            Integer idEstadoCivil = columnas[Constante._29].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._29].trim());
            if (idEstadoCivil != Constante.NEGATIVO)
            {
                clienteMO.setIdEstadoCivil(idEstadoCivil);
            }
            String descripcionEstadoCivil = columnas[Constante._30].trim();
            if (!IsNullOrEmpty(descripcionEstadoCivil))
            {
                clienteMO.setDescripcionEstadoCivil(descripcionEstadoCivil);
            }
            String naturalezaDeRelacion = columnas[Constante._31].trim();
            if (!IsNullOrEmpty(naturalezaDeRelacion))
            {
                clienteMO.setNaturalezaDeRelacion(naturalezaDeRelacion);
            }
            String profesion = columnas[Constante._32].trim();
            if (!IsNullOrEmpty(profesion))
            {
                clienteMO.setProfesion(profesion);
            }
            String origenDeFondos = columnas[Constante._33].trim();
            if (!IsNullOrEmpty(origenDeFondos))
            {
                clienteMO.setOrigenDeFondos(origenDeFondos);
            }
            String fechaNacimiento = columnas[Constante._34].trim();
            if (!IsNullOrEmpty(fechaNacimiento))
            {
                clienteMO.setFechaNacimiento(fechaNacimiento);
            }
            String LugarNacimiento = columnas[Constante._35].trim();
            if (!IsNullOrEmpty(LugarNacimiento))
            {
                clienteMO.setLugarNacimiento(LugarNacimiento);
            }
            String incluidoEnMatching = columnas[Constante._36].trim();
            if (!IsNullOrEmpty(incluidoEnMatching))
            {
                Boolean IncluidoEnMatching = Boolean.parseBoolean(incluidoEnMatching);
                clienteMO.setIncluidoEnMatching(IncluidoEnMatching);
            }
            String descripcionEmpresa = columnas[Constante._37].trim();
            if (!IsNullOrEmpty(descripcionEmpresa))
            {
                clienteMO.setDescripcionEmpresa(descripcionEmpresa);
            }
            String incluidoEnMatchingIntegrantes = columnas[Constante._38].trim();
            if (!IsNullOrEmpty(incluidoEnMatchingIntegrantes))
            {
                Boolean IncluidoEnMatchingIntegrantes = Boolean.parseBoolean(incluidoEnMatchingIntegrantes);
                clienteMO.setIncluidoEnMatchingIntegrantes(IncluidoEnMatchingIntegrantes);
            }
            Integer idRiesgo = columnas[Constante._39].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._39].trim());
            if (idRiesgo != Constante.NEGATIVO)
            {
                clienteMO.setIdRiesgo(idRiesgo);
            }
            String descripcionRiesgo = columnas[Constante._40].trim();
            if (!IsNullOrEmpty(descripcionRiesgo))
            {
                clienteMO.setDescripcionRiesgo(descripcionRiesgo);
            }
		}
		catch (Exception e)
		{
            throw e;
		}
		return clienteMO;
    }
    
    public ProveedorMO ObtenerProveedor(String cadena) throws Exception
	{
		ProveedorMO proveedorMO = new ProveedorMO();
		try
		{
            String[] columnas = cadena.split(Constante.DELIMITER_SCAPE + Constante.DELIMITADOR_BARRA, Constante.NO_LIMIT);
            proveedorMO.setIdInternoCliente(columnas[Constante._1].trim());
            proveedorMO.setDenominacion(columnas[Constante._2].trim());
            proveedorMO.setDocumento(columnas[Constante._3].trim());
            Integer idTipoDocumento = columnas[Constante._4].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._4].trim());
            if (idTipoDocumento != Constante.NEGATIVO)
            {
                proveedorMO.setIdTipoDocumento(idTipoDocumento);
            }
            Integer idTipoEntidad = columnas[Constante._5].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._5].trim());
            if (idTipoEntidad != Constante.NEGATIVO)
            {
                proveedorMO.setIdTipoEntidad(idTipoEntidad);
            }
            Integer idCategoria = columnas[Constante._6].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._6].trim());
            if (idCategoria != Constante.NEGATIVO)
            {
                proveedorMO.setIdCategoria(idCategoria);
            }
            Integer idEmpresa = columnas[Constante._7].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._7].trim());
            if (idEmpresa != Constante.NEGATIVO)
            {
                proveedorMO.setIdEmpresa(idEmpresa);
            }
            String direccion = columnas[Constante._8].trim();
            if (!IsNullOrEmpty(direccion))
            {
                proveedorMO.setDireccion(direccion);
            }
            String telefono = columnas[Constante._9].trim();
            if (!IsNullOrEmpty(telefono))
            {
                proveedorMO.setTelefono(telefono);
            }
            String email = columnas[Constante._10].trim();
            if (!IsNullOrEmpty(email))
            {
                proveedorMO.setEmail(email);
            }
            String descripcionTipoDocumento = columnas[Constante._11].trim();
            if (!IsNullOrEmpty(descripcionTipoDocumento))
            {
                proveedorMO.setDescripcionTipoDocumento(descripcionTipoDocumento);
            }
            Integer idPaisDocumento = columnas[Constante._12].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._12].trim());
            if (idPaisDocumento != Constante.NEGATIVO)
            {
                proveedorMO.setIdPaisDocumento(idPaisDocumento);
            }
            String descripcionPaisDocumento = columnas[Constante._13].trim();
            if (!IsNullOrEmpty(descripcionPaisDocumento))
            {
                proveedorMO.setDescripcionPaisDocumento(descripcionPaisDocumento);
            }
            String descripcionTipoEntidad = columnas[Constante._14].trim();
            if (!IsNullOrEmpty(descripcionTipoEntidad))
            {
                proveedorMO.setDescripcionTipoEntidad(descripcionTipoEntidad);
            }
            String nombreCliente = columnas[Constante._15].trim();
            if (!IsNullOrEmpty(nombreCliente))
            {
                proveedorMO.setNombreCliente(nombreCliente);
            }
            String primerNombre = columnas[Constante._16].trim();
            if (!IsNullOrEmpty(primerNombre))
            {
                proveedorMO.setPrimerNombre(primerNombre);
            }
            String segundoNombre = columnas[Constante._17].trim();
            if (!IsNullOrEmpty(segundoNombre))
            {
                proveedorMO.setSegundoNombre(segundoNombre);
            }
            String primerApellido = columnas[Constante._18].trim();
            if (!IsNullOrEmpty(primerApellido))
            {
                proveedorMO.setPrimerApellido(primerApellido);
            }
            String segundoApellido = columnas[Constante._19].trim();
            if (!IsNullOrEmpty(segundoApellido))
            {
                proveedorMO.setSegundoApellido(segundoApellido);
            }
            String contacto = columnas[Constante._20].trim();
            if (!IsNullOrEmpty(contacto))
            {
                proveedorMO.setContacto(contacto);
            }
            Integer idPaisResidencia = columnas[Constante._21].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._21].trim());
            if (idPaisResidencia != Constante.NEGATIVO)
            {
                proveedorMO.setIdPaisResidencia(idPaisResidencia);
            }
            String descripcionPaisResidencia = columnas[Constante._22].trim();
            if (!IsNullOrEmpty(descripcionPaisResidencia))
            {
                proveedorMO.setDescripcionPaisResidencia(descripcionPaisResidencia);
            }
            Integer idPaisOrigen = columnas[Constante._23].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._23].trim());
            if (idPaisOrigen != Constante.NEGATIVO)
            {
                proveedorMO.setIdPaisOrigen(idPaisOrigen);
            }
            String descripcionPaisOrigen = columnas[Constante._24].trim();
            if (!IsNullOrEmpty(descripcionPaisOrigen))
            {
                proveedorMO.setDescripcionPaisOrigen(descripcionPaisOrigen);
            }
            String observaciones = columnas[Constante._25].trim();
            if (!IsNullOrEmpty(observaciones))
            {
                proveedorMO.setObservaciones(observaciones);
            }
            String descripcionCategoria = columnas[Constante._26].trim();
            if (!IsNullOrEmpty(descripcionCategoria))
            {
                proveedorMO.setDescripcionCategoria(descripcionCategoria);
            }
            String cargoPEP = columnas[Constante._27].trim();
            if (!IsNullOrEmpty(cargoPEP))
            {
                proveedorMO.setCargoPEP(cargoPEP);
            }
            String pep = columnas[Constante._28].trim();
            if (!IsNullOrEmpty(pep))
            {
                Boolean PEP = Boolean.parseBoolean(pep);
                proveedorMO.setPEP(PEP);
            }
            Integer idEstadoCivil = columnas[Constante._29].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._29].trim());
            if (idEstadoCivil != Constante.NEGATIVO)
            {
                proveedorMO.setIdEstadoCivil(idEstadoCivil);
            }
            String descripcionEstadoCivil = columnas[Constante._30].trim();
            if (!IsNullOrEmpty(descripcionEstadoCivil))
            {
                proveedorMO.setDescripcionEstadoCivil(descripcionEstadoCivil);
            }
            String naturalezaDeRelacion = columnas[Constante._31].trim();
            if (!IsNullOrEmpty(naturalezaDeRelacion))
            {
                proveedorMO.setNaturalezaDeRelacion(naturalezaDeRelacion);
            }
            String profesion = columnas[Constante._32].trim();
            if (!IsNullOrEmpty(profesion))
            {
                proveedorMO.setProfesion(profesion);
            }
            String origenDeFondos = columnas[Constante._33].trim();
            if (!IsNullOrEmpty(origenDeFondos))
            {
                proveedorMO.setOrigenDeFondos(origenDeFondos);
            }
            String fechaNacimiento = columnas[Constante._34].trim();
            if (!IsNullOrEmpty(fechaNacimiento))
            {
                proveedorMO.setFechaNacimiento(fechaNacimiento);
            }
            String LugarNacimiento = columnas[Constante._35].trim();
            if (!IsNullOrEmpty(LugarNacimiento))
            {
                proveedorMO.setLugarNacimiento(LugarNacimiento);
            }
            String incluidoEnMatching = columnas[Constante._36].trim();
            if (!IsNullOrEmpty(incluidoEnMatching))
            {
                Boolean IncluidoEnMatching = Boolean.parseBoolean(incluidoEnMatching);
                proveedorMO.setIncluidoEnMatching(IncluidoEnMatching);
            }
            String descripcionEmpresa = columnas[Constante._37].trim();
            if (!IsNullOrEmpty(descripcionEmpresa))
            {
                proveedorMO.setDescripcionEmpresa(descripcionEmpresa);
            }
            String incluidoEnMatchingIntegrantes = columnas[Constante._38].trim();
            if (!IsNullOrEmpty(incluidoEnMatchingIntegrantes))
            {
                Boolean IncluidoEnMatchingIntegrantes = Boolean.parseBoolean(incluidoEnMatchingIntegrantes);
                proveedorMO.setIncluidoEnMatchingIntegrantes(IncluidoEnMatchingIntegrantes);
            }
            Integer idRiesgo = columnas[Constante._39].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._39].trim());
            if (idRiesgo != Constante.NEGATIVO)
            {
                proveedorMO.setIdRiesgo(idRiesgo);
            }
            String descripcionRiesgo = columnas[Constante._40].trim();
            if (!IsNullOrEmpty(descripcionRiesgo))
            {
                proveedorMO.setDescripcionRiesgo(descripcionRiesgo);
            }
		}
		catch (Exception e)
		{
            throw e;
		}
		return proveedorMO;
    }
    
    public ColaboradorMO ObtenerColaborador(String cadena) throws Exception
	{
		ColaboradorMO colaboradorMO = new ColaboradorMO();
		try
		{
            String[] columnas = cadena.split(Constante.DELIMITER_SCAPE + Constante.DELIMITADOR_BARRA, Constante.NO_LIMIT);
            colaboradorMO.setIdInternoCliente(columnas[Constante._1].trim());
            colaboradorMO.setDenominacion(columnas[Constante._2].trim());
            colaboradorMO.setDocumento(columnas[Constante._3].trim());
            Integer idTipoDocumento = columnas[Constante._4].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._4].trim());
            if (idTipoDocumento != Constante.NEGATIVO)
            {
                colaboradorMO.setIdTipoDocumento(idTipoDocumento);
            }
            Integer idTipoEntidad = columnas[Constante._5].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._5].trim());
            if (idTipoEntidad != Constante.NEGATIVO)
            {
                colaboradorMO.setIdTipoEntidad(idTipoEntidad);
            }
            Integer idCategoria = columnas[Constante._6].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._6].trim());
            if (idCategoria != Constante.NEGATIVO)
            {
                colaboradorMO.setIdCategoria(idCategoria);
            }
            Integer idEmpresa = columnas[Constante._7].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._7].trim());
            if (idEmpresa != Constante.NEGATIVO)
            {
                colaboradorMO.setIdEmpresa(idEmpresa);
            }
            String direccion = columnas[Constante._8].trim();
            if (!IsNullOrEmpty(direccion))
            {
                colaboradorMO.setDireccion(direccion);
            }
            String telefono = columnas[Constante._9].trim();
            if (!IsNullOrEmpty(telefono))
            {
                colaboradorMO.setTelefono(telefono);
            }
            String email = columnas[Constante._10].trim();
            if (!IsNullOrEmpty(email))
            {
                colaboradorMO.setEmail(email);
            }
            String descripcionTipoDocumento = columnas[Constante._11].trim();
            if (!IsNullOrEmpty(descripcionTipoDocumento))
            {
                colaboradorMO.setDescripcionTipoDocumento(descripcionTipoDocumento);
            }
            Integer idPaisDocumento = columnas[Constante._12].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._12].trim());
            if (idPaisDocumento != Constante.NEGATIVO)
            {
                colaboradorMO.setIdPaisDocumento(idPaisDocumento);
            }
            String descripcionPaisDocumento = columnas[Constante._13].trim();
            if (!IsNullOrEmpty(descripcionPaisDocumento))
            {
                colaboradorMO.setDescripcionPaisDocumento(descripcionPaisDocumento);
            }
            String descripcionTipoEntidad = columnas[Constante._14].trim();
            if (!IsNullOrEmpty(descripcionTipoEntidad))
            {
                colaboradorMO.setDescripcionTipoEntidad(descripcionTipoEntidad);
            }
            String nombreCliente = columnas[Constante._15].trim();
            if (!IsNullOrEmpty(nombreCliente))
            {
                colaboradorMO.setNombreCliente(nombreCliente);
            }
            String primerNombre = columnas[Constante._16].trim();
            if (!IsNullOrEmpty(primerNombre))
            {
                colaboradorMO.setPrimerNombre(primerNombre);
            }
            String segundoNombre = columnas[Constante._17].trim();
            if (!IsNullOrEmpty(segundoNombre))
            {
                colaboradorMO.setSegundoNombre(segundoNombre);
            }
            String primerApellido = columnas[Constante._18].trim();
            if (!IsNullOrEmpty(primerApellido))
            {
                colaboradorMO.setPrimerApellido(primerApellido);
            }
            String segundoApellido = columnas[Constante._19].trim();
            if (!IsNullOrEmpty(segundoApellido))
            {
                colaboradorMO.setSegundoApellido(segundoApellido);
            }
            String contacto = columnas[Constante._20].trim();
            if (!IsNullOrEmpty(contacto))
            {
                colaboradorMO.setContacto(contacto);
            }
            Integer idPaisResidencia = columnas[Constante._21].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._21].trim());
            if (idPaisResidencia != Constante.NEGATIVO)
            {
                colaboradorMO.setIdPaisResidencia(idPaisResidencia);
            }
            String descripcionPaisResidencia = columnas[Constante._22].trim();
            if (!IsNullOrEmpty(descripcionPaisResidencia))
            {
                colaboradorMO.setDescripcionPaisResidencia(descripcionPaisResidencia);
            }
            Integer idPaisOrigen = columnas[Constante._23].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._23].trim());
            if (idPaisOrigen != Constante.NEGATIVO)
            {
                colaboradorMO.setIdPaisOrigen(idPaisOrigen);
            }
            String descripcionPaisOrigen = columnas[Constante._24].trim();
            if (!IsNullOrEmpty(descripcionPaisOrigen))
            {
                colaboradorMO.setDescripcionPaisOrigen(descripcionPaisOrigen);
            }
            String observaciones = columnas[Constante._25].trim();
            if (!IsNullOrEmpty(observaciones))
            {
                colaboradorMO.setObservaciones(observaciones);
            }
            String descripcionCategoria = columnas[Constante._26].trim();
            if (!IsNullOrEmpty(descripcionCategoria))
            {
                colaboradorMO.setDescripcionCategoria(descripcionCategoria);
            }
            String cargoPEP = columnas[Constante._27].trim();
            if (!IsNullOrEmpty(cargoPEP))
            {
                colaboradorMO.setCargoPEP(cargoPEP);
            }
            String pep = columnas[Constante._28].trim();
            if (!IsNullOrEmpty(pep))
            {
                Boolean PEP = Boolean.parseBoolean(pep);
                colaboradorMO.setPEP(PEP);
            }
            Integer idEstadoCivil = columnas[Constante._29].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._29].trim());
            if (idEstadoCivil != Constante.NEGATIVO)
            {
                colaboradorMO.setIdEstadoCivil(idEstadoCivil);
            }
            String descripcionEstadoCivil = columnas[Constante._30].trim();
            if (!IsNullOrEmpty(descripcionEstadoCivil))
            {
                colaboradorMO.setDescripcionEstadoCivil(descripcionEstadoCivil);
            }
            String naturalezaDeRelacion = columnas[Constante._31].trim();
            if (!IsNullOrEmpty(naturalezaDeRelacion))
            {
                colaboradorMO.setNaturalezaDeRelacion(naturalezaDeRelacion);
            }
            String profesion = columnas[Constante._32].trim();
            if (!IsNullOrEmpty(profesion))
            {
                colaboradorMO.setProfesion(profesion);
            }
            String origenDeFondos = columnas[Constante._33].trim();
            if (!IsNullOrEmpty(origenDeFondos))
            {
                colaboradorMO.setOrigenDeFondos(origenDeFondos);
            }
            String fechaNacimiento = columnas[Constante._34].trim();
            if (!IsNullOrEmpty(fechaNacimiento))
            {
                colaboradorMO.setFechaNacimiento(fechaNacimiento);
            }
            String LugarNacimiento = columnas[Constante._35].trim();
            if (!IsNullOrEmpty(LugarNacimiento))
            {
                colaboradorMO.setLugarNacimiento(LugarNacimiento);
            }
            String incluidoEnMatching = columnas[Constante._36].trim();
            if (!IsNullOrEmpty(incluidoEnMatching))
            {
                Boolean IncluidoEnMatching = Boolean.parseBoolean(incluidoEnMatching);
                colaboradorMO.setIncluidoEnMatching(IncluidoEnMatching);
            }
            String descripcionEmpresa = columnas[Constante._37].trim();
            if (!IsNullOrEmpty(descripcionEmpresa))
            {
                colaboradorMO.setDescripcionEmpresa(descripcionEmpresa);
            }
            String incluidoEnMatchingIntegrantes = columnas[Constante._38].trim();
            if (!IsNullOrEmpty(incluidoEnMatchingIntegrantes))
            {
                Boolean IncluidoEnMatchingIntegrantes = Boolean.parseBoolean(incluidoEnMatchingIntegrantes);
                colaboradorMO.setIncluidoEnMatchingIntegrantes(IncluidoEnMatchingIntegrantes);
            }
            Integer idRiesgo = columnas[Constante._39].trim().equals("") ? Constante.NEGATIVO : Integer.parseInt(columnas[Constante._39].trim());
            if (idRiesgo != Constante.NEGATIVO)
            {
                colaboradorMO.setIdRiesgo(idRiesgo);
            }
            String descripcionRiesgo = columnas[Constante._40].trim();
            if (!IsNullOrEmpty(descripcionRiesgo))
            {
                colaboradorMO.setDescripcionRiesgo(descripcionRiesgo);
            }
		}
		catch (Exception e)
		{
            throw e;
		}
		return colaboradorMO;
    }

    public static Boolean IsNullOrEmpty(String valor) throws Exception
    {
        Boolean isNullOrEmpty = false;
        try
        {
            isNullOrEmpty = valor == null || valor.length() == Constante._0;
        }
        catch (Exception e)
        {
            throw e;
        }
        return isNullOrEmpty;
    }

    public static Boolean IsNullOrEmpty(Integer valor) throws Exception
    {
        Boolean isNullOrEmpty = false;
        try
        {
            isNullOrEmpty = valor == null || valor < Constante._0;
        }
        catch (Exception e)
        {
            throw e;
        }
        return isNullOrEmpty;
    }
}
