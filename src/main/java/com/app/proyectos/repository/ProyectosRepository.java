package com.app.proyectos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.proyectos.models.Proyectos;

public interface ProyectosRepository extends MongoRepository<Proyectos, String> {

	@RestResource(path = "buscar-codigo")
	public Proyectos findByCodigoProyecto(@Param("codigoProyecto") Integer codigoProyecto);

	@RestResource(path = "exist-codigo")
	public Boolean existsByCodigoProyecto(@Param("codigoProyecto") Integer codigoProyecto);

	@RestResource(path = "delete-codigo")
	public void deleteByCodigoProyecto(@Param("codigoProyecto") Integer codigoProyecto);

}
