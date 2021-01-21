package com.grupogloria.splaconsola.Negocio;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.grupogloria.splaconsola.Comun.Constante;
import com.grupogloria.splaconsola.Comun.Log;
import com.grupogloria.splaconsola.Comun.Util;
import com.grupogloria.splaconsola.Modelo.ArchivoMO;
import com.grupogloria.splaconsola.Modelo.ConexionMO;
import com.grupogloria.splaconsola.Modelo.NotificacionMO;
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

	public ProcessTasklet() throws Exception
	{
		_clienteNE = new ClienteNE();
		_proveedorNE = new ProveedorNE();
		_colaboradorNE = new ColaboradorNE();
		_notificacionNE = new NotificacionNE();
		_util = new Util();
		_log = new Log(ProcessTasklet.class.getName(), "");
	}

    @Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		try
		{
			ProcesarInformacion();
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

	private void ProcesarInformacion() throws Exception
	{
		FTPClient ftpClient = new FTPClient();
		try
		{
			ConexionMO conexionMO = _util.ObtenerConexion();
			_log.info(String.format(Constante.FTP_CONNECTION, conexionMO.getFtpServer(), conexionMO.getFtpPort()));
			ftpClient.connect(conexionMO.getFtpServer(), conexionMO.getFtpPort());
			Integer replyCode = ftpClient.getReplyCode();
			String replyString = ftpClient.getReplyString();
			_log.info(String.format(Constante.FTP_REPLY, replyCode, replyString));

			if (!FTPReply.isPositiveCompletion(replyCode))
			{
				_log.info(String.format(Constante.SERVIDOR_CAIDO, conexionMO.getFtpServer()));
				_log.ShowServerReply(ftpClient);
			}
			else
			{
				_log.info(String.format(Constante.FTP_INICIO_SESION, conexionMO.getFtpUsername(), conexionMO.getFtpPassword()));
				Boolean isConnected = ftpClient.login(conexionMO.getFtpUsername(), conexionMO.getFtpPassword());
			
				if (!isConnected)
				{
					_log.info(String.format(Constante.CREDENCIALES_INCORRECTAS, conexionMO.getFtpUsername(), conexionMO.getFtpPassword()));
				}
				else
				{
					_log.info(String.format(Constante.FTP_WORKSPACE, conexionMO.getFtpDirectory()));
					Boolean isDirectory = ftpClient.changeWorkingDirectory(conexionMO.getFtpDirectory());
					
					if (!isDirectory)
					{
						_log.info(String.format(Constante.DIRECTORIO_CAIDO, conexionMO.getFtpDirectory(), conexionMO.getFtpServer()));
						_log.ShowServerReply(ftpClient);
					}
					else
					{
						_log.info(String.format(Constante.ENCOLA_ARCHIVO, ftpClient.listFiles().length));
						FTPFile[] ftpFiles = ftpClient.listFiles();
						
						for (FTPFile ftpFile : ftpFiles)
						{
							Integer fileType = ftpFile.getType();
							String nombreArchivo = ftpFile.getName();
							String nombreArchivoSinExtension = nombreArchivo.contains(Constante.DELIMITADOR_PUNTO) ? nombreArchivo.substring(Constante._0, nombreArchivo.indexOf(Constante.DELIMITADOR_PUNTO)) : "";
							String extension = nombreArchivo.contains(Constante.DELIMITADOR_PUNTO) ? nombreArchivo.substring(nombreArchivo.indexOf(Constante.DELIMITADOR_PUNTO) + Constante._1).toLowerCase() : "";
							try
							{
								if (fileType == FTPFile.FILE_TYPE && extension.equals(Constante.EXTENSION_ZIP))
								{
									_log.info(String.format(Constante.RECORRIENDO_ARCHIVO, nombreArchivo));
									Boolean esCliente = nombreArchivo.toLowerCase().contains(Constante.ENTIDAD_CLIENTE.toLowerCase());
									Boolean esProveedor = nombreArchivo.toLowerCase().contains(Constante.ENTIDAD_PROVEEDOR.toLowerCase());
									Boolean esColaborador = nombreArchivo.toLowerCase().contains(Constante.ENTIDAD_COLABORADOR.toLowerCase());
									InputStream inputStream = ftpClient.retrieveFileStream(nombreArchivo);
									replyCode = ftpClient.getReply();
									replyString = ftpClient.getReplyString();
									_log.info(String.format(Constante.FTP_REPLY, replyCode, replyString));
									File tempFile = File.createTempFile(nombreArchivoSinExtension, Constante.DELIMITADOR_PUNTO + Constante.EXTENSION_ZIP);
									FileUtils.copyInputStreamToFile(inputStream, tempFile);
									inputStream.close();
									ZipFile zipFile = new ZipFile(tempFile);
									Enumeration<? extends ZipEntry> entries = zipFile.entries();
									NotificacionMO notificacionMO = new NotificacionMO();
									notificacionMO.setNombreArchivo(nombreArchivo);
									List<ArchivoMO> listaArchivos = null;

									while(entries.hasMoreElements())
									{
										listaArchivos = new ArrayList<>();
										ZipEntry entry = entries.nextElement();
										InputStream currentInputStream = zipFile.getInputStream(entry);
										List<String> lines = IOUtils.readLines(currentInputStream, Constante.UTF_8);
										currentInputStream.close();
										ArchivoMO archivoMO = new ArchivoMO();
										String archivoEnCurso = entry.getName();
										archivoMO.setNombreArchivo(archivoEnCurso);
										_log.info(String.format(Constante.ENCURSO_ARCHIVO, archivoEnCurso));
										
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

										listaArchivos.add(archivoMO);
									}

									zipFile.close();
									FileUtils.forceDelete(tempFile);
									ftpClient.deleteFile(ftpFile.getName());
									notificacionMO.setListaArchivos(listaArchivos);
									ObjetoNotificacionMO objetoNotificacionMO = _notificacionNE.EnviarNotificacion(notificacionMO);
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
			ftpClient.logout();
			ftpClient.disconnect();
		}
	}
}
