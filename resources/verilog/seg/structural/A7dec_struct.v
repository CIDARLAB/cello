module structuralXOR(output out, input A, B, C, D);

wire w1,w2,w3,w4,w5,w6,w7,w8,w9;

   not (w9, D);
   nor (w8, C, A);
   not (w7, B);
   nor (w6, w9, w7);
   nor (w5, D, B);
   nor (w4, w5, w6);
   not (w3, w8);
   not (w2, w4);
   nor (w1, w2, w3);
   not (out, w1);
   
endmodule

