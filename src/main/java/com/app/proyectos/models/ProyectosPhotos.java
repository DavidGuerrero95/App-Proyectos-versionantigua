package com.app.proyectos.models;

import java.util.Date;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "proyectoPhotos")
@Data
@NoArgsConstructor
public class ProyectosPhotos {

	@Id
	@JsonIgnore
	private String id;

	@Indexed(unique = true)
	private Integer codigoProyecto;

	private String name; // file name
	private Date createdtime; // upload time
	private Binary content; // file content
	private String contentType; // file type
	private long size; // file size
	private String suffix;

}
