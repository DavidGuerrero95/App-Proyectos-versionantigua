package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-busqueda")
public interface BusquedaFeignClient {

	@PutMapping("/busqueda/proyecto/editar/")
	public Boolean editarProyecto(@RequestParam("nombre") String nombre);

	@PutMapping("/busqueda/proyecto/eliminar/")
	public Boolean eliminarProyecto(@RequestParam("nombre") String nombre);

}
