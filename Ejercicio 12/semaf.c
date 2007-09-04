#include "mm.h"
#include <minix/callnr.h>
#include <signal.h>
#include "mproc.h"
#include <stdlib.h>
#include "semaf.h"
#include <minix/constsemaf.h>
#include <minix/com.h>
#include <minix/type.h>
#include <string.h>

FORWARD _PROTOTYPE( int do_is_sem, (void)				);
FORWARD _PROTOTYPE( int do_val_sem, (void)				);
FORWARD _PROTOTYPE( int do_get_next_bloq_proc, (void)			);
FORWARD _PROTOTYPE( void do_add_bloq_proc, (int)			);
FORWARD _PROTOTYPE( void do_init_sem, (void)				);

/*===========================================================================*
 *			do_crear_sem					     *
 *===========================================================================*/

PUBLIC int do_crear_sem(void) {

	register struct semaf *sp = semaforos;

	semaforo s;

	pid_t procID		= mproc[who].mp_pid;
	char* nombre		= mm_in.NOMBRE_SEM;
	int valor		= mm_in.VALOR;

	int i, j;

	i = 0;

	/* printf("este es el proc en crear %d\n",procID); */


	for(i=0;i<MAX_SEM;i++) {
		/* Busco si el nombre corresponde a un semaforo ya creado */
		if (!strcmp(semaforos[i].nombre, nombre)) {
			/* printf("semaforo encontrado!!\n"); */
			break;
		}
	}

	/* i se incrementa en uno mas, no se xq, entonces lo decremento */

	if ( i < MAX_SEM ) {
		/* Si el semaforo ya existe lo selecciono */
		s = i;

	} else {
		/* Sino busco el primer semaforo sin uso */
		for(i=0;i<MAX_SEM;i++) {
			if (semaforos[i].semafEnUso == 0) {

				s = i;
				/* Defino el semaforo */

				strcpy(semaforos[s].nombre, nombre);
				semaforos[s].valor = valor;
				semaforos[s].semafEnUso = 1;
				semaforos[s].cant_proc  = 0;
				semaforos[s].inicio_cola_bloq = 0;
				semaforos[s].fin_cola_bloq    = 0;

				break;

			}
		}
	}


	if (i == MAX_SEM) {

		/* si no hay semaforos disponibles retorno error */
		return -1;

	} else {

		semaforos[s].cant_proc++;

		/* Asigno al proceso al primer lugar vacio de la lista de procesos */
		for(j=0;j<MAX_PROC;j++) {
			if (semaforos[s].procEnUso[j] == 0) {

				semaforos[s].procEnUso[j] = procID;
				break;

			}
		}

		return s;

	}

}


/*===========================================================================*
 *			do_is_sem		    			     *
 *===========================================================================*/

PRIVATE int do_is_sem(void) {

	register int i;

	register struct semaf *sp = semaforos;

	pid_t procID = mproc[who].mp_pid;
	semaforo s = mm_in.SEMAFORO;

	/* printf("el proc es: %d\n", procID); */

	for(i=0;i<MAX_PROC;i++) {
		/*
		printf("proc en uso actual: %d\n", semaforos[s].procEnUso[i]);
		*/
		if (semaforos[s].procEnUso[i] == procID) {
			return 1;
		}
	}

	return 0;

}

/*===========================================================================*
 *				do_p_sem					     							 *
 *===========================================================================*/

PUBLIC int do_p_sem(void) {

	message m;

	register struct semaf *sp = semaforos;

	register int proc_nr;
	register semaforo s = mm_in.SEMAFORO;

       /**
	 * Calculo la posicion del proceso en la tabla.
	 * Esta es el puntero al proceso menos el puntero a la lista
	 * de procesos.
 	 */

	proc_nr = (int) (mp - mproc);

	if (do_is_sem()) {

		/* decremento el valor del semaforo */
		semaforos[s].valor--;

		if(do_val_sem() < 0 ) {

			/* Agrego el proceso a bloqueados */
			do_add_bloq_proc(proc_nr);

			/*  debo bloquear el proceso */
			sys_block(proc_nr);

		}

		return 0;

	} else {

		return -1;

	}

}

/*===========================================================================*
 *				do_v_sem					     							 *
 *===========================================================================*/

