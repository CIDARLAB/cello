module A(output out1, input a, b, c);

  assign out1 = ( ~a & b ) | ( a & ~c );

endmodule
