use strict;
use Data::Dumper;
use Carp;

=head1 NAME

net_build_internal_network - build an internally connected neighbor network 

=head1 SYNOPSIS

net_build_internal_network [--url=http://kbase.us/services/networks] dataset_ids edge_types < query_entity_ids

=head1 DESCRIPTION

Build an internal network given the list of dataset, edge types, and entity ids.

=head2 Documentation for underlying call

Returns an "internal" network constructed from a given list of datasets. An internal network contains the nodes defined by the query_entity_ids parameter, and edges representing interactions between these nodes.  Only interactions of given types are considered.    

list<string> dataset_ids
List of dataset identifiers to be used for building a network

list<string> query_entity_ids
List of entity identifiers of interest for building a network         
                
list<EdgeType> edge_types
List of possible edge types to be considered for building a network

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

 echo "kb|g.3899.locus.2366 kb|g.3899.locus.2366" | net_build_internal_network "kb|netdataset.plant.cn.7,kb|netdataset.plant.fn.25" "GENE_GENE"
  net_build_internal_network --help
   net_build_internal_network --version

=head1 VERSION

1.0

=cut


use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

my $usage = "Usage: net_build_internal_network [--url=http://kbase.us/services/networks] dataset_ids edge_types < query_entity_ids\n";

my $url       = "http://kbase.us/services/networks";
my $help       = 0;
my $version    = 0;

GetOptions("help"       => \$help,
           "version"    => \$version,
           "url=s"     => \$url) or die $usage;
if($help){
print "NAME\n";
print "net_build_internal_network  -- This service will retrieve a list of network edges among the input genes. \n";
print "\n";
print "VERSION\n";
print "1.0\n";
print "\n";
print "SYNOPSIS\n";
print "net_build_internal_network <--url URL> dataset_ids edge_types < query_entity_ids\n";
print "\n";
print "DESCRIPTION\n";
print "INPUT:    This input for this command requires the URL of the service, the dataset_ids, edge_types and the      \n";
print "          query_entity_ids read from STDIN.\n";
print "\n";
print "OUTPUT:   The output file for this command is a list of network edges;\n";
print "	  The first and second column of the output are two nodes for an edge;\n";
print "	  The third column of the output is the confidence score.\n";
print "\n";
print "\n";
print "PARAMETERS:\n";
print "query_entity_ids  A KBase internal ID for entity types including a gene locus, protein, or clusters.\n";
print "\n";
print "\n";
print "dataset_ids       Dataset_ids can be retrieved by running the  net_get_all_datasets command. \n";
print "                  Dataset ID list (comma separated, xxx,yyy,zzz,...)\n";
print " \n";
print "edge_types        Edge_types are defined in the API documentation. Edge_type is string value.\n";
print "                  Edge_types can also be found on http://kbase.us/developer-zone/documentation/networks-service/.\n";
print "                  Interested edge types (comma separated, xxx,yyy,zzz,...)\n";
print "\n";
print "--url             The URL of the service, --url=http://kbase.us/services/networks.\n";
print "\n";
print "--help            Display help message to standard out and exit with error code zero;\n";
print "                  ignore all other command-line arguments.  \n";
print "--version         Print version information. \n";
print "\n";
print "\n";
print "\n";
print "\n";
print "EXAMPLES \n";
print "echo 'kb|g.3899.locus.33447 kb|g.3899.locus.2366 kb|g.3899.locus.21632 kb|g.3899.locus.30194 kb|g.3899.locus.10 kb|g.3899.locus.11' | net_build_internal_network 'kb|netdataset.plant.fn.25,kb|netdataset.plant.cn.6' 'GENE_GENE' \n";
print "\n";
print "This command will retrieve the network edges among a given list of genes.\n";
print "\n";
print "\n";
exit(0);

}
if($help)
{
    print "$usage\n";
    print "\n";
    print "General options\n";
    print "\t--url=[http://kbase.us/services/networks]\t\tthe service url\n";
    print "\t--help\t\tprint help information\n";
    print "\t--version\t\tprint version information\n";
    print "\tdataset_ids=[xxx,yyy,zzz,...]\t\tdataset id list(comma separated)\n";
    print "\tedge_types=[xxx,yyy,zzz,...]\t\tinterested edge types(comma separated)\n";
    print "\n";
    print "Examples: \n";
    print "echo 'kb|g.3899.locus.10 kb|g.3899.locus.11' | net_build_internal_network 'kb|netdataset.plant.fn.25,kb|netdataset.plant.cn.6' 'GENE_GENE'\n";
    print "\n";
    print "net_build_internal_network --help\tprint out help\n";
    print "\n";
    print "net_build_internal_network --version\tprint out version information\n";
    print "\n";
    print "Report bugs to kbase-networks\@lists.kbase.us\n";
    exit(0);
}

if($version)
{
    print "net_build_internal_network version 1.0\n";
    print "Copyright (C) 2012 KBase Network Team\n";
    print "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n";
    print "This is free software: you are free to change and redistribute it.\n";
    print "There is NO WARRANTY, to the extent permitted by law.\n";
    print "\n";
    print "Written by Shinjae Yoo\n";
    exit(0);
}

die $usage unless @ARGV == 2;
my $dataset_ids = $ARGV[0];
my $edge_types = $ARGV[1];

my $oc = Bio::KBase::KBaseNetworksService::Client->new($url);
my @datasetIds = split/,/, $dataset_ids;
my @edgeTypes = split/,/, $edge_types;
my @input = <STDIN>;
my $istr = join(" ", @input);
$istr =~ s/[,]/ /g;
@input = split /\s+/, $istr;
my $results = $oc->build_internal_network(\@datasetIds, \@input, \@edgeTypes);
my %nodes = ();
foreach my $hr (@{$results->{'nodes'}}) {
  $nodes{$hr->{'id'}} = [$hr->{'entity_id'}, $hr->{'name'}];
}
foreach my $hr (@{$results->{'edges'}}) {
  my $id1 = $nodes{$hr->{'node_id1'}}[0];
  my $id2 = $nodes{$hr->{'node_id2'}}[0];
  my $nm1 = $nodes{$hr->{'node_id1'}}[1];
  my $nm2 = $nodes{$hr->{'node_id2'}}[1];
  my $strength = $hr->{'strength'};
  my $ds  = $hr->{'dataset_id'};
  my $enm = $hr->{'name'};
  print "$id1\t$id2\t$strength\t$ds\t$nm1\t$nm2\t$enm\n";
}
