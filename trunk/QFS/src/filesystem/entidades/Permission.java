package filesystem.entidades;

public class Permission {

	private User usuario;
	private File archivo;
	private int permiso;

	public Permission(User usuario, File archivo, int permiso) {
		super();
		this.usuario = usuario;
		this.archivo = archivo;
		this.permiso = permiso;
	}

	public User getUsuario() {
		return usuario;
	}

	public void setUsuario(User usuario) {
		this.usuario = usuario;
	}

	public File getArchivo() {
		return archivo;
	}

	public void setArchivo(File archivo) {
		this.archivo = archivo;
	}

	public int getPermiso() {
		return permiso;
	}

	public void setPermiso(int permiso) {
		this.permiso = permiso;
	}


}
