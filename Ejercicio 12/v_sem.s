.sect .text
.extern __v_sem
.define _v_sem

.align 2

_v_sem:
	jmp __v_sem
