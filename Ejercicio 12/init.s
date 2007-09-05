.sect .text
.extern __inicializar
.define _inicializar

.align 2

_liberar_sem:
	jmp __inicializar
