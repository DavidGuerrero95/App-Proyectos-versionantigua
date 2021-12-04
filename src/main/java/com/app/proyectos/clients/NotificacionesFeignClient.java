package com.app.proyectos.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "app-notificaciones")
public interface NotificacionesFeignClient {

	@PutMapping("/notificaciones/proyecto/edit/enabled/")
	public void enviarMensajeEnabled(@RequestParam("nombre") String nombre, @RequestParam("enabled") Boolean enabled);

	@PutMapping("/notificaciones/proyecto/edit/estado/")
	public void enviarMensajeEstado(@RequestParam("nombre") String nombre, @RequestParam("estado") Integer estado);

}
