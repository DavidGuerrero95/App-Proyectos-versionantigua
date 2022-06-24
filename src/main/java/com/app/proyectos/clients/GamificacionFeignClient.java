package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "app-gamification")
public interface GamificacionFeignClient {

	@GetMapping("/gamificacion/proyectos/existe/{nombre}")
	public Boolean existeGamificacionProyecto(@PathVariable("nombre") String nombre);

	@DeleteMapping("/gamificacion/proyectos/eliminar/{nombre}")
	public Boolean eliminarGamificacionProyecto(@PathVariable("nombre") String nombre);

}
