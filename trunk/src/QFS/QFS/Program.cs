using System;
using QFSvb.QFSvb.FileSystem;

namespace QFS
{
	/// <summary>
	/// Punto de entrada de la aplicacion
	/// </summary>
	static class Program
	{
		[STAThread]
		static void Main()
		{
			// Inicializo la consola
            // System.Console.WriteLine("hola, mundo");
            File elem = new File(1);

            elem.name = "archivo.txt";

            System.Console.WriteLine(elem.idFSElement);
            System.Console.WriteLine(elem.name);
			
		}
	}
}