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
	chomp($c2);
	$counter++;

	print "0x$c1$c2 $Hash{$c1}$Hash{$c2}\n";
    }
}
