module structuralAND(output out, input inA, inB);

   wire w1, w2, w3, w4, w5;
   
   nor (w2, inA, inB);
   not (w4, inB);
   not (w5, inA);
   nor (w3, w4, w5);
   nor (w1, w2, w3);
   not (out, w1);
   
endmodule

