use strict;
use Data::Dumper;
use Carp;

=head1 NAME

net_entity_to_datasets - find a list of all datasets from a given entity id

=head1 SYNOPSIS

net_entity_to_datasets [--url=http://kbase.us/services/networks]  < entity_id

=head1 DESCRIPTION

Find a list of all datasets from a given entity id.

=head2 Documentation for underlying call

Returns a list of all datasets that have at least one interaction for a given KBase entity (gene, protein, molecule, genome, etc)

string entity_id
The entity identifier 

=head1 OPTIONS

=over 6

=item B<-u> I<[http://kbase.us/services/networks]> B<--url>=I<[http://kbase.us/services/networks]>
the service url

=item B<-h> B<--help>
print help information

=item B<--version>
print version information

=back

=head1 EXAMPLE

 echo "INTACT" | net_entity_to_datasets 
 net_entity_to_datasets --help
 net_entity_to_datasets --version

=head1 VERSION

1.0

=cut


use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

my $usage = "Usage: net_entity_to_datasets [--url=http://kbase.us/services/networks] < enity_id\n";

my $url       = "http://kbase.us/services/networks";
my $help       = 0;
my $version    = 0;

GetOptions("help"       => \$help,
           "version"    => \$version,
           "url=s"     => \$url) or die $usage;
if($help){
print "NAME\n";
print "net_entity_to_datasets  -- This command will retrieve all datasets containing the input gene. \n";
print "\n";
print "VERSION\n";
print "1.0\n";
print "\n";
print "SYNOPSIS\n";
print "\n";
print "USAGE:  net_entity_to_datasets <--url URL> < entity_id\n";
print "\n";
print "DESCRIPTION\n";
print "INPUT:     The input for this command includes the URL of the service (required) and the entity_id read from STDIN.\n";
print "\n";
print "OUTPUT:    The output file for this command will include a list of dataset containing the input gene.\n";
print "\n";
print "PARAMETERS:\n";
print "--url             The URL of the service, --url=http://kbase.us/services/networks.\n";
print "\n";
print "--help            Display help message to standard out and exit with error code zero;\n";
print "                  ignore all other command-line arguments.  \n";
print "--version         Print version information. \n";
print "\n";
print "\n";
print "EXAMPLES\n";
print "echo 'kb|g.3899.locus.10' | net_entity_to_datasets\n";
print "\n";
print "This command will retrieve a dataset which contains the input gene. \n";
print "\n";
print "\n";
exit(0);
}
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
    print "echo 'kb|g.3899.locus.10' | net_entity_to_datasets\n";
    print "\n";
    print "net_entity_to_datasets --help\tprint out help\n";
    print "\n";
    print "net_entity_to_datasets --version\tprint out version information\n";
    print "\n";
    print "Report bugs to kbase-networks\@lists.kbase.us\n";
    exit(0);
}

if($version)
{
    print "net_entity_to_datasets version 1.0\n";
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
my $input = <STDIN>;
$input =~ s/\s+//g;
my $results = $oc->entity2datasets($input);
foreach my $rh (@{$results}) {
  print $rh->{"id"}."\t".$rh->{'network_type'}."\t".$rh->{"source_reference"}."\t".$rh->{"name"}."\t".$rh->{"description"}."\n";
}
