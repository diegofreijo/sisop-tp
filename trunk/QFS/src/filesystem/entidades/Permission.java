package filesystem.entidades;

import filesystem.varios.PermissionLevel;

public class Permission {

	private User usuario;
	private File archivo;
	private PermissionLevel permiso;

	public Permission() {
		this.usuario = null;
		this.archivo = null;
		this.permiso = PermissionLevel.VISTA;
	}

	public Permission(User usuario, File archivo, PermissionLevel permiso) {
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

	public PermissionLevel getPermiso() {
		return permiso;
	}

	public void setPermiso(PermissionLevel permiso) {
		this.permiso = permiso;
	}


}
