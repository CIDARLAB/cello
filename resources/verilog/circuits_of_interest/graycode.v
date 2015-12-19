module A(output out1, out2, out3,  input in1, in2, in3);
  reg r_out;
  assign out = r_out;
  always@(in1,in2,in3)
    begin
      case({in1,in2,in3})
        3'b000: {out1,out2,out3} = 3'b000;
        3'b001: {out1,out2,out3} = 3'b001;
        3'b010: {out1,out2,out3} = 3'b011;
        3'b011: {out1,out2,out3} = 3'b010;
        3'b100: {out1,out2,out3} = 3'b110;
        3'b101: {out1,out2,out3} = 3'b111;
        3'b110: {out1,out2,out3} = 3'b101;
        3'b111: {out1,out2,out3} = 3'b100;
      endcase
    end
endmodule
