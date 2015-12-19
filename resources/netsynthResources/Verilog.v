module structural(output out1, out2, input A, B, C);

wire w1,w2,w3,w4,w5,w6,w7,w8;

   nor (w5, A, B);
   not (w6, C);
   nor (w2, w5, w6);
   not (w1, w2);
   nor (w4, w2, w6);
   nor (out2, B, w4);
   not (out1, w1);   
   
endmodule