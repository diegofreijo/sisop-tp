package filesystem.entidades;

import filesystem.varios.Util;

public class User extends FSElement  {
	
	private String pass;
	
	public User() {
		super(-1, "");
	}

	public User(int id, String name, String pass) {
		super(id, name);
		this.pass = Util.encript(pass);
	}

	public String getPass() {
		return Util.decript(pass);
	}

	public void setPass(String pass) {
		this.pass = Util.encript(pass);
	}

}
