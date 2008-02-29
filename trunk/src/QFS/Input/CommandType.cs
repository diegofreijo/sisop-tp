namespace QFS.Input
{
	/// <summary>
	/// Tipos de comandos existentes
	/// </summary>
	enum CommandType
	{
		abrir,				// Abre un archivo y devuelve un identificador para accederlo
		cerrar,				// Cierra el archivo del identificador dado
		crear,				// Crea un archivo (no debe existir)
		modificar,			// Modifica un archivo (debe existir)
		borrar,				// Borra un archivo (debe existir)
		consultar			// Ejecuta una consulta al FS y devuelve una lista de archivos
	}
}
