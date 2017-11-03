module rule30struct(output out1,  input in1, in2, in3);
   wire w1,w2,w3,w4,w5;
   nor (w1, in1, in2);
   not (w2, w1);
   not (w3, in3);
   nor (w4, w1, in3);
   nor (w5, w2, w3);
   or (out1, w4, w5);
endmodule // rule30struct
