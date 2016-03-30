module A(output out1,  input in1, in2, in3);
  always@(in1,in2)
    begin
      case({in1,in2,in3})
        3'b000: {out1} = 1'b1;
        3'b001: {out1} = 1'b1;
        3'b010: {out1} = 1'b1;
        3'b011: {out1} = 1'b1;
        3'b100: {out1} = 1'b1;
        3'b101: {out1} = 1'b1;
        3'b110: {out1} = 1'b1;
        3'b111: {out1} = 1'b0;
      endcase
    end
endmodule
