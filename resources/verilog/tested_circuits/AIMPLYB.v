

module AIMPLYB(output out,input A,B);
always@(B,A)
 begin
  case({B,A})
   2'b00: {out} = 1'b1;
   2'b01: {out} = 1'b0;
   2'b10: {out} = 1'b1;
   2'b11: {out} = 1'b1;
  endcase
 end
endmodule

