use strict;
use Data::Dumper;
use Carp;

use Getopt::Long;
use Bio::KBase::KBaseNetworksService::Client;

#This API is for demo use only. A standard one will take place by the end Jan, 2014
#example:
#grep 'kb' genelistfile|perl /kb/dev_container/modules/networks/scripts/net_fetch_internal_networks.pl 'kb|g.3899'



#my $time= localtime;
#print "start at $time\n";

my $url="http://140.221.85.171:7064/KBaseNetworksRPC/networks";
#http://140.221.85.171:7064/KBaseNetworksRPC/networks  is the VM from Shinjae.
my $oc = Bio::KBase::KBaseNetworksService::Client->new($url);
my $species=$ARGV[0];
my $res1 = $oc->taxon2Datasets($species);
my @datasetIds;
my $i=0;
my $dataset2name;
my $dataset2des;
my $dataset2type;
my $dataset2source;
foreach (@{$res1}){
$i++;
#next if $i>20;
	push @datasetIds,$_->{'id'};
	$dataset2name->{$_->{'id'}}=$_->{'name'};
	$dataset2source->{$_->{'id'}}=$_->{'sourceReference'};
	$dataset2type->{$_->{'id'}}=$_->{'networkType'};
	$dataset2des->{$_->{'id'}}=$_->{'description'};

}
my $edge_types = "GENE_GENE";
my @input = <STDIN>;                                                                             
my $istr = join(" ", @input);                                                                    
$istr =~ s/[,]/ /g;
@input = split /\s+/, $istr;          
my @edgeTypes = split/,/, $edge_types;
my $results =$oc->buildInternalNetwork(\@datasetIds, \@input, \@edgeTypes);
#print "@datasetIds\n\n";
#print Dumper($results);

#$time= localtime;
#print "end at $time\n";
#die;
my %nodes = ();
foreach my $hr (@{$results->{'nodes'}}) {
  $nodes{$hr->{'id'}} = [$hr->{'entityId'}, $hr->{'name'}];
}

my $rec_node;
my $rec_edge;
my $density;
foreach my $hr (@{$results->{'edges'}}) {
  my $id1 = $nodes{$hr->{'nodeId1'}}[0];
  my $id2 = $nodes{$hr->{'nodeId2'}}[0];
  my $nm1 = $nodes{$hr->{'nodeId1'}}[1];
  my $nm2 = $nodes{$hr->{'nodeId2'}}[1];
  my $strength = $hr->{'strength'};
  my $ds  = $hr->{'datasetId'};
  my $enm = $hr->{'name'};
#count the data for each dataset 
  #print "$id1\t$id2\t$strength\t$ds\t$rname\t$rtype\t$rdes\t$rsource\t$nm1\t$nm2\t$enm\n";
  $rec_node->{$ds}->{$id1}=1;
  $rec_node->{$ds}->{$id2}=1;
  $rec_edge->{$ds}->{$id1.$id2}=1;

}
my %no_node;
my %no_edge;
foreach (keys %{$rec_node}){
	foreach my $in(keys %{$rec_node->{$_}}){
		$no_node{$_}++;
	}
}

foreach (keys %{$rec_edge}){
	foreach my $in(keys %{$rec_edge->{$_}}){
		$no_edge{$_}++;
	}
}
foreach (keys %no_node){
	$density->{$_}=sprintf "%.2f",($no_edge{$_}*2)/$no_node{$_};
}
#start to generate core-table for narrative2 
print "Dataset\tNo. of nodes\tNo. of edges\tdensity\tDescription\tType\tSource\n";
foreach (sort {$no_node{$b} <=> $no_node{$a} } keys %no_node){

#col1
my $c1=$_;
#col2
my $c2=$no_node{$_};
#col3
my $c3=$no_edge{$_};
#col4
my $c4=$density->{$_};
#col5
my $c5=$dataset2name->{$_}."-".$dataset2des->{$_};
#col6
my $c6=$dataset2type->{$_};
#col7
my $c7=$dataset2source->{$_};

#output core-table by line
print "$c1\t$c2\t$c3\t$c4\t$c5\t$c6\t$c7\n";

}










