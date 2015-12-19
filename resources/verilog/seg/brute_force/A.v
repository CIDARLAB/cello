module A(output out, input IN1, IN2, IN3, IN4);

   wire W1, W2, W3, W4, W5, W6, W7, W8;

   nor (W1,IN2,IN3);
   nor (W2,IN3,IN4);
   nor (W3,W1,W2);
   nor (W4,W2,IN1);
   nor (W5,W4,IN2);
   or (out,W3,W5);
   

endmodule
