package filesystem.driver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class HDDriver {
	
	private byte disco[];
	
	public HDDriver() {
		
		try {
			
            FileInputStream fileinputstream = new FileInputStream("file.dat");

            int numberBytes = fileinputstream.available();
            byte disco[] = new byte[numberBytes];

            fileinputstream.read(disco);

            for(int i = 0; i < numberBytes; i++){
                System.out.println(disco[i]);
            }

            fileinputstream.close();
			
					
		} catch (FileNotFoundException e) {
			System.err.println("error");
		} catch (IOException e) {
			System.err.println("error");
		}		
	}
	
	public void getBytes(int init, int lenght, byte[] datos) {
		for (int i = 0; i < lenght; i++) {
			datos[i] = disco[init+i];
		}
	}
	
	public void setBytes(int init, int lenght, byte[] datos) {
		for (int i = 0; i < lenght; i++) {
			disco[init+i] = datos[i];
		}
	}

}
