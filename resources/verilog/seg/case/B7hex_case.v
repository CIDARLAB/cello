module B7hex(output out, input in1, in2, in3, in4);
  reg r_out;
  assign out = r_out;
  always@(in1, in2, in3, in4)
    begin
      case({in1, in2, in3, in4})
        4'b0000: out = 1'b1;
        4'b0001: out = 1'b1;
        4'b0010: out = 1'b1;
        4'b0011: out = 1'b1;
        4'b0100: out = 1'b1;
        4'b0101: out = 1'b0;
        4'b0110: out = 1'b0;
        4'b0111: out = 1'b1;
	4'b1000: out = 1'b1;
        4'b1001: out = 1'b1;
        4'b1010: out = 1'b1;
        4'b1011: out = 1'b0;
        4'b1100: out = 1'b0;
        4'b1101: out = 1'b1;
        4'b1110: out = 1'b0;
        4'b1111: out = 1'b0;
	
        default: out = 1'b0;
      endcase
    end
endmodule
