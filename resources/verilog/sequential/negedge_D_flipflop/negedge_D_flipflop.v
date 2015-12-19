module A(output Qa, Qb, input C, D);

   wire w1, w2, w3, w4, w5;
   
   nor (w1,w4,w2);
   nor (w2,C,w1);
   nor (w3,w4,C,w2);
   nor (w4,w3,D);
   nor (Qa,w2,Qb);
   nor (Qb,w3,Qa);


   
endmodule
