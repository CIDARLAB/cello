module A(output out1, out2, out3,  input in1, in2, in3, in4);
  reg r_out;
  assign out = r_out;
  always@(in1,in2,in3,in4)
    begin
      case({in1,in2,in3,in4})
        4'b0000: {out1,out2,out3} = 3'b000;
        4'b0001: {out1,out2,out3} = 3'b000;
        4'b0010: {out1,out2,out3} = 3'b000;
        4'b0011: {out1,out2,out3} = 3'b000;
        4'b0100: {out1,out2,out3} = 3'b000;
        4'b0101: {out1,out2,out3} = 3'b000;
        4'b0110: {out1,out2,out3} = 3'b000;
        4'b0111: {out1,out2,out3} = 3'b000;
        4'b1000: {out1,out2,out3} = 3'b000;
        4'b1001: {out1,out2,out3} = 3'b001;
        4'b1010: {out1,out2,out3} = 3'b010;
        4'b1011: {out1,out2,out3} = 3'b010;
        4'b1100: {out1,out2,out3} = 3'b100;
        4'b1101: {out1,out2,out3} = 3'b100;
        4'b1110: {out1,out2,out3} = 3'b100;
        4'b1111: {out1,out2,out3} = 3'b100;
      endcase
    end
endmodule
