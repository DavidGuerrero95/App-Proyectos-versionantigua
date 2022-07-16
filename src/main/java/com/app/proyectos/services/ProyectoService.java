package com.app.proyectos.services;

import java.io.IOException;
import java.util.Date;

import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.app.proyectos.clients.NotificacionesFeignClient;
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

	@Autowired
	NotificacionesFeignClient nClient;

	@Override
	public String editarProyecto(Integer codigoProyecto, Proyectos proyectoEditar) {
		Proyectos proyecto = pRepository.findByCodigoProyecto(codigoProyecto);

		if (proyectoEditar.getNombre() != null) {
			proyecto.setNombre(proyectoEditar.getNombre());
		}
		if (proyectoEditar.getPalabrasClave() != null) {
			proyecto.setPalabrasClave(proyectoEditar.getPalabrasClave());
		}
		if (proyectoEditar.getUbicacion() != null) {
			proyecto.setUbicacion(proyectoEditar.getUbicacion());
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
		if (proyectoEditar.getActivo() != null) {
			proyecto.setActivo(proyectoEditar.getActivo());
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
		if (proyectoEditar.getGamificacion() != null) {
			proyecto.setGamificacion(proyectoEditar.getGamificacion());
		}
		if (proyectoEditar.getMensajeParticipacion() != null) {
			proyecto.setMensajeParticipacion(proyectoEditar.getMensajeParticipacion());
		}
		pRepository.save(proyecto);
		return "Actualizado correctamente";
	}

	@Override
	public void savePhoto(Integer codigoProyecto, MultipartFile file) {
		try {
			ProyectosPhotos ph = phRepository.findByCodigoProyecto(codigoProyecto);
			ph.setCodigoProyecto(codigoProyecto);
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
	public void saveFile(Integer codigoProyecto, MultipartFile file) {
		try {
			ProyectosFiles pf = pfRepository.findByCodigoProyecto(codigoProyecto);
			pf.setCodigoProyecto(codigoProyecto);
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

	@Override
	public void saveFistPhoto(MultipartFile image) {
		try {
			ProyectosPhotos pf = new ProyectosPhotos();
			pf.setCodigoProyecto(-1);
			pf.setName(image.getOriginalFilename());
			pf.setCreatedtime(new Date());
			pf.setContent(new Binary(image.getBytes()));
			pf.setContentType(image.getContentType());
			pf.setSize(image.getSize());
			pf.setSuffix(image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".")));
			phRepository.save(pf);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public ProyectosPhotos crearPhotofirst(Integer codigoProyecto) {
		ProyectosPhotos f = phRepository.findByCodigoProyecto(-1);
		ProyectosPhotos uf = new ProyectosPhotos();
		uf.setCodigoProyecto(codigoProyecto);
		uf.setName(f.getName());
		uf.setCreatedtime(new Date());
		uf.setContent(f.getContent());
		uf.setContentType(f.getContentType());
		uf.setSize(f.getSize());
		uf.setSuffix("");
		return uf;
	}

	@Override
	public void crearProyecto(Proyectos proyectos, ProyectosPhotos pPhotos, ProyectosFiles pFiles) {
		pRepository.save(proyectos);
		phRepository.save(pPhotos);
		pfRepository.save(pFiles);
	}

}
