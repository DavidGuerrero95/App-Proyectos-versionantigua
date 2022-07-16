package com.app.proyectos.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-muro")
public interface MuroFeignClient {

	@PostMapping("/muros/proyectos/crear/")
	public Integer crearMurosProyectos(@RequestParam("idProyecto") Integer idProyecto,
			@RequestParam("localizacion") List<Double> localizacion);

	@PutMapping("/muros/proyecto/eliminar/{codigo}")
	public Boolean eliminarProyecto(@PathVariable("codigo") Integer codigo,
			@RequestParam("idProyecto") Integer idProyecto);
}
