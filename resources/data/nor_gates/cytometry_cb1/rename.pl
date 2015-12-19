#!/usr/bin/perl

my @dirs = qx{ls -1p | grep /};
foreach my $dir(@dirs) {
    chomp($dir);
    chop($dir);
    chdir($dir);

    my @files = qx{ls *.txt};
    foreach my $file(@files) {
	chomp($file);
	my $new = $file;
	$new =~ s/NOR_//g;
	$new =~ s/-/_/g;
	print "$new\n";
	system("mv $file $new");
    }

    chdir("..");
}
