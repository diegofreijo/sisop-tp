package filesystem.estructura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import filesystem.driver.HDDriver;
import filesystem.entidades.Dir;
import filesystem.entidades.FSElement;
import filesystem.entidades.File;
import filesystem.entidades.Permission;
import filesystem.entidades.Query;
import filesystem.entidades.User;
import filesystem.exceptions.NoMorePlaceException;
import filesystem.varios.PermissionLevel;
import filesystem.varios.TipoArchivo;

public class FS {

	public HDDriver driver;

	private File[] files	= new File[5];
	private Dir[] querys 	= new Dir[5];
	private User[] usuarios = new User[5];
	private Permission[] permisos = new Permission[25];

	private boolean[] fileOcupado		= new boolean[5];
	private boolean[] queryOcupado		= new boolean[5];
	private boolean[] usuarioOcupado	= new boolean[5];
	private boolean[] permisoOcupado	= new boolean[10];

	private User actualUser = new User();

	private String[] forDir = new String[10];

	private boolean[] byteOcupado		= new boolean[64000];

	private Connection conn;
	private Statement stat;


	public boolean[] getByteOcupado() {
		return byteOcupado;
	}

	public void setByteOcupado(boolean[] byteOcupado) {
		this.byteOcupado = byteOcupado;
	}


	public String[] getForDir() {
		return forDir;
	}

	public void setForDir(String[] forDir) {
		this.forDir = forDir;
	}

	public User getActualUser() {
		return actualUser;
	}

	public void setActualUser(User actualUser) {
		this.actualUser = actualUser;
	}


	/////////////////////////// METODOS PUBLICOS ////////////////////////////

	public FS() throws ClassNotFoundException, SQLException {

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

		ResultSet rs = stat.executeQuery("SELECT POSITION, BUSY FROM BUSY");
		while (rs.next()) {
			int i = rs.getInt("POSITION");
			int ocupado = rs.getInt("BUSY");
			byteOcupado[i] = (ocupado == 1)?true:false;
		}
	}

	private File getFile(int i) {
		return files[i];
	}

	private Query getQuery(int i) {
		return querys[i];
	}

	private Dir getDir(int i) {
		return querys[i];
	}

	private User getUser(int i) {
		return usuarios[i];
	}

	private Permission getPermission(int i) {
		return permisos[i];
	}

	/**
	 * Getters de identificadores
	 */
	private int getIdentificadorFile() {
		return getIdentificadorArray(fileOcupado);
	}

	private int getIdentificadorQuery() {
		return getIdentificadorArray(queryOcupado);
	}

	private int getIdentificadorUser() {
		return getIdentificadorArray(usuarioOcupado);
	}

	private int getIdentificadorPermission() {
		return getIdentificadorArray(permisoOcupado);
	}


	/**
	 * Eliminadores
	 */
	public void eliminarFile(int id) {
		eliminarDato(fileOcupado, id);
	}

	public void eliminarQuery(int id) {
		eliminarDato(queryOcupado, id);
	}

	public void eliminarUser(int id) {
		eliminarDato(usuarioOcupado, id);
	}

	public void eliminarPermiso(int id) {
		eliminarDato(permisoOcupado, id);
	}

	/**
	 * Encuentra un lugar libre para alojar
	 */
	public int getFileId(String nombre) {
		return getElementoId(files, fileOcupado, nombre);
	}

	public int getQueryId(String nombre) {
		return getElementoId(querys, queryOcupado, nombre);
	}

	public int getUserId(String nombre) {
		return getElementoId(usuarios, usuarioOcupado, nombre);
	}

	public int getPermissionId(String nombre) {
		return getElementoId(permisos, permisoOcupado, nombre);
	}

	/**
	 * Modificadores de Files
	 */
	public void newFile(String nombreArchivo, TipoArchivo tipo) throws ClassNotFoundException, SQLException {

		int idFile = getIdentificadorFile();
		File file = getFile(idFile);
		file.setName(nombreArchivo);
		file.setTipo(tipo);

		String sql = "INSERT INTO FILES (IDDIR, NAME, TIPO, CREATED) VALUES ( " +
			file.getIdDir() + ", " +
			"'" + file.getName() + "', " +
			file.getTipo().ordinal() + ", " +
			"DATE(\"now\") )";
		System.out.println(sql);
		stat.execute(sql);
		ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM FILES");
		file.setId(rs.getInt(1));
		conn.close();
	}

