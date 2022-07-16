package com.app.proyectos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.proyectos.models.ProyectosFiles;

public interface ProyectosFilesRepository extends MongoRepository<ProyectosFiles, String> {

	@RestResource(path = "buscar-name")
	public ProyectosFiles findByCodigoProyecto(@Param("codigoProyecto") Integer codigoProyecto);

	@RestResource(path = "existNombre")
	public Boolean existsByCodigoProyecto(@Param("codigoProyecto") Integer codigoProyecto);

	public ProyectosFiles findImageById(String id, Class<ProyectosFiles> class1);
}
