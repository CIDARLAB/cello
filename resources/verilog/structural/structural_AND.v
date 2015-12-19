module structuralAND(output out, input inA, inB);

wire w2,w3;

   not (w3, inB);
   not (w2, inA);
   nor (out, w2, w3);
   
endmodule

