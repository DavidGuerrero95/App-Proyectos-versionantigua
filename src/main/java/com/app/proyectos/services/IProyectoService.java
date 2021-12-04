package com.app.proyectos.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.app.proyectos.models.Proyectos;
import com.app.proyectos.models.ProyectosFiles;
import com.app.proyectos.models.ProyectosPhotos;

public interface IProyectoService {

	public ProyectosPhotos crearPhoto(MultipartFile file, String nombre) throws IOException;

	public ProyectosFiles crearFile(MultipartFile upload, String nombre) throws IOException;

	public String editarProyecto(String nombre, Proyectos proyectoEditar);

}
