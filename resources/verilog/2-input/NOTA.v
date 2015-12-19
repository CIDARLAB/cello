module NOTA(output out, input in1, in2);
  reg r_out;
  assign out = r_out;
  always@(in1, in2)
    begin
      case({in1,in2})
        2'b00: out = 1'b1;
        2'b01: out = 1'b0;
        2'b10: out = 1'b1;
        2'b11: out = 1'b0;
        default: out = 1'b0;
      endcase
    end
endmodule
