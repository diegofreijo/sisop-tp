.sect .text
.extern __p_sem
.define _p_sem

.align 2

_p_sem:
	jmp __p_sem
