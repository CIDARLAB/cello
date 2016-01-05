module A(output out1,  input in1, in2);
  always@(in1,in2)
    begin
      case({in1,in2})
        2'b00: {out1} = 1'b0;
        2'b01: {out1} = 1'b0;
        2'b10: {out1} = 1'b0;
        2'b11: {out1} = 1'b1;
      endcase
    end
endmodule
