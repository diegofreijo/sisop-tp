package filesystem.entidades;

import java.util.Date;

import filesystem.varios.TipoArchivo;

public class File extends FSElement {

	private int init;

	public File() {
		super(-1, "");
		tipo = TipoArchivo.datos;
		contenido = new byte[100];
		largo = 0;
		idDir = -1;
		init = 0;
	}

	public int getInit() {
		return init;
	}

	public void setInit(int init) {
		this.init = init;
	}

	private int idDir;
	private TipoArchivo tipo;
	private byte[] contenido;
	private int largo;
	private Date created;
	private Date modificated;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModificated() {
		return modificated;
	}

	public void setModificated(Date modificated) {
		this.modificated = modificated;
	}

	public int getLargo() {
		return largo;
	}

	public void setLargo(int largo) {
		this.largo = largo;
	}

	public byte[] getContenido() {
		return contenido;
	}

	public void setContenido(byte[] contenido) {
		this.contenido = contenido;
	}

	public TipoArchivo getTipo() {
		return tipo;
	}

	public void setTipo(TipoArchivo tipo) {
		this.tipo = tipo;
	}

	/**
	 * Constructor de Archivos.
	 * @param id
	 * @param name
	 */
	public File(int id, String name, TipoArchivo tipo) {
		super(id, name);
		setTipo(tipo);
	}

	public String toString() {
		return "File: " + this.getId() + " - " + this.getName() + " - " + getTipo().name();
	}

	public int getIdDir() {
		return idDir;
	}

	public void setIdDir(int idDir) {
		this.idDir = idDir;
	}


}
