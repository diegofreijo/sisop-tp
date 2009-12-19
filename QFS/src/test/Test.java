package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import filesystem.estructura.FS;
import filesystem.varios.TipoArchivo;

public class Test {

	  public static void main(String[] args) throws Exception {

		//HDDriver hd = new HDDriver();



		FS fs = new FS();


		String array[] = {"Datos", "Ayer"};
		String dir[] = fs.multiDir(array);
		for (int i = 0; i < dir.length; i++) {
			if (dir[i].length() > 0)
					System.out.println(dir[i]);
		}
//		dir = fs.dir("Ayer");
//		for (int i = 0; i < dir.length; i++) {
//			System.out.println(dir[i]);
//		}

	  }


}
