package com.app.proyectos.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.server.ResponseStatusException;

import com.app.proyectos.clients.EstadisticaFeignClient;
import com.app.proyectos.clients.GamificacionFeignClient;
import com.app.proyectos.clients.InterventorFeignClient;
import com.app.proyectos.clients.MuroFeignClient;
import com.app.proyectos.clients.NotificacionesFeignClient;
import com.app.proyectos.clients.ParametrosFeignClient;
import com.app.proyectos.clients.PreguntasFeignClient;
import com.app.proyectos.clients.RespuestasFeignClient;
import com.app.proyectos.clients.SuscripcionesFeignClient;
import com.app.proyectos.models.Proyectos;
import com.app.proyectos.models.ProyectosFiles;
import com.app.proyectos.models.ProyectosPhotos;
import com.app.proyectos.repository.ProyectosFilesRepository;
import com.app.proyectos.repository.ProyectosPhotosRepository;
import com.app.proyectos.repository.ProyectosRepository;
import com.app.proyectos.services.IProyectoService;
import com.mongodb.MongoException;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ProyectosController {

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
	MuroFeignClient mClient;

	@Autowired
	EstadisticaFeignClient eClient;

	@Autowired
	SuscripcionesFeignClient sClient;

	@Autowired
	NotificacionesFeignClient nClient;

	@Autowired
	ParametrosFeignClient paramClient;

	@Autowired
	GamificacionFeignClient gClient;

	@Autowired
	PreguntasFeignClient pregClient;

	@Autowired
	RespuestasFeignClient respClient;

//  ****************************	PROYECTOS	***********************************  //

	// CREAR IMAGEN INICIAL DE PROYECTOS
	// @PostMapping("/proyectos/image/")
	@PostMapping("/proyectos/image/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean ponerImagen(@RequestParam(value = "image") MultipartFile image) {
		if (image != null && !image.isEmpty()) {
			try {
				pService.saveFistPhoto(image);
				return true;
			} catch (Exception e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la insercion de la foto");
			}
		} else {
			return false;
		}
	}

	// CREAR PROYECTOS
	// @PostMapping("/proyectos/crear/")
	@PostMapping("/proyectos/crear/")
	@ResponseStatus(code = HttpStatus.CREATED)
	public Boolean crearProyectos(@RequestBody @Validated Proyectos proyectos) throws IOException {
		ProyectosPhotos pPhotos = pService.crearPhotofirst(proyectos.getCodigoProyecto());
		ProyectosFiles pFiles = new ProyectosFiles(proyectos.getCodigoProyecto(), "", new Date(), null, "", 0, "");
		proyectos.setUbicacion(new ArrayList<Double>(Arrays.asList(
				new BigDecimal(proyectos.getUbicacion().get(0)).setScale(5, RoundingMode.HALF_UP).doubleValue(),
				new BigDecimal(proyectos.getUbicacion().get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue())));
		if (proyectos.getGamificacion() == null)
			proyectos.setGamificacion(false);
		if (proyectos.getMensajeParticipacion() == null)
			proyectos.setMensajeParticipacion("Gracias por participar en el proyecto: " + proyectos.getNombre()
					+ ", sus aportes serán muy valiosos para el diseño y seguimiento del proyecto."
					+ " Puedes ver las estadística de participación en la City SuperApp."
					+ "\nDeseas adquirir información de la evolución del proyecto, inscríbete!");
		if (proyectos.getFechaLanzamiento() == null) {
			proyectos.setFechaLanzamiento(new Date());
		}

		if (proyectos.getMuro() == null) {
			proyectos.setMuro(cbFactory.create("proyecto").run(
					() -> mClient.crearMurosProyectos(proyectos.getCodigoProyecto(), proyectos.getUbicacion()),
					e -> errorCreacionMuro(e)));
		}

		if (cbFactory.create("proyecto").run(() -> paramClient.agregarProyecto(), e -> errorCreacionParametros(e))) {
			log.info("parametro agregado correctamente -> servicio parametros");
			proyectos.setCodigoProyecto(paramClient.obtenerCodigo());

			if (cbFactory.create("proyecto").run(() -> eClient.crearEstadistica(proyectos.getCodigoProyecto()),
					e -> errorCreacionEstadistica(e))) {
				log.info("Creacion -> Estadistica");

				if (cbFactory.create("proyecto").run(() -> sClient.crearSuscripciones(proyectos.getCodigoProyecto()),
						e -> errorCreacionSuscripcion(e))) {
					log.info("Creacion -> Suscripcion");

					if (cbFactory
							.create("proyecto").run(
									() -> gClient.crearGamificacion(proyectos.getCodigoProyecto(), null, null, null,
											null, null, null, null, null, null, null),
									e -> errorCreacionGamificacion(e))) {
						log.info("Creacion -> Suscripcion");
						try {
							pService.crearProyecto(proyectos, pPhotos, pFiles);
							return true;
						} catch (Exception e) {
							throw new ResponseStatusException(HttpStatus.CONFLICT,
									"Creacion fallida, proyecto: " + e.getMessage());
						}
					} else {
						sClient.borrarSuscripciones(proyectos.getCodigoProyecto());
						eClient.borrarEstadisticas(proyectos.getCodigoProyecto());
						mClient.eliminarProyecto(proyectos.getMuro(), proyectos.getCodigoProyecto());
						throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
								"Servicio Suscripcion no esta disponible");
					}
				} else {
					eClient.borrarEstadisticas(proyectos.getCodigoProyecto());
					mClient.eliminarProyecto(proyectos.getMuro(), proyectos.getCodigoProyecto());
					throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
							"Servicio Suscripcion no esta disponible");
				}
			} else {
				mClient.eliminarProyecto(proyectos.getMuro(), proyectos.getCodigoProyecto());
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio Estadistica no esta disponible");
			}
		} else {
			mClient.eliminarProyecto(proyectos.getMuro(), proyectos.getCodigoProyecto());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio Parametros no esta disponible");
		}
	}

	// EDITAR IMAGEN PROYECTO
	// @PutMapping("/proyectos/imagen/poner/{codigoProyecto}")
	@PutMapping("/proyectos/imagen/poner/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean ponerImagen(@PathVariable("codigoProyecto") Integer codigoProyecto,
			@RequestParam(value = "image") MultipartFile image) throws IOException {
		if (image != null && !image.isEmpty()) {
			pService.savePhoto(codigoProyecto, image);
			return true;
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la insercion de la foto");
	}

	// PONER FILE EN EL PROYECTO
	// @PutMapping("/proyectos/file/poner/{codigoProyecto}")
	@PutMapping("/proyectos/file/poner/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean ponerFile(@PathVariable("codigoProyecto") Integer codigoProyecto,
			@RequestParam(value = "file") MultipartFile file) throws IOException {
		if (file != null && !file.isEmpty()) {
			pService.saveFile(codigoProyecto, file);
			return true;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay imagen");
		}
	}

	// PETICION ELIMINAR PROYECTO
	// @PutMapping("/proyectos/eliminarAdmin/{codigoProyecto}")
	@PutMapping("/proyectos/eliminarAdmin/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarAdmin(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		if (existNombre(codigoProyecto)) {
			if (cbFactory.create("proyecto").run(() -> iClient.peticionEliminarProyectos(codigoProyecto),
					e -> errorPeticionEliminarProyecto(e))) {
				log.info("Peticion de eliminacion enviada");
			}
		}
	}

	// ELIMINAR PETICION PARA ELIMINAR PROYECTO
	// @PutMapping("/proyectos/eliminarAdmin/{codigoProyecto}")
	@PutMapping("/proyectos/eliminarPeticionAdmin/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public void eliminarPeticionProyecto(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		if (existNombre(codigoProyecto)) {
			if (cbFactory.create("proyecto").run(() -> iClient.eliminarPeticionProyecto(codigoProyecto),
					e -> errorPeticionEliminarProyecto(e))) {
				log.info("Eliminacion de peticion lista");
			}
		}
	}

	// EDITAR ENABLED DE PROYECTO
	@PutMapping("/proyectos/editEnabled/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> editEnabled(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		Proyectos proyecto = pRepository.findByCodigoProyecto(codigoProyecto);
		if (proyecto.getActivo()) {
			proyecto.setActivo(false);
		} else {
			proyecto.setActivo(true);
		}
		pRepository.save(proyecto);
		nClient.enviarMensajeEnabled(codigoProyecto, proyecto.getActivo(), proyecto.getNombre());
		return ResponseEntity.ok("Habilitacion del proyecto ha cambiado");
	}

	// EDITAR ESTADO DEL PROYECTO
	@PutMapping("/proyectos/editEstado/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> editEstado(@PathVariable("codigoProyecto") Integer codigoProyecto,
			@RequestParam("estadoProyecto") Integer estadoProyecto) {
		Proyectos proyecto = pRepository.findByCodigoProyecto(codigoProyecto);
		proyecto.setEstadoProyecto(estadoProyecto);
		pRepository.save(proyecto);
		nClient.enviarMensajeEstado(codigoProyecto, proyecto.getEstadoProyecto(), proyecto.getNombre());
		return ResponseEntity.ok("Estado del proyecto ha cambiado");
	}

	// EDITAR PROYECTOS
	@PutMapping("/proyectos/editarProyectos/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> editarProyectos(@PathVariable Integer codigoProyecto,
			@RequestBody Proyectos proyectoEditar) {
		if (pRepository.existsByCodigoProyecto(codigoProyecto))
			return new ResponseEntity<>(pService.editarProyecto(codigoProyecto, proyectoEditar), HttpStatus.OK);
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe");
	}

	// HABILITAR GAMIFICACION
	@PutMapping("/proyectos/gamificacion/cambiar-estado/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean habilitarGamification(@PathVariable("codigoProyecto") Integer codigoProyecto) throws MongoException {
		if (pfRepository.existsByCodigoProyecto(codigoProyecto)) {
			Proyectos p = pRepository.findByCodigoProyecto(codigoProyecto);
			if (p.getGamificacion())
				p.setGamificacion(false);
			else
				p.setGamificacion(true);
			pRepository.save(p);
			return p.getGamificacion();
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe");
	}

	// LISTAR PROYECTOS
	// MICROSERVICIO -> BUSQUEDA
	// MICROSERVICIO -> PARAMETRIZACION
	// MICROSERVICIO -> RECOMENDACIONES
	// MICROSERVICIO -> ESTADISTICAS DASHBOARD
	// MICROSERVICIO -> GAMIFICACION
	// @GetMapping("/proyectos/listar/")
	@GetMapping("/proyectos/listar/")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> getProyectos() throws IOException {
		try {
			return pRepository.findAll();
		} catch (Exception e) {
			throw new IOException("error listar proyectos, proyectos: " + e.getMessage());
		}
	}

	// CONVERTIR FOTO BINARIO A STRING
	// @GetMapping("/proyectos/imagen/binary/{codigoProyecto}")
	@GetMapping("/proyectos/imagen/binary/{codigoProyecto}")
	@ResponseStatus(HttpStatus.OK)
	public String binaryToStringPhoto(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		if (phRepository.existsByCodigoProyecto(codigoProyecto)) {
			ProyectosPhotos ph = phRepository.findByCodigoProyecto(codigoProyecto);
			byte[] data = null;
			ProyectosPhotos file = phRepository.findImageById(ph.getId(), ProyectosPhotos.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return Base64.getEncoder().encodeToString(data);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe");
	}

	// DESCARGAR IMAGEN
	// @GetMapping(value = "/proyectos/imagen/downloadImage/{codigoProyecto}"
	@GetMapping(value = "/proyectos/imagen/downloadImage/{codigoProyecto}", produces = { MediaType.IMAGE_JPEG_VALUE,
			MediaType.IMAGE_PNG_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public byte[] image(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		if (phRepository.existsByCodigoProyecto(codigoProyecto)) {
			ProyectosPhotos proyecto = phRepository.findByCodigoProyecto(codigoProyecto);
			byte[] data = null;
			ProyectosPhotos file = phRepository.findImageById(proyecto.getId(), ProyectosPhotos.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return data;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe");
	}

	// CONVERTIR FILE BINARIO A STRING
	// @GetMapping("/proyectos/imagen/binary/{codigoProyecto}")
	@GetMapping("/proyectos/file/binary/{codigoProyecto}")
	@ResponseStatus(HttpStatus.OK)
	public String binaryToStringFile(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		if (phRepository.existsByCodigoProyecto(codigoProyecto)) {
			ProyectosFiles pf = pfRepository.findByCodigoProyecto(codigoProyecto);
			byte[] data = null;
			ProyectosFiles file = pfRepository.findImageById(pf.getId(), ProyectosFiles.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return Base64.getEncoder().encodeToString(data);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe");
	}

	// DESCARGAR IMAGEN
	// @GetMapping(value = "/proyectos/file/downloadFile/{codigoProyecto}"
	@GetMapping(value = "/proyectos/file/downloadFile/{codigoProyecto}", produces = { MediaType.APPLICATION_PDF_VALUE })
	@ResponseStatus(HttpStatus.OK)
	public byte[] file(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		if (pfRepository.existsByCodigoProyecto(codigoProyecto)) {
			ProyectosFiles proyecto = pfRepository.findByCodigoProyecto(codigoProyecto);
			byte[] data = null;
			ProyectosFiles file = pfRepository.findImageById(proyecto.getId(), ProyectosFiles.class);
			if (file != null) {
				data = file.getContent().getData();
			}
			return data;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe");
	}

	// Obtener descripcion del proyecto
	// @GetMapping("/proyectos/descripcion/{codigoProyecto}")
	@GetMapping("/proyectos/descripcion/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.FOUND)
	public String descripcionMuro(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		return pRepository.findByCodigoProyecto(codigoProyecto).getDescripcion();
	}

	// VER PROYECTO
	// @GetMapping("/proyectos/ver/proyecto/{codigoProyecto}")
	@GetMapping("/proyectos/ver/proyecto/{idProyecto}")
	@ResponseStatus(code = HttpStatus.FOUND)
	public Proyectos verProyecto(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		if (pRepository.existsByCodigoProyecto(codigoProyecto))
			return pRepository.findByCodigoProyecto(codigoProyecto);
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proyecto no existe");
	}

	// LISTA DE PROYECTOS POR MURO
	// @GetMapping("/proyectos/listarByMuro/{codigo}")
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

	// LISTA DE PROYECTOS POR CREADOR
	@GetMapping("/proyectos/ver/creador/{username}")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Proyectos> verCreador(@PathVariable String username) {
		List<Proyectos> lista = new ArrayList<Proyectos>();
		List<Proyectos> listaProyectos = pRepository.findAll();
		listaProyectos.forEach(l -> {
			l.getCreador().forEach(x -> {
				if (x.equals(username)) {
					lista.add(l);
				}
			});

		});
		return lista;
	}

	// PROYECTO EXISTE POR CODIGO DE PROYECTO
	@GetMapping("/proyectos/existsByCodigoProyecto/")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean existNombre(@RequestParam("codigoProyecto") Integer codigoProyecto) {
		return pRepository.existsByCodigoProyecto(codigoProyecto);
	}

	// VER ESTADO GAMIFICACION
	@GetMapping("/proyectos/gamificacion/ver-estado/{codigoProyecto}")
	public Boolean verEstadoGamificacion(@PathVariable("codigoProyecto") Integer codigoProyecto) {
		Proyectos p = pRepository.findByCodigoProyecto(codigoProyecto);
		return p.getGamificacion();
	}

	// EXISTE PROYECTO
	@GetMapping("/proyectos/exists-codigo-proyecto/")
	public Boolean existCodigoProyecto(@RequestParam("codigoProyecto") Integer codigoProyecto) throws IOException {
		return pRepository.existsByCodigoProyecto(codigoProyecto);
	}

	// OBTENER CODIGOS PROYECTO
	@GetMapping("/proyectos/obtener-lista-codigo-proyecto/")
	public List<Integer> obtenerListaCodigo() {
		List<Proyectos> p = pRepository.findAll();
		if (!p.isEmpty()) {
			List<Integer> cp = new ArrayList<Integer>();
			p.forEach(x -> cp.add(x.getCodigoProyecto()));
			return cp;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existen proyectos");
	}

	// BUSQUEDA
	@GetMapping("/proyectos/busqueda/obtener/")
	public List<Proyectos> busquedaObtener(List<Integer> codigos) {
		List<Proyectos> listaProyectos = new ArrayList<Proyectos>();
		codigos.forEach(x -> {
			if (pRepository.existsByCodigoProyecto(x)) {
				listaProyectos.add(pRepository.findByCodigoProyecto(x));
			}
		});
		return listaProyectos;
	}

	@DeleteMapping("/proyectos/eliminar/{codigoProyecto}")
	@ResponseStatus(code = HttpStatus.OK)
	public Boolean eliminarProyectos(@PathVariable("nombre") Integer codigoProyecto) throws IOException {
		try {
			Proyectos proyectos = pRepository.findByCodigoProyecto(codigoProyecto);
			ProyectosFiles pf = pfRepository.findByCodigoProyecto(codigoProyecto);
			ProyectosPhotos pp = phRepository.findByCodigoProyecto(codigoProyecto);
			if (gClient.existeGamificacionProyecto(codigoProyecto)) {
				if (cbFactory.create("proyecto").run(() -> gClient.eliminarGamificacionProyecto(codigoProyecto),
						e -> errorConexion(e))) {
					log.info("Eliminacion Recomendacion");
				}
			}
			pRepository.delete(proyectos);
			pfRepository.delete(pf);
			phRepository.delete(pp);

			if (cbFactory.create("proyecto").run(() -> mClient.eliminarProyecto(proyectos.getMuro(), codigoProyecto),
					e -> errorConexion(e))) {
				log.info("Eliminacion Muro");
			}
			if (cbFactory.create("proyecto").run(() -> eClient.borrarEstadisticas(codigoProyecto),
					e -> errorConexion(e))) {
				log.info("Eliminacion Estadisticas");
			}

			if (cbFactory.create("proyecto").run(() -> sClient.borrarSuscripciones(codigoProyecto),
					e -> errorConexion(e))) {
				log.info("Eliminacion Suscripciones");
			}
			if (cbFactory.create("proyecto").run(() -> nClient.borrarSuscripciones(codigoProyecto),
					e -> errorConexion(e))) {
				log.info("Eliminacion Suscripciones");
			}
			if (cbFactory.create("proyecto").run(() -> pregClient.eliminarProyecto(proyectos.getCodigoProyecto()),
					e -> errorConexion(e))) {
				log.info("Eliminacion Preguntas -> Correctamente");
			}
			if (cbFactory.create("proyecto").run(
					() -> respClient.eliminarRespuestasProyecto(proyectos.getCodigoProyecto()),
					e -> errorConexion(e))) {
				log.info("Eliminacion Respuestas -> Correctamente");
			}
			if (cbFactory.create("proyecto").run(() -> nClient.eliminarProyecto(proyectos.getCodigoProyecto()),
					e -> errorConexion(e))) {
				log.info("Eliminacion Proyecto Notificaciones -> Correctamente");
			}
			return true;
		} catch (Exception e2) {
			throw new IOException("error eliminar proyecto, proyectos: " + e2.getMessage());
		}

	}

//  ****************************	FUNCIONES 	***********************************  //

	@PutMapping("/proyectos/arreglar/")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> arreglarProyectos() throws MongoException {
		List<Proyectos> p = pRepository.findAll();
		p.forEach(x -> {
			x.setMensajeParticipacion("");
			x.setGamificacion(false);
			pRepository.save(x);
		});
		return ResponseEntity.ok("Arreglado correctamente");
	}

//  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //
	public Integer errorCreacionMuro(Throwable e) {
		log.info("Error creacion muro: " + e.getMessage());
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio Muro no esta disponible");
	}

	private Boolean errorCreacionParametros(Throwable e) {
		log.info("Error creacion parmetros: " + e.getMessage());
		return false;
	}

	private Boolean errorCreacionEstadistica(Throwable e) {
		log.info("Error creacion estadisticas: " + e.getMessage());
		return false;
	}

	private Boolean errorCreacionSuscripcion(Throwable e) {
		log.info("Error creacion suscripcion: " + e.getMessage());
		return false;
	}

	private Boolean errorCreacionGamificacion(Throwable e) {
		log.info("Error creacion suscripcion: " + e.getMessage());
		return false;
	}

	private Boolean errorPeticionEliminarProyecto(Throwable e) {
		log.info("Error peticion eliminar proyecto: " + e.getMessage());
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio Interventor no esta disponible");
	}

	private Boolean errorConexion(Throwable e) {
		log.info(e.getMessage());
		return false;
	}

}
