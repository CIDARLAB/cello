module structuralXOR(output out, input A, B, C, D);

wire w1,w2,w3,w4,w5,w6,w7,w8,w9;

   not (w4, C);
   nor (w3, w4, B);
   not (w2, w3);
   nor (w1, w2, D);
   not (out, w1);
   
   
endmodule

