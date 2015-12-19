module structuralXOR(output out, input A, B, C, D);

wire w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,w11;

   not (w8, C);
   not (w7, B);
   nor (w6, w8, B);
   nor (w5, w7, C);
   nor (w4, w6, D);
   nor (w3, w4, w5);
   not (w2, w3);
   nor (w1, w2, A);
   not (out, w1);
   

endmodule

