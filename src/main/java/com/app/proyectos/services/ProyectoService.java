package com.app.proyectos.services;

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

	@Override
	public void savePhoto(String nombre, MultipartFile file) {
		try {
			ProyectosPhotos ph = phRepository.findByNombre(nombre);
			ph.setNombre(nombre);
			ph.setName(file.getOriginalFilename());
			ph.setSize(file.getSize());
			ph.setContent(new Binary(file.getBytes()));
			ph.setContentType(file.getContentType());
			ph.setCreatedtime(new Date());
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			ph.setSuffix(suffix);
			phRepository.save(ph);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveFile(String nombre, MultipartFile file) {
		try {
			ProyectosFiles pf = pfRepository.findByNombre(nombre);
			pf.setNombre(nombre);
			pf.setName(file.getOriginalFilename());
			pf.setSize(file.getSize());
			pf.setContent(new Binary(file.getBytes()));
			pf.setContentType(file.getContentType());
			pf.setCreatedtime(new Date());
			String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
			pf.setSuffix(suffix);
			pfRepository.save(pf);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
