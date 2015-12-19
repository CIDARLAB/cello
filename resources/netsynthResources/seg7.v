// Benchmark "simple" written by ABC on Mon Apr 14 16:39:21 2014

module simple ( 
    a, b, c, d,
    o  );
  input  a, b, c, d;
  output o;
  wire n4, n5, n6;
  assign n4 = b & ~d;
  assign n5 = b & ~c;
  assign n6 = ~c & ~d;
  assign o = n4 | n5 | n6 | a;
endmodule

