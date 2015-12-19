module Structural(output out1, input in1,in2,in3);

   wire w1,w2,w3,w4;
   
   not (w1, in1);
   not (w2, in2);
   nor (w3, w1, w2);
   nor (w4, w3, in3);
   nor (out1, w4);   
   
endmodule

