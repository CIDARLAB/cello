module bestdouble(output o0, o1, o2, input ina, inb, inc);
   

   wire w0, w1, w2, w3, w4, w5, w6;
   

   not (w0, ina);
   
   not (w1, inb);
   
   not (w2, inc);
   
   nor (w3, w0, inb);
   
   nor (w4, w1, inc);
   
   nor (w5, w2, ina);
   
   not (w6, w3);
   
   not (o0, w4);
   
   not (o1, w5);
   
   nor (o2, w6, inc);
   

endmodule
