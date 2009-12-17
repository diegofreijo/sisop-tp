package filesystem.entidades;

import filesystem.varios.TipoArchivo;

public class File extends FSElement {

	public File() {
		super(-1, "");
		tipo = TipoArchivo.datos;
		contenido = new byte[100];
		largo = 0;
		idDir = -1;
	}

	private int idDir;
	private TipoArchivo tipo;
	private byte[] contenido;
	private int largo;

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
