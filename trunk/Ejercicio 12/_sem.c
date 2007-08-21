#include <lib.h>
#include <minix/semaforo.h>
#include <minix/com.h>
#include <minix/constsemaf.h>
#include <stdio.h>
#include <string.h>

#define crear_sem	_crear_sem
#define p_sem		_p_sem
#define v_sem 		_v_sem
#define liberar_sem	_liberar_sem

semaforo crear_sem(char* nombre, int valor) {

	message m;
	semaforo new_sem;

	strcpy(m.NOMBRE_SEM,"");

	if(strlen(nombre) < M3_STRING) {
		strcpy(m.NOMBRE_SEM, nombre);
	} else {
		strncpy(m.NOMBRE_SEM, nombre, M3_STRING-1);
	}

	m.VALOR = valor;

	new_sem = _syscall(MM, CREAR_SEM, &m);
	if(new_sem==-1) {
		printf("error: crear_sem\n");
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
		printf("error: p_sem\n");
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
		printf("error: v_sem\n");
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
		printf("error: liberar_sem");
		return ERROR;
	} else {
		return r;
	}

}

void inicializar() {

	message m;
	
	_syscall(MM, INIT_ALL_SEM, &m);

	return 0;

}
