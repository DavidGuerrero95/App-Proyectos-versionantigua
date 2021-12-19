package com.app.proyectos.models;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "proyectoPhotos")
public class ProyectosPhotos {

	@Id
	private String id;

	@NotBlank(message = "Nombre cannot be null")
	@Indexed(unique = true)
	private String nombre;

	private String name; // file name
	private Date createdtime; // upload time
	private Binary content; // file content
	private String contentType; // file type
	private long size; // file size
	private String suffix;
	private String link;

	public ProyectosPhotos() {
	}

	public ProyectosPhotos(String nombre, String name, Date createdtime, Binary content, String contentType, long size,
			String suffix, String link) {
		super();
		this.nombre = nombre;
		this.name = name;
		this.createdtime = createdtime;
		this.content = content;
		this.contentType = contentType;
		this.size = size;
		this.suffix = suffix;
		this.link = link;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatedtime() {
		return createdtime;
	}

	public void setCreatedtime(Date createdtime) {
		this.createdtime = createdtime;
	}

	public Binary getContent() {
		return content;
	}

	public void setContent(Binary content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
