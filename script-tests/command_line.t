#!/usr/bin/perl

#This is a command line testing script, wrote by Fei He 1/19/2013


use strict;
use warnings;

use Test::More tests => 15;
use Test::Cmd;
use String::Random qw(random_regex random_string);
use JSON;


my $host = "http://localhost:7064/KBaseNetworksRPC/networks";
my $bin  = "scripts";

my $genelist='kb|g.3899.locus.2366';
my $gene_list='kb|g.3899.locus.2366,kb|g.3899.locus.1892,kb|g.3899.locus.2354,kb|g.3899.locus.2549,kb|g.3899.locus.2420,kb|g.3899.locus.2253,kb|g.3899.locus.2229';


#1
my $tes = Test::Cmd->new(prog => "$bin/net_get_all_dataset_sources.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
ok($tes, "creating Test::Cmd object for net_get_all_dataset_sources");
$tes->run(args => "--url=$host");
ok($? == 0,"Running net_get_all_dataset_sources");
my @tem=$tes->stdout;
print "Number of datasource:\t",$#tem+1,"\n";
ok($#tem > 5 , "Datasource detected!");
my $tm_line=join "\t",@tem;
ok($tm_line=~/AGRIS/ && $tm_line=~/AraNet/ && $tm_line=~/PopNet/ && $tm_line=~/PlantCyc/, "No missing datasource detected!");

#2
$tes = Test::Cmd->new(prog => "$bin/net_get_all_datasets.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
ok($tes, "creating Test::Cmd object for net_get_all_datasets");
$tes->run(args => "--url=$host");
ok($? == 0,"Running net_get_all_datasets"); 
@tem=$tes->stdout;
ok($#tem > 5000 , "More than 5000 dataset detected!");

#3
$tes = Test::Cmd->new(prog => "$bin/net_get_all_network_types.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
ok($tes, "creating Test::Cmd object for net_get_all_network_types");
$tes->run(args => "--url=$host");
ok($? == 0,"Running net_get_all_network_types");
@tem=$tes->stdout;
ok($#tem > 2 , "More than 3 network types detected!");

#4
my $tye="PROT_PROT_INTERACTION";
$tes = Test::Cmd->new(prog => "$bin/net_network_type_to_datasets.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
$tes->run(args => "--url=$host", stdin => "$tye");
ok($? == 0,"Running net_network_type_to_datasets");
@tem=$tes->stdout;
ok(@tem > 5, "More than 5 datasets for this type of network has been found!");
$tm_line=join "\t",@tem;
ok($tm_line=~/bicolor/ && $tm_line=~/Poplar/ && $tm_line=~/distachyon/, "No missing dataset detected!(randomly check)");

#5
my $kbg="kb|g.3907";
$tes = Test::Cmd->new(prog => "$bin/net_taxon_to_datasets.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
$tes->run(args => "--url=$host", stdin => "$kbg");
ok($? == 0,"Running net_taxon_to_datasets");
@tem=$tes->stdout;
ok(@tem > 10, "More than 10 datasets for $kbg has been found!");
 $tm_line=join "\t",@tem;
ok($tm_line=~/drought/ && $tm_line=~/xylem/ && $tm_line=~/METABOLIC_SUBSYSTEM/, "No missing dataset detected!(randomly check)");


#6
my $loid="kb|g.3899.locus.10";
$tes = Test::Cmd->new(prog => "$bin/net_entity_to_datasets.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
$tes->run(args => "--url=$host", stdin => "$loid");
ok($? == 0,"Running net_entity_to_datasets");
@tem=$tes->stdout;
ok(@tem > 1, "More than 1 datasets for $loid has been found!");
$tm_line=join "\t",@tem;
ok($tm_line=~/25/ && $tm_line=~/leaf/ && $tm_line=~/cn\.6/, "No missing dataset detected!(randomly check)");


#7
my $kbgene='kb|g.3899.locus.10';

$tes = Test::Cmd->new(prog => "$bin/net_build_first_neighbor_network.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
$tes->run(args => "--url=$host 'kb|netdataset.plant.fn.25,kb|netdataset.plant.cn.6' 'GENE_GENE' ", stdin => "$kbgene");
@tem=$tes->stdout;
ok($? == 0,"Running net_build_first_neighbor_network for $kbgene");
ok(@tem>100, "More than 100 edges in the network");
#print join "\t",@tem;
#print "$tem[0]\n$tem[-1]\n";


$tes = Test::Cmd->new(prog => "$bin/net_build_first_neighbor_network_limited_by_strength.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
$tes->run(args => "--url=$host 'kb|netdataset.plant.fn.25,kb|netdataset.plant.cn.6' 'GENE_GENE' '1.5' ", stdin => "$kbgene");
@tem=$tes->stdout;
ok($? == 0,"Running net_build_first_neighbor_network_limited_by_strength for $kbgene");
ok(@tem<50 && @tem>5, "Strenth cutoff applied!");


my $gene_list='kb|g.3899.locus.10 kb|g.3899.locus.11 kb|g.3899.locus.18543 kb|g.3899.locus.7765  kb|g.3899.locus.2137 kb|g.3899.locus.21155';
$tes = Test::Cmd->new(prog => "$bin/net_build_internal_network.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
$tes->run(args => "--url=$host 'kb|netdataset.plant.fn.25,kb|netdataset.plant.cn.6' 'GENE_GENE' ", stdin => "$gene_list");
ok($? == 0,"Running net_build_internal_network for $gene_list");
@tem=$tes->stdout;
ok(@tem > 5, "Internal network has been built!");


my $gene_list='kb|g.3899.locus.10 kb|g.3899.locus.11 kb|g.3899.locus.18543 kb|g.3899.locus.7765  kb|g.3899.locus.2137 kb|g.3899.locus.21155';
$tes = Test::Cmd->new(prog => "$bin/net_build_internal_network.pl", workdir => '', interpreter => '/kb/runtime/bin/perl');
$tes->run(args => "--url=$host 'kb|netdataset.plant.fn.25,kb|netdataset.plant.cn.6' 'GENE_GENE' ", stdin => "$gene_list");
ok($? == 0,"Running net_build_internal_network for $gene_list");
@tem=$tes->stdout;
ok(@tem > 5, "Internal network has been built!");






