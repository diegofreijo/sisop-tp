.sect .text
.extern __crear_sem
.define _crear_sem

.align 2

_crear_sem:
	jmp __crear_sem
