module structuralXOR(output out, input A, B, C, D);

wire w1,w2,w3,w4,w5,w6,w7,w8,w9;

   not (w8, D);
   not (w7, C);
   not (w2, B);
   nor (w5, D, C);
   nor (w6, w7, w8);
   nor (w4, w5, w6);
   not (w3, w4);
   nor (w1, w2, w3);
   not (out, w1);
   
   
endmodule

