package com.app.proyectos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import com.app.proyectos.models.Proyectos;

public interface ProyectosRepository extends MongoRepository<Proyectos, String> {
	
	@RestResource(path = "buscar-name")
	public Proyectos findByNombre(@Param("nombre") String nombre);
	
	@RestResource(path = "buscar")
	public Proyectos findByNombreOrCodigoProyecto(@Param("nombre") String nombre, @Param("codigoProyecto") Integer codigoProyecto);
	
	@RestResource(path = "buscar-label")
	public Proyectos findByCodigoProyectoOrNombre(@Param("codigoProyecto") Integer codigoProyecto,
			@Param("nombre") String nombre);
	
	@RestResource(path = "existNombre")
	public Boolean existsByNombre(@Param("nombre") String nombre);
}
