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

public class KBaseNetwork
{
    public Caller caller;

    public KBaseNetwork(String url) throws MalformedURLException
    {
	caller = new Caller(url);
    }



/**
All datasets, datasources, and network types available in KBase
**/
    public List<Dataset> allDatasets() throws Exception
    {
	try {
	    $args$allDatasets args = new $args$allDatasets();

	    $return$allDatasets res = caller.jsonrpc_call("KBaseNetwork.allDatasets", args, $return$allDatasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public List<DatasetSource> allDatasetSources() throws Exception
    {
	try {
	    $args$allDatasetSources args = new $args$allDatasetSources();

	    $return$allDatasetSources res = caller.jsonrpc_call("KBaseNetwork.allDatasetSources", args, $return$allDatasetSources.class);
	    return res.datasetSources;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public List<String> allNetworkTypes() throws Exception
    {
	try {
	    $args$allNetworkTypes args = new $args$allNetworkTypes();

	    $return$allNetworkTypes res = caller.jsonrpc_call("KBaseNetwork.allNetworkTypes", args, $return$allNetworkTypes.class);
	    return res.networkTypes;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Datasets of a given type available in KBase
**/
    public List<Dataset> datasetSource2Datasets(String datasetSourceRef) throws Exception
    {
	try {
	    $args$datasetSource2Datasets args = new $args$datasetSource2Datasets();
	    args.datasetSourceRef = datasetSourceRef;

	    $return$datasetSource2Datasets res = caller.jsonrpc_call("KBaseNetwork.datasetSource2Datasets", args, $return$datasetSource2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public List<Dataset> taxon2Datasets(String taxon) throws Exception
    {
	try {
	    $args$taxon2Datasets args = new $args$taxon2Datasets();
	    args.taxon = taxon;

	    $return$taxon2Datasets res = caller.jsonrpc_call("KBaseNetwork.taxon2Datasets", args, $return$taxon2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public List<Dataset> networkType2Datasets(String networkType) throws Exception
    {
	try {
	    $args$networkType2Datasets args = new $args$networkType2Datasets();
	    args.networkType = networkType;

	    $return$networkType2Datasets res = caller.jsonrpc_call("KBaseNetwork.networkType2Datasets", args, $return$networkType2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public List<Dataset> entity2Datasets(String entityId) throws Exception
    {
	try {
	    $args$entity2Datasets args = new $args$entity2Datasets();
	    args.entityId = entityId;

	    $return$entity2Datasets res = caller.jsonrpc_call("KBaseNetwork.entity2Datasets", args, $return$entity2Datasets.class);
	    return res.datasets;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


/**
Buid network methods
**/
    public Network buildFirstNeighborNetwork(List<String> datasetIds, String geneId, List<String> edgeTypes) throws Exception
    {
	try {
	    $args$buildFirstNeighborNetwork args = new $args$buildFirstNeighborNetwork();
	    args.datasetIds = datasetIds;
	    args.geneId = geneId;
	    args.edgeTypes = edgeTypes;

	    $return$buildFirstNeighborNetwork res = caller.jsonrpc_call("KBaseNetwork.buildFirstNeighborNetwork", args, $return$buildFirstNeighborNetwork.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public Network buildInternalNetwork(List<String> datasetIds, List<String> geneIds, List<String> edgeTypes) throws Exception
    {
	try {
	    $args$buildInternalNetwork args = new $args$buildInternalNetwork();
	    args.datasetIds = datasetIds;
	    args.geneIds = geneIds;
	    args.edgeTypes = edgeTypes;

	    $return$buildInternalNetwork res = caller.jsonrpc_call("KBaseNetwork.buildInternalNetwork", args, $return$buildInternalNetwork.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }

}


