#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';

my $location = $ARGV[0];
chdir($location);
my $dateID = $ARGV[1];

my $q = $dateID . "*.dot";

my @dots = qx{ls $q};
foreach my $dot(@dots) {
    chomp($dot);
    print "$dot\n";

    my $eps = substr($dot, 0, -4) . ".eps";
    my $pdf = substr($dot, 0, -4) . ".pdf";
    my $png = substr($dot, 0, -4) . ".png";

    system("dot -Teps $dot > $eps");
    system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r100  $eps");
    system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png -r300 $pdf");
}
