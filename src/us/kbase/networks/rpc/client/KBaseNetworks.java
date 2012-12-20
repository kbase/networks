package us.kbase.networks.rpc.client;

import java.io.Serializable;

import java.net.*;
import java.io.*;
import java.util.*;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.annotate.*;
import org.codehaus.jackson.type.*;
import org.codehaus.jackson.*;

import us.kbase.rpc.Caller;

/**
Module KBaseNetworks version 1.0
This module provides access to various types of network-related datasets accross all domains of KBase in the unified format.

All methods in this module can be classified into two types: 
i. getting general information about datasets currently available via KBaseNetworks API
ii. building various types of Network objects

Some definition of a KBase network would be desirable here....
**/
public class KBaseNetworks
{
    public Caller caller;

    public KBaseNetworks(String url) throws MalformedURLException
    {
	caller = new Caller(url);
    }



/**
Returns a list of all network-related datasets that can be used to create a network.
**/
    public List<Dataset> allDatasets() throws Exception
    {
	try {
	    $args$allDatasets args = new $args$allDatasets();

	    $return$allDatasets res = caller.jsonrpc_call("KBaseNetworks.allDatasets", args, $return$allDatasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a list of all dataset sources available in KBase via KBaseNetworks API.
**/
    public List<DatasetSource> allDatasetSources() throws Exception
    {
	try {
	    $args$allDatasetSources args = new $args$allDatasetSources();

	    $return$allDatasetSources res = caller.jsonrpc_call("KBaseNetworks.allDatasetSources", args, $return$allDatasetSources.class);
	    return res.datasetSources;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a list of all types of networks that can be created.
**/
    public List<String> allNetworkTypes() throws Exception
    {
	try {
	    $args$allNetworkTypes args = new $args$allNetworkTypes();

	    $return$allNetworkTypes res = caller.jsonrpc_call("KBaseNetworks.allNetworkTypes", args, $return$allNetworkTypes.class);
	    return res.networkTypes;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a list of all datasets from a given dataset source.

DatasetSourceRef datasetSourceRef
A reference to a dataset source
**/
    public List<Dataset> datasetSource2Datasets(String datasetSourceRef) throws Exception
    {
	try {
	    $args$datasetSource2Datasets args = new $args$datasetSource2Datasets();
	    args.datasetSourceRef = datasetSourceRef;

	    $return$datasetSource2Datasets res = caller.jsonrpc_call("KBaseNetworks.datasetSource2Datasets", args, $return$datasetSource2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a list of all datasets that can be used to build a network for a particular genome represented by NCBI taxonomy id. 
                  
                  Taxon taxon
                  NCBI taxonomy id
**/
    public List<Dataset> taxon2Datasets(String taxon) throws Exception
    {
	try {
	    $args$taxon2Datasets args = new $args$taxon2Datasets();
	    args.taxon = taxon;

	    $return$taxon2Datasets res = caller.jsonrpc_call("KBaseNetworks.taxon2Datasets", args, $return$taxon2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a list of all datasets that can be used to build a netowrk of a given type.

NetworkType networkType
The type of network
**/
    public List<Dataset> networkType2Datasets(String networkType) throws Exception
    {
	try {
	    $args$networkType2Datasets args = new $args$networkType2Datasets();
	    args.networkType = networkType;

	    $return$networkType2Datasets res = caller.jsonrpc_call("KBaseNetworks.networkType2Datasets", args, $return$networkType2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a list of all datasets that have at least one interection for a given KBase entity (gene, protein, molecule, genome, etc)
**/
    public List<Dataset> entity2Datasets(String entityId) throws Exception
    {
	try {
	    $args$entity2Datasets args = new $args$entity2Datasets();
	    args.entityId = entityId;

	    $return$entity2Datasets res = caller.jsonrpc_call("KBaseNetworks.entity2Datasets", args, $return$entity2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains 
"source" nodes and all other nodes that have at least one interaction with the "source" nodes. Only interactions of given types are 
considered.    

list<string> datasetIds
List of dataset identifiers to be used for building a network

                  list<string> entityIds
                  List of entity identifiers to be used as source nodes (can be genes, regulons, bi-clusters, etc)           
                
list<EdgeType> edgeTypes
List of possible edge types to be considered for building a network
**/
    public Network buildFirstNeighborNetwork(List<String> datasetIds, List<String> entityIds, List<String> edgeTypes) throws Exception
    {
	try {
	    $args$buildFirstNeighborNetwork args = new $args$buildFirstNeighborNetwork();
	    args.datasetIds = datasetIds;
	    args.entityIds = entityIds;
	    args.edgeTypes = edgeTypes;

	    $return$buildFirstNeighborNetwork res = caller.jsonrpc_call("KBaseNetworks.buildFirstNeighborNetwork", args, $return$buildFirstNeighborNetwork.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns a "first-neighbor" network constructed basing on a given list of datasets. First-neighbor network contains 
"source" nodes and all other nodes that have at least one interaction with the "source" nodes. Only interactions of given types are 
considered. Additional cutOff parameter allows to set a threshold on the strength of edges to be considered.   

list<string> datasetIds
List of dataset identifiers to be used for building a network

                  list<string> entityIds
                  List of entity identifiers to be used as source nodes (can be genes, regulons, bi-clusters, etc)                   
                
list<EdgeType> edgeTypes
List of possible edge types to be considered for building a network

float cutOff
The threshold on the strength of edges to be considered for building a network
**/
    public Network buildFirstNeighborNetworkLimtedByStrength(List<String> datasetIds, List<String> entityIds, List<String> edgeTypes, float cutOff) throws Exception
    {
	try {
	    $args$buildFirstNeighborNetworkLimtedByStrength args = new $args$buildFirstNeighborNetworkLimtedByStrength();
	    args.datasetIds = datasetIds;
	    args.entityIds = entityIds;
	    args.edgeTypes = edgeTypes;
	    args.cutOff = cutOff;

	    $return$buildFirstNeighborNetworkLimtedByStrength res = caller.jsonrpc_call("KBaseNetworks.buildFirstNeighborNetworkLimtedByStrength", args, $return$buildFirstNeighborNetworkLimtedByStrength.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns an "internal" network constructed basing on a given list of datasets. Internal network contains the only nodes defined by the geneIds parameter, 
and edges representing interactions between these nodes.  Only interactions of given types are considered.    

list<string> datasetIds
List of dataset identifiers to be used for building a network

                  list<string> geneIds
                  Identifiers of genes of interest for building a network         
                
list<EdgeType> edgeTypes
List of possible edge types to be considered for building a network
**/
    public Network buildInternalNetwork(List<String> datasetIds, List<String> geneIds, List<String> edgeTypes) throws Exception
    {
	try {
	    $args$buildInternalNetwork args = new $args$buildInternalNetwork();
	    args.datasetIds = datasetIds;
	    args.geneIds = geneIds;
	    args.edgeTypes = edgeTypes;

	    $return$buildInternalNetwork res = caller.jsonrpc_call("KBaseNetworks.buildInternalNetwork", args, $return$buildInternalNetwork.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Returns an "internal" network constructed basing on a given list of datasets. Internal network contains the only nodes defined by the geneIds parameter, 
and edges representing interactions between these nodes.  Only interactions of given types are considered. 
Additional cutOff parameter allows to set a threshold on the strength of edges to be considered.     

list<string> datasetIds
List of dataset identifiers to be used for building a network

                  list<string> geneIds
                  Identifiers of genes of interest for building a network         
                
list<EdgeType> edgeTypes
List of possible edge types to be considered for building a network

float cutOff
The threshold on the strength of edges to be considered for building a network
**/
    public Network buildInternalNetworkLimitedByStrength(List<String> datasetIds, List<String> geneIds, List<String> edgeTypes, float cutOff) throws Exception
    {
	try {
	    $args$buildInternalNetworkLimitedByStrength args = new $args$buildInternalNetworkLimitedByStrength();
	    args.datasetIds = datasetIds;
	    args.geneIds = geneIds;
	    args.edgeTypes = edgeTypes;
	    args.cutOff = cutOff;

	    $return$buildInternalNetworkLimitedByStrength res = caller.jsonrpc_call("KBaseNetworks.buildInternalNetworkLimitedByStrength", args, $return$buildInternalNetworkLimitedByStrength.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }

}


