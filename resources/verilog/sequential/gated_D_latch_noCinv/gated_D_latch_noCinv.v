module A(output Qa, Qb, input D, C);

   wire w1, w2, w3;

   not (w1, D);
   nor (w2, D, C);
   nor (w3, w1, C);
   nor (Qa, w2, Qb);
   nor (Qb, w3, Qa);

endmodule // A
