module B(output out, input IN1, IN2, IN3, IN4);

   wire W1, W2, W3, W4, W5, W6, W7, W8;

   
   not (W1,IN2);
   nor (W2,W1,IN3);
   nor (W3,W1,IN4);
   nor (W4,W2,W3);
   nor (W5,IN3,IN4);
   or (out,W4,W5);
   
   
endmodule
