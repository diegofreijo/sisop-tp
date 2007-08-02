PUBLIC int do_crear_sem(void) {

	register struct mproc *rmp = mp;

	register struct semaf *s = semaforos;

	int i;

	for(i=0;i<MAX_SEM;i++) {
		if (semaforos[i].semafEnUso == 0) {
			/* asignar proceso a semaforo */
			return i;
		}
	}
	return -1;

}

PUBLIC int do_is_sem(void) {

	register struct mproc *rmp = mp;

	register pid procID = mp->PROC1;
	register semaforo s = mp->SEMAFORO;

	return (semaforos[s].procEnUso == procID)

}

PUBLIC int do_p_sem(void) {

	register struct mproc *rmp = mp;

	register semaforo s = mp->SEMAFORO;

	if (is_sem(s)) {

		/* decrementar semaforo */

		if(val_sem(s) < 0 ) {
			/* taskcall DO_BLOCK proceso */
		}

	} else {
		return -1;
	}

}

PUBLIC int do_v_sem(void) {

	register struct mproc *rmp = mp;

	register semaforo s = mp->SEMAFORO;

	if (is_sem(s)) {

		/* incrementar semaforo */

		if(val_sem(s) >= 0 ) {
			/* taskcall DO_UNBLOCK prox proceso */
		}

	} else {
		return -1;
	}

}

PUBLIC int do_val_sem(void) {

	register struct mproc *rmp = mp;

	register semaforo s = mp->SEMAFORO;

	if (is_sem(s)) {
		return semaforos[s].valor;
	} else {
		return -1;
	}
}

PUBLIC int do_liberar_sem(void) {

	register struct mproc *rmp = mp;

	register struct semaf *s = semaforos;

	if (is_sem(s)) {

		/* libera a todos los procesos bloqueados */

	} else {
		return -1;
	}

}