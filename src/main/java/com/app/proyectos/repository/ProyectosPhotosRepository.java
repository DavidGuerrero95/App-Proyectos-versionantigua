package com.app.proyectos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.proyectos.models.ProyectosPhotos;

public interface ProyectosPhotosRepository extends MongoRepository<ProyectosPhotos, String> {

	@RestResource(path = "buscar-codigo")
	public ProyectosPhotos findByCodigoProyecto(@Param("codigoProyecto") Integer codigoProyecto);

	@RestResource(path = "exist-codigo")
	public Boolean existsByCodigoProyecto(@Param("codigoProyecto") Integer codigoProyecto);

	public ProyectosPhotos findImageById(String id, Class<ProyectosPhotos> class1);

}
