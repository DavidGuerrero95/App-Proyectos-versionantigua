package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-preguntasrespuestas")
public interface PreguntasRespuestasFeignClient {

	@PostMapping("/preguntasrespuestas/cuestionario/crear/")
	public Boolean crearCuestionario(@RequestParam("nombre") String nombre);

	@DeleteMapping("/preguntasrespuestas/preguntas/borrar/{nombre}")
	public Boolean borrarPreguntas(@PathVariable("nombre") String nombre);

}
