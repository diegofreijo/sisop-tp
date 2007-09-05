/* El header <semaforos.h> contiene constantes para la definicion de semaforos */

#ifndef _SEMAFOROS_H_
#define _SEMAFOROS_H_

#define ERROR -1
typedef int semaforo;

_PROTOTYPE( semaforo crear_sem, (char* nombre, int valor) 	);

_PROTOTYPE( int p_sem, (semaforo) 				);

_PROTOTYPE( int v_sem, (semaforo)				);

_PROTOTYPE( int liberar_sem, (semaforo)				);

_PROTOTYPE( void inicializar, (void) 				);

#endif 
