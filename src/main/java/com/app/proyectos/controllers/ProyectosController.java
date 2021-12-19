package com.app.proyectos.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.app.proyectos.clients.BusquedaFeignClient;
import com.app.proyectos.clients.EstadisticaFeignClient;
import com.app.proyectos.clients.InterventorFeignClient;
import com.app.proyectos.clients.MuroFeignClient;
import com.app.proyectos.clients.NotificacionesFeignClient;
import com.app.proyectos.clients.PreguntasRespuestasFeignClient;
import com.app.proyectos.clients.RecomendacionesFeignClient;
import com.app.proyectos.clients.SuscripcionesFeignClient;
import com.app.proyectos.models.Proyectos;
import com.app.proyectos.models.ProyectosFiles;
import com.app.proyectos.models.ProyectosPhotos;
import com.app.proyectos.repository.ProyectosFilesRepository;
import com.app.proyectos.repository.ProyectosPhotosRepository;
import com.app.proyectos.repository.ProyectosRepository;
import com.app.proyectos.services.IProyectoService;

@RestController
public class ProyectosController {

	private final Logger logger = LoggerFactory.getLogger(ProyectosController.class);

	@SuppressWarnings("rawtypes")
	@Autowired
	private CircuitBreakerFactory cbFactory;

	@Autowired
	ProyectosRepository pRepository;

	@Autowired
	ProyectosPhotosRepository phRepository;

	@Autowired
	ProyectosFilesRepository pfRepository;

	@Autowired
	IProyectoService pService;

	@Autowired
	InterventorFeignClient iClient;

	@Autowired
	RecomendacionesFeignClient rClient;

	@Autowired
	MuroFeignClient mClient;

	@Autowired
	EstadisticaFeignClient eClient;

	@Autowired
	PreguntasRespuestasFeignClient prClient;

	@Autowired
	SuscripcionesFeignClient sClient;

	@Autowired
	BusquedaFeignClient bClient;

	@Autowired
	NotificacionesFeignClient nClient;

	@GetMapping("/proyectos/listar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> getProyectos() throws IOException {
		try {
			return pRepository.findAll();
		} catch (Exception e) {
			throw new IOException("error listar proyectos, proyectos: " + e.getMessage());
		}

	}

	@GetMapping("/proyectos/buscarProyecto/")
	@ResponseStatus(code = HttpStatus.FOUND)
	public Proyectos findByNombreOrToken(@RequestParam("nombre") String nombre,
			@RequestParam("codigoProyecto") Integer codigoProyecto) {
		return pRepository.findByNombreOrCodigoProyecto(nombre, codigoProyecto);
	}

