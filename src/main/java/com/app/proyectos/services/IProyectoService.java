package com.app.proyectos.services;

import org.springframework.web.multipart.MultipartFile;

import com.app.proyectos.models.Proyectos;
import com.app.proyectos.models.ProyectosFiles;
import com.app.proyectos.models.ProyectosPhotos;

public interface IProyectoService {

	public String editarProyecto(Integer codigoProyecto, Proyectos proyectoEditar);

	public void savePhoto(Integer codigoProyecto, MultipartFile file);

	public void saveFile(Integer codigoProyecto, MultipartFile file);

	public void saveFistPhoto(MultipartFile image);

	public ProyectosPhotos crearPhotofirst(Integer codigoProyecto);

	public void crearProyecto(Proyectos proyectos, ProyectosPhotos pPhotos, ProyectosFiles pFiles);

}
