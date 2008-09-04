using System;
using System.Collections.Generic;
using System.Text;

namespace QFS.FileSystem
{
    class Dir : FSElement
    {
        private Dir dPadre;

        /// <summary>
        /// Constructor por defecto
        /// </summary>
        public Dir() { 
        }

        /// <summary>
        /// Constructor con id
        /// </summary>
        public Dir(int id)
        {
            base(id);
        }

        public Dir padre {
            get { return this.dPadre; }
            set { this.dPadre = value; }
        }
 


    }
}
