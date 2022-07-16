package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-notificaciones")
public interface NotificacionesFeignClient {

	@PutMapping("/notificaciones/proyecto/edit/enabled/")
	public void enviarMensajeEnabled(@RequestParam("idProyecto") Integer idProyecto,
			@RequestParam("enabled") Boolean enabled, @RequestParam("nombre") String nombre);

	@PutMapping("/notificaciones/proyecto/edit/estado/")
	public void enviarMensajeEstado(@RequestParam("idProyecto") Integer idProyecto,
			@RequestParam("estado") Integer estado, @RequestParam("nombre") String nombre);

	@DeleteMapping("/notificaciones/suscripciones/borrar/")
	public Boolean borrarSuscripciones(@RequestParam("idProyecto") Integer idProyecto);

	@PostMapping("/proyectos/crear/")
	public Boolean crearProyecto(@RequestParam("idProyecto") Integer idProyecto,
			@RequestParam("mensajeParticipacion") String mensajeParticipacion,
			@RequestParam("gamificacion") Boolean gamificacion);

	@PutMapping("/proyectos/editar/")
	public Boolean editarProyecto(@RequestParam("idProyecto") Integer idProyecto,
			@RequestParam("mensajeParticipacion") String mensajeParticipacion,
			@RequestParam("gamificacion") Boolean gamificacion);

	@PutMapping("/proyectos/eliminar/")
	public Boolean eliminarProyecto(@RequestParam("idProyecto") Integer idProyecto);

}
