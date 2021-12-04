package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-subscripciones")
public interface SuscripcionesFeignClient {

	@PostMapping("/suscripciones/crear/")
	public Boolean crearSuscripciones(@RequestParam("nombre") String nombre);

	@DeleteMapping("/suscripciones/borrar/{nombre}")
	public Boolean borrarSuscripciones(@PathVariable("nombre") String nombre);

}
