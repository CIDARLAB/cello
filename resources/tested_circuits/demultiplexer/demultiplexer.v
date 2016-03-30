

module demultiplexer(output out,input A,B);
always@(B,A)
 begin
  case({B,A})
   2'b00: {outZ,outY,outX,outW} = 4'b0010;
   2'b01: {outZ,outY,outX,outW} = 4'b1000;
   2'b10: {outZ,outY,outX,outW} = 4'b0100;
   2'b11: {outZ,outY,outX,outW} = 4'b0001;
  endcase
 end
endmodule