PUBLIC int do_v_sem(void) {

	message m;

	register struct semaf *sp = semaforos;

	register int proc_nr;
	semaforo s = mm_in.SEMAFORO;

	if (do_is_sem()) {

		/* incremento el valor del semaforo */
		semaforos[s].valor++;

		if(do_val_sem() <= 0 ) {

			/* Busco si existe algun proceso bloqueado */
			proc_nr = do_get_next_bloq_proc();

			if (proc_nr > 0) {

				sys_unblock(proc_nr);

			}

		}

		return 0;

	} else {

		return -1;

	}

}

/*===========================================================================*
 *			do_val_sem					     *
 *===========================================================================*/

PRIVATE int do_val_sem(void) {

	register struct semaf *sp = semaforos;

	register semaforo s = mm_in.SEMAFORO;

	return semaforos[s].valor;

}

/*===========================================================================*
 *			do_liberar_sem   				     *
 *===========================================================================*/

PUBLIC int do_liberar_sem(void) {

	message m;

	register struct semaf *sp = semaforos;

	register int proc_nr;

	pid_t procID = mproc[who].mp_pid;
	semaforo s = mm_in.SEMAFORO;

	int i;

	if (do_is_sem()) {

		semaforos[s].cant_proc--;

		if (semaforos[s].cant_proc > 0) {
		/* si el semaforo posee procesos bloqueados los desbloquea */

			for(i=semaforos[s].inicio_cola_bloq;i<=semaforos[s].inicio_cola_bloq;i++) {

				proc_nr = semaforos[s].procBloqueados[i];
				sys_unblock(proc_nr);

			}

		/* Luego elimino todos los procesos asociados referencia al proceso */

			for(i=0;i<MAX_PROC;i++) {

				semaforos[s].procEnUso[i] = 0;

			}

		}

		/* finalmente inicializo el semaforo */

		do_init_sem();

		return 0;

	} else {

		return -1;

	}

}

/*===========================================================================*
 *			do_get_next_bloq_proc				     *
 *===========================================================================*/

PRIVATE int do_get_next_bloq_proc(void) {

	register struct semaf *sp = semaforos;

	semaforo s = mm_in.SEMAFORO;

	register int r = 0;

	if (semaforos[s].inicio_cola_bloq != semaforos[s].inicio_cola_bloq) {

		/* si existe algun proceso bloqueado lo elijo, y adelanto el inicio de la cola */

		semaforos[s].cant_proc--;

		/* primer proceso bloqueado */
		r = semaforos[s].procBloqueados[semaforos[s].inicio_cola_bloq];

		semaforos[s].inicio_cola_bloq++;
    		semaforos[s].inicio_cola_bloq %= MAX_PROC;

	}
	/* sino retorna 0 */

	return r;

}

/*===========================================================================*
 *			do_add_bloq_proc				     *
 *===========================================================================*/

PRIVATE void do_add_bloq_proc(int proc_nr) {

	register struct semaf *sp = semaforos;

	semaforo s = mm_in.SEMAFORO;

	semaforos[s].cant_proc++;

	semaforos[s].procBloqueados[semaforos[s].fin_cola_bloq] = proc_nr;

	semaforos[s].fin_cola_bloq++;
	semaforos[s].fin_cola_bloq %= MAX_PROC;

}


/*===========================================================================*
 *				do_init_sem				   									 *
 *===========================================================================*/

PRIVATE void do_init_sem(void) {

	register struct semaf *sp = semaforos;

	register semaforo s = mm_in.SEMAFORO;

	semaforos[s].semafEnUso = 0;

	semaforos[s].valor 	= 0;
	semaforos[s].cant_proc 	= 0;

	semaforos[s].inicio_cola_bloq	= 0;
	semaforos[s].fin_cola_bloq	= 0;

}



/*===========================================================================*
 *				do_init_all_sem											     *
 *===========================================================================*/

PUBLIC int do_init_all_sem(void) {

	int i, j;
	register struct semaf *sp = semaforos;

	for(i=0;i<MAX_SEM;i++) {

		strcpy(semaforos[i].nombre,"");
		semaforos[i].semafEnUso			= 0;
		semaforos[i].valor 			= 0;
		semaforos[i].cant_proc 			= 0;
		semaforos[i].inicio_cola_bloq		= 0;
		semaforos[i].fin_cola_bloq		= 0;


		for(j=0;j<MAX_PROC;j++) {

			semaforos[i].procBloqueados[j]	= 0;
			semaforos[i].procEnUso[j] 	= 0;

		}

	}

	return 0;
}