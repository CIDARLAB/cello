module A(output Qa, Qb, input D, C);

   wire w1, w2, w3;
   
   nor (w1, D, C);
   nor (w2, w1, C);
   nor (Qa, w1, Qb);
   nor (Qb, w2, Qa);

endmodule // A
