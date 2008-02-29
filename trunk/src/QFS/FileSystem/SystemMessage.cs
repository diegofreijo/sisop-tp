using System;
using System.Collections.Generic;
using System.Text;

namespace QFS.FileSystem
{
	enum MessageType {error, warning, comment, none}
	
	/// <summary>
	/// Representa un mensaje devuelto por el FS, o sea que puede ser:
	///		+ un error
	///		+ una advertencia
	///		+ un comentario
	///		+ nada
	/// Y a su vez puede contener informacion de algun tipo (el receptor debera entender de que se trata)
	/// </summary>
	class SystemMessage
	{
		#region Miembros
		
		public MessageType type;
		public object value;

		#endregion
		


		#region Metodos publicos

		public SystemMessage(MessageType type, object value)
		{
			this.type = type;
			this.value = value;
		}

		public SystemMessage(MessageType type)
		{
			this.type = type;
			this.value = null;
		}

		#endregion
	}
}
