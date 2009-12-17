package filesystem.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class HDDriver {
	
	private byte disco[] = new byte[10000];
	
	public HDDriver() {
		
		 
		try {
			File file = new File("file.dat");

			BufferedReader br = new BufferedReader(new FileReader(file));

			for (int i = 0; i < disco.length; i++) {
				disco[i] = (byte)br.read(); 	
			}
			
					
		} catch (FileNotFoundException e) {
			System.err.println("error");
		} catch (IOException e) {
			System.err.println("error");
		}		
	}

}
