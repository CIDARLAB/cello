module GrayCode(output out1, out2, out3, input in1, in2, in3);
  reg r_out;
  assign out = r_out;
  always@(in1, in2, in3)
    begin
      case({in3,in2,in1})
        3'b000: {out3,out2,out1} = 3'b000;
	3'b001: {out3,out2,out1} = 3'b001;
	3'b010: {out3,out2,out1} = 3'b011;
	3'b011: {out3,out2,out1} = 3'b010;
	3'b100: {out3,out2,out1} = 3'b110;
	3'b101: {out3,out2,out1} = 3'b111;
	3'b110: {out3,out2,out1} = 3'b101;
	3'b111: {out3,out2,out1} = 3'b100;

	default: {out1,out2,out3} = 3'b000;
      endcase
    end
endmodule
