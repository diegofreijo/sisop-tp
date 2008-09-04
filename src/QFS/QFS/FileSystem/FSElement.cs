using System;
using System.Collections.Generic;
using System.Text;

namespace QFS.FileSystem
{
    abstract class FSElement
    {
        public FSElement() { }

        public FSElement(int id, String name) {
            this.iId = id;
            this.sName = name;
        }

        public FSElement(int id)
        {
            this.iId = id;
         
        }

        private int iId;
        private String sName;

        public readonly int id
        {
            get { return ciudad; }
        }

        public String name
        {
            get { return this.sName; }
            set { this.sName = value; } 
        }


    }
}
