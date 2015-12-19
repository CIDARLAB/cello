module A(output out1, out2, out3, out4,  input in1, in2);
  reg r_out;
  assign out = r_out;
  always@(in1,in2)
    begin
      case({in1,in2})
        2'b00: {out1,out2,out3,out4} = 4'b1000;
        2'b01: {out1,out2,out3,out4} = 4'b0100;
        2'b10: {out1,out2,out3,out4} = 4'b0010;
        2'b11: {out1,out2,out3,out4} = 4'b0001;
      endcase
    end
endmodule
