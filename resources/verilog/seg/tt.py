#!/usr/bin/python


def f1():
    print "test"

f1()



def truthtable (n):
  if n < 1:
    return [[]]
  subtable = truthtable(n-1)
  return [ row + [v] for row in subtable for v in [0,1] ] 

a = truthtable(3)

print a
