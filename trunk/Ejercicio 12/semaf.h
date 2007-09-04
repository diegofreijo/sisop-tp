#define MAX_SEM 	100	
#define MAX_PROC 	100

#include <minix/type.h>

typedef int semaforo;

struct semaf {

	char nombre[M3_STRING];

	int semafEnUso; /* 0 o 1 */

	int valor;
	int cant_proc;

	int procEnUso[MAX_PROC];

	int inicio_cola_bloq;
	int fin_cola_bloq;
	int procBloqueados[MAX_PROC];

};

struct semaf semaforos[MAX_SEM];