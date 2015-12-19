module bestdepth(output o0, o1, o2, input ina, inb, inc);
   

// o0 is a or b or (not c)
// o1 is a or (not b) or c
// o2 is a or b or c

   wire w0, w1, w2, w3, w4, w5, w6, w7, w8, w9;
   

   nor (w0, ina, inb);
   
   not (w1, inc);
   
   not (w2, w0);

   nor (w3, ina, inc);
   
   not (w4, inb);
   
   not (w5,w3);
   
   nor (w6, inb, inc);
   
   not (w7, ina);
   
   nor (w8, w0, w7);
   
   not (w9, w6);
   
   or (o2, w8, w9);

   or (o0, w2, w1);

   or (o1, w4, w5);

endmodule
