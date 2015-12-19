module A(output out1, out2,  input in1, in2);
  reg r_out;
  assign out = r_out;
  always@(in1,in2)
    begin
      case({in1,in2})
        2'b00: {out2,out1} = 2'b00;
        2'b01: {out2,out1} = 2'b01;
        2'b10: {out2,out1} = 2'b11;
        2'b11: {out2,out1} = 2'b10;
      endcase
    end
endmodule
