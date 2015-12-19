module D(output out, input IN1, IN2, IN3, IN4);

   wire W1, W2, W3, W4, W5, W6, W7, W8;

   
   nor (W1,IN3,IN4);
   nor (W2,W1,IN3);
   nor (W3,W1,IN4);
   nor (W4,W2,W3);
   not (W5,IN2);
   nor (W6,W4,W5);
   nor (W7,W2,IN2);
   or (out,W6,W7);
   
   
endmodule
