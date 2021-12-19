package com.app.proyectos.services;

import org.springframework.web.multipart.MultipartFile;

import com.app.proyectos.models.Proyectos;

public interface IProyectoService {

	public String editarProyecto(String nombre, Proyectos proyectoEditar);

	public void savePhoto(String nombre, MultipartFile file);

	public void saveFile(String nombre, MultipartFile file);

}
