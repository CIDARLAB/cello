

module priority_detector(output out,input A,B,C);
always@(C,B,A)
 begin
  case({C,B,A})
   3'b000: {outZ,outY,outX} = 3'b000;
   3'b001: {outZ,outY,outX} = 3'b001;
   3'b010: {outZ,outY,outX} = 3'b100;
   3'b011: {outZ,outY,outX} = 3'b100;
   3'b100: {outZ,outY,outX} = 3'b010;
   3'b101: {outZ,outY,outX} = 3'b001;
   3'b110: {outZ,outY,outX} = 3'b100;
   3'b111: {outZ,outY,outX} = 3'b100;
  endcase
 end
endmodule

