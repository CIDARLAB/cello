// ~: ~
// &: &
// |:  |
// order of operations: ()

module A(out1, out2, a, b, c);

output out1, out2;
input a, b, c;


  wire w0;
  assign w0 = a & ~b;
  assign out1 = w0 | (a & c);

endmodule

