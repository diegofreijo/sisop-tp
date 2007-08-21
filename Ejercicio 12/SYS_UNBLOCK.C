#include "syslib.h"

PUBLIC int sys_unblock(proc)
int proc;		/* process to unblock */
{
  message m;

  m.m1_i1 = proc;
  return(_taskcall(SYSTASK, SYS_UNBLOCK, &m));
}
