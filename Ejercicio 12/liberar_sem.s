.sect .text
.extern __liberar_sem
.define _liberar_sem

.align 2

_liberar_sem:
	jmp __liberar_sem