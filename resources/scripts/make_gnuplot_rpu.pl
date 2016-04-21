#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';

my $location = $ARGV[0];
chdir($location);
my $dateID = $ARGV[1];


###################################### gnuplot figures for RPU wiring

my $gpquery = $dateID . "*xfer*.gp";
my @files = qx{ls $gpquery};
foreach my $file(@files) {
    chomp($file);
    my $eps = substr($file, 0, -3) . ".eps";
    my $pdf = substr($file, 0, -3) . ".pdf";
    my $png = substr($file, 0, -3) . ".png";
    system("gnuplot $file");
    system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r100 $eps");
    system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png -r300 $pdf");
}
