module bestbetween(output o1, o2, o3, input in1, in2, in3);

   wire w0, w1, w2, w3, w4, w5, w6, w7, w8;
   
   
   not (w6, in1);
   not (w7, in2);
   not (w8, in3);
   nor (w3, w6, in2);
   nor (w4, w7, in3);
   nor (w5, w8, in1);
   not (o1, w3);
   not (o2, w4);
   not (o3, w5);

endmodule
