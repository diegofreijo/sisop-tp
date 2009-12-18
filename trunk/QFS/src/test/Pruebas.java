package test;

import java.sql.SQLException;

import filesystem.entidades.FSElement;
import filesystem.estructura.FS;

public class Pruebas {

	public static void main(String[] args) {

		FS fs;
		try {
			fs = new FS();
			
			byte[] contenido = fs.open("arch1.txt");
			
			fs.mkDir("directorio");
			
			contenido = fs.open("arch1.txt");
			String[] archivos = fs.dir("directorio");
			for (int i = 0; i < archivos.length; i++) {
				System.out.println(archivos[i]);
			}
			
			String dir[];
			dir = fs.dir("directorio");
			for (int i = 0; i < dir.length; i++) {
				System.out.println(dir[i]);
			}
			System.out.println("cd directorio");
			
//			for (int i = 0; i < dir.length; i++) {
//				System.out.println(dir[i]);
//			}			
//			
//			System.out.println("cd query");
//			dir = fs.dir();
//			for (int i = 0; i < dir.length; i++) {
//				System.out.println(dir[i]);
//			}
//			

//			fs.newFile("arch1.txt",TipoArchivo.datos);
//			fs.newFile("arch2.txt",TipoArchivo.datos);
//			fs.newFile("arch3.txt",TipoArchivo.datos);
//
//			
//
//			fs.addFileToDir("arch1.txt", "directorio");
//
////			int idFile = fs.getFileId("arch1.txt");
//
////			fs.eliminarFile(idFile);
//
//			fs.newFile("arch4.txt",TipoArchivo.ejecutable);
			

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
