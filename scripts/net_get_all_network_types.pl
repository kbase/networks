use strict;
use Data::Dumper;
use Carp;

=head1 NAME

net_get_all_network_types - list all network types

=head1 SYNOPSIS

net_get_all_network_types [--url=http://kbase.us/services/networks]

=head1 DESCRIPTION

List all network types

=head2 Documentation for underlying call

Returns a list of all network types.

=head1 OPTIONS

=over 6

=item B<-u> I<[http://kbase.us/services/networks]> B<--url>=I<[http://kbase.us/services/networks]>
the service url

=item B<-h> B<--help>
prints help information

=item B<--version>
print version information

=back

=head1 EXAMPLE

 net_get_all_network_types 
 net_get_all_network_types --help
 net_get_all_network_types --version

=head1 VERSION

1.0

=cut

use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

my $usage = "Usage: net_get_all_network_types [--url=http://kbase.us/services/networks/]\n";

my $url       = "http://kbase.us/services/networks/";
my $help       = 0;
my $version    = 0;

GetOptions("help"       => \$help,
           "version"    => \$version,
           "url=s"     => \$url) or die $usage;

if($help)
{
	print "$usage\n";
	print "\n";
	print "General options\n";
	print "\t--url=[http://kbase.us/services/networks/]\t\tthe url of the service\n";
	print "\t--help\t\tprint help information\n";
	print "\t--version\t\tprint version information\n";
	print "\n";
	print "Examples: \n";
	print "net_get_all_network_types\n";
	print "\n";
	print "net_get_all_network_types --help\tprint out help\n";
	print "\n";
	print "net_get_all_network_types --version\tprint out version information\n";
	print "\n";
	print "Report bugs to kbase-networks\@lists.kbase.us\n";
	exit(0);
}

if($version)
{
	print "net_get_all_network_types version 1.0\n";
	print "Copyright (C) 2012 KBase Network Team\n";
	print "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n";
	print "This is free software: you are free to change and redistribute it.\n";
	print "There is NO WARRANTY, to the extent permitted by law.\n";
	print "\n";
	print "Written by Shinjae Yoo\n";
	exit(0);
}

die $usage unless @ARGV == 0;

my $oc = Bio::KBase::KBaseNetworksService::Client->new($url);
my $results = $oc->allNetworkTypes();
foreach my $rh (@{$results}) {
  print "$rh\n";
}
