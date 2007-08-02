#define MAX_SEM
#define MAX_PROC

struct semaf {

	int semafEnUso; /* 0 o 1 */

	int valor;
	int cant_proc;

	pid procEnUso[MAX_PROC];

	int inicio_cola_bloq;
	int fin_cola_bloq;
	pid procBloqueados[MAX_PROC];

};

struct semaf semaforos[MAX_SEM];
