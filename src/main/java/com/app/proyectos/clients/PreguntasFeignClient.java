package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-preguntas")
public interface PreguntasFeignClient {

	@PostMapping("/preguntas/proyectos/crear/")
	public Boolean crearProyecto(@RequestParam("codigoProyecto") Integer codigoProyecto);

	@DeleteMapping("/preguntas/eliminar-proyecto/{idProyecto}/")
	public Boolean eliminarProyecto(@PathVariable(name = "idProyecto") Integer idProyecto);

}
