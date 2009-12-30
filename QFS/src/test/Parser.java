package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import filesystem.estructura.FS;
import filesystem.exceptions.ElementDoesNotExistsException;
import filesystem.exceptions.ExistingFileException;
import filesystem.varios.PermissionLevel;
import filesystem.varios.TipoArchivo;

public class Parser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		BufferedReader dis = new BufferedReader(new	InputStreamReader(System.in));

		FS fs;
		try {
			fs = new FS();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		} catch (SQLException e1) {
			e1.printStackTrace();
			return;
		}

		while(true) {
		try {
				System.out.print(":");
				String line = dis.readLine();
				String array[] = line.split(" ");

				if (array[0].equals("newFile")) {
					fs.newFile(array[1], TipoArchivo.values()[Integer.parseInt(array[2])]);
				} else if (array[0].equals("updateFile")) {
					char chars[] = array[2].toCharArray();
					byte bytes[] = new byte[chars.length];
					for (int i = 0; i < chars.length; i++) {
						bytes[i] = (byte)chars[i];
					}
					fs.updateFile(array[1], bytes, bytes.length);
				} else if (array[0].equals("open")) {
					byte bytes[] = fs.open(array[1]);
					String cadena = "";
					for (int i = 0; i < bytes.length; i++) {
						cadena += (char)bytes[i];
					}
					System.out.println(cadena);
				} else if (array[0].equals("deleteFile")) {
					fs.deleteFile(array[1]);
				} else if (array[0].equals("mkQuery")) {
					fs.mkQuery(array[1], line.split("'")[1]);
				} else if (array[0].equals("mkDir")) {
					fs.mkDir(array[1]);
				} else if (array[0].equals("deleteQuery")) {
					fs.deleteQuery(array[1]);
				} else if (array[0].equals("deleteDir")) {
					fs.deleteDir(array[1]);
				} else if (array[0].equals("addFileToDir")) {
					fs.addFileToDir(array[1], array[2]);
				} else if (array[0].equals("dir")) {
					String lista[] = fs.dir(array[1]);
					for (int i = 0; i < lista.length; i++) {
						if ("".equals(lista[i]))
							break;
						System.out.println(lista[i]);
					}
				} else if (array[0].equals("multiDir")) {
					String lista[] = line.split("'")[1].split(",");
					lista = fs.multiDir(lista);
					for (int i = 0; i < lista.length; i++) {
						if ("".equals(lista[i]))
							break;
						System.out.println(lista[i]);
					}
				} else if (array[0].equals("querys")) {
					String lista[] = fs.querys();
					for (int i = 0; i < lista.length; i++) {
						if ("".equals(lista[i]))
							break;
						System.out.println(lista[i]);
					}
				} else if (array[0].equals("mkUser")) {
					fs.mkUser(array[1], array[2]);
				} else if (array[0].equals("deleteUser")) {
					fs.deleteUser(array[1]);
				} else if (array[0].equals("addPermission")) {
					fs.addPermission(array[1],array[2], PermissionLevel.values()[Integer.parseInt(array[3])]);
				} else if (array[0].equals("deletePermission")) {
					fs.deletePermission(array[1],array[2]);
				} else if (array[0].equals("su")) {
					fs.su(array[1], array[2]);
				} else if (array[0].equals("root")) {
					fs.root();
				} else if (array[0].equals("exit")) {
					break;
				} else {
					System.out.println("Comando \"" + array[0] + "\" desconocido");
				}

			} catch (IOException e) {
				System.err.println("Error de ingreso de datos");
			} catch (NumberFormatException e) {
				System.err.println("Se esperaba un número");
			} catch (ClassNotFoundException e) {
				System.err.println("Clase no encontrada");
			} catch (SQLException e) {
				System.err.println("Error en conexion con base de datos");
			} catch (ExistingFileException e) {
				System.err.println("Existe un file con el nombre ingresado");
			} catch (ElementDoesNotExistsException e) {
				System.err.println("No existe elemento con el nombre ingresado");
			} catch (Exception e) {
				System.err.println("Error, llamada incorrecta");
			}
		}

	}

}

