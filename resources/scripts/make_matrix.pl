#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';

my %Hash;

$Hash{"SrpR"}=   "1F78B4";
$Hash{"BM3R1"}=  "33A02C";
$Hash{"PhlF"}=   "E31A1C";
$Hash{"AmtR"}=   "FF7F00";
$Hash{"HlyIIR"}= "6A3D9A";
$Hash{"BetI"}=   "A6CEE3";
$Hash{"IcaRA"}=  "B2DF8A";
$Hash{"QacR"}=   "FB9A99";
$Hash{"LitR"}=   "FDBF6F";
$Hash{"AmeR"}=   "CAB2D6";


my @dirs = qx{ls -1p | grep / | grep NOR | grep -v YFP};
foreach my $dir(@dirs) {
    chomp($dir);
    chop($dir);
    my @split = split /-/, $dir;
    my $cds = $split[1];
    
    chdir($dir);

    my $txt = qx{ls matrix_xfer_NOR*.txt};
    chomp($txt);

    my $gp  = substr($txt, 0, -4) . ".gp";
    my $eps = substr($txt, 0, -4) . ".eps";

    print "$dir $cds $Hash{$cds} $txt\n";

    open FILE, "<../matrix_xfer.gp";
    while (<FILE>) {
	my $line = $_;
	chomp($line);
	$line =~ s/matrix_xfer.eps/$eps/g;
	$line =~ s/matrix_xfer.txt/$txt/g;
	$line =~ s/hexcolor/$Hash{$cds}/g;

	system("echo \"$line\" >> $gp");
    }

    system("gnuplot $gp");
    system("~/epstopdf.pl $eps");

    chdir("..");
}
