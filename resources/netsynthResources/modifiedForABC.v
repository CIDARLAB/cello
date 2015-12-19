module newAssignVerilog(a,b,c,out1); input a,b,c; output out1;  assign out1 = (a & ~b) | (a & c); endmodule
