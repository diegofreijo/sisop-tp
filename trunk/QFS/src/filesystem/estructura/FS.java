package filesystem.estructura;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import filesystem.driver.HDDriver;
import filesystem.entidades.Dir;
import filesystem.entidades.FSElement;
import filesystem.entidades.File;
import filesystem.entidades.Permission;
import filesystem.entidades.Query;
import filesystem.entidades.User;
import filesystem.exceptions.ElementDoesNotExistsException;
import filesystem.exceptions.ExistingFileException;
import filesystem.exceptions.NoMorePlaceException;
import filesystem.varios.PermissionLevel;
import filesystem.varios.TipoArchivo;
import filesystem.varios.Util;

public class FS {

	public HDDriver driver;

	private File[] files	= new File[5];
	private Dir[] querys 	= new Dir[5];
	private User[] usuarios = new User[5];

	private boolean[] fileOcupado		= new boolean[5];
	private boolean[] queryOcupado		= new boolean[5];
	private boolean[] usuarioOcupado	= new boolean[5];

	private User actualUser = new User();

	private String[] forDir = new String[20];

	private boolean[] byteOcupado		= new boolean[64000];

	private Connection conn;
	private Statement stat;

	public User getActualUser() {
		return actualUser;
	}

	public void setActualUser(User actualUser) {
		this.actualUser = actualUser;
	}


	/////////////////////////// METODOS PUBLICOS ////////////////////////////

