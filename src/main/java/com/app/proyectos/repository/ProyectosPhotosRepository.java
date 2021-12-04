package com.app.proyectos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.proyectos.models.ProyectosPhotos;

public interface ProyectosPhotosRepository extends MongoRepository<ProyectosPhotos, String> {

	@RestResource(path = "buscar-name")
	public ProyectosPhotos findByNombre(@Param("nombre") String nombre);

	@RestResource(path = "existNombre")
	public Boolean existsByNombre(@Param("nombre") String nombre);

	public ProyectosPhotos findImageById(String id, Class<ProyectosPhotos> class1);
}
