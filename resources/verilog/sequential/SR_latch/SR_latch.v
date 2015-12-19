module A(output Qa, Qb, input R, S);

   wire w1, w2;

   nor (Qa, R, Qb);
   nor (Qb, S, Qa);


endmodule
