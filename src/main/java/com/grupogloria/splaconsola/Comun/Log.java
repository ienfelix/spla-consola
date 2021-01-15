package com.grupogloria.splaconsola.Comun;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.core.io.ClassPathResource;

public class Log
{
    private Logger _logger = null;
    private String _entidad = "";

    public Log(String className, String entidad)
    {
        _logger = Logger.getLogger(className);
        _entidad = entidad;
    }

    public void ShowServerReply(FTPClient ftpClient) throws Exception
	{
        try
        {
            String[] replies = ftpClient.getReplyStrings();
		
            if (replies != null && replies.length > Constante._0)
            {
                for (String reply : replies)
                {
                    _logger.info(String.format(Constante.SERVIDOR_RESPUESTA, reply));
                }
            }
        }
        catch (Exception e)
        {
            error(e);
            throw e;
        }
    }

    private FileHandler GetFileHandler(String entidad) throws Exception
    {
        FileHandler fileHandler = null;
        InputStream inputStream = null;
        try
        {
            ClassPathResource classPathResource = new ClassPathResource("application.properties");
            InputStream inputStreamTemp = classPathResource.getInputStream();
            File tempFile = File.createTempFile(Constante.APPLICATION_PROPERTIES, null);
            FileUtils.copyInputStreamToFile(inputStreamTemp, tempFile);
            inputStreamTemp.close();
            inputStream = new FileInputStream(tempFile);
            tempFile.delete();
            Properties properties = new Properties();
            properties.load(inputStream);
            inputStream.close();
            String rutaBitacora = Util.IsNullOrEmpty(properties.getProperty(Constante.API_RUTA_BITACORA)) ? "" : properties.getProperty(Constante.API_RUTA_BITACORA);
            String nombreBitacora = Util.IsNullOrEmpty(properties.getProperty(Constante.API_NOMBRE_BITACORA)) ? "" : properties.getProperty(Constante.API_NOMBRE_BITACORA);
            properties.clear();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(new Date());
            String nombre = "";

            if (entidad.equals(""))
            {
                nombre = nombreBitacora + Constante.DELIMITADOR_PUNTO + Constante.EXTENSION_TXT;
            }
            else
            {
                nombre = entidad + Constante.DELIMITADOR_GUION_BAJO + nombreBitacora + Constante.DELIMITADOR_PUNTO + Constante.EXTENSION_TXT;
            }
            
            String rutaArchivo = rutaBitacora + Constante.DELIMITADOR_BARRA_OBLICUA + date + Constante.DELIMITADOR_GUION_BAJO + nombre;
            File fileBitacora = new File(rutaBitacora);

            if (!fileBitacora.exists())
            {
                fileBitacora.mkdirs();
            }

            Boolean append = true;
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler = new FileHandler(rutaArchivo, append);
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setEncoding(Constante.UTF_8);
        }
        catch (Exception e)
        {
            _logger.log(Level.INFO, e.getMessage());
            error(e);
            throw e;
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
        return fileHandler;
    }
    
    public void info(String message) throws Exception
    {
        try
        {
            _logger.log(Level.INFO, message);
            FileHandler fileHandler = GetFileHandler(_entidad);
		    _logger.addHandler(fileHandler);
            _logger.log(Level.INFO, message);
            fileHandler.close();
        }
        catch (Exception e)
        {
            error(e);
            throw e;
        }
    }

    public void error(Exception e) throws Exception
    {
        try
        {
            _logger.log(Level.SEVERE, e.getMessage());
            FileHandler fileHandler = GetFileHandler(_entidad);
		    _logger.addHandler(fileHandler);
            var stack = e.getStackTrace()[Constante._0];
            _logger.log(Level.SEVERE, String.format(Constante.ERROR, stack.getClassName(), stack.getMethodName(), stack.getLineNumber(), e.getMessage()));
            fileHandler.close();
        }
        catch (Exception ex)
        {
            var stack = ex.getStackTrace()[Constante._0];
            _logger.log(Level.SEVERE, String.format(Constante.ERROR, stack.getClassName(), stack.getMethodName(), stack.getLineNumber(), ex.getMessage()));
            throw ex;
        }
    }
}
