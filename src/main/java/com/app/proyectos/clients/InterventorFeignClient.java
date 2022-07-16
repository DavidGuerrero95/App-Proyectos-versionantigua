package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-interventor")
public interface InterventorFeignClient {

	@PostMapping("/interventor/proyectosEliminar/")
	public Boolean peticionEliminarProyectos(@RequestParam("codigoProyecto") Integer codigoProyecto);

	@PutMapping("/interventor/eliminar/peticion/proyecto/")
	public Boolean eliminarPeticionProyecto(@RequestParam("codigoProyecto") Integer codigoProyecto);
}