	@PostMapping("/proyectos/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<?> crearProyectos(@RequestBody @Validated Proyectos proyectos) throws IOException {
		ProyectosPhotos pPhotos = new ProyectosPhotos(proyectos.getNombre(), "", new Date(), null, "", 0, "", "");
		ProyectosFiles pFiles = new ProyectosFiles(proyectos.getNombre(), "", new Date(), null, "", 0, "");
		proyectos.setLocalizacion(new ArrayList<Double>(Arrays.asList(
				new BigDecimal(proyectos.getLocalizacion().get(0)).setScale(5, RoundingMode.HALF_UP).doubleValue(),
				new BigDecimal(proyectos.getLocalizacion().get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue())));
		if (cbFactory.create("proyecto").run(() -> bClient.editarProyecto(proyectos.getNombre()),
				e -> errorConexion(e))) {
			logger.info("Creacion Busqueda");
		}
		if (proyectos.getMuro() == null) {
			proyectos.setMuro(
					cbFactory.create("proyecto").run(() -> mClient.crearMurosProyectos(proyectos), e -> errorMuro(e)));
		}
		proyectos.setCodigoProyecto(pRepository.findAll().size() + 1);
		if (cbFactory.create("proyecto").run(() -> prClient.crearCuestionario(proyectos.getNombre()),
				e -> errorConexion(e))) {
			logger.info("Creacion PreguntasRespuestas");
		}
		proyectos.setFecha(LocalDate.now());
		if (cbFactory.create("proyecto").run(() -> eClient.crearEstadistica(proyectos.getNombre()),
				e -> errorConexion(e))) {
			logger.info("Creacion Estadistica");
		}
		if (cbFactory.create("proyecto").run(() -> sClient.crearSuscripciones(proyectos.getNombre()),
				e -> errorConexion(e))) {
			logger.info("Creacion Suscripcion");
		}
		if (cbFactory.create("proyecto").run(() -> rClient.anadirProyectos(proyectos), e -> errorConexion(e))) {
			logger.info("Creacion Recomendaciones");
		}

		try {
			pRepository.save(proyectos);
			phRepository.save(pPhotos);
			pfRepository.save(pFiles);
			return ResponseEntity.ok("Creacion exitosa, proyecto (Puesto en muro): " + proyectos.getNombre());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Creacion fallida, proyecto: " + e.getMessage());
		}
	}

	@PutMapping("/proyectos/imagen/poner/{nombre}")
	@ResponseStatus
	public ResponseEntity<?> ponerImagen(@PathVariable("nombre") String nombre,
			@RequestParam(value = "image") MultipartFile image) throws IOException {
		if (image != null && !image.isEmpty()) {
			pService.savePhoto(nombre, image);
			return ResponseEntity.ok("Foto Creada correctamente");
		} else {
			return ResponseEntity.ok("Inserte la imagen");
		}
	}

	@GetMapping("/proyectos/imagen/binary/{nombre}")
	@ResponseStatus(HttpStatus.OK)
	public String binaryToStringPhoto(@PathVariable("nombre") String nombre) {
		if (phRepository.existsByNombre(nombre)) {
			ProyectosPhotos ph = phRepository.findByNombre(nombre);
			byte[] data = null;
			ProyectosPhotos file = phRepository.findImageById(ph.getId(), ProyectosPhotos.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return Base64.getEncoder().encodeToString(data);
		}
		return "Proyecto no encontrado";
	}

	// Descargar imagen
	@GetMapping(value = "/proyectos/imagen/downloadImage/{nombre}", produces = { MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public byte[] image(@PathVariable("nombre") String nombre) {
		if (phRepository.existsByNombre(nombre)) {
			ProyectosPhotos proyecto = phRepository.findByNombre(nombre);
			byte[] data = null;
			ProyectosPhotos file = phRepository.findImageById(proyecto.getId(), ProyectosPhotos.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return data;
		}
		return null;
	}

	@PutMapping("/proyectos/file/poner/{nombre}")
	@ResponseStatus
	public ResponseEntity<?> ponerFile(@PathVariable("nombre") String nombre,
			@RequestParam(value = "file") MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
			pService.saveFile(nombre, file);
			return ResponseEntity.ok("Foto Creada correctamente");
		} else {
			return ResponseEntity.ok("Inserte la imagen");
		}
	}

	@GetMapping("/proyectos/file/binary/{nombre}")
	@ResponseStatus(HttpStatus.OK)
	public String binaryToStringFile(@PathVariable("nombre") String nombre) {
		if (phRepository.existsByNombre(nombre)) {
			ProyectosFiles pf = pfRepository.findByNombre(nombre);
			byte[] data = null;
			ProyectosFiles file = pfRepository.findImageById(pf.getId(), ProyectosFiles.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return Base64.getEncoder().encodeToString(data);
		}
		return "Proyecto no encontrado";
	}

	@GetMapping(value = "/proyectos/file/downloadFile/{nombre}", produces = { MediaType.APPLICATION_PDF_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public byte[] file(@PathVariable("nombre") String nombre) {
		ProyectosFiles proyecto = pfRepository.findByNombre(nombre);
		byte[] data = null;
		ProyectosFiles file = pfRepository.findImageById(proyecto.getId(), ProyectosFiles.class);
		if (file != null) {
			data = file.getContent().getData();
		}
		return data;
	}

	@PutMapping("/proyectos/eliminarAdmin/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarAdmin(@PathVariable("nombre") String nombre) {
		if (existNombre(nombre)) {
			if (cbFactory.create("proyecto").run(() -> iClient.peticionEliminarProyectos(nombre),
					e -> errorConexion(e))) {
				logger.info("Peticion de eliminacion enviada");
			}
		}
	}

	@DeleteMapping("/proyectos/eliminar/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarProyectos(@PathVariable("nombre") String nombre) throws IOException {
		try {
			Proyectos proyectos = pRepository.findByNombre(nombre);
			ProyectosFiles pf = pfRepository.findByNombre(nombre);
			ProyectosPhotos pp = phRepository.findByNombre(nombre);
			pRepository.delete(proyectos);
			pfRepository.delete(pf);
			phRepository.delete(pp);
			if (cbFactory.create("proyecto").run(() -> rClient.deleteProyectos(nombre), e -> errorConexion(e))) {
				logger.info("Eliminacion Recomendacion");
			}
			if (cbFactory.create("proyecto").run(() -> bClient.eliminarProyecto(nombre), e -> errorConexion(e))) {
				logger.info("Eliminacion Busqueda");
			}
			if (cbFactory.create("proyecto").run(() -> mClient.eliminarProyecto(proyectos.getMuro(), nombre),
					e -> errorConexion(e))) {
				logger.info("Eliminacion Muro");
			}
			if (cbFactory.create("proyecto").run(() -> eClient.borrarEstadisticas(nombre), e -> errorConexion(e))) {
				logger.info("Eliminacion Estadisticas");
			}
			if (cbFactory.create("proyecto").run(() -> prClient.borrarPreguntas(nombre), e -> errorConexion(e))) {
				logger.info("Eliminacion PreguntasRespuestas");
			}
			if (cbFactory.create("proyecto").run(() -> sClient.borrarSuscripciones(nombre), e -> errorConexion(e))) {
				logger.info("Eliminacion Suscripciones");
			}
			if (cbFactory.create("proyecto").run(() -> nClient.borrarSuscripciones(nombre), e -> errorConexion(e))) {
				logger.info("Eliminacion Suscripciones");
			}
			return true;
		} catch (Exception e2) {
			throw new IOException("error eliminar proyecto, proyectos: " + e2.getMessage());
		}

	}

	@GetMapping("/proyectos/descripcion/{nombre}")
	@ResponseStatus(code = HttpStatus.FOUND)
	public String descripcionMuro(@PathVariable("nombre") String nombre) {
		return pRepository.findByNombre(nombre).getDescripcion();
	}

	@GetMapping("/proyectos/listarByMuro/{codigo}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> getProyecyosByMuro(@PathVariable("codigo") Integer codigo) {
		List<Proyectos> lista = new ArrayList<Proyectos>();
		List<Proyectos> listaProyectos = pRepository.findAll();
		listaProyectos.forEach(l -> {
			if (l.getMuro() == codigo)
				lista.add(l);
		});
		return lista;
	}

	@GetMapping("/proyectos/ver/creador/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> verCreador(@PathVariable String username) {
		List<Proyectos> lista = new ArrayList<Proyectos>();
		List<Proyectos> listaProyectos = pRepository.findAll();
		listaProyectos.forEach(l -> {
			if (l.getCreador().equals(username)) {
				lista.add(l);
			}
		});
		return lista;
	}

	@PutMapping("/proyectos/visualizaciones/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void visualizacion(@PathVariable("nombre") String nombre) {
		if (cbFactory.create("proyecto").run(() -> eClient.aumentarVisualizaciones(nombre), e -> errorConexion(e))) {
			logger.info("Visualizacion aumentadas correctamente Suscripciones");
		}
	}

	@GetMapping("/proyectos/obtenerProyectoByNombre/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public Proyectos getProyectosByNombre(@PathVariable("nombre") String nombre) {
		return pRepository.findByNombre(nombre);
	}

	@PutMapping("/proyectos/editEnabled/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> editEnabled(@PathVariable("nombre") String nombre) {
		Proyectos proyecto = pRepository.findByNombre(nombre);
		if (proyecto.getEnabled()) {
			proyecto.setEnabled(false);
		} else {
			proyecto.setEnabled(true);
		}
		pRepository.save(proyecto);
		nClient.enviarMensajeEnabled(nombre, proyecto.getEnabled());
		return ResponseEntity.ok("Habilitacion del proyecto ha cambiado");
	}

	@PutMapping("/proyectos/editEstado/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> editEstado(@PathVariable("nombre") String nombre,
			@RequestParam("estadoProyecto") Integer estadoProyecto) {
		Proyectos proyecto = pRepository.findByNombre(nombre);
		proyecto.setEstadoProyecto(estadoProyecto);
		pRepository.save(proyecto);
		nClient.enviarMensajeEstado(nombre, proyecto.getEstadoProyecto());
		return ResponseEntity.ok("Estado del proyecto ha cambiado");
	}

	@PutMapping("/proyectos/editarProyectos/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> editarProyectos(@PathVariable String nombre, @RequestBody Proyectos proyectoEditar) {
		return new ResponseEntity<>(pService.editarProyecto(nombre, proyectoEditar), HttpStatus.OK);
	}

	@GetMapping("/proyectos/existsByNombre")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean existNombre(@RequestParam("nombre") String nombre) {
		return pRepository.existsByNombre(nombre);
	}

	public Boolean errorConexion(Throwable e) {
		logger.info(e.getMessage());
		return false;
	}

	public Integer errorMuro(Throwable e) {
		logger.info(e.getMessage());
		List<Proyectos> p = pRepository.findAll();
		if (!p.isEmpty())
			return p.get(0).getMuro();
		return 1;
	}

	@PutMapping("/proyectos/imagen/poner/link/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public void linkImagen(@PathVariable String nombre, @RequestParam("link") String link) {
		ProyectosPhotos pp = phRepository.findByNombre(nombre);
		pp.setLink(link);
		phRepository.save(pp);
	}

	@GetMapping("/proyectos/imagen/ver/link/{nombre}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> verImagen(@PathVariable String nombre) {
		ProyectosPhotos pp = phRepository.findByNombre(nombre);
		return ResponseEntity.ok(pp.getLink());
	}
}
