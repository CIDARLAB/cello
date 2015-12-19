#!/usr/bin/perl

my @files = qx{ls *.v};
foreach my $file(@files) {
    chomp($file);
    open TEMP, ">temp.v";
    open FILE, "<$file";
    while (<FILE>) {
	my $line = $_;
	chomp($line);
	$line =~ s/\’/\'/g;
	print TEMP "$line\n";
    }
    system("mv temp.v $file");
}
