namespace QFS.Input
{
	/// <summary>
	/// Representa la consola por la que el usuario maneja el FS
	/// </summary>
	class Console
	{
		#region Miembros
		
		/// <summary>
		/// Cuando pase a false, se cierra la consola
		/// </summary>
		private static bool _running = true;
		
		#endregion


		
		#region Metodos publicos
		
		/// <summary>
		/// Inicia la consola para que comience a leer comandos
		/// </summary>
		public static void Start()
		{
			// Bucle principal de la consola
			while(_running)
			{
				Console.ReadCommand().Execute();
			}
		}

		#endregion


		
		#region Metodos privados

		/// <summary>
		/// Lee un comando desde la consola
		/// </summary>
		/// <returns></returns>
		private static Command ReadCommand()
		{
			throw new System.Exception();
		}

		#endregion

	}
}
