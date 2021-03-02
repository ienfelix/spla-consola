package com.grupogloria.splaconsola.Negocio;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.grupogloria.splaconsola.Comun.Constante;
import com.grupogloria.splaconsola.Comun.Log;
import com.grupogloria.splaconsola.Comun.Util;
import com.grupogloria.splaconsola.Modelo.ArchivoMO;
import com.grupogloria.splaconsola.Modelo.ConexionMO;
import com.grupogloria.splaconsola.Modelo.ObjetoClienteMO;
import com.grupogloria.splaconsola.Modelo.ObjetoColaboradorMO;
import com.grupogloria.splaconsola.Modelo.ObjetoNotificacionMO;
import com.grupogloria.splaconsola.Modelo.ObjetoProveedorMO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public class ProcessTasklet implements Tasklet, InitializingBean
{
	private ClienteNE _clienteNE = null;
	private ProveedorNE _proveedorNE = null;
	private ColaboradorNE _colaboradorNE = null;
	private NotificacionNE _notificacionNE = null;
	private Util _util = null;
	private Log _log = null;
	private FTPClient _ftpClient = null;

	public ProcessTasklet() throws Exception
	{
		_clienteNE = new ClienteNE();
		_proveedorNE = new ProveedorNE();
		_colaboradorNE = new ColaboradorNE();
		_notificacionNE = new NotificacionNE();
		_util = new Util();
		_log = new Log(ProcessTasklet.class.getName(), "");
		_ftpClient = new FTPClient();
	}

    @Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		try
		{
			ConexionMO conexionMO = DescargarArchivos();
			ProcesarArchivos(conexionMO);
		}
		catch (Exception e)
		{
			_log.error(e);
			throw e;
		}
		return RepeatStatus.FINISHED;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(_util, "Problemas en la definición de ProcessTasklet");
	}

	private ConexionMO DescargarArchivos() throws Exception
	{
		ConexionMO conexionMO = null;
		try
		{
			conexionMO = _util.ObtenerConexion();
			_log.info(String.format(Constante.FTP_CONNECTION, conexionMO.getFtpServer(), conexionMO.getFtpPort()));
			_ftpClient.connect(conexionMO.getFtpServer(), conexionMO.getFtpPort());
			Integer replyCode = _ftpClient.getReplyCode();
			String replyString = _ftpClient.getReplyString();
			_log.info(String.format(Constante.FTP_REPLY, replyCode, replyString));

			if (!FTPReply.isPositiveCompletion(replyCode))
			{
				_log.info(String.format(Constante.SERVIDOR_CAIDO, conexionMO.getFtpServer()));
				_log.ShowServerReply(replyCode, _ftpClient.getReplyStrings());
			}
			else
			{
				_log.info(String.format(Constante.FTP_INICIO_SESION, conexionMO.getFtpUsername(), conexionMO.getFtpPassword()));
				Boolean isConnected = _ftpClient.login(conexionMO.getFtpUsername(), conexionMO.getFtpPassword());
				_ftpClient.enterLocalPassiveMode();
				replyCode = _ftpClient.getReplyCode();
				replyString = _ftpClient.getReplyString();
				_log.info(String.format(Constante.FTP_REPLY, replyCode, replyString));
			
				if (!isConnected)
				{
					_log.info(String.format(Constante.CREDENCIALES_INCORRECTAS, conexionMO.getFtpUsername(), conexionMO.getFtpPassword()));
				}
				else
				{
					_log.info(String.format(Constante.FTP_WORKSPACE, conexionMO.getFtpWorkspace()));
					Boolean isDirectory = _ftpClient.changeWorkingDirectory(conexionMO.getFtpWorkspace());
					replyCode = _ftpClient.getReplyCode();
					replyString = _ftpClient.getReplyString();
					_log.info(String.format(Constante.FTP_REPLY, replyCode, replyString));
					
					if (!isDirectory)
					{
						_log.info(String.format(Constante.DIRECTORIO_CAIDO, conexionMO.getFtpWorkspace(), conexionMO.getFtpServer()));
						_log.ShowServerReply(replyCode, _ftpClient.getReplyStrings());
					}
					else
					{
						Integer contador = Constante._0;
						FTPFile[] ftpFiles = _ftpClient.listFiles();
						_log.info(String.format(Constante.ARCHIVO_ENCOLA, ftpFiles.length));
						
						for (FTPFile ftpFile : ftpFiles)
						{
							_log.info(String.format(Constante.ARCHIVO_RECORRIDO, ftpFile.getName()));
							Integer fileType = ftpFile.getType();
							String nombreArchivo = ftpFile.getName();
							String extension = nombreArchivo.contains(Constante.DELIMITADOR_PUNTO) ? nombreArchivo.substring(nombreArchivo.indexOf(Constante.DELIMITADOR_PUNTO) + Constante._1).toLowerCase() : "";
							try
							{
								if (fileType == FTPFile.FILE_TYPE && extension.equals(Constante.EXTENSION_ZIP))
								{
									String carpeta = conexionMO.getRutaTrabajo();
									String ruta = carpeta + Constante.DELIMITADOR_BARRA_OBLICUA + nombreArchivo;
									File file = new File(ruta);
									FileOutputStream fileOutputStream = new FileOutputStream(file);
									OutputStream outputStream = new BufferedOutputStream(fileOutputStream);
									Boolean isDownloaded = _ftpClient.retrieveFile(nombreArchivo, outputStream);
									contador = isDownloaded ? ++contador : contador;
									_log.info(String.format(Constante.ARCHIVO_DESCARGADO, nombreArchivo, isDownloaded));
									outputStream.close();
								}
							}
							catch (Exception e)
							{
								_log.error(e);
								continue;
							}
						}

						_log.info(String.format(Constante.ARCHIVOS_DESCARGADOS, contador, ftpFiles.length));
					}
				}
			}
		}
		catch (Exception e)
		{
			_log.error(e);
			throw e;
		}
		finally
		{
			if (_ftpClient.isConnected())
			{
				_ftpClient.logout();
				_ftpClient.disconnect();
			}
		}
		return conexionMO;
	}

	private void ProcesarArchivos(ConexionMO conexionMO) throws Exception
	{
		try
		{
			File carpeta = new File(conexionMO.getRutaTrabajo());
			File[] arreglo = carpeta.listFiles();

			for (Integer i = Constante._0; i < arreglo.length; i++)
			{
				try
				{
					_log.info(String.format(Constante.ARCHIVO_RECORRIDO, arreglo[i].getName()));
					File file = arreglo[i];
					Boolean isFile = file.isFile();
					String nombreArchivo = file.getName();
					String nombreArchivoSinExtension = nombreArchivo.contains(Constante.DELIMITADOR_PUNTO) ? nombreArchivo.substring(Constante._0, nombreArchivo.indexOf(Constante.DELIMITADOR_PUNTO)) : "";
					String extension = nombreArchivo.contains(Constante.DELIMITADOR_PUNTO) ? nombreArchivo.substring(nombreArchivo.indexOf(Constante.DELIMITADOR_PUNTO) + Constante._1).toLowerCase() : "";
					
					if (isFile && extension.equals(Constante.EXTENSION_ZIP))
					{
						SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constante.FORMAT_FECHA_HORA);
						String fechaInicial = simpleDateFormat.format(new Date());
						Boolean esCliente = nombreArchivo.toLowerCase().contains(Constante.ENTIDAD_CLIENTE.toLowerCase());
						Boolean esProveedor = nombreArchivo.toLowerCase().contains(Constante.ENTIDAD_PROVEEDOR.toLowerCase());
						Boolean esColaborador = nombreArchivo.toLowerCase().contains(Constante.ENTIDAD_COLABORADOR.toLowerCase());
						ZipFile zipFile = new ZipFile(file.getPath());
						Enumeration<? extends ZipEntry> entries = zipFile.entries();
						List<ArchivoMO> listaArchivos = null;

						while(entries.hasMoreElements())
						{
							listaArchivos = new ArrayList<>();
							String fechaInicio = simpleDateFormat.format(new Date());
							ZipEntry entry = entries.nextElement();
							InputStream currentInputStream = zipFile.getInputStream(entry);
							List<String> lines = IOUtils.readLines(currentInputStream, Constante.UTF_8);
							currentInputStream.close();
							ArchivoMO archivoMO = new ArchivoMO();
							String archivoEnCurso = entry.getName();
							archivoMO.setNombreArchivo(archivoEnCurso);
							_log.info(String.format(Constante.ARCHIVO_ENCURSO, archivoEnCurso));
							
							if (esCliente)
							{
								ObjetoClienteMO objetoClienteMO = _clienteNE.ProcesarClientes(lines, nombreArchivoSinExtension, conexionMO.getRutaProcesado(), conexionMO.getRutaNoProcesado());
								archivoMO.setMensaje(objetoClienteMO.getMensaje());
							}
							else if (esProveedor)
							{
								ObjetoProveedorMO objetoProveedorMO = _proveedorNE.ProcesarProveedores(lines, nombreArchivoSinExtension, conexionMO.getRutaProcesado(), conexionMO.getRutaNoProcesado());
								archivoMO.setMensaje(objetoProveedorMO.getMensaje());
							}
							else if (esColaborador)
							{
								ObjetoColaboradorMO objetoColaboradorMO = _colaboradorNE.ProcesarColaboradores(lines, nombreArchivoSinExtension, conexionMO.getRutaProcesado(), conexionMO.getRutaNoProcesado());
								archivoMO.setMensaje(objetoColaboradorMO.getMensaje());
							}

							String fechaFin = simpleDateFormat.format(new Date());
							archivoMO.setFechaInicio(fechaInicio);
							archivoMO.setFechaFin(fechaFin);
							listaArchivos.add(archivoMO);
							_log.info(String.format(Constante.ARCHIVO_DURACION, fechaInicio, fechaFin));
						}

						zipFile.close();
						FileUtils.forceDelete(file);
						EliminarArchivo(conexionMO, nombreArchivo);
						String entidad = esCliente ? Constante.ENTIDAD_CLIENTE : esProveedor ? Constante.ENTIDAD_PROVEEDOR : Constante.ENTIDAD_COLABORADOR;
						String fechaFinal = simpleDateFormat.format(new Date());
						ObjetoNotificacionMO objetoNotificacionMO = _notificacionNE.EnviarNotificacion(nombreArchivo, listaArchivos, entidad, fechaInicial, fechaFinal);
						_log.info(objetoNotificacionMO.getMensaje());
					}
				}
				catch (Exception e)
				{
					_log.error(e);
					continue;
				}
			}
		}
		catch (Exception e)
		{
			_log.error(e);
			throw e;
		}
	}

	private Boolean EliminarArchivo(ConexionMO conexionMO, String nombreArchivo) throws Exception
	{
		Boolean esEliminado = false;
		try
		{
			_log.info(String.format(Constante.FTP_CONNECTION, conexionMO.getFtpServer(), conexionMO.getFtpPort()));
			_ftpClient.connect(conexionMO.getFtpServer(), conexionMO.getFtpPort());
			Integer replyCode = _ftpClient.getReplyCode();
			String replyString = _ftpClient.getReplyString();
			_log.info(String.format(Constante.FTP_REPLY, replyCode, replyString));

			if (!FTPReply.isPositiveCompletion(replyCode))
			{
				_log.info(String.format(Constante.SERVIDOR_CAIDO, conexionMO.getFtpServer()));
				_log.ShowServerReply(replyCode, _ftpClient.getReplyStrings());
			}
			else
			{
				_log.info(String.format(Constante.FTP_INICIO_SESION, conexionMO.getFtpUsername(), conexionMO.getFtpPassword()));
				Boolean isConnected = _ftpClient.login(conexionMO.getFtpUsername(), conexionMO.getFtpPassword());
				_ftpClient.enterLocalPassiveMode();
				replyCode = _ftpClient.getReplyCode();
				replyString = _ftpClient.getReplyString();
				_log.info(String.format(Constante.FTP_REPLY, replyCode, replyString));
			
				if (!isConnected)
				{
					_log.info(String.format(Constante.CREDENCIALES_INCORRECTAS, conexionMO.getFtpUsername(), conexionMO.getFtpPassword()));
				}
				else
				{
					String ruta = conexionMO.getFtpWorkspace() + Constante.DELIMITADOR_BARRA_OBLICUA + nombreArchivo;
					esEliminado = _ftpClient.deleteFile(ruta);
					_log.info(String.format(Constante.ARCHIVO_ELIMINADO, nombreArchivo, esEliminado));
				}
			}
		}
		catch (Exception e)
		{
			_log.error(e);
			throw e;
		}
		finally
		{
			if (_ftpClient.isConnected())
			{
				_ftpClient.logout();
				_ftpClient.disconnect();
			}
		}
		return esEliminado;
	}
}
