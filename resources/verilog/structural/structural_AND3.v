module structuralAND(output out, input inA, inB, inC);

   wire w1, w2;
   
   and (w1, inA, inB);
   and (w2, w1, inC);
   not (out, w2);
   
   
endmodule

