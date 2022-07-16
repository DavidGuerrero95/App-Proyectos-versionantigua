package com.app.proyectos.clients;

import java.util.Date;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-gamification")
public interface GamificacionFeignClient {

	@PostMapping("/gamificacion/proyectos/crear/{idProyecto}")
	public Boolean crearGamificacion(@PathVariable("idProyecto") Integer idProyecto,
			@RequestParam(value = "titulo", defaultValue = "Titulo") String titulo,
			@RequestParam(value = "premios", defaultValue = "") List<String> premios,
			@RequestParam(value = "tyc", defaultValue = "Terminos y Condiciones") String tyc,
			@RequestParam(value = "fechaTerminacion") Date fechaTerminacion,
			@RequestParam(value = "patrocinadores", defaultValue = "") List<String> patrocinadores,
			@RequestParam(value = "habilitado", defaultValue = "false") Boolean habilitado,
			@RequestParam(value = "ganadores", defaultValue = "1") Integer ganadores,
			@RequestParam(value = "mensajeParticipacion", defaultValue = "\n Ya estas participando en la rifa de: ") String mensajeParticipacion,
			@RequestParam(value = "mensajeGanador", defaultValue = "Ganaste el sorteo en el marco del proyecto: ") String mensajeGanador,
			@RequestParam(value = "mensajeBienvenida", defaultValue = "Por participar en este proyecto podras ser parte de una gran rifa") String mensajeBienvenida);

	@GetMapping("/gamificacion/proyectos/existe/{codigoProyecto}")
	public Boolean existeGamificacionProyecto(@PathVariable("codigoProyecto") Integer codigoProyecto);

	@DeleteMapping("/gamificacion/proyectos/eliminar/{codigoProyecto}")
	public Boolean eliminarGamificacionProyecto(@PathVariable("codigoProyecto") Integer codigoProyecto);

}
