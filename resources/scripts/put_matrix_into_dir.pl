#!/usr/bin/perl

my @matrix_files = qx{ls matrix*.txt};
foreach my $file(@matrix_files) {
    chomp($file);
    
    my @split = split /matrix_xfer_/, $file;
    my $dir = substr($split[1], 0, -4);
    print "$dir\n";

    system("cp $file $dir");
}
