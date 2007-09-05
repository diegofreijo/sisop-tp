#include <lib.h>
#include <unistd.h>
#include <minix/com.h>
#define llamsistema _llamsistema

PUBLIC int llamsistema( int opcion ) {

	message m;

	switch( opcion ) {

		case 1:	/* pid */
		case 3: /* text */
		case 4: /* data */
		case 5: /* stack */

			m.OPC_NEWCALL = opcion;
	
			return(_syscall( MM, LLAMSISTEMA, &m ));

		case 2: /* ppid */
	
			_syscall( MM, LLAMSISTEMA, &m );
			return m.m2_i1;

		default:

			return -1;

	} 
}
