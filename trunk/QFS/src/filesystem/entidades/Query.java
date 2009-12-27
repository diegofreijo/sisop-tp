package filesystem.entidades;

public class Query extends FSElement {

	public String getConsulta() {
		return consulta;
	}
	// Datos del Query.
	private String consulta = "";

	public Query() {
		super(-1, "");
	}

	public Query(int id, String name) {
		super(id, name);
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}

	public String toString() {
		return "Query: " + this.getId() + " - " + this.getName();
	}

}
