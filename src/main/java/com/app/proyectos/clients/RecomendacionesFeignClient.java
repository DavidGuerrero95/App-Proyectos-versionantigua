package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.proyectos.models.Proyectos;

@FeignClient(name = "app-recomendacion")
public interface RecomendacionesFeignClient {

	@GetMapping("/recomendaciones/proyecto/crear/")
	public Boolean anadirProyectos(@RequestBody Proyectos proyecto);

	@DeleteMapping("/recomendaciones/proyectos/eliminar/")
	public Boolean deleteProyectos(@RequestParam("nombre") String nombre);
}
