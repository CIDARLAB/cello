#!/usr/bin/perl

$ENV{'PATH'} = '/bin:/usr/bin:/usr/local/bin';


#####################################
# make a circular plasmid image file
#####################################

my $location = $ARGV[0];
chdir($location);
my $dateID = $ARGV[1];
my $file = $ARGV[2]; # cirdna.txt
system("cp $file cirdna.cirp");

my $plasmid_size = qx{grep \"End\" $file | awk '{print \$2}'};
chomp($plasmid_size);
$plasmid_size = sprintf '%.1f', $plasmid_size / 1000;
$plasmid_size = $plasmid_size . " kb";
print "$plasmid_size\n";


system("cirdna -infile cirdna.cirp -ruler N -blocktype Outline -posticks In -posblocks Out -graphout cps -gapsize 1000 -blockheight 2.0 -originangle 270 -gtitle \"$plasmid_size\" -textheight 1.5 -textlength 1.5 -tickheight 1.5 -postext 1.0 -intercolour 7");

system("ps2pdf -dPDFSETTINGS=/prepress -dEPSCrop -r100  cirdna.ps");
system("rm cirdna.cirp");
system("rm cirdna.ps");

my $pdf = substr($file, 0, -4) . ".pdf";
my $png = substr($file, 0, -4) . ".png";
system("mv cirdna.pdf $pdf");
system("gs -dQUIET -dNOPAUSE -dBATCH -sDEVICE=pngalpha -sOutputFile=$png -r100 $pdf");




