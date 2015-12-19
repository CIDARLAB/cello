// Benchmark "test" written by ABC on Fri Jan  2 17:53:46 2015

module test ( 
    a, b, c,
    out  );
  input  a, b, c;
  output out;
  assign out = ~c & (a | b);
endmodule


