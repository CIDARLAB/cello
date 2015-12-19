module ten_v2(output o1, o2, o3, input in1, in2);

   wire w1, w2, w3, w4, w5, w6, w7;

   not (w6, in2);
   not (w5, in1);
   nor (w4, w6, in1);
   nor (w2, w5, in2);
   nor (w7, w2, w6);
   not (w3, w4);
   nor (w1, w4, w5);
   not (o3, w7);
   nor (o2, w2, w3);
   not (o1, w1);


endmodule
