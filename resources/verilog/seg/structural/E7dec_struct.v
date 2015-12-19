module structuralXOR(output out, input A, B, C, D);

wire w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,w11;

   not (w2, B);
   nor (w1, w2, C);
   nor (out, w1, D);

endmodule

