#!/usr/bin/perl

#this script generates all verilog files for a given number of inputs
#

use strict;
(scalar(@ARGV) >= 1)  or die "missing parameter(s) : input number of inputs.";
my $number_of_inputs=$ARGV[0];
my @tt_set = (); #holds an array of truthtables, which are itself arrays of boolean rows
my @output_rows = generate_input(2**$number_of_inputs);

#generate all the truth tables
foreach my $output_row (@output_rows)
	{
		my @tt = generate_input($number_of_inputs);
		my @output_vals = split('',$output_row);
		for (my $i = 0; $ i < scalar (@output_vals) ; $i++)
		{
		    $tt[$i].=$output_vals[$i];
		    #print "$tt[$i]\n";
		}
	push(@tt_set,\@tt);
	}	
 
# now generate sop expressions ( and verilog files ) for all the tts in tt_set
my $count = 0;
for my $tt_ref (@tt_set)
{

    my @tt = @{$tt_ref};
    my $count_1 = $count+1;
    print "$count_1  tt : @tt \n";
	my @implicants = ();
	for my $row (@tt)
	{
	    #chomp($row);
	    #print "bd $row\n";
		if  ($row =~ /(.*)1$/ ) #ends with a 1 (output of tt row is 1 )
		{
			push (@implicants, make_implicant($1));
		}
	}
	if (scalar (@implicants) > 0 ) 
	{
		#print "implicants : @implicants\n";
		my $sop = make_sop(\@implicants);
		#print "assign output = $sop\n"; # need to wrap this in verilog code	
		my $verilog = generate_verilog($sop, $count);
		#print "$verilog\n";
	}
	$count++;
} 


############################################################# End of main ##########################################################

sub generate_verilog {

	my $sop = shift;
	my $index = shift;
	my $currpath = `pwd`;
	chop($currpath);
	my $filename = "Input$number_of_inputs"."_$index".".v"; 
	my $verilog = "module "."Input$number_of_inputs"."_$index (";
	my $input_decl = "";
	for (my $i = 0; $i < $number_of_inputs ; $i++)
	{
		$verilog.="i$i, ";
		$input_decl.="input i$i;\n";
	}
	$verilog.="out );\n\n$input_decl\noutput out;\n\nassign out = $sop\n\nendmodule\n";
	
	if ( ! (-d "verilog$number_of_inputs") )
	{
		`mkdir verilog$number_of_inputs `;
	}  
	open (OUTPUTFILE, '>'."$currpath/verilog$number_of_inputs/$filename");
	print OUTPUTFILE "$verilog";
	
	return $verilog;
}

sub make_implicant {

	my $input_row = shift;
	my $count = 0;
	my $implicant = "";
	my @tokens = split('',$input_row);
	foreach my $token (@tokens) 
	{
		my $expr = ($token =~/^1$/ ) ? "i$count" : "~ ( i$count )";
		if (length($implicant) > 0 ) {
			$implicant = "( $implicant & $expr )";
		}
		else {
			$implicant = $expr;
		}
	$count++;
	}
	return $implicant;
}


sub make_sop {
	my $impl_ref = shift;
	my @implicants = @{ $impl_ref }; 
	#print "implicants : @implicants\n";
	my $sop = "";
	foreach my $implicant (@implicants)
	{
		if (length($sop) > 0 ) {
		$sop = "( $sop | $implicant )";
		}
		else {
		$sop = $implicant;
		}
	}
	return "$sop ;" 
}

sub generate_input {

	my $num = shift;
	my $entry = 0b0;
	my @entries = ();
	my $s = "\%$num"."b";
	while ($entry < 2**$num)
	{
		my $row = sprintf($s, $entry);
		$row =~ s/\s/0/g;
		#print "$row\n";
		push(@entries,$row);
		$entry +=0b1;
		
		}
	return @entries;
	}
