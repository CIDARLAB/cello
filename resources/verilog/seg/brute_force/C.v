module C(output out, input IN1, IN2, IN3, IN4);

   wire W1, W2, W3, W4, W5, W6, W7, W8;

   nor (W1,IN2,IN4);
   nor (W2,W1,IN1);
   nor (W3,IN2,IN3);
   or (out,W2,W3)
   
   
endmodule
