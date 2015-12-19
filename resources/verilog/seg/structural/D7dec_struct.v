module structuralXOR(output out, input A, B, C, D);

wire w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,w11;


   not (w11, C);
   not (w10, D);
   nor (w7, w10, C);
   not (w9, w7);
   not (w8, B);
   nor (w6, w8, w9);
   nor (w5, w7, B);
   nor (w4, w5, w6);
   not (w3, w4);
   nor (w2, w11, D);   
   nor (w1, w2, w3);
   not (out, w1);
   
   
   
endmodule
