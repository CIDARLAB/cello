module F(output out, input IN1, IN2, IN3, IN4);

   wire W1, W2, W3, W4, W5, W6, W7, W8;

   not (W1,IN3);
   nor (W2,W1,IN2);
   nor (W3,W2,IN4);
   nor (W4,IN1,IN2);
   nor (W5,W4,IN3);
   or (out,W3,W5);
   
   
   
endmodule