	public synchronized void updateFile(int idFile, byte[] contenido, int largo) {
		File file = getFile(idFile);

		try {

			String sql = "UPDATE files SET " +
				"idDir = " + file.getIdDir() + ", " +
				"name = '" + file.getName() + "', " +
				"tipo = " + file.getTipo().ordinal() + ", " +
				"mod = DATE(\"now\")" +
				" WHERE id = " + file.getId();

			desalocarContenido(file.getInit(), file.getLargo());

			file.setContenido(contenido);
			file.setLargo(largo);

			int init = alocarContenido(file.getContenido(), file.getLargo());
			file.setInit(init);

			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public byte[] open(String name) {
		int idFile = getFileId(name);
		// Si no esta cargado
		if (idFile == -1) {
			idFile = loadFile(name);
		}
		return getFile(idFile).getContenido();
	}

	/**
	 * Modificadores de Querys y Dirs
	 */
	public void mkQuery(String nombreQuery, String consulta) {
		int idQuery = getIdentificadorQuery();
		Query query = getQuery(idQuery);
		query.setName(nombreQuery);
		query.setConsulta(consulta);

		try {

			String sql = "INSERT INTO QUERYS (NAME, CONSULTA) VALUES ( " +
				"'" + query.getName() + "', " +
				"'" + query.getConsulta() + "')";
			System.out.println(sql);

			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM QUERYS");
			query.setId(rs.getInt(1));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void mkDir(String nombreDir) {
		int idDir = getIdentificadorQuery();
		Dir dir = getDir(idDir);
		dir.setId(idDir);
		dir.setName(nombreDir);

		try {

			String sql = "INSERT INTO QUERYS (NAME, CONSULTA) VALUES ( " +
				"'" + dir.getName() + "', " +
				"'" + dir.getConsulta() + "')";
			System.out.println(sql);

			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM QUERYS");
			dir.setId(rs.getInt(1));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteQuery(String name) {

		int id = getQuery(getQueryId(name)).getId();

		try {

			// Borro el query
			String sql = "DELETE FROM QUERYS WHERE ID = " + id;
			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteDir(String name) {

		int id = getDir(getQueryId(name)).getId();

		try {

			// Borro query
			String sql = "DELETE FROM QUERYS WHERE ID = " + id;
			System.out.println(sql);
			stat.execute(sql);
			// Borro la referencia de los archivos a ese directorio
			sql = "UPDATE FILES SET IDDIR = -1 WHERE IDDIR = " + id;
			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void addFileToDir(String nombreArchivo, String nombreDir) {
		int idDir = getQueryId(nombreDir);
		int idFile = getFileId(nombreArchivo);
		File file = getFile(idFile);
		file.setIdDir(idDir);
		updateFile(idFile, file.getContenido(), file.getLargo());
	}

	/**
	 * Modificadores de User
	 */
	public void mkUser(String nombreUser, String pass) {
		int idUser = getIdentificadorUser();
		User user = getUser(idUser);

		user.setName(nombreUser);
		user.setPass(pass);

		try {

			Class.forName("org.sqlite.JDBC");

			String sql = "INSERT INTO USERS (NAME, PASS) VALUES ( " +
				"'" + user.getName() + "', " +
				"'" + user.pass + "')";
			System.out.println(sql);

			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM USERS");
			user.setId(rs.getInt(1));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(String name) {

		int id = getUser(getUserId(name)).getId();

		try {

			// Borro usuario
			String sql = "DELETE FROM USERS WHERE ID = " + id;
			System.out.println(sql);
			stat.execute(sql);
			// Borro la referencia de los archivos a ese directorio
			sql = "DELETE FROM PERMISSIONS WHERE IDUSER = " + id;
			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modificadores de Permisos
	 */
	public void addPermission(String nombreArchivo, String nombreUsuario, PermissionLevel permiso) {
		int idFile = getFileId(nombreArchivo);
		int idUser = getUserId(nombreUsuario);
		int idPerm = getIdentificadorPermission();
		Permission perm = getPermission(idPerm);
		perm.setArchivo(getFile(idFile));
		perm.setUsuario(getUser(idUser));
		perm.setPermiso(permiso);

		try {

			Class.forName("org.sqlite.JDBC");

			String sql = "INSERT INTO PERMISSIONS (IDFILE, IDUSER, PERMISO) VALUES ( " +
			perm.getArchivo().getId() + ", " +
			perm.getUsuario().getId() + ", " +
			permiso.ordinal();

			System.out.println(sql);
			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM PERMISSIONS");
			perm.setId(rs.getInt(1));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void deletePermission(String name) {

		int id = getPermission(getPermissionId(name)).getId();

		try {

			// Borro usuario
			String sql = "DELETE FROM USERS WHERE ID = " + id;
			System.out.println(sql);
			stat.execute(sql);
			// Borro la referencia de los archivos a ese directorio
			sql = "DELETE FROM PERMISSIONS WHERE IDUSER = " + id;
			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	///////////////////////////// METODOS PRIVADOS /////////////////////////////

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

	private int loadFile(String name) {

		int i = -1;
		// Ya tiene que existir el archivo
		try {
			ResultSet rs = stat.executeQuery("SELECT * FROM FILES WHERE NAME = '" + name + "'");
			i = getIdentificadorFile();
			// Si no hay lugar... desalojo a alguno
			if (i==-1) {
				i = desalojarFile();
			}
			File file = files[i];
			file.setId(rs.getInt("id"));
			file.setIdDir(rs.getInt("idDir"));
			file.setName(rs.getString("name"));
			file.setTipo(TipoArchivo.values()[rs.getInt("tipo")]);
			file.setInit(rs.getInt("init"));
			file.setLargo(rs.getInt("length"));

			driver.getBytes(file.getInit(), file.getLargo(), file.getContenido());

			fileOcupado[i] = true;

			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return i;
	}

	private int desalojarFile() {
		int idFile = 0;
		Date masAntiguo = files[0].getModificated();
		// Desalojo el archivo mas antiguo
		for (int j = 0; j < files.length; j++) {
			if (masAntiguo.after(files[j].getModificated())) {
				idFile = j;
				masAntiguo = files[j].getModificated();
			}
		}
		files[idFile].setId(-1);
		files[idFile].setName("");
		files[idFile].setTipo(TipoArchivo.datos);
		fileOcupado[idFile] = false;

		return idFile;
	}

	public String[] dir(String nombreDir) {

		for (int i = 0; i < forDir.length; i++) {
			forDir[i] = "";
		}
		try {
			String sql = "";
			sql = "SELECT CONSULTA FROM QUERYS WHERE NAME = '" + nombreDir + "'";
			System.out.println(sql);
			ResultSet rs = stat.executeQuery(sql);

			// FILTRO LCU
			if (this.actualUser.getId() ==	-1)
				sql = "SELECT NAME FROM FILES WHERE " + rs.getString("consulta");
			else
				sql = "SELECT FILES.NAME FROM FILES, PERMISSIONS WHERE FILES.ID = PERMISSIONS.IDFILE " +
						" AND PERMISSIONS.IDUSER = " + actualUser.getId() +
						" AND " + rs.getString("consulta");

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

	public String[] multiDir(String[] nombreDir) {

		for (int i = 0; i < forDir.length; i++) {
			forDir[i] = "";
		}
		try {
			String filtro = "";
			for (int i = 0; i < nombreDir.length; i++) {
				String sql1 = "SELECT CONSULTA FROM QUERYS WHERE NAME = '" + nombreDir[i] + "'";
				System.out.println(sql1);
				ResultSet rs = stat.executeQuery(sql1);
				filtro += rs.getString("consulta") + " AND ";
			}

			// FILTRO LCU
			String sql = "";
			if (this.actualUser.getId() ==	-1)
				sql = "SELECT NAME FROM FILES WHERE " + filtro.substring(0, filtro.length() - 5);
			else
				sql = "SELECT FILES.NAME FROM FILES, PERMISSIONS WHERE FILES.ID = PERMISSIONS.IDFILE " +
						" AND PERMISSIONS.IDUSER = " + actualUser.getId() +
						" AND " + filtro.substring(0, filtro.length() - 5);

			System.out.println(sql);
			ResultSet rs = stat.executeQuery(sql);
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

	private int alocarContenido(byte[] contenido, int largo) {
		// FIRST FIT
		int i = 0;
		int desdePos = 0;
		try {
			while (i < largo) {
				if (desdePos >= byteOcupado.length)
					throw new NoMorePlaceException();
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
			String sql = "UPDATE BUSY SET BUSY = 1 WHERE POSITION <= " + desdePos + " AND POSITION < " + desdePos + largo;
			System.out.println(sql);

			stat.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoMorePlaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return desdePos;
	}

	private void desalocarContenido(int desdePos, int largo) {
		for (int j = 0; j < largo; j++) {
			byteOcupado[desdePos+j] = false;
		}
		String sql = "UPDATE BUSY SET BUSY = 0 WHERE POSITION <= " + desdePos + " AND POSITION < " + desdePos + largo;
		System.out.println(sql);
		try {
			stat.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void su(String string) {

	}



}




