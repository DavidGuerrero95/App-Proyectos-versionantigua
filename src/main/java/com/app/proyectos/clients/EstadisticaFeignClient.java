package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-estadistica")
public interface EstadisticaFeignClient {

	@PostMapping("/estadistica/crear/")
	public Boolean crearEstadistica(@RequestParam("nombre") String nombre);

	@PutMapping("/estadistica/visualizaciones/{nombre}")
	public Boolean aumentarVisualizaciones(@PathVariable("nombre") String nombre);

	@DeleteMapping("/estadistica/borrarEstadisticas/{nombre}")
	public Boolean borrarEstadisticas(@PathVariable("nombre") String nombre);

}
