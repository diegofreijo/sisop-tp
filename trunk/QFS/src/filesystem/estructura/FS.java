package filesystem.estructura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import filesystem.driver.HDDriver;
import filesystem.entidades.Dir;
import filesystem.entidades.FSElement;
import filesystem.entidades.File;
import filesystem.entidades.Permission;
import filesystem.entidades.Query;
import filesystem.entidades.User;
import filesystem.varios.TipoArchivo;

public class FS {

	public HDDriver driver;

	private File[] files	= new File[5];
	private Dir[] querys 	= new Dir[5];
	private User[] usuarios = new User[5];
	private Permission[] permisos = new Permission[10];

	private String[] forDir = new String[10];

	public String[] getForDir() {
		return forDir;
	}

	public void setForDir(String[] forDir) {
		this.forDir = forDir;
	}

	private boolean[] fileOcupado		= new boolean[5];
	private boolean[] queryOcupado		= new boolean[5];
	private boolean[] usuarioOcupado	= new boolean[5];
	private boolean[] permisoOcupado	= new boolean[10];
	private boolean[] byteOcupado		= new boolean[64000];

	public boolean[] getByteOcupado() {
		return byteOcupado;
	}

	public void setByteOcupado(boolean[] byteOcupado) {
		this.byteOcupado = byteOcupado;
	}

	private Connection conn;
	private Statement stat;

	// METODOS PUBLICOS

	public FS () throws ClassNotFoundException, SQLException {
		for(int i = 0; i < files.length; i++) {
			files[i]	= new File();
			fileOcupado[i]	= true;
		}
		for (int i = 0; i < querys.length; i++) {
			querys[i]	= new Dir();
			queryOcupado[i]	= false;
		}
		for (int i = 0; i < usuarios.length; i++) {
			usuarios[i]	= new User();
			usuarioOcupado[i]	= false;
		}
		for (int i = 0; i < permisos.length; i++) {
			permisos[i]	= new Permission();
			permisoOcupado[i]	= false;
		}
		
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		stat = conn.createStatement();
		
		ResultSet rs = stat.executeQuery("SELECT ID, BUSY FROM BUSY");
		while (rs.next()) {
			int i = rs.getInt("ID");
			int ocupado = rs.getInt("BUSY");
			byteOcupado[i] = (ocupado == 1)?true:false;			
		}

	}

	public File getFile(int i) {
		return files[i];
	}

	public Query getQuery(int i) {
		return querys[i];
	}

	public int getIdentificadorFile() {
		return getIdentificadorArray(fileOcupado);
	}

	public int getIdentificadorQuery() {
		return getIdentificadorArray(queryOcupado);
	}

	public void eliminarFile(int id) {
		eliminarDato(fileOcupado, id);
	}

	public void eliminarQuery(int id) {
		eliminarDato(queryOcupado, id);
	}

	public int getFileId(String nombre) {
		return getElementoId(files, fileOcupado, nombre);
	}

	public int getQueryId(String nombre) {
		return getElementoId(querys, queryOcupado, nombre);
	}

	public void newFile(String nombreArchivo, TipoArchivo tipo) throws ClassNotFoundException, SQLException {

		int idFile = getIdentificadorFile();
		File file = getFile(idFile);
		file.setName(nombreArchivo);
		file.setTipo(tipo);

		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");
		Statement stat = conn.createStatement();

		String sql = "INSERT INTO FILES (IDDIR, NAME, TIPO) VALUES( " +
			file.getIdDir() + ", " +
			"'" + file.getName() + "', " +
			file.getTipo().ordinal() + ")";
		System.out.println(sql);
		stat.execute(sql);
		ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM FILES");
		file.setId(rs.getInt(1));
		conn.close();
	}

