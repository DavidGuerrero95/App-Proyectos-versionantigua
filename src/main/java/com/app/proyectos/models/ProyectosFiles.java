package com.app.proyectos.models;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "proyectoFiles")
public class ProyectosFiles {

	@Id
	private String id;

	@NotBlank(message = "Nombre cannot be null")
	@Indexed(unique = true)
	private String nombre;

	private String name; // file name
	private Date createdtime; // upload time
	private Binary content; // file content
	private String contenttype; // file type
	private long size; // file size

	public ProyectosFiles() {
	}

	public ProyectosFiles(String nombre, String name, Date createdtime, Binary content, String contenttype,
			long size) {
		super();
		this.nombre = nombre;
		this.name = name;
		this.createdtime = createdtime;
		this.content = content;
		this.contenttype = contenttype;
		this.size = size;
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

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

}
