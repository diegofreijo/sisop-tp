package filesystem.varios;

public class Util {
	
	// Codificacion del Cesar
	
	private static final int clave = 5; 
	public static String encript(String texto) {
		String ret = "";
		for (int i = 0; i < texto.length() ; i++) {
			char aux =(char)(texto.charAt(i) + clave);
			ret += aux;
		}
		return ret;
	}

	public static String decript(String texto) {
		String ret = "";
		for (int i = 0; i < texto.length() ; i++) {
			char aux =(char)(texto.charAt(i) - clave);
			ret += aux;
		}
		return ret;
	}
}