	public void mkQuery(String nombreQuery, String consulta) {
		int idQuery = getIdentificadorQuery();
		Query query = getQuery(idQuery);
		query.setName(nombreQuery);
		query.setConsulta(consulta);
		
		try {

			Class.forName("org.sqlite.JDBC");

			String sql = "INSERT INTO QUERYS (NAME, CONSULTA) VALUES ( " +
				"'" + query.getName() + "', " +
				"'" + query.getConsulta() + "')";
			System.out.println(sql);

			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM QUERYS");
			query.setId(rs.getInt(1));

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String toString() {
		return driver.toString();
	}

	public void addFileToDir(String nombreArchivo, String nombreDir) {
		int idDir = getQueryId(nombreDir);
		int idFile = getFileId(nombreArchivo);
		getFile(idFile).setIdDir(idDir);
		updateFile(idFile);
	}

	// METODOS PRIVADOS

	private void updateFile(int idFile) {
		File file = getFile(idFile);

		try {
			Class.forName("org.sqlite.JDBC");

			String sql = "UPDATE files SET " +
				"idDir = " + file.getIdDir() + ", " +
				"name = '" + file.getName() + "', " +
				"tipo = " + file.getTipo().ordinal() +
				" WHERE id = " + file.getId();
			System.out.println(sql);

			stat.execute(sql);


		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Retorna la proxima posicion libre del array de objetos.
	 * @param datoOcupado
	 * @return
	 */
	private int getIdentificadorArray( boolean[] datoOcupado ) {

		int ret = -1;

		for(int i=0;i<datoOcupado.length;i++) {
			if (!datoOcupado[i]) {
				datoOcupado[i] = !datoOcupado[i];
				ret = i;
				break;
			}
		}

		return ret;
	}

	/**
	 * Elimina una entrada.
	 * @param datoOcupado
	 * @param id
	 */
	private void eliminarDato(boolean[] datoOcupado, int id) {
		datoOcupado[id] = !datoOcupado[id];
	}

	/**
	 * A partir de un nombre de un FSElement, retorna el id del objeto correspondiente.
	 * @param dato
	 * @param datoOcupado
	 * @param nombreDir
	 * @return
	 */
	private int getElementoId(FSElement dato[], boolean datoOcupado[], String nombre) {

		int ret = -1;

		for(int i=0;i<datoOcupado.length;i++) {
			if (datoOcupado[i] && dato[i].getName().equals(nombre))
			{
				ret = dato[i].getId();
				break;
			}
		}

		return ret;
	}

	public void mkDir(String Dir) {
		int idDir = getIdentificadorQuery();
		Dir dir = getDir(idDir);
		dir.setId(idDir);
		dir.setName(Dir);

		try {

			Class.forName("org.sqlite.JDBC");

			String sql = "INSERT INTO QUERYS (NAME, CONSULTA) VALUES ( " +
				"'" + dir.getName() + "', " +
				"'" + dir.getConsulta() + "')";
			System.out.println(sql);

			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM QUERYS");
			dir.setId(rs.getInt(1));

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Dir getDir(int i) {
		return querys[i];
	}

	public byte[] open(String name) {
		int idFile = getFileId(name);
		// Si no esta cargado
		if (idFile == -1) {
			idFile = loadFile(name);
		}
		return getFile(idFile).getContenido();
	}

	private int loadFile(String name) {

		int i = -1;
		// Ya tiene que existir el archivo
		try {
			ResultSet rs = stat.executeQuery("SELECT * FROM FILES WHERE NAME = '" + name + "'");
			i = getIdentificadorFile();
			// Si no hay lugar... desalojo a alguno
			if (i==-1) {
				i = unAlocateFile();
			}
			files[i].setId(rs.getInt("id"));
			files[i].setIdDir(rs.getInt("idDir"));
			files[i].setName(rs.getString("name"));
			files[i].setTipo(TipoArchivo.values()[rs.getInt("tipo")]);
			fileOcupado[i] = true;
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return i;
	}

	private int unAlocateFile() {
		int idFile = 0;
		// Desalojo todos los archivos
		for (int j = 0; j < files.length; j++) {
			files[j].setId(-1);
			files[j].setName("");
			files[j].setTipo(TipoArchivo.datos);
			for (int i = 0; i < files[0].getContenido().length; i++) {
				files[j].getContenido()[i] = 0;
			}
			fileOcupado[j] = false;
		}
		return idFile;
	}

	public String[] dir(String nombreDir) {
//		int idDir = getQueryId(nombreDir);
//		Dir dir = getDir(idDir);
		for (int i = 0; i < forDir.length; i++) {
			forDir[i] = "";
		}
		try {
			String sql = "SELECT CONSULTA FROM QUERYS WHERE NAME = '" + nombreDir + "'";
			System.out.println(sql);
			ResultSet rs = stat.executeQuery(sql);
			sql = rs.getString("consulta"); 
			System.out.println(sql);
			rs = stat.executeQuery(sql);
			int i = 0;
			while(rs.next()) {
				forDir[i] = rs.getString("name");
				i++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return forDir;
	}

	public void cd(String string) {
		// TODO Auto-generated method stub
		
	}
	
	public int alocarContenido(byte[] contenido, int largo) {
		// FIRST FIT
		int i = 0;
		int desdePos = 0;		
		while (i < largo) {
			if (desdePos >= byteOcupado.length)
				return 1;
			if (byteOcupado[desdePos+i]) {
				desdePos +=i+1;
				i = 0;
			} else {
				i++;				
			}				
		}		
		//entra desdePos
		driver.setBytes(desdePos, largo, contenido);
		for (int j = 0; j < largo; j++) {
			byteOcupado[desdePos+j] = true;
		}
		return 0;
	}
	
	public void desalocarContenido(int desdePos, int largo) {
		for (int j = 0; j < largo; j++) {
			byteOcupado[desdePos+j] = false;
		}
	}
}



