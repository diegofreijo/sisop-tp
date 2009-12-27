package filesystem.driver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class HDDriver {

	private byte disco[];

	public HDDriver() {

		try {

            FileInputStream file = new FileInputStream("file.dat");

            int numberBytes = file.available();
            disco = new byte[numberBytes];

            file.read(disco);
            file.close();

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
		FileOutputStream file;
		try {
			file = new FileOutputStream("file.dat");
			file.write(disco);
			file.close();
		} catch (FileNotFoundException e) {
			System.err.println("error");
		} catch (IOException e) {
			System.err.println("error");
		}

	}

}
