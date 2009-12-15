package filesystem.entidades;

import filesystem.varios.TipoArchivo;

public class Query extends FSElement {

	public String getConsulta() {
		return consulta;
	}

	public void setTipo(TipoArchivo tipo) {
		this.tipo = tipo;
	}

	// Datos del Query.
	private String consulta = "";

	public Query() {
		super(-1, "");
	}

	private TipoArchivo tipo;

	public TipoArchivo getTipo() {
		return tipo;
	}

	public Query(int id, String name, TipoArchivo tipo) {
		super(id, name);
		this.tipo = tipo;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	public String toString() {
		return "Query: " + this.getId() + " - " + this.getName();
	}

}
