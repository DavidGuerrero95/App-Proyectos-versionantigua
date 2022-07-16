package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-respuestas")
public interface RespuestasFeignClient {

	@PostMapping("/respuestas/proyectos/crear/")
	public Boolean crearProyecto(@RequestParam("codigoProyecto") Integer codigoProyecto);

	@DeleteMapping("/respuestas/eliminar/proyecto/todo/{idProyecto}")
	public Boolean eliminarRespuestasProyecto(@PathVariable(name = "idProyecto") Integer idProyecto);

}
