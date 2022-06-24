package com.app.proyectos.clients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "app-parametrizacion")
public interface ParametrosFeignClient {

	@GetMapping("/parametros/proyectos/obtener-codigo/")
	public Integer obtenerCodigo();

	@GetMapping("/parametros/proyectos/obtener-lista-codigos/")
	public List<Integer> obtenerListaCodigo();

	@PutMapping("/parametros/proyectos/agregar-proyecto/")
	public Boolean agregarProyecto();
}
