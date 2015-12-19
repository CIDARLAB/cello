#!/usr/bin/perl

open FILE, "<list_3input.txt";
while (<FILE>) {
    my $line = $_;
    chomp($line);

    my $v = qx{echo \"$line\"  | awk '{print \$1}'};
    chomp($v);
    my $num = qx{echo \"$line\"  | awk '{print \$2}'};
    chomp($num);
    my $OR = qx{echo \"$line\"  | awk '{print \$3}'};
    chomp($OR);

    my $num_or = $num;
    if($OR eq "OUTPUT_OR") {
	$num_or = $num - 2;
    }
    print "$v \t $num \t $num_or \t $OR\n";
     
}
