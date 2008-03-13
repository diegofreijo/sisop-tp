using System.Collections.Generic;

namespace QFS.Input
{
	/// <summary>
	/// Representa un comando al sistema de archivos
	/// </summary>
	class Command
	{
		#region Miembros
		
		/// <summary>
		/// Lista de argumentos pasados al comando
		/// </summary>
		private List<CommandArgument> _arguments;
		
		#endregion


		
		#region Metodos publicos
		
		/// <summary>
		/// Constructor del comando
		/// </summary>
		/// <param name="arguments">Argumentos pasados</param>
		public Command(List<CommandArgument> arguments)
		{
			_arguments = arguments;
		}

		
		public virtual object Execute()
		{
			throw new System.Exception("The method or operation is not implemented.");
		}
		
		#endregion
	}
}