	public FS() throws ClassNotFoundException, SQLException {

		driver = new HDDriver();

		for(int i = 0; i < files.length; i++) {
			files[i]	= new File();
			fileOcupado[i]	= false;
		}
		for (int i = 0; i < querys.length; i++) {
			querys[i]	= new Dir();
			queryOcupado[i]	= false;
		}
		for (int i = 0; i < usuarios.length; i++) {
			usuarios[i]	= new User();
			usuarioOcupado[i]	= false;
		}

		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:base.db");
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

//	private int getIdentificadorPermission() {
//		return getIdentificadorArray(permisoOcupado);
//	}


	/**
	 * Eliminadores
	 */
	private void eliminarFile(int id) {
		eliminarDato(fileOcupado, id);
	}

	private void eliminarQuery(int id) {
		eliminarDato(queryOcupado, id);
	}

	private void eliminarUser(int id) {
		eliminarDato(usuarioOcupado, id);
	}

//	private void eliminarPermiso(int id) {
//		eliminarDato(permisoOcupado, id);
//	}

	/**
	 * Encuentra un lugar libre para alojar
	 */
	private int getFileId(String nombre) {
		return getElementoId(files, fileOcupado, nombre);
	}

	private int getQueryId(String nombre) {
		return getElementoId(querys, queryOcupado, nombre);
	}

	private int getUserId(String nombre) {
		return getElementoId(usuarios, usuarioOcupado, nombre);
	}

//	private int getPermissionId(String nombre) {
//		return getElementoId(permisos, permisoOcupado, nombre);
//	}

	/**
	 * Modificadores de Files
	 * @throws ExistingFileException
	 */
	public void newFile(String nombreArchivo, TipoArchivo tipo) throws ClassNotFoundException, SQLException, ExistingFileException {

		String sql = "SELECT NAME FROM FILES WHERE NAME = '" + nombreArchivo + "'";
		System.out.println(sql);
		ResultSet rs = stat.executeQuery(sql);
		if (rs.next())
			throw new ExistingFileException();

		int idFile = getIdentificadorFile();
		if (idFile==-1) {
			idFile = desalojarFile();
		}
		File file = getFile(idFile);
		file.setName(nombreArchivo);
		file.setTipo(tipo);

		sql = "INSERT INTO FILES (IDDIR, NAME, TIPO, CREATED, MOD) VALUES ( " +
			file.getIdDir() + ", " +
			"'" + file.getName() + "', " +
			file.getTipo().ordinal() + ", " +
			"DATE(\"now\") , " +
			"DATE(\"now\") )";
		System.out.println(sql);
		stat.execute(sql);
		rs = stat.executeQuery("SELECT MAX(ID) FROM FILES");
		file.setId(rs.getInt(1));
	}

	public synchronized void updateFile(String name, byte[] contenido, int largo) throws ElementDoesNotExistsException {

		int idFile = getFileId(name);
		// Si no esta cargado
		if (idFile == -1)
			idFile = loadFile(name);

		File file = getFile(idFile);

		try {

			if (this.actualUser.getId() !=	-1) {
				String sql = "SELECT PERMISO FROM PERMISSIONS WHERE IDFILE = " + file.getId();
				System.out.println(sql);
				ResultSet rs = stat.executeQuery(sql);
				if (!rs.next()) {
					System.err.println("No tiene permiso");
					return;
				}
				PermissionLevel permiso = PermissionLevel.values()[rs.getInt("permiso")];
				if (permiso != PermissionLevel.FULL || permiso != PermissionLevel.LECTURA_ESCRITURA) {
					System.err.println("No tiene permiso");
					return;
				}
			}

			desalocarContenido(file.getInit(), file.getLargo());

			file.setContenido(contenido);
			file.setLargo(largo);

			int init = alocarContenido(file.getContenido(), file.getLargo());
			file.setInit(init);

			String sql = "UPDATE files SET " +
			"idDir = " + file.getIdDir() + ", " +
			"name = '" + file.getName() + "', " +
			"tipo = " + file.getTipo().ordinal() + ", " +
			"init = " + file.getInit() + ", " +
			"length = " + file.getLargo() + ", " +
			"mod = DATE(\"now\")" +
			" WHERE id = " + file.getId();

			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public byte[] open(String name) throws ElementDoesNotExistsException {

		try {
			int idFile = getFileId(name);
			// Si no esta cargado
			if (idFile == -1)
				idFile = loadFile(name);

			File file = getFile(idFile);

			if (this.actualUser.getId() !=	-1) {
				String sql = "SELECT PERMISO FROM PERMISSIONS WHERE IDFILE = " + file.getId();
				System.out.println(sql);
				ResultSet rs = stat.executeQuery(sql);
				if (!rs.next()) {
					System.err.println("No tiene permiso");
					return null;
				}
				PermissionLevel permiso = PermissionLevel.values()[rs.getInt("permiso")];
				if (permiso == PermissionLevel.VISTA) {
					System.err.println("No tiene permiso");
					return null;
				}
			}
			return file.getContenido();
		} catch (SQLException e) {
			// TODO: handle exception
		}

		return null;

	}

	public void deleteFile(String name) throws ElementDoesNotExistsException {

		try {
			int idFile = getFileId(name);
			// 	Si no esta cargado
			if (idFile == -1)
				idFile = loadFile(name);

			eliminarFile(idFile);
			//Borro el contenido
			File file = getFile(idFile);
			desalocarContenido(file.getInit(), file.getLargo());
			// Borro el file
			String sql = "DELETE FROM FILES WHERE ID = " + file.getId();
			System.out.println(sql);
			stat.execute(sql);
			// Borro los permisos asociados
			sql = "DELETE FROM PERMISSIONS WHERE IDFILE = " + file.getId();
			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Modificadores de Querys y Dirs
	 */
	public void mkQuery(String nombreQuery, String consulta) {
		int idQuery = getIdentificadorQuery();
		// Si no hay lugar... desalojo a alguno
		if (idQuery==-1)
			idQuery = desalojarQuery();

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
		// Si no hay lugar... desalojo a alguno
		if (idDir==-1)
			idDir = desalojarQuery();

		Dir dir = getDir(idDir);
		dir.setName(nombreDir);

		try {

			String sql = "INSERT INTO QUERYS (NAME, CONSULTA) VALUES ( " +
				"'" + dir.getName() + "', " +
				"'" + dir.getConsulta() + "')";
			System.out.println(sql);
			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM QUERYS");
			dir.setId(rs.getInt(1));
			
			sql = "UPDATE QUERYS SET CONSULTA = 'IDDIR = " + dir.getId() + "' WHERE ID = " + dir.getId();
			System.out.println(sql);
			stat.execute(sql);
			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteQuery(String name) {

		try {

			int id = getQueryId(name);
			if (id == -1)
				id = loadQuery(name);

			// Borro el query
			String sql = "DELETE FROM QUERYS WHERE ID = " + getQuery(id).getId();
			System.out.println(sql);
			stat.execute(sql);

			eliminarQuery(id);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ElementDoesNotExistsException e) {
			e.printStackTrace();
		}

	}

	public void deleteDir(String name) {

		try {

			int idDir = getQueryId(name);
			if (idDir == -1)
				idDir = loadQuery(name);

			// Borro query
			String sql = "DELETE FROM QUERYS WHERE ID = " + getDir(idDir).getId();
			System.out.println(sql);
			stat.execute(sql);
			// Borro la referencia de los archivos a ese directorio
			sql = "UPDATE FILES SET IDDIR = -1 WHERE IDDIR = " + getDir(idDir).getId();
			System.out.println(sql);
			stat.execute(sql);

			eliminarQuery(idDir);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ElementDoesNotExistsException e) {
			e.printStackTrace();
		}

	}

	public void addFileToDir(String nombreArchivo, String nombreDir) throws ElementDoesNotExistsException {
		int idDir = getQueryId(nombreDir);
		if (idDir == -1) {
			idDir = loadQuery(nombreDir);
		}
		int idFile = getFileId(nombreArchivo);
		if (idFile == -1) {
			idFile = loadFile(nombreArchivo);
		}
		File file = getFile(idFile);
		Dir dir = getDir(idDir);
		file.setIdDir(dir.getId());
		updateFile(nombreArchivo, file.getContenido(), file.getLargo());
	}



	/**
	 * Modificadores de User
	 */
	public void mkUser(String nombreUser, String pass) {
		int idUser = getIdentificadorUser();
		if (idUser==-1)
			idUser = desalojarUser();

		User user = getUser(idUser);

		user.setName(nombreUser);
		user.setPass(pass);

		try {

			String sql = "INSERT INTO USERS (NAME, PASS) VALUES ( " +
				"'" + user.getName() + "', " +
				"'" + user.pass + "')";
			System.out.println(sql);

			stat.execute(sql);
			ResultSet rs = stat.executeQuery("SELECT MAX(ID) FROM USERS");
			user.setId(rs.getInt(1));

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void deleteUser(String nombreUsuario) throws ElementDoesNotExistsException {

		int idUser = getUserId(nombreUsuario);
		if (idUser == -1) {
			idUser = loadUser(nombreUsuario);
		}
		User user = getUser(idUser);

		try {

			// Borro usuario
			String sql = "DELETE FROM USERS WHERE ID = " + user.getId();
			System.out.println(sql);
			stat.execute(sql);
			// Borro la referencia de los archivos a ese directorio
			sql = "DELETE FROM PERMISSIONS WHERE IDUSER = " + user.getId();
			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Modificadores de Permisos
	 * @throws ElementDoesNotExistsException
	 */
	public void addPermission(String nombreArchivo, String nombreUsuario, PermissionLevel permiso) throws ElementDoesNotExistsException {

		int idUser = getUserId(nombreUsuario);
		if (idUser == -1) {
			idUser = loadUser(nombreUsuario);
		}
		int idFile = getFileId(nombreArchivo);
		if (idFile == -1) {
			idFile = loadFile(nombreArchivo);
		}
		File file = getFile(idFile);
		User user = getUser(idUser);

		try {

			String sql = "INSERT INTO PERMISSIONS (IDFILE, IDUSER, PERMISO) VALUES ( " +
			file.getId() + ", " +
			user.getId() + ", " +
			permiso.ordinal() + " )" ;

			System.out.println(sql);
			stat.execute(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void deletePermission(String nombreArchivo, String nombreUsuario) throws ElementDoesNotExistsException {

		int idUser = getUserId(nombreUsuario);
		if (idUser == -1) {
			idUser = loadUser(nombreUsuario);
		}
		int idFile = getFileId(nombreArchivo);
		if (idFile == -1) {
			idFile = loadFile(nombreArchivo);
		}
		File file = getFile(idFile);
		User user = getUser(idUser);

		try {

			// Borro el permiso
			String sql = "DELETE FROM PERMISSIONS WHERE IDUSER = " + user.getId() + " AND IDFILE = " + file.getId();
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
				ret = i;
				break;
			}
		}

		return ret;
	}

	private int loadFile(String name) throws ElementDoesNotExistsException {

		int i = -1;
		try {
			String sql = "SELECT * FROM FILES WHERE NAME = '" + name + "'";
			System.out.println(sql);
			ResultSet rs = stat.executeQuery(sql);
			if (!rs.next())
				throw new ElementDoesNotExistsException();
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

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			file.setCreated(df.parse(rs.getString("created")));
			file.setModificated(df.parse(rs.getString("mod")));
			df = null;

			file.setContenido(new byte[file.getLargo()]);
			driver.getBytes(file.getInit(), file.getLargo(), file.getContenido());
			fileOcupado[i] = true;

			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return i;
	}

	private int loadQuery(String nombreDir) throws ElementDoesNotExistsException {
		int i = -1;
		// Ya tiene que existir el directorio
		try {
			String sql = "SELECT * FROM QUERYS WHERE NAME = '" + nombreDir + "'";
			System.out.println(sql);
			ResultSet rs = stat.executeQuery(sql);
			if (!rs.next())
				throw new ElementDoesNotExistsException();

			i = getIdentificadorQuery();
			// Si no hay lugar... desalojo a alguno
			if (i==-1) {
				i = desalojarQuery();
			}
			Dir dir = querys[i];
			dir.setId(rs.getInt("id"));
			dir.setName(rs.getString("name"));
			dir.setConsulta(rs.getString("consulta"));

			queryOcupado[i] = true;

			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return i;
	}

	private int loadUser(String nombre) throws ElementDoesNotExistsException {
		int i = -1;
		try {
			String sql = "SELECT * FROM USERS WHERE NAME = '" + nombre + "'";
			System.out.println(sql);
			ResultSet rs = stat.executeQuery(sql);
			if (!rs.next())
				throw new ElementDoesNotExistsException();

			i = getIdentificadorUser();
			// Si no hay lugar... desalojo a alguno
			if (i==-1) {
				i = desalojarUser();
			}
			User user = usuarios[i];
			user.setId(rs.getInt("id"));
			user.setName(rs.getString("name"));
			user.setPass(rs.getString("pass"));
			rs.close();
			usuarioOcupado[i] = true;

		} catch (SQLException e) {
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
		eliminarFile(idFile);

		return idFile;
	}

	private int desalojarQuery() {
		// Desalojo un query aleatorio
		Random rnd = new Random();
		int i = rnd.nextInt(querys.length);
		querys[i].setId(-1);
		querys[i].setName("");
		querys[i].setConsulta("");
		eliminarQuery(i);
		return i;
	}

	private int desalojarUser() {
		// Desalojo un usuario aleatorio
		Random rnd = new Random();
		int i = rnd.nextInt(usuarios.length);
		usuarios[i].setId(-1);
		usuarios[i].setName("");
		usuarios[i].pass = "";
		eliminarUser(i);
		return i;
	}

	public String[] dir(String nombreDir) throws ElementDoesNotExistsException {

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
			throw new ElementDoesNotExistsException();
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

	public String[] querys()  {

		for (int i = 0; i < forDir.length; i++) {
			forDir[i] = "";
		}
		try {
			String sql = "";
			sql = "SELECT NAME FROM QUERYS";
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
				String sql = "INSERT INTO BUSY (POSITION, BUSY)	VALUES (" + (desdePos+j) + " , 1)";
				System.out.println(sql);
				stat.execute(sql);
			}
			//String sql = "UPDATE BUSY SET BUSY = 1 WHERE POSITION <= " + desdePos + " AND POSITION < " + (desdePos + largo);

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
		String sql = "DELETE FROM BUSY WHERE POSITION >= " + desdePos + " AND POSITION < " + (desdePos + largo);
		System.out.println(sql);
		try {
			stat.execute(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void su(String user, String pass) {
		try {
			String sql = "SELECT * FROM USERS WHERE NAME = '" + user + "'";
			System.out.println(sql);

			ResultSet rs = stat.executeQuery(sql);
			if (rs.next() && rs.getString("PASS").equals(Util.encript(pass))) {
				actualUser.setId(rs.getInt("ID"));
				actualUser.setName(rs.getString("NAME"));
				actualUser.pass = rs.getString("PASS");
			} else {
				System.out.println("Usuario inexistente");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void root() {
		actualUser.setId(-1);
		actualUser.setName("");
		actualUser.pass = "";
	}
}




