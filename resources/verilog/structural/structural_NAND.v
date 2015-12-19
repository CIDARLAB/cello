module structuralAND(output out, input inA, inB);

   wire w1;
   
   and (w1, inA, inB);
   not (out, w1);
   
   
endmodule

