#!/usr/bin/perl

my %Hash;
$Hash{"0"} = "0000";
$Hash{"1"} = "0001";
$Hash{"2"} = "0010";
$Hash{"3"} = "0011";
$Hash{"4"} = "0100";
$Hash{"5"} = "0101";
$Hash{"6"} = "0110";
$Hash{"7"} = "0111";
$Hash{"8"} = "1000";
$Hash{"9"} = "1001";
$Hash{"A"} = "1010";
$Hash{"B"} = "1011";
$Hash{"C"} = "1100";
$Hash{"D"} = "1101";
$Hash{"E"} = "1110";
$Hash{"F"} = "1111";



my $counter = 0;
my @characters = (0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F);
foreach my $c1(@characters){
    chomp($c1);
    foreach my $c2(@characters){
	$counter++;

	chomp($c2);
	my $hex = "0x$c1$c2";
	#print "<option value=\"$hex\">$hex</option>  ";
	
	my $bin1 = $Hash{$c1};
	my $bin2 = $Hash{$c2};
	my $bin1A = substr($bin1, 0, 1);
	my $bin1B = substr($bin1, 1, 1);
	my $bin1C = substr($bin1, 2, 1);
	my $bin1D = substr($bin1, 3, 1);
	my $bin2A = substr($bin2, 0, 1);
	my $bin2B = substr($bin2, 1, 1);
	my $bin2C = substr($bin2, 2, 1);
	my $bin2D = substr($bin2, 3, 1);

	my $file = "hex3input/$hex.v";
	#system("cp and2.v $file");
	system("echo \"module $hex(output out, input in1, in2, in3);\" > $hex.v");
	system("echo \"  reg r_out;\" >> $hex.v");
	system("echo \"  assign out = r_out;\" >> $hex.v");
	system("echo \"  always@(in1, in2, in3)\" >> $hex.v");
	system("echo \"    begin\" >> $hex.v");
	system("echo \"      case({in1,in2,in3})\" >> $hex.v");
	system("echo \"        3'b000: out = 1'b$bin1A;\" >> $hex.v");
        system("echo \"        3'b001: out = 1'b$bin1B;\" >> $hex.v");
        system("echo \"        3'b010: out = 1'b$bin1C;\" >> $hex.v");
        system("echo \"        3'b011: out = 1'b$bin1D;\" >> $hex.v");
        system("echo \"        3'b100: out = 1'b$bin2A;\" >> $hex.v");
        system("echo \"        3'b101: out = 1'b$bin2B;\" >> $hex.v");
        system("echo \"        3'b110: out = 1'b$bin2C;\" >> $hex.v");
        system("echo \"        3'b111: out = 1'b$bin2D;\" >> $hex.v");
	system("echo \"        default: out = 1'b0;\" >> $hex.v");
	system("echo \"      endcase\" >> $hex.v");
	system("echo \"    end\" >> $hex.v");
	system("echo \"endmodule\" >> $hex.v");
    
	#system("echo \"# 0 0 0  $bin1A\" >> $file");
	#system("echo \"# 0 0 1  $bin1B\" >> $file");
	#system("echo \"# 0 1 0  $bin1C\" >> $file");
	#system("echo \"# 0 1 1  $bin1D\" >> $file");
	#system("echo \"# 1 0 0  $bin2A\" >> $file");
	#system("echo \"# 1 0 1  $bin2B\" >> $file");
	#system("echo \"# 1 1 0  $bin2C\" >> $file");
	#system("echo \"# 1 1 1  $bin2D\" >> $file");


	if($counter %4 == 0) {
	    #print "\n";
	}
    }
}
