#include "syslib.h"

PUBLIC int sys_block(proc)
int proc;		/* process to block */
{
  message m;

  m.m1_i1 = proc;
  return(_taskcall(SYSTASK, SYS_BLOCK, &m));
}
