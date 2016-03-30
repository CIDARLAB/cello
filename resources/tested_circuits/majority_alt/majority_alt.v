

module majority_alt(output out,input A,B,C);
always@(C,B,A)
 begin
  case({C,B,A})
   3'b000: {out} = 1'b0;
   3'b001: {out} = 1'b0;
   3'b010: {out} = 1'b0;
   3'b011: {out} = 1'b1;
   3'b100: {out} = 1'b0;
   3'b101: {out} = 1'b1;
   3'b110: {out} = 1'b1;
   3'b111: {out} = 1'b1;
  endcase
 end
endmodule

