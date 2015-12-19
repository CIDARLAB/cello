module structuralXOR(output out, input A, B, C, D);

wire w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,w11;

   not (w9, D);
   not (w7, C);
   not (w6, B);
   nor (w4, w6, C);
   nor (w8, w9, w6);
   nor (w5, w7, w8);
   nor (w3, w4, w5);
   not (w2, w3);
   nor (w1, w2, A);
   not (out, w1);

endmodule

