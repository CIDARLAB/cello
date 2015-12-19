#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';

my $location = $ARGV[0];
chdir($location);
my $pdf = $ARGV[1];

my $png = substr($pdf, 0, -4) . ".png";

#system("$gs_executable -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png -r300 $pdf");
system("convert -density 300 $pdf -transparent white $png"); 
