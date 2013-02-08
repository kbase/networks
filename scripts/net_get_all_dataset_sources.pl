use strict;
use Data::Dumper;
use Carp;

=head1 NAME

get_goidlist - find out which GO terms are associated with a gene

=head1 SYNOPSIS

get_goidlist [--url=http://kbase.us/services/ontology_service] [--domain_list=biological_process] [--evidence_code_list=IEA] < geneIDs

=head1 DESCRIPTION

For a given list of Features (aka Genes) from a particular genome (for example "Athaliana" Arabidopsis thaliana ) extract corresponding list of GO identifiers along with GO description for each GO term.

=head2 Documentation for underlying call

This function call accepts three parameters: a list of gene-identifiers, a list of ontology domains, and a list of evidence codes. The list of gene identifiers cannot be empty; however the list of ontology domains and the list of evidence codes can be empty. If any of the last two lists is not empty then the gene-id and go-id pairs retrieved from KBase are further filtered by using the desired ontology domains and/or evidence codes supplied as input. So, if you don't want to filter the initial results then it is recommended to provide empty domain and evidence code lists. Finally, this function returns a mapping of gene-id to go-ids along with go-description, ontology domain, and evidence code; note that in the returned table of results, each gene-id is associated with a list of one of more go-ids.

=head1 OPTIONS

=over 6

=item B<--url> I<[http://kbase.us/services/ontology_service]> B<--url>=I<[http://kbase.us/services/ontology_service]>
url of the server


=item B<--help>
prints help information

=item B<--version>
print version information

=item B<--domain_list> comma separated list of ontology domains e.g. --domain_list=[biological_process,cellular_component]

=item B<--evidence_code_list> comma separated list of ontology evidence codes e.g. --evidence_code_list=[IEA,IEP]

=back

=head1 EXAMPLE

 echo "kb|g.3899.locus.192" | get_goidlist 
  echo "kb|g.3899.locus.192" | get_goidlist --evidence_code=IEA
   get_goidlist --help
    get_goidlist --version

=head1 VERSION

0.1

=cut



use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

my $usage = "Usage: $0 [--host=140.221.92.222:7064]\n";

#my $host       = "140.221.92.222:7064";
my $host       = "kbase.us/services/networks";
my $help       = 0;
my $version    = 0;

GetOptions("help"       => \$help,
           "version"    => \$version,
           "host=s"     => \$host) or die $usage;

if($help)
{
	print "$usage\n";
	print "\n";
	print "General options\n";
	print "\t--host=[xxx.xxx.xx.xxx:xxxx]\t\thostname of the server\n";
	print "\t--help\t\tprint help information\n";
	print "\t--version\t\tprint version information\n";
	print "\n";
	print "Examples: \n";
	print "$0 --host=x.x.x.x:x \n";
	print "\n";
	print "$0 --help\tprint out help\n";
	print "\n";
	print "$0 --version\tprint out version information\n";
	print "\n";
	print "Report bugs to kbase-networks\@lists.kbase.us\n";
	exit(1);
}

if($version)
{
	print "$0 version 1.0\n";
	print "Copyright (C) 2012 KBase Network Team\n";
	print "License GPLv3+: GNU GPL version 3 or later <http://gnu.org/licenses/gpl.html>.\n";
	print "This is free software: you are free to change and redistribute it.\n";
	print "There is NO WARRANTY, to the extent permitted by law.\n";
	print "\n";
	print "Written by Shinjae Yoo\n";
	exit(1);
}

die $usage unless @ARGV == 0;

my $oc = Bio::KBase::KBaseNetworksService::Client->new("http://".$host."/KBaseNetworksRPC/networks");
#my $oc = Bio::KBase::KBaseNetworksService::Client->new("http://".$host);
my $results = $oc->allDatasetSources();
foreach my $rh (@{$results}) {
  print $rh->{"id"}."\t".$rh->{"name"}."\t".$rh->{"description"}."\n";
}
