module A(output out1, out2, out3,  input in1, in2);
  reg r_out;
  assign out = r_out;
  always@(in1,in2)
    begin
      case({in1,in2})
        2'b00: {out1,out2,out3} = 3'b000;
        2'b01: {out1,out2,out3} = 3'b001;
        2'b10: {out1,out2,out3} = 3'b010;
        2'b11: {out1,out2,out3} = 3'b100;
      endcase
    end
endmodule
