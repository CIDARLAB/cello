#!/usr/bin/perl

my $file = $ARGV[0];

my $new = substr($file, 0, -4) . "_new.csv";

open FILE, "<$file";
open NEW, ">$new";

while (<FILE>) {
    my $line = $_;
    chomp($line);

    $line =~ s/pTac/input_pTac/g;
    $line =~ s/pTet/input_pTet/g;
    $line =~ s/pBAD/input_pBAD/g;
    $line =~ s/pLuxStar/input_pLuxStar/g;

    $line =~ s/pPhlF/P3_PhlF/g;
    $line =~ s/pSrpR/S4_SrpR/g;
    $line =~ s/pBM3R1/B3_BM3R1/g;
    $line =~ s/pAmtR/A1_AmtR/g;
    $line =~ s/pHlyIIR/H1_HlyIIR/g;
    $line =~ s/pQacR/Q2_QacR/g;
    $line =~ s/pIcaRA/I1_IcaRA/g;
    $line =~ s/pBetI/E1_BetI/g;
    $line =~ s/pAmeR/F1_AmeR/g;

    print NEW "$line\n";
}
