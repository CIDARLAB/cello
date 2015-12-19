module E(output out, input IN1, IN2, IN3, IN4);

   wire W1, W2, W3, W4, W5, W6, W7, W8;

   not (W1,IN2);
   nor (W2,W1,IN3);
   nor (out,W2,IN4)
   
   
endmodule
