module A(output Qa, Qb, input in1, in2, C);

   wire w1, w2, w3, w4, w5, D;


   not (w4, in1);
   not (w5, in2);
   nor (D, w4, w5);
   not (w1, D);
   nor (w2, D, C);
   nor (w3, w1, C);
   nor (Qa, w2, Qb);
   nor (Qb, w3, Qa);

endmodule // A
