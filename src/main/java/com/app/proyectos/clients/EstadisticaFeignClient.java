package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-estadistica")
public interface EstadisticaFeignClient {

	@PostMapping("/estadisticas/crear/")
	public Boolean crearEstadistica(@RequestParam("idProyecto") Integer idProyecto);

	@DeleteMapping("/estadistica/proyecto/{idProyecto}")
	public Boolean borrarEstadisticas(@PathVariable("idProyecto") Integer idProyecto);

}
