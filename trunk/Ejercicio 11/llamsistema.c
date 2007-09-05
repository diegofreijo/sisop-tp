#include "mm.h"
#include <minix/callnr.h>
#include <signal.h>
#include "mproc.h"
#include <stdlib.h>
#include <minix/com.h>
#include <minix/type.h>

PUBLIC int do_llamsistema( void ) {

	register struct mproc *rmp = mp;
	
	register int r;

	switch( mm_in.OPC_NEWCALL ) {

		case 1: /* pid */

			r = mproc[who].mp_pid;
			break;

		case 2: /* ppid */

			break;

		case 3: /* text */

			r = (int) mproc[who].mp_seg[T].mem_phys;
			break;

		case 4: /* data */
			
			r = (int) mproc[who].mp_seg[D].mem_phys;
			break;

		case 5: /* stack */

			r = (int) mproc[who].mp_seg[S].mem_phys;
			break;

		default:
			
			break;

	}

	return r;

}


