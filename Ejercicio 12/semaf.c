/*===========================================================================*
 *				do_crear_sem			     								 *
 *===========================================================================*/

PUBLIC int do_crear_sem(void) {

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register semaforo s;

	pid procID		= mp->PROC1;
	char* nombre[]	= mp->NOMBRE_SEM;

	int i;

	for(i=0;i<MAX_SEM;i++) {
		/* Busco si el nombre corresponde a un semaforo ya creado */
		if (strcmp(semaforos[i].nombre, nombre) {
			break;
		}
	}


	if ( i > 0 ) {
		/* Si el semaforo ya existe lo selecciono */
		s = i;

	} else {
		/* Sino busco el primer semaforo sin uso */
		for(i=0;i<MAX_SEM;i++) {
			if (semaforos[i].semafEnUso == 0) {

				s = i;
				break;

			}
		}
	}


	if (i == MAX_SEM) {

		/* si no hay semaforos disponibles retorno error */
		return -1;

	} else {

		/* asigno datos a semáforo */
		semaforos[s].nombre[10] = nombre;
		semaforos[s].semafEnUso = 1;

		semaforos[s].valor		= 0;
		semaforos[s].cant_proc++;

		semaforos[s].inicio_cola_bloq	= 0;
		semaforos[s].fin_cola_bloq	 	= 0;

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
 *				do_is_sem					     							 *
 *===========================================================================*/

PRIVATE int do_is_sem(void) {

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register pid procID = mp->PROC1;
	register semaforo s = mp->SEMAFORO;

	int i;

	for(i=0;i<MAX_PROC;i++) {

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

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register pid procID = mp->PROC1;
	register semaforo s = mp->SEMAFORO;

	if (is_sem(s)) {

		/*  decremento el valor del semaforo */
		semaforos[s].valor--;

		if(val_sem(s) < 0 ) {

			/* Agrego el proceso a bloqueados */
			do_add_bloq_proc(procID);

			/*  debo bloquear el proceso */
			m.m1_i1 = procID;
			_taskcall(SYSTASK, SYS_BLOCK, &m);

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

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register pid procID;
	register semaforo s = mp->SEMAFORO;

	if (is_sem(s)) {

		/* incremento el valor del semaforo */
		semaforos[s].valor++;

		if(val_sem(s) >= 0 ) {

			/* Busco si existe algun proceso bloqueado */
			procID = do_get_next_bloq_proc();

			if (procID > 0) {

				/* Existe un proceso bloqueado --> lo desbloqueo */
				m.m1_i1 = procID;
				_taskcall(SYSTASK, SYS_UNBLOCK, &m);

			}

		}

	} else {
		return -1;
	}

}

/*===========================================================================*
 *				do_val_sem					     							 *
 *===========================================================================*/

PRIVATE int do_val_sem(void) {

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register semaforo s = mp->SEMAFORO;

	return semaforos[s].valor;

}

/*===========================================================================*
 *				do_liberar_sem					     						 *
 *===========================================================================*/

PUBLIC int do_liberar_sem(void) {

	message m;

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register semaforo s = mp->SEMAFORO;

	int i;

	if (is_sem(s)) {

		/* Limpio la cola de procesos bloqueados */
		for(i=semaforos[s].inicio_cola_bloq;i=<semaforos[s].fin_cola_bloq;i++) {

			/* por cada proceso ejecuto un syscall para desbloquearlo */
			m.m1_i1 = semaforos[s].procBloqueados[i];
			_taskcall(SYSTASK, SYS_UNBLOCK, &m);

			/* borro el id de proceso que fue desbloqueado */
			semaforos[s].procBloqueados[i] = 0;

		}

		/* Limpio la lista de procesos */
		for(i=0;i<MAX_PROC;i++) {

			/* borro el id de proceso */
			semaforos[s].procEnUso[i] = 0;
		}

		/* inicializo el semaforo */
		do_init_sem();

	} else {
		return -1;
	}

}

/*===========================================================================*
 *			do_get_next_bloq_proc					     					 *
 *===========================================================================*/

PRIVATE int do_get_next_bloq_proc(void) {

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register semaforo s = mp->SEMAFORO;

	int r = 0

	if (semaforos[s].inicio_cola_bloq != semaforos[s].fin_cola_bloq) {

		/* si existe algun proceso bloqueado lo elijo, y adelanto el inicio de la cola */

		semaforos[s].cant_proc--;

		/* primer proceso bloqueado */
		r = semaforos[s].procBloqueados[semaforos[s].inicio_cola_bloq];

		semaforos[s].inicio_cola_bloq++;
    	semaforos[s].inicio_cola_bloq %= MAX;

	}
	/* sino retorna 0 */

	return r;

}

/*===========================================================================*
 *			do_add_bloq_proc						     					 *
 *===========================================================================*/

PRIVATE void do_add_bloq_proc(int procID) {

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register semaforo s = mp->SEMAFORO;

	semaforos[s].cant_proc++;

	semaforos[s].procBloqueados[semaforos[s].fin_cola_bloq] = procID;

	semaforos[s].fin_cola_bloq++;
   	semaforos[s].fin_cola_bloq %= MAX;

	return 0;

}


/*===========================================================================*
 *				do_init_sem							     					 *
 *===========================================================================*/

PRIVATE int do_init_sem(void) {

	register struct mproc *rmp = mp;

	register struct semaf *sp = semaforos;

	register semaforo s = mp->SEMAFORO;

	semaforos[s].semafEnUso = 0;

	semaforos[s].valor 		= 0;
	semaforos[s].cant_proc 	= 0;

	semaforos[s].inicio_cola_bloq	= 0;
	semaforos[s].fin_cola_bloq	 	= 0;

	}
	/* sino retorna 0 */

	return r;

}