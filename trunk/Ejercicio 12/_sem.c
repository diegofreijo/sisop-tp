semaforo crear_sem(char* nombre[]) {

	message m;
	m.NOMBRE_SEM = nombre;

	semaforo new_sem = _syscall(MM, CREAR_SEM, &m);
	if(new_sem==-1) {
		printf("error");
		return ERROR;
	} else {
		return new_sem;
	}

}

int p_sem(semaforo x) {

	message m;
	int r;

	m.SEMAFORO = x;

	r = _syscall(MM, P_SEM, &m);

	if(r==-1) {
		printf("error");
		return ERROR;
	} else {
		return r;
	}

}

int v_sem(semaforo x) {

	message m;
	int r;

	m.SEMAFORO = x;

	r = _syscall(MM, V_SEM, &m);

	if(r==-1) {
		printf("error");
		return ERROR;
	} else {
		return r;
	}

}

int liberar_sem(semaforo x) {

	message m;
	int r;

	m.SEMAFORO = x;

	r = _syscall(MM, LIBERAR_SEM, &m);

	if(r==-1) {
		printf("error");
		return ERROR;
	} else {
		return r;
	}

}