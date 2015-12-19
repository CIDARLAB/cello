module AND_NAND(output out1, out2, input in1, in2);
  reg r_out;
  assign out = r_out;
  always@(in1, in2)
    begin
      case({in1,in2})
        2'b00: {out1,out2} = 2'b00;
	2'b01: {out1,out2} = 2'b00;
	2'b10: {out1,out2} = 2'b01;
	2'b11: {out1,out2} = 2'b11;

	default: {out1,out2} = 2'b00;
      endcase
    end
endmodule
