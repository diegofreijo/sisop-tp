package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import filesystem.entidades.FSElement;
import filesystem.estructura.FS;
import filesystem.exceptions.ElementDoesNotExistsException;
import filesystem.exceptions.ExistingFileException;
import filesystem.varios.PermissionLevel;
import filesystem.varios.TipoArchivo;

public class Pruebas {

	public static void main(String[] args) {

		FS fs;
		try {
			fs = new FS();

			//fs.addPermission("archivonuevo.exe", "Nacho", PermissionLevel.VISTA);

			////fs.deletePermission("archivonuevo.exe", "Nacho");
			//fs.su("Nacho","pass");

			//byte imagen[] = fs.open("arc");
			//fs.updateFile("archivonuevo.exe", imagen, imagen.length);
			String array[] = fs.querys();
			for (int i = 0; i < array.length; i++) {
				System.out.println(array[i]);
			}

			//fs.mkUser("Nacho", "pass");

//			fs.deleteFile("archivonuevo1.exe");

			//fs.addFileToDir("archivonuevo1.exe", "directorio1");

//			String array[] = fs.dir("Imagen");
//			for (int i = 0; i < array.length; i++) {
//				System.out.println(array[i]);
//			}
//			byte imagen[] = fs.open("donCarlos.bmp");
//
//			FileOutputStream file;
//			file = new FileOutputStream("otroDonCarlos.bmp");
//			file.write(imagen);
//			file.close();
//			fs.newFile("donCarlos.bmp",TipoArchivo.imagen);
//
//            FileInputStream file = new FileInputStream("donCarlos.bmp");
//            int numberBytes = file.available();
//            byte imagen[] = new byte[numberBytes];
//
//            file.read(imagen);
//            file.close();
//
//            fs.updateFile("donCarlos.bmp", imagen, imagen.length);



			//fs.newFile("archivonuevo3.exe",TipoArchivo.imagen);

			//fs.open("archivonuevo.exe");

			//for (int i = 0; i < array.length; i++)
			//	System.out.println(array[i]);

			//byte array[] = {(byte)0xAA,(byte)0xAA,(byte)0xAA, (byte)0xAA,(byte)0xFF,(byte)0x56,(byte)0xBB, (byte)0x40,(byte)0xFF,(byte)0x56,(byte)0xBB, (byte)0x40};

			//fs.open("archivonuevo1.exe");

			//byte array[] = {(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA};

			//fs.updateFile("archivonuevo.exe",array, 12);





			//byte[] contenido = fs.open("arch1.txt");

//			fs.mkDir("directorio");
//
//			contenido = fs.open("arch1.txt");
//			String[] archivos = fs.dir("directorio");
//			for (int i = 0; i < archivos.length; i++) {
//				System.out.println(archivos[i]);
//			}
//
//			String dir[];
//			dir = fs.dir("directorio");
//			for (int i = 0; i < dir.length; i++) {
//				System.out.println(dir[i]);
//			}
			//System.out.println("cd directorio");

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
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
//		} catch (ElementDoesNotExistsException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
		}

	}

}
