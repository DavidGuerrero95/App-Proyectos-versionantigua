package com.app.proyectos.services;

import java.io.IOException;
import java.util.Date;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.proyectos.models.Proyectos;
import com.app.proyectos.models.ProyectosFiles;
import com.app.proyectos.models.ProyectosPhotos;
import com.app.proyectos.repository.ProyectosFilesRepository;
import com.app.proyectos.repository.ProyectosPhotosRepository;
import com.app.proyectos.repository.ProyectosRepository;

@Service
public class ProyectoService implements IProyectoService {

	@Autowired
	ProyectosRepository pRepository;
	
	@Autowired
	ProyectosFilesRepository pfRepository;
	
	@Autowired
	ProyectosPhotosRepository phRepository;

	@Override
	public ProyectosPhotos crearPhoto(MultipartFile file, String nombre) throws IOException {
		ProyectosPhotos pPhotos = new ProyectosPhotos();
		if(phRepository.existsByNombre(nombre)) {
			pPhotos = phRepository.findByNombre(nombre);
		} else {
			pPhotos.setNombre(nombre);
		}
		if (file == null) {
			return pPhotos;
		}
		String fileName = file.getOriginalFilename();
		pPhotos.setName(fileName);
		pPhotos.setCreatedtime(new Date());
		pPhotos.setContent(new Binary(file.getBytes()));
		pPhotos.setContenttype(file.getContentType());
		pPhotos.setSize(file.getSize());
		return pPhotos;
	}

	@Override
	public ProyectosFiles crearFile(MultipartFile file, String nombre) throws IOException {
		ProyectosFiles pFiles = new ProyectosFiles();
		pFiles.setNombre(nombre);
		if(pfRepository.existsByNombre(nombre)) {
			pFiles = pfRepository.findByNombre(nombre);
		} else {
			pFiles.setNombre(nombre);
		}
		if (file == null) {
			return pFiles;
		}
		String fileName = file.getOriginalFilename();
		pFiles.setName(fileName);
		pFiles.setCreatedtime(new Date());
		pFiles.setContent(new Binary(file.getBytes()));
		pFiles.setContenttype(file.getContentType());
		pFiles.setSize(file.getSize());
		return pFiles;
	}

	@Override
	public String editarProyecto(String nombre, Proyectos proyectoEditar) {
		Proyectos proyecto = pRepository.findByNombre(nombre);
		if (proyectoEditar.getNombre() != null) {
			proyecto.setNombre(proyectoEditar.getNombre());
		}
		if (proyectoEditar.getPalabrasClave() != null) {
			proyecto.setPalabrasClave(proyectoEditar.getPalabrasClave());
		}
		if (proyectoEditar.getLocalizacion() != null) {
			proyecto.setLocalizacion(proyectoEditar.getLocalizacion());
		}
		if (proyectoEditar.getResumen() != null) {
			proyecto.setResumen(proyectoEditar.getResumen());
		}
		if (proyectoEditar.getObjetivos() != null) {
			proyecto.setObjetivos(proyectoEditar.getObjetivos());
		}
		if (proyectoEditar.getDescripcion() != null) {
			proyecto.setDescripcion(proyectoEditar.getDescripcion());
		}
		if (proyectoEditar.getPresupuesto() != null) {
			proyecto.setPresupuesto(proyectoEditar.getPresupuesto());
		}
		if (proyectoEditar.getCronograma() != null) {
			proyecto.setCronograma(proyectoEditar.getCronograma());
		}
		if (proyectoEditar.getEnabled() != null) {
			proyecto.setEnabled(proyectoEditar.getEnabled());
		}
		if (proyectoEditar.getEstadoProyecto() != null) {
			proyecto.setEstadoProyecto(proyectoEditar.getEstadoProyecto());
		}
		if (proyectoEditar.getProyectoDesarrollo() != null) {
			proyecto.setProyectoDesarrollo(proyectoEditar.getProyectoDesarrollo());
		}
		if (proyectoEditar.getProyectoDesarrollo() != null) {
			proyecto.setProyectoDesarrollo(proyectoEditar.getProyectoDesarrollo());
		}
		pRepository.save(proyecto);
		return "Actualizado correctamente";
	}

}
