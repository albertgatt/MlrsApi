#!/usr/bin/perl
use File::Find;

my $indir = $ARGV[0];
my $out = $ARGV[1];
my %freqs;
opendir(DIR, $indir);

while(my $f = readdir(DIR)) {
	next if ($f eq '.' || $f eq '..');
	$f = $indir . $f;
	print "$f \n";
	open(FILE, "<:encoding(utf8)", $f) || die("$f could not be opened\n");
	
	while(<FILE>) {
		chomp();
		next if $_ =~ /<.+>/;
		foreach my $w(split(/\s+/,$_)) {		
			$freqs{lc($w)}++;
		}
	}	
}

open (OUT, ">:encoding(utf8)", $out);
print OUT "$_ \t $freqs{$_} \n" foreach keys %freqs;
close(OUT);
