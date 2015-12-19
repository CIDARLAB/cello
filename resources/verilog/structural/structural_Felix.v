module structuralAND(output out, input o, a, g);

   wire w1, w2, w3, w4, w5;
   
   not (w5, g);
   not (w4, a);
   not (w3, o);
   nor (w2, w4, w5);
   nor (w1, w3, w4);
   or  (out, w1, w2);
   
endmodule

