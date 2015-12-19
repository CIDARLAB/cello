module A(output Qa, Qb, input in1, in2, C);

   wire w1, w2, w3, w4, w5;

   not (w4, in1);
   not (w5, in2);
   nor (D, w4, w5);
   nor (w1, D, C);
   nor (w2, w1, C);
   nor (Qa, w1, Qb);
   nor (Qb, w2, Qa);

endmodule // A
