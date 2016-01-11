#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';

my $location = $ARGV[0];
chdir($location);
my $dot = $ARGV[1];

my $eps = substr($dot, 0, -4) . ".eps";
my $pdf = substr($dot, 0, -4) . ".pdf";
my $png = substr($dot, 0, -4) . ".png";
my $svg = substr($dot, 0, -4) . ".svg";

#system("dot -Teps $dot > $eps");
#system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r300  $eps");
#system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png -r150 $pdf");
#system("dot -Tpng $dot > $png");

system("dot -Tsvg $dot > $svg");
system("convert -density 200 -transparent white $svg $png");
