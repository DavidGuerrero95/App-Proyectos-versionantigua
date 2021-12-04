package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.proyectos.models.Proyectos;

@FeignClient(name = "app-muro")
public interface MuroFeignClient {

	@PostMapping("/muros/proyectos/crear/")
	public Integer crearMurosProyectos(@RequestBody Proyectos proyectos);

	@PutMapping("/muros/proyecto/eliminar/{codigo}")
	public Boolean eliminarProyecto(@PathVariable("codigo") Integer codigo, @RequestParam("nombre") String nombre);
}
