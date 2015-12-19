module structural(output out1, out2, out3, out4, input inA, inB);

   wire w1, w2;
   
   not (w1, inA);
   nor (out4, inA, inB);
   not (w2, inB);
   nor (out3, inA, w2); 
   nor (out2, inB, w1);
   nor (out1, w1, w2); 
   
endmodule

