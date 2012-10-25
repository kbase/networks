package us.kbase.networks.client;

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



    public List<Dataset> getDatasets(List<Parameter> ParameterList) throws Exception
    {
	try {
	    $args$getDatasets args = new $args$getDatasets();
	    args.ParameterList = ParameterList;

	    $return$getDatasets res = caller.jsonrpc_call("KBaseNetwork.getDatasets", args, $return$getDatasets.class);
	    return res.datasetList;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public Network buildNetwork(List<Parameter> ParameterList) throws Exception
    {
	try {
	    $args$buildNetwork args = new $args$buildNetwork();
	    args.ParameterList = ParameterList;

	    $return$buildNetwork res = caller.jsonrpc_call("KBaseNetwork.buildNetwork", args, $return$buildNetwork.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public Network buildFirstNeighborNetwork(List<Parameter> ParameterList) throws Exception
    {
	try {
	    $args$buildFirstNeighborNetwork args = new $args$buildFirstNeighborNetwork();
	    args.ParameterList = ParameterList;

	    $return$buildFirstNeighborNetwork res = caller.jsonrpc_call("KBaseNetwork.buildFirstNeighborNetwork", args, $return$buildFirstNeighborNetwork.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }


    public Network buildInternalNetwork(List<Parameter> ParameterList) throws Exception
    {
	try {
	    $args$buildInternalNetwork args = new $args$buildInternalNetwork();
	    args.ParameterList = ParameterList;

	    $return$buildInternalNetwork res = caller.jsonrpc_call("KBaseNetwork.buildInternalNetwork", args, $return$buildInternalNetwork.class);
	    return res.network;
	} catch (IOException e) {
	    System.out.println("Failed with exception: " + e);
	}
	return null;
    }

}


